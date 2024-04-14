package com.example.detector.asr;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @author Paval Shlyk
 * @since 13/04/2024
 */
public interface RecorderListener {
    enum State {
	STOP,
	START,
	RECORDING,
	DONE,
    }

    void onStateUpdate(@NonNull State state, @Nullable String msg);

    void onDataUpdate(float[] samples);
}
