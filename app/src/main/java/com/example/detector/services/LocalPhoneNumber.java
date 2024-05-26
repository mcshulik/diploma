package com.example.detector.services;

import lombok.*;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public final class LocalPhoneNumber {
    private String number;
    private String owner;
    private boolean isSynchronized;

    public LocalPhoneNumber asNotSynchronized() {
	isSynchronized = false;
	return this;
    }

    public LocalPhoneNumber asSynchronized() {
	isSynchronized = true;
	return this;
    }
}
