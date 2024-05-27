package com.example.detector.services.storage.context;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.detector.services.storage.PhoneNumberDao;
import com.example.detector.services.storage.VoiceRecordDao;
import com.example.detector.services.storage.model.BlackNumber;
import com.example.detector.services.storage.model.PhoneNumber;
import com.example.detector.services.storage.model.VoiceRecord;
import com.example.detector.services.storage.model.WhiteNumber;

/**
 * @author Paval Shlyk
 * @since 27/05/2024
 */
@Database(
    entities = {PhoneNumber.class, VoiceRecord.class},
    views = {BlackNumber.class, WhiteNumber.class},
    version = 1
)
public abstract class DatabaseConfig extends RoomDatabase {
    public abstract PhoneNumberDao phoneNumberDao();
    public abstract VoiceRecordDao voiceRecordDao();
}
