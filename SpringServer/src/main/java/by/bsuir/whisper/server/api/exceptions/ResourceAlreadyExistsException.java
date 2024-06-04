package by.bsuir.whisper.server.api.exceptions;

/**
 * @author Paval Shlyk
 * @since 04/06/2024
 */
public class ResourceAlreadyExistsException extends ResourceException {
    public ResourceAlreadyExistsException(String message, int code) {
	super(message, code);
    }
}
