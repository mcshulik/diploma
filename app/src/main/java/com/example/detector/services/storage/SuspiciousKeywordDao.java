package com.example.detector.services.storage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.detector.services.storage.model.SuspiciousKeyword;
import io.reactivex.rxjava3.core.Single;

/**
 * @author Paval Shlyk
 * @since 02/06/2024
 */
@Dao
public interface SuspiciousKeywordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(SuspiciousKeyword entity);

    @Query("delete from suspicious_keyword where id = :id")
    void deleteById(long id);

    @Query("select exists (select 1 from suspicious_keyword where  :keyword like keyword)")
    Single<Boolean> existsByValue(String keyword);
}
