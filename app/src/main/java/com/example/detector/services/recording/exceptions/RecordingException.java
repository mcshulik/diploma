package com.example.detector.services.recording.exceptions;

/**
 * @author Paval Shlyk
 * @since 27/05/2024
 */
public class RecordingException extends RuntimeException {
    public RecordingException(String message) {
	super(message);
    }

    public RecordingException(Throwable t) {
	super(t);
    }
}
