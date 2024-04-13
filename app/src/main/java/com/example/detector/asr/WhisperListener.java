package com.example.detector.asr;

/**
 * @author Paval Shlyk
 * @since 13/04/2024
 */
public interface WhisperListener {
    void onUpdate(String message);
    void onResult(String result);
}
