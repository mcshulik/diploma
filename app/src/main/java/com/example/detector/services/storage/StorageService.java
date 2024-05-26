package com.example.detector.services.storage;

import android.util.Pair;
import com.example.detector.services.LocalPhoneNumber;
import com.example.detector.services.LocalRecognitionResult;
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

    void addBlackNumber(LocalPhoneNumber number, Maybe<LocalRecognitionResult> recognition);

    default void addBlackNumberAll(List<LocalPhoneNumber> numbers) {
	for (final LocalPhoneNumber number : numbers) {
	    addBlackNumber(number, Maybe.empty());
	}
    }


    Maybe<LocalPhoneNumber> findBlackNumber(String number);

    ///return only not synchronized black numbers
    Flowable<Pair<LocalPhoneNumber, List<LocalRecognitionResult>>> findBlackNumbers();

    //todo: add removing functionality

}
