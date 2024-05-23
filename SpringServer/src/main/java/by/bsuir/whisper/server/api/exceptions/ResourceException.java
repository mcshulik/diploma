package by.bsuir.whisper.server.api.exceptions;

import lombok.Getter;

/**
 * The wrapper around exception to make exception handler more lightweight.
 * This exception is used with violation of business logic rules.
 *
 * @author Paval Shlyk
 * @since 01/02/2024
 */
@Getter
public class ResourceException extends RuntimeException {
    private final int code;

    public ResourceException(String message, int code) {
	super(message);
	this.code = code;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
	return this;
    }
}
