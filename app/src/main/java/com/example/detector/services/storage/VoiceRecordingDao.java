package com.example.detector.services.storage;

import androidx.room.*;
import com.example.detector.services.storage.model.VoiceRecord;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@Dao
public interface VoiceRecordingDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    Single<Long> insert(VoiceRecord entity);
    @Query("select * from voice_record where isSynchronized = 0 and number_id = :id")
    Flowable<VoiceRecord> allNotSynchronizedByBlackNumberId(long id);

    @Query("update voice_record set isSynchronized = 1 where id = :id")
    void updateStatus(long id);

    @Query("delete from voice_record where isSynchronized = 1")
    void deleteSynchronized();
}
