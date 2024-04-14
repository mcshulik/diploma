package com.example.detector.asr;

/**
 * @author Paval Shlyk
 * @since 13/04/2024
 */
public interface WhisperListener {
    enum State {
	START,
	DONE,
	ERROR
    }

    void onState(State state, String message);

    void onResult(String result);
}
