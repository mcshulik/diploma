package com.example.detector.engine.impl;

import android.util.Log;
import com.example.detector.asr.WhisperListener;
import com.example.detector.engine.ResourceNotFoundException;
import com.example.detector.engine.WhisperEngine;
import com.example.detector.engine.WhisperEngineConfig;
import com.google.errorprone.annotations.ThreadSafe;

@ThreadSafe
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
    public String transcribeBuffer(float[] samples) {
	return transcribeBuffer(nativePtr, samples);
    }

    @Override
    public String transcribeFile(String waveFile) {
	return transcribeFile(nativePtr, waveFile);
    }

    @Override
    public void interrupt() {
    }

    @Override
    public void setListener(WhisperListener listener) {

    }

    static {
	System.loadLibrary("audioEngine");
    }

    // Native methods
    private native long createTFLiteEngine();

    private native int loadModel(long nativePtr, String modelPath, boolean isMultilingual);

    private native void freeModel(long nativePtr);

    private native String transcribeBuffer(long nativePtr, float[] samples);

    private native String transcribeFile(long nativePtr, String waveFile);

    @Override
    public void close() throws Exception {
	freeModel(nativePtr);
    }
}
