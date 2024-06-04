package by.bsuir.whisper.server.context;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.data.util.Pair;

import java.lang.reflect.*;
import java.util.*;

/**
 * There are several limitations of reflective implementation:
 * <ul>
 * 	<li>
 *       Only one type of catchLevel can be handled
 * 	</li>
 * 	<li>
 * 	The order of arguments should be the same as in target class
 * 	</li>
 * 	<li>
 * 	It's really slow in run-time
 * 	</li>
 * </ul>
 * The catch handler should accept at least one parameter ―
 * the exception was thrown (this exception will always last argument)
 *
 * @author Paval Shlyk
 * @since 13/02/2024
 */
@RequiredArgsConstructor
public class CatchThrowsBeanPostProcessor implements BeanPostProcessor {
    private final Map<String, Pair<Class<?>, Map<String, ExceptionHandlerParams>>> map = new HashMap<>();

    private static Pair<Class<?>[], boolean[]> fetchInvokableArguments(Method target, CatchThrows annotation) {
	boolean[] fieldsMap = new boolean[target.getParameters().length];//array is filled with default (false) values
	if (annotation.args().length == 0) {
	    return Pair.of(new Class<?>[]{annotation.value()}, fieldsMap);
	}
	String[] targetArgs = annotation.args();
	//assert all name unique by java convention
	int index = 0;
	List<Class<?>> args = new ArrayList<>();
	for (Parameter parameter : target.getParameters()) {
	    if (parameter.isNamePresent()) {
		String name = parameter.getName();
		var optional = Arrays.stream(targetArgs)
				   .filter(name::equals)
				   .findAny();
		if (optional.isPresent()) {
		    args.add(parameter.getType());
		    fieldsMap[index] = true;
		}
	    }
	    index += 1;
	}
	args.add(annotation.value());
	return Pair.of(
	    args.toArray(Class<?>[]::new),
	    fieldsMap
	);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, @NonNull String beanName) throws BeansException {
	Class<?> clazz = bean.getClass();
	Map<String, ExceptionHandlerParams> methodsMap = new HashMap<>();
	Class<? extends Throwable> defaultCatchLevel;
	if (clazz.isAnnotationPresent(CatchLevel.class)) {
	    CatchLevel levelAnnotation = clazz.getAnnotation(CatchLevel.class);
	    defaultCatchLevel = levelAnnotation.value();
	} else {
	    defaultCatchLevel = null;
	}
	for (Method method : clazz.getDeclaredMethods()) {
	    if (method.isAnnotationPresent(CatchThrows.class)) {
		method.setAccessible(true);
		CatchThrows annotation = method.getAnnotation(CatchThrows.class);
		assert annotation != null;
		String invokable = annotation.call();
		Pair<Class<?>[], boolean[]> argsPair = fetchInvokableArguments(method, annotation);
		Method handler;
		try {
		    handler = clazz.getDeclaredMethod(invokable, argsPair.getFirst());
		    handler.setAccessible(true);
		} catch (Throwable t) {
		    throw new RuntimeException(t);
		}
		if (methodsMap.containsKey(method.getName()) || !(Throwable.class.isAssignableFrom(handler.getReturnType()))) {
		    throw new IllegalStateException(STR."""
		    Failed to create CatchInvoke annotation implementation for\{method.getName()} of \{clazz}
		    The catch method\{annotation.call()} should be unique by name and return exception
		    """);
		}
		Class<? extends Throwable> catchLevel;
		if (defaultCatchLevel != null) {
		    catchLevel = defaultCatchLevel;
		} else {
		    catchLevel = annotation.value();
		}
		var params = ExceptionHandlerParams.builder()
				 .bean(bean)
				 .catchLevel(catchLevel)
				 .method(handler)
				 .arguments(argsPair.getSecond())
				 .build();
		methodsMap.put(method.getName(), params);
	    }
	}
	if (!methodsMap.isEmpty()) {
	    map.put(beanName, Pair.of(clazz, methodsMap));
	}
	return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
	var pair = map.get(beanName);
	if (pair == null) {
	    return bean;
	}
	Class<?> clazz = pair.getFirst();
	return Proxy.newProxyInstance(
	    clazz.getClassLoader(),
	    clazz.getInterfaces(),
	    MethodHandler.builder()
		.bean(bean)
		.methodsMap(pair.getSecond())
		.build()
	);
    }

    @Builder
    private record ExceptionHandlerParams(
	Object bean,
	Method method,
	Class<? extends Throwable> catchLevel,
	boolean[] arguments
    ) {
    }

    @Builder
    private record MethodHandler(
	Object bean,
	Map<String, ExceptionHandlerParams> methodsMap
    ) implements InvocationHandler {
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	    var params = methodsMap.get(method.getName());
	    try {
		if (params == null) {
		    return method.invoke(bean, args);
		}
	    } catch (InvocationTargetException e) {
		throw e.getCause();
	    }
	    Object result;
	    try {
		result = method.invoke(bean, args);
	    } catch (InvocationTargetException e) {
		Throwable cause = e.getCause();
		assert cause != null : "e is only wrapper under real catchLevel";
		if (params.catchLevel.isAssignableFrom(cause.getClass())) {
		    Object[] realArgs = fillArgs(args, params.arguments, cause);
		    try {
			cause = (Throwable) params.method.invoke(params.bean, realArgs);
		    } catch (Throwable _) {
			//catchLevel in handler will be suppressed
		    }
		    assert cause != null : "The call method should always return non null";
		}
		throw cause;
	    }
	    return result;
	}

	private static Object[] fillArgs(Object[] args, boolean[] bits, Throwable t) {
	    assert args.length == bits.length : "The bits for arguments should be used only with method accepting corresponding count of arguments";
	    List<Object> list = new ArrayList<>(args.length + 1);
	    for (int i = 0; i < bits.length; i++) {
		if (bits[i]) {
		    list.add(args[i]);
		}
	    }
	    list.add(t);
	    return list.toArray(Object[]::new);
	}

	private boolean isCompatibleException(
	    Class<? extends Throwable>[] exceptions,
	    Class<? extends Throwable> target
	) {
	    for (Class<?> exception : exceptions) {
		if (target.isAssignableFrom(exception)) {
		    return true;
		}
	    }
	    return false;
	}
    }
}
