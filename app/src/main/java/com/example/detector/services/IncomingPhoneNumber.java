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
public final class IncomingPhoneNumber {
    private String number;
    private String owner;
}
