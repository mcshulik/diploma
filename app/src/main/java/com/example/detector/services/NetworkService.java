package com.example.detector.services;

import java.util.List;
import java.util.Optional;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
public interface NetworkService {
    Optional<List<IncomingPhoneNumber>> tryAccessWhiteList();

    Optional<List<IncomingPhoneNumber>> tryAccessBlackList();

    //return true if data is sent
    //return false otherwise
    boolean trySendWhiteList(List<IncomingPhoneNumber> numbers);

    //return true if data is sent
    //return false otherwise
    boolean trySendBlackList(List<IncomingPhoneNumber> numbers);
}

