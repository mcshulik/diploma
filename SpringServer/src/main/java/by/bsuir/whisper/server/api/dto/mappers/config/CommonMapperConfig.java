package by.bsuir.whisper.server.api.dto.mappers.config;

import by.bsuir.whisper.server.dao.BlockedNumberRepository;
import by.bsuir.whisper.server.dao.UserRepository;
import by.bsuir.whisper.server.model.BlockedNumber;
import by.bsuir.whisper.server.model.RecognitionModel;
import by.bsuir.whisper.server.model.RecognitionModelRepository;
import by.bsuir.whisper.server.model.User;
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
    private final RecognitionModelRepository modelRepository;
    private final UserRepository userRepository;
    private final BlockedNumberRepository blockedNumberRepository;

    @Named("getRecognitionModelRef")
    public RecognitionModel getRecognitionModelRef(long modelId) {
	return modelRepository.getReferenceById(modelId);
    }

    @Named("getUserRef")
    public User getUserRef(long userId) {
	return userRepository.getReferenceById(userId);
    }

    @Named("getBlockedNumberRef")
    public BlockedNumber getBlockedNumberRef(long numberId) {
	return blockedNumberRepository.getReferenceById(numberId);
    }

}
