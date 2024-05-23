package by.bsuir.whisper.server.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Builder;
import org.springframework.http.HttpStatus;

/**
 * @author Paval Shlyk
 * @since 01/02/2024
 */
@Builder
public record ErrorDto(
    String errorCode,
    String errorMessage,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Nullable String[] errors
) {
    public static final int MAGIC_CODE = 66;

    public static String codeOf(HttpStatus status, int code) {
	return STR."\{status.value()}\{code}";
    }
}
