package by.bsuir.whisper.server.model;

import lombok.NonNull;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * DTO for {@link VoiceRecord}
 */
public record VoiceRecordDto(
    long id,
    @NonNull
    Timestamp duration,
    float quality,
    @NonNull
    String recognitionText,
    @NonNull
    String speechText,
    long recorderId,
    long modelId
) implements Serializable {
}