package com.example.detector.services.storage;

import androidx.room.*;
import com.example.detector.services.storage.model.VoiceRecording;
import io.reactivex.rxjava3.core.Single;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@Dao
public interface VoiceRecordingDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    Single<Long> insert(VoiceRecording entity);

    @Query("update voice_recording set isSynchronized = 1 where id = :id")
    void updateStatus(long id);

    @Query("delete from voice_recording where isSynchronized = 1")
    void deleteSynchronized();
}
