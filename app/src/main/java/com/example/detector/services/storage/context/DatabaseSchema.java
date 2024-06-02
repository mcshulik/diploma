package com.example.detector.services.storage.context;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.detector.services.storage.PhoneNumberDao;
import com.example.detector.services.storage.SuspiciousKeywordDao;
import com.example.detector.services.storage.VoiceRecordDao;
import com.example.detector.services.storage.model.*;
import dagger.Provides;

/**
 * @author Paval Shlyk
 * @since 27/05/2024
 */
@Database(
    entities = {PhoneNumber.class, VoiceRecord.class, SuspiciousKeyword.class},
    views = {BlackNumber.class, WhiteNumber.class},
    version = 1,
    exportSchema = false
)
public abstract class DatabaseSchema extends RoomDatabase {
    public abstract PhoneNumberDao phoneNumberDao();

    public abstract VoiceRecordDao voiceRecordDao();

    public abstract SuspiciousKeywordDao suspiciousKeywordDao();
}
