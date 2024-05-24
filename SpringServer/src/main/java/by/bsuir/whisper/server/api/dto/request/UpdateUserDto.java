package by.bsuir.whisper.server.api.dto.request;

import by.bsuir.whisper.server.api.dto.groups.Create;
import by.bsuir.whisper.server.api.dto.groups.Update;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

/**
 * @author Paval Shlyk
 * @since 24/05/2024
 */
public record UpdateUserDto(
    @NotNull(groups = Create.class)
    String login,
    @NotNull(groups = Create.class)
    @Null(groups = Update.class)
    String email,
    @NotNull(groups = Create.class)
    String password
    ) {
}
