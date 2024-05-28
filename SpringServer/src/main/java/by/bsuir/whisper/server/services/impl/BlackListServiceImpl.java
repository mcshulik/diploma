package by.bsuir.whisper.server.services.impl;

import by.bsuir.whisper.server.api.dto.mappers.BlockedNumberMapper;
import by.bsuir.whisper.server.api.dto.mappers.VoiceRecordMapper;
import by.bsuir.whisper.server.api.dto.request.UpdateVoiceRecordDto;
import by.bsuir.whisper.server.api.dto.response.BlockedNumberDto;
import by.bsuir.whisper.server.api.dto.response.VoiceRecordDto;
import by.bsuir.whisper.server.context.CatchLevel;
import by.bsuir.whisper.server.dao.BlockedNumberRepository;
import by.bsuir.whisper.server.dao.VoiceRecordRepository;
import by.bsuir.whisper.server.model.BlockedNumber;
import by.bsuir.whisper.server.model.UpdateBlockedNumberDto;
import by.bsuir.whisper.server.model.VoiceRecord;
import by.bsuir.whisper.server.services.BlackListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Paval Shlyk
 * @since 28/05/2024
 */
@Slf4j
@Service
@CatchLevel(DataAccessException.class)
@RequiredArgsConstructor
public class BlackListServiceImpl implements BlackListService {
    private final BlockedNumberRepository blockedNumberRepository;
    private final VoiceRecordRepository voiceRecordRepository;

    private final BlockedNumberMapper numberMapper;
    private final VoiceRecordMapper voiceRecordMapper;

    @Override
    public List<BlockedNumberDto> getBlackList() {
	return numberMapper.toDtoList(blockedNumberRepository.findAll());
    }

    @Override
    public Optional<BlockedNumberDto> getByOwnerAndUserId(String owner, long userId) {
	return Optional.empty();
    }

    @Override
    public Optional<BlockedNumberDto> getById(long numberId) {
	return blockedNumberRepository
		   .findById(numberId)
		   .map(numberMapper::toDto);
    }

    @Override
    public BlockedNumberDto create(UpdateBlockedNumberDto dto) {
	BlockedNumber entity = numberMapper.toEntity(dto);
	BlockedNumber saved = blockedNumberRepository.save(entity);
	return numberMapper.toDto(saved);
    }

    @Override
    public VoiceRecordDto createVoiceRecord(long numberId, UpdateVoiceRecordDto dto) {
	VoiceRecord entity = voiceRecordMapper.toEntity(dto, numberId);
	VoiceRecord saved = voiceRecordRepository.save(entity);
	return voiceRecordMapper.toDto(saved);
    }
}