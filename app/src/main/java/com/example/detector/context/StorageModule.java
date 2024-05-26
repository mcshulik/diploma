package com.example.detector.context;

import androidx.datastore.core.Serializer;
import com.example.detector.services.StorageService;
import com.example.detector.services.storage.StorageServiceImpl;
import com.example.services.storage.BlackPhoneNumber;
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
public abstract class StorageModule {
    @Binds
    @Singleton
    public abstract StorageService bindStorageService(
	StorageServiceImpl service
    );
}
