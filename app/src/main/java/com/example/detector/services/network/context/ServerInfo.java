package com.example.detector.services.network.context;

import lombok.Builder;
import lombok.Value;

/**
 * @author Paval Shlyk
 * @since 04/06/2024
 */
@Value
@Builder
public class ServerInfo {
    String baseUrl;
}
