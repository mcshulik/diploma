package com.example.detector.asr;

import android.util.Log;
import androidx.annotation.NonNull;
import com.example.detector.services.whisper.engine.WhisperEngine;
import com.example.detector.services.whisper.engine.WhisperEngineConfig;
import com.google.common.base.Optional;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.example.detector.asr.WhisperListener.*;

/**
 * @author Paval Shlyk
 * @since 13/04/2024
 */
public final class Whisper implements AutoCloseable {
    public static final String TAG = "Whisper";
    public static final String ACTION_TRANSCRIBE = "TRANSCRIBE";
    public static final String MSG_PROCESSING = "Processing...";
    public static final String MSG_PROCESSING_DONE = "Processing done...!";
    public static final String MSG_FILE_NOT_FOUND = "Input file doesn't exist..!";
    private final AtomicBoolean inProgress = new AtomicBoolean(false);
    private final Object audioBufferLock = new Object();
    private final Object whisperEngineLock = new Object();
    private final Queue<float[]> audioBufferQueue = new LinkedList<>();
    private final BlockingQueue<float[]> queue = new LinkedBlockingQueue<>();
    private Thread mMicTranscribeThread = null;
    private final WhisperEngine engine;

    private Whisper(WhisperEngineConfig config) {
	engine = WhisperEngine.withConfig(config);
    }

    public static Optional<Whisper> of(WhisperEngineConfig config) {
	Whisper instance = null;
	try {
	    instance = new Whisper(config);
	} catch (Throwable t) {
	    Log.e(TAG, "Failed to instantiate whisper: " + t.getMessage());
	}
	return Optional.fromNullable(instance);
    }

    private Thread executorThread;
    private WhisperListener listener;

    public void setListener(WhisperListener listener) {
	this.listener = listener;
	engine.setListener(listener);
	startMicTranscriptionThread();
    }

    public void start(@NonNull String filePath) {
	Log.d(TAG, "WaveFile: " + filePath);
	File soundPath = new File(filePath);
	if (!soundPath.exists()) {
	    sendUpdate(State.ERROR, "File not found");
	    return;
	}
	if (isInProgress()) {
	    Log.d(TAG, "Execution is already in progress...");
	    return;
	}
	executorThread = new Thread(() -> {
	    inProgress.set(true);
	    threadFunction(filePath);
	    inProgress.set(false);
	});
	executorThread.start();
    }

    public void stop() {
	inProgress.set(false);
	try {
	    if (executorThread != null) {
		engine.interrupt();
		executorThread.join();
		executorThread = null;
	    }
	} catch (InterruptedException e) {
	    throw new RuntimeException(e);
	}
    }

    private void sendUpdate(State state, String message) {
	if (listener != null) {
	    listener.onState(state, message);
	}
    }

    private void sendUpdate(State state) {
	if (listener != null) {
	    listener.onState(state, null);
	}
    }

    private void threadFunction(String fullPath) {
	try {
	    long startTime = System.currentTimeMillis();
	    sendUpdate(State.START);

//                    String result = "";
//                    if (mAction.equals(ACTION_TRANSCRIBE))
//                        result = mWhisperEngine.getTranscription(mWavFilePath);
//                    else if (mAction == ACTION_TRANSLATE)
//                        result = mWhisperEngine.getTranslation(mWavFilePath);
	    // Get result from wav file
	    synchronized (whisperEngineLock) {
		String result = engine.transcribeFile(fullPath);
		sendResult(result);
		Log.d(TAG, "Result len: " + result.length() + ", Result: " + result);
	    }
	    sendUpdate(State.DONE);
	    // Calculate time required for transcription
	    long endTime = System.currentTimeMillis();
	    long timeTaken = endTime - startTime;
	    Log.d(TAG, "Time Taken for transcription: " + timeTaken + "ms");
	} catch (Exception e) {
	    Log.e(TAG, "Error...", e);
	    sendUpdate(State.ERROR, e.getMessage());
	}
    }

    public boolean isInProgress() {
	return inProgress.get();
    }

    private void sendResult(String message) {
	if (listener != null) {
	    listener.onResult(message);
	}
    }

    public void writeBuffer(float[] samples) {
	try {
	    queue.put(samples);
	} catch (InterruptedException ignore) {
	    Thread.currentThread().interrupt();
	}
//	synchronized (audioBufferLock) {
//	    audioBufferQueue.add(samples);
//	    audioBufferLock.notify(); // Notify waiting threads
//	}
    }

    private @NonNull float[] readBuffer() {
	float[] values = null;
	while (values == null) {
	    try {
		values = queue.take();
	    } catch (InterruptedException ignore) {
		Thread.currentThread().interrupt();
	    }
	}
	return values;
    }

    private void startMicTranscriptionThread() {
	if (mMicTranscribeThread == null) {
	    // Create a transcribe thread
	    mMicTranscribeThread = new Thread(() -> {
		while (true) {
		    try {
			float[] samples = readBuffer();
			synchronized (whisperEngineLock) {
			    String result = engine.transcribeBuffer(samples);
			    sendResult(result);
			}
		    } catch (Throwable t) {
			Log.e(TAG, t.getMessage());
		    }
		}
	    });

	    // Start the transcribe thread
	    mMicTranscribeThread.start();
	}
    }

    @Override
    public void close() {
	try {
	    engine.close();
	} catch (Exception ignored) {
	}
    }
}
