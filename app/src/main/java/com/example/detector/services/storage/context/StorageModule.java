package com.example.detector.context;

import android.content.Context;
import androidx.datastore.rxjava3.RxDataStoreBuilder;
import com.example.detector.services.StorageService;
import com.example.detector.services.storage.StorageServiceImpl;
import com.example.services.storage.BlackListSerializer;
import com.example.services.storage.WhiteListSerializer;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import lombok.val;

import javax.inject.Singleton;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@Module
@InstallIn(SingletonComponent.class)
public abstract class StorageModule {
    @Provides
    @Singleton
    public static StorageService bindStorageService(
	@ApplicationContext Context context
    ) {
	val blackList = new RxDataStoreBuilder<>(
	    context, "black-list.pb", new BlackListSerializer()
	).build();
	val whiteList = new RxDataStoreBuilder<>(
	    context, "white-list.pb", new WhiteListSerializer()
	).build();
	return StorageServiceImpl.builder()
		   .blackList(blackList)
		   .whiteList(whiteList)
		   .build();
    }
}
