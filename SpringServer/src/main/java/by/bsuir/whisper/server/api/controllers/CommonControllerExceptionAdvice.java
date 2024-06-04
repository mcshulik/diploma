package by.bsuir.whisper.server.api.controllers;

import by.bsuir.whisper.server.api.dto.response.ErrorDto;
import by.bsuir.whisper.server.api.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Paval Shlyk
 * @since 01/02/2024
 */
@Slf4j
@RestControllerAdvice
public class CommonControllerExceptionAdvice {
    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ErrorDto> catchNotFoundException(ResourceNotFoundException e) {
	return errorOf(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler({ResourceModifyingException.class})
    public ResponseEntity<ErrorDto> catchModifyingException(ResourceModifyingException e) {
	return errorOf(HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler({ResourceAlreadyExistsException.class})
    public ResponseEntity<ErrorDto> catchResourceAlreadyExists(ResourceAlreadyExistsException e) {
	return errorOf(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorDto> catchNotValidMethodException(HttpRequestMethodNotSupportedException e) {
	log.trace("Request to not implemented method");
	return errorOf(HttpStatus.METHOD_NOT_ALLOWED, e);
    }

    @ExceptionHandler({ResourceBusyException.class})
    public ResponseEntity<ErrorDto> catchBusyException(ResourceBusyException e) {
	return errorOf(HttpStatus.TOO_MANY_REQUESTS, e);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorDto> catchValidationException(MethodArgumentNotValidException e) {
	return errorOf(HttpStatus.BAD_REQUEST, e);
    }

    @ExceptionHandler({Throwable.class})
    public ResponseEntity<ErrorDto> catchThrowable(Throwable t) {
	return errorOf(HttpStatus.INTERNAL_SERVER_ERROR, t);
    }

    private ResponseEntity<ErrorDto> errorOf(
	HttpStatus status,
	ResourceException e
    ) {
	ErrorDto dto = ErrorDto.builder()
			   .errorCode(ErrorDto.codeOf(status, e.getCode()))
			   .errorMessage(e.getMessage())
			   .build();
	return ResponseEntity.status(status).body(dto);
    }

    private ResponseEntity<ErrorDto> errorOf(
	HttpStatus status,
	Throwable t) {
	ErrorDto dto = ErrorDto.builder()
			   .errorCode(ErrorDto.codeOf(status, ErrorDto.MAGIC_CODE))
			   .errorMessage("Server doesn't know how to deal your request. Sorry...(")
			   .errors(new String[]{t.getMessage()})
			   .build();
	return ResponseEntity.status(status).body(dto);
    }

    private ResponseEntity<ErrorDto> errorOf(
	HttpStatus status,
	MethodArgumentNotValidException e) {
	List<String> messages = new ArrayList<>();
	for (ObjectError error : e.getAllErrors()) {
	    String message;
	    if (error instanceof FieldError fieldError) {
		message = STR."Field `\{fieldError.getField()}`: \{fieldError.getDefaultMessage()}";
	    } else {
		message = error.toString();
	    }
	    messages.add(message);
	}
	String errorMessage = "Input request violates some rules";
	ErrorDto dto = ErrorDto.builder()
			   .errorCode(ErrorDto.codeOf(status, ErrorDto.MAGIC_CODE + 1))
			   .errorMessage(errorMessage)
			   .errors(messages.toArray(String[]::new))
			   .build();
	return ResponseEntity.status(status).body(dto);
    }
}
