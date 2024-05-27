package com.example.detector.services.whisper.engine.impl;

import android.util.Log;
import com.example.detector.services.whisper.engine.ResourceNotFoundException;
import com.example.detector.services.whisper.engine.WhisperEngine;
import com.example.detector.services.whisper.engine.WhisperEngineConfig;
import lombok.val;

import java.util.Optional;

public class NativeWhisperEngine implements WhisperEngine {
    private final String TAG = "WhisperEngineNative";
    private final long nativePtr; // Native pointer to the TFLiteEngine instance

    public NativeWhisperEngine(WhisperEngineConfig config) {
	final String modelPath = config.modelPath();
//	final String vocabPath = config.vocabPath();//native engine uses hard-coded values
	final boolean isMultiLang = config.isMultiLang();
	nativePtr = createTFLiteEngine();
	int loadedCode = loadModel(nativePtr, modelPath, isMultiLang);
	if (loadedCode != 0) {
	    final String msg = "Failed to found whisper model: " + modelPath;
	    Log.e(TAG, msg);
	    throw new ResourceNotFoundException(msg);
	}
	Log.d(TAG, "Model is loaded..." + modelPath);
    }


    @Override
    public Optional<String> transcribeBuffer(float[] samples) {
	val msg = transcribeBuffer(nativePtr, samples);
	if (msg == null || msg.isEmpty()) {
	    return Optional.empty();
	}
	return Optional.of(msg);
    }

    @Override
    public void interrupt() {
    }

    static {
	System.loadLibrary("audioEngine");
    }

    // Native methods
    private native long createTFLiteEngine();

    private native int loadModel(long nativePtr, String modelPath, boolean isMultilingual);

    private native void freeModel(long nativePtr);

    private native String transcribeBuffer(long nativePtr, float[] samples);

    @Deprecated
    private native String transcribeFile(long nativePtr, String waveFile);

    @Override
    public void close() throws Exception {
	freeModel(nativePtr);
    }
}
