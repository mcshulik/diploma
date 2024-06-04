package by.bsuir.whisper.server.services;

import by.bsuir.whisper.server.api.dto.request.UpdateVoiceRecordDto;
import by.bsuir.whisper.server.api.dto.response.BlockedNumberDto;
import by.bsuir.whisper.server.api.dto.response.VoiceRecordDto;
import by.bsuir.whisper.server.model.UpdateBlockedNumberDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

/**
 * @author Paval Shlyk
 * @since 24/05/2024
 */
public interface BlackListService {
    List<BlockedNumberDto> getBlackList();

    Optional<BlockedNumberDto> getByOwnerAndUserId(
	String owner,
	long userId
    );

    Optional<BlockedNumberDto> getById(
	long numberId
    );

    //By convention is possible to invoke to create several times
    BlockedNumberDto create(
	@Valid UpdateBlockedNumberDto dto
    );


    VoiceRecordDto createVoiceRecord(
	long numberId,
	@Valid UpdateVoiceRecordDto dto
    );
}
