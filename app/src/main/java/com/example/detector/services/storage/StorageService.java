package com.example.detector.services.storage;

import android.util.Pair;
import com.example.detector.services.LocalPhoneNumber;
import com.example.detector.services.LocalRecognitionResult;
import com.google.android.material.internal.ScrimInsetsFrameLayout;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
public interface StorageService {
    //add number available only locally

    void addWhiteNumber(String number);

    Single<Boolean> isWhiteNumber(String number);

    Single<Boolean> isBlackNumber(String number);

    Single<Boolean> isSuspiciousText(String text);

    Single<?> addBlackNumber(LocalPhoneNumber number, Maybe<LocalRecognitionResult> recognition);

    void synchronizeNumbers(List<LocalPhoneNumber> numbers);

    Maybe<LocalPhoneNumber> findBlackNumber(String number);

    ///return only not synchronized black numbers
    Flowable<Pair<LocalPhoneNumber, List<LocalRecognitionResult>>> notSyncBlackNumbers();

    Single<List<LocalPhoneNumber>> allBlackNumbers();

    //todo: add removing functionality

}
