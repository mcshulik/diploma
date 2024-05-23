package by.bsuir.whisper.server.api.exceptions;

/**
 * @author Paval Shlyk
 * @since 05/02/2024
 */
public class ResourceBusyException extends ResourceException {
    public ResourceBusyException(String message, int code) {
	super(message, code);
    }
}
