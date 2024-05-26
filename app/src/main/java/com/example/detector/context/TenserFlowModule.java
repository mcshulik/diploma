package com.example.detector.context;

import com.example.detector.services.whisper.WhisperService;
import com.example.detector.services.whisper.WhisperServiceImpl;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

import javax.inject.Singleton;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@Module
@InstallIn(SingletonComponent.class)
public abstract class TenserFlowModule {
    @Binds
    @Singleton
    public abstract WhisperService bindWhisperServer(
	WhisperServiceImpl whisperService
    );
}
