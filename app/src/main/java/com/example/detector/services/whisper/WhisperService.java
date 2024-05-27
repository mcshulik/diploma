package com.example.detector.services.whisper;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.disposables.Disposable;

import java.io.File;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
public interface WhisperService extends Disposable {
    Maybe<String> transcript(float[] samples);

    Maybe<String> transcript(File file);
}
