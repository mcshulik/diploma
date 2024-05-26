package com.example.detector.services;

import lombok.val;

import java.util.List;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
public interface StorageService {
    //add number available only locally
    void addPrivateWhiteNumber(String number);
    void addWhiteNumber(IncomingPhoneNumber number, boolean isNew);

    void addBlackNumber(IncomingPhoneNumber number, boolean isNew);
    default void addWhiteNumberAll(List<IncomingPhoneNumber> numbers) {
	for (val number : numbers) {
	    addWhiteNumber(number, true);
	}
    }

    default void addBlackNumberAll(List<IncomingPhoneNumber> numbers) {
	for (val number : numbers) {
	    addBlackNumber(number, true);
	}
    }

    boolean isWhiteNumber(String number);

    boolean isBlackNumber(String number);

    List<IncomingPhoneNumber> getNewWhiteNumbers();

    List<IncomingPhoneNumber> getNewBlackNumbers();
}
