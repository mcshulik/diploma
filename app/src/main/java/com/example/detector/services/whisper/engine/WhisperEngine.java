package com.example.detector.services.whisper.engine;

import com.example.detector.services.whisper.engine.impl.JavaWhisperEngine;
import com.example.detector.services.whisper.engine.impl.NativeWhisperEngine;
import lombok.NonNull;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Optional;

@NotThreadSafe
public interface WhisperEngine extends AutoCloseable {
    enum Type {
	NATIVE, JAVA
    }

    void interrupt();

    Optional<String> transcribeBuffer(float[] samples);

    static WhisperEngine withConfig(@NonNull WhisperEngineConfig config) throws ResourceNotFoundException {
	final WhisperEngine engine;
	switch (config.type()) {
	    case NATIVE:
		engine = new NativeWhisperEngine(config);
		break;
	    case JAVA:
		engine = new JavaWhisperEngine(config);
		break;
	    default:
		throw new IllegalStateException("Unexpected value: " + config.type());
	}
	return engine;
    }
    //String getTranslation(String wavePath);
}
