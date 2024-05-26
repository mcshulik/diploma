package com.example.detector.context;

import com.example.detector.WhisperActivity;
import com.example.detector.services.WhisperService;
import com.example.detector.services.whisper.WhisperServiceImpl;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;

import javax.inject.Singleton;
import java.nio.file.WatchService;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@Module
@InstallIn(WhisperActivity.class)
public abstract class TenserFlowModule {
    @Binds
    @Singleton
    public abstract WhisperService bindWhisperServer(
	WhisperServiceImpl whisperService
    );
}
