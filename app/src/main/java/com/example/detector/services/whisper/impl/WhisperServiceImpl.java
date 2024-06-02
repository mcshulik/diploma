package com.example.detector.services.whisper.impl;

import android.util.Log;
import com.example.detector.services.whisper.engine.WhisperEngine;
import com.example.detector.services.whisper.WhisperException;
import com.example.detector.services.whisper.WhisperService;
import com.example.detector.services.whisper.engine.impl.NativeWhisperEngine;
import com.example.detector.utils.WaveUtil;
import com.google.errorprone.annotations.ThreadSafe;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import javax.inject.Inject;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@ThreadSafe
public class WhisperServiceImpl implements WhisperService {
    private static final String TAG = "WhisperServiceImpl";
    private final WhisperEngine engine;
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicBoolean isDisposed = new AtomicBoolean(false);

    @Override
    public Maybe<String> transcript(float[] samples) {
	if (isDisposed()) {
	    val msg = "Service is already closed";
	    return Maybe.error(new WhisperException(msg));
	}
	Maybe<String> maybe = Maybe.create(emitter -> {
	    lock.lock();
	    try {
		val stringOpt = engine.transcribeBuffer(samples);
		if (stringOpt.isPresent()) {
		    val transcription = stringOpt.get();
		    emitter.onSuccess(transcription);
		} else {
		    emitter.onComplete();
		}
	    } catch (Throwable t) {
		emitter.onError(t);
	    } finally {
		lock.unlock();
	    }
	});
	return maybe.subscribeOn(Schedulers.computation());
    }

    @Override
    public Single<String> transcript(File file) {
	if (!file.exists()) {
	    val msg = "Failed to find file by given path: " + file;
	    return Single.error(new WhisperException(msg));
	}
	//fixme: migrate to vert.x

	val samplesOpt = WaveUtil.getSamples(file.getAbsolutePath());
	if (!samplesOpt.isPresent()) {
	    val msg = "Failed to parse Wav format";
	    return Single.error(new WhisperException(msg));
	}
	final float[] samples = samplesOpt.get();
	return transcript(samples).toSingle();
    }

    @Override
    @SneakyThrows
    public void dispose() {
	isDisposed.set(true);
	lock.lock();
	engine.close();
	lock.unlock();
    }

    @Override
    public boolean isDisposed() {
	return isDisposed.get();
    }
}
