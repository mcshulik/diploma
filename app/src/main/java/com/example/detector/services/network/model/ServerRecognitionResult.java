package com.example.detector.services.network.model;

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
public class ServerRecognitionResult {
    Duration duration;
    float quality;
    String speechText;
    String recognizedText;
    byte[] audio;

    long recorderId;
    @Builder.Default
    long modelId = 1L;
}
