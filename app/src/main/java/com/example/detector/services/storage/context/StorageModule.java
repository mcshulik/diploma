package com.example.detector.services.storage.context;

import com.example.detector.services.storage.StorageService;
import com.example.detector.services.storage.impl.StorageServiceImpl;
import com.example.detector.services.storage.mappers.PhoneNumberMapper;
import com.example.detector.services.storage.mappers.VoiceRecordMapper;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import org.mapstruct.factory.Mappers;

import javax.inject.Singleton;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@Module
@InstallIn(SingletonComponent.class)
public abstract class StorageModule {

    @Provides
    public static PhoneNumberMapper numberMapper() {
	return Mappers.getMapper(PhoneNumberMapper.class);
    }

    @Provides
    public static VoiceRecordMapper recordMapper() {
	return Mappers.getMapper(VoiceRecordMapper.class);
    }

    @Binds
    @Singleton
    public abstract StorageService bindStorageService(
	StorageServiceImpl storageService
    );
}
