package by.bsuir.whisper.server.api.dto.mappers.config;

import by.bsuir.whisper.server.model.RecognitionModel;
import groovy.util.logging.Commons;
import lombok.RequiredArgsConstructor;
import org.mapstruct.MapperConfig;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

/**
 * @author Paval Shlyk
 * @since 24/05/2024
 */
@MapperConfig
@Component
@RequiredArgsConstructor
public class CommonMapperConfig {
    @Named("getRecognitionModelRef")
    public RecognitionModel getRecognitionModelRef(long modelId) {
	return null;
    }
}
