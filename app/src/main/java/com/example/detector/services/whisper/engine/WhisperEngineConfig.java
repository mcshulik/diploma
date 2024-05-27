package com.example.detector.services.whisper.engine;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

import static com.example.detector.services.whisper.engine.WhisperEngine.*;

/**
 * @author Paval Shlyk
 * @since 13/04/2024
 */
@Accessors(fluent = true)
@Builder
@Getter
public final class WhisperEngineConfig {
    @NonNull
    private final String modelPath;
    @NonNull
    private final String vocabPath;
    @NonNull
    private final Type type;
    private final boolean isMultiLang;
}
