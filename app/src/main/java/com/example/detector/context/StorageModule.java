package com.example.detector.context;

import com.example.detector.WhisperActivity;
import com.example.detector.services.StorageService;
import com.example.detector.services.storage.StorageServiceImpl;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;

import javax.inject.Singleton;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@Module
@InstallIn(WhisperActivity.class)
public abstract class StorageModule {
    @Binds
    @Singleton
    public abstract StorageService bindStorageService(
	StorageServiceImpl service
    );
}
