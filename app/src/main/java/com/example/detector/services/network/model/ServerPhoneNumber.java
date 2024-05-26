package com.example.detector.services.network.model;

import com.google.gson.annotations.JsonAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

/**
 * @author Paval Shlyk
 * @since 27/05/2024
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServerPhoneNumber {
    long id;
    String number;
    String owner;
}
