package com.example.detector.services.network;

import android.util.Pair;
import com.example.detector.services.LocalPhoneNumber;
import com.example.detector.services.LocalRecognitionResult;
import io.reactivex.rxjava3.core.Maybe;

import java.util.List;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
public interface NetworkService {
    Maybe<List<LocalPhoneNumber>> tryAccessBlackList();

    //return true if data is sent
    //return false otherwise
    Maybe<?> trySendBlackList(List<Pair<LocalPhoneNumber, List<LocalRecognitionResult>>> numbers);
}

