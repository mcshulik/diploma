package com.example.detector.services;

import lombok.Builder;
import lombok.Value;

import java.time.Duration;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@Builder
@Value
public class LocalRecognitionResult {
    Duration duration;
    float quality;
    String speechText;
    String recognizedText;
    byte[] audio;
}
