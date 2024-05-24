package by.bsuir.whisper.server.api.dto.response;

import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * DTO for {@link by.bsuir.whisper.server.model.User}
 */
public record UserDto(
    long id,
    String login,
    String email,
    @Size(min = 64, max = 64) String hash,
    @Size(min = 64, max = 64) String salt
) implements Serializable {
}