package by.bsuir.whisper.server.context;

import java.lang.annotation.RetentionPolicy;

/**
 * @author Paval Shlyk
 * @since 12/02/2024
 */
@java.lang.annotation.Retention(RetentionPolicy.RUNTIME)
public @interface CatchThrows {
    /**
     * The default exception can be overridden by {@link CatchLevel}
     *
     * @return the default exception will be caught
     */
    Class<? extends Throwable> value() default Throwable.class;

    /**
     * If named method throws exception, it will be suppressed by implementation
     *
     * @return the name of handler for given exception
     */
    String call();

    //the arguments of origin method
    String[] args() default {};
}
