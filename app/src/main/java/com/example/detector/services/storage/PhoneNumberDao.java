package com.example.detector.services.storage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.detector.services.storage.model.BlackNumber;
import com.example.detector.services.storage.model.PhoneNumber;
import com.example.detector.services.storage.model.WhiteNumber;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@Dao
public interface PhoneNumberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(PhoneNumber entity);

    @Query("select * from black_list")
    Single<List<BlackNumber>> allBlackList();

    @Query("select * from white_list")
    Flowable<WhiteNumber> allWhiteList();

    @Query("select * from black_list where isSynchronized = 0")
    Flowable<BlackNumber> notSynchronizedBlackList();

    @Query("update phone_number set isSynchronized = 1 where id = :numberId")
    void syncBlackNumber(long numberId);

    @Query("delete from phone_number where number = :number and numberType = 1")
    void deleteWhiteByNumber(String number);

    @Query("delete from phone_number where number = :number and numberType = 2")
    void deleteBlackByNumber(String number);

    @Query("select exists (select 1 from white_list where number = :number)")
    Single<Boolean> existsWhiteNumber(String number);

    @Query("select exists (select 1 from black_list where number = :number)")
    Single<Boolean> existsBlackNumber(String number);

    @Query("select * from black_list where number = :number")
    Maybe<BlackNumber> findBlackNumber(String number);
}
