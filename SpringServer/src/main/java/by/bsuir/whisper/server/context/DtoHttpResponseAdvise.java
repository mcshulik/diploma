package by.bsuir.whisper.server.context;

import by.bsuir.whisper.server.api.dto.response.PresenceDto;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DtoHttpResponseAdvise {
    @Around(value = "@annotation(org.springframework.web.bind.annotation.DeleteMapping) " +
			"&& execution(public Object *(..))")
    public Object handleDeleteDto(ProceedingJoinPoint joinPoint) throws Throwable {
	Object object = joinPoint.proceed();
	if (!(object instanceof PresenceDto dto)) {
	    return object;
	}
	var status = dto.isPresent()
			 ? HttpStatus.NO_CONTENT
			 : HttpStatus.NOT_FOUND;
	return ResponseEntity.status(status).build();
    }
}
