package com.example.detector.services.whisper;

/**
 * @author Paval Shlyk
 * @since 27/05/2024
 */
public class WhisperException extends RuntimeException {
    public WhisperException() {

    }

    public WhisperException(String message) {
	super(message);
    }

    public WhisperException(Throwable t) {
	super(t);
    }
}
