package com.example.detector.asr;

/**
 * @author Paval Shlyk
 * @since 13/04/2024
 */
public interface RecorderListener {
    void onUpdateReceived(String message);
    void onDataReceived(float[] samples);
}
