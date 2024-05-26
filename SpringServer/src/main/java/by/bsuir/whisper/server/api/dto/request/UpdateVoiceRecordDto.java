package by.bsuir.whisper.server.api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.sql.Timestamp;

/**
 * @author Paval Shlyk
 * @since 24/05/2024
 */
public record UpdateVoiceRecordDto(
    @NotNull
    Timestamp duration,
    @NotNull
    Float quality,
    @NotNull
    String recognitionText,
    @NotNull
    String speechText,
    byte @NotNull [] audio,
    @NotNull
    Long recorderId,
    @Max(1)
    @Min(1)
    Long modelId //this value should always be equal to 1
) {
}
