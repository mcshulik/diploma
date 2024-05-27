package com.example.detector.services.recording.impl;

import com.example.detector.asr.Recorder;
import com.example.detector.services.recording.RecordingService;
import com.example.detector.services.recording.exceptions.RecordingException;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;

/**
 * @author Paval Shlyk
 * @since 27/05/2024
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class RecordingServiceImpl implements RecordingService {
    private final Object lock = new Object();
    private final Recorder recorder;

    public Single<float[]> start() {
	if (isRunning()) {
	    return Single.error(new RecordingException("Recorder is already started"));
	}
	return null;
    }

    public Flowable<float[]> startBackground() {
	if (isRunning()) {
	    return Flowable.error(new RecordingException("Recorder is already started"));
	}
	return null;
    }

    public boolean isRunning() {
	synchronized (lock) {
	    return recorder.isInProgress();
	}
    }

}
