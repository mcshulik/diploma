package com.example.detector.services.storage.context;

import android.content.Context;
import androidx.room.PrimaryKey;
import androidx.room.Room;
import com.example.detector.services.storage.PhoneNumberDao;
import com.example.detector.services.storage.StorageService;
import com.example.detector.services.storage.SuspiciousKeywordDao;
import com.example.detector.services.storage.VoiceRecordDao;
import com.example.detector.services.storage.impl.StorageServiceImpl;
import com.example.detector.services.storage.mappers.PhoneNumberMapper;
import com.example.detector.services.storage.mappers.VoiceRecordMapper;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@Module(includes = {
    StorageModule.DaoBindings.class
})
@InstallIn(SingletonComponent.class)
public abstract class StorageModule {
    @Provides
    static PhoneNumberMapper numberMapper() {
	return org.mapstruct.factory.Mappers.getMapper(PhoneNumberMapper.class);
    }

    @Provides
    static VoiceRecordMapper recordMapper() {
	return org.mapstruct.factory.Mappers.getMapper(VoiceRecordMapper.class);
    }

    @Binds
    public abstract StorageService bindStorageService(StorageServiceImpl storageService);

    @Module
    @InstallIn(SingletonComponent.class)
    public static class DaoBindings {
	@Provides
	@Singleton
	public DatabaseSchema buildSchema(@ApplicationContext Context context) {
	    return Room.databaseBuilder(
		context,
		DatabaseSchema.class,
		"local.db"
	    ).build();
	}

	@Provides
	@Singleton
	public PhoneNumberDao phoneNumberDao(DatabaseSchema schema) {
	    return schema.phoneNumberDao();
	}

	@Provides
	@Singleton
	public VoiceRecordDao voiceRecordDao(DatabaseSchema schema) {
	    return schema.voiceRecordDao();
	}

	@Provides
	@Singleton
	public SuspiciousKeywordDao suspiciousKeywordDao(DatabaseSchema schema) {
	    return schema.suspiciousKeywordDao();
	}
    }

}
