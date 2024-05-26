package by.bsuir.whisper.server.api.dto.response;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
public record BlockedNumberDto(
    long id,
    @NotNull
    String number,
    @Nullable
    String owner
) {
}
