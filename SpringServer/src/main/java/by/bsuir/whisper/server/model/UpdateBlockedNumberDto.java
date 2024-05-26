package by.bsuir.whisper.server.model;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link BlockedNumber}
 */
public record UpdateBlockedNumberDto(
    @NotNull
    String number,
    String owner
) implements Serializable {
}