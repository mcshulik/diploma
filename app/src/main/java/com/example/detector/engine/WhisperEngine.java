package com.example.detector.engine;

import com.example.detector.asr.WhisperListener;
import com.example.detector.engine.impl.JavaWhisperEngine;
import com.example.detector.engine.impl.NativeWhisperEngine;
import lombok.NonNull;

public interface WhisperEngine extends AutoCloseable {
    enum Type {
	NATIVE, JAVA
    }

    void interrupt();

    void setListener(WhisperListener listener);

    String transcribeFile(String wavePath);

    String transcribeBuffer(float[] samples);

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
