package com.example.detector.engine;

/**
 * @author Paval Shlyk
 * @since 13/04/2024
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
	super(message);
    }

    public ResourceNotFoundException(Throwable t) {
	super(t);
    }
}
