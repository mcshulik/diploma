package by.bsuir.whisper.server.api.dto.mappers;

import by.bsuir.whisper.server.api.dto.mappers.config.CentralConfig;
import by.bsuir.whisper.server.api.dto.request.UpdateVoiceRecordDto;
import by.bsuir.whisper.server.model.VoiceRecord;
import by.bsuir.whisper.server.api.dto.response.VoiceRecordDto;
import org.mapstruct.*;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    config = CentralConfig.class)
public abstract class VoiceRecordMapper {
    @Mapping(target = "recorderId", source = "recorder.id")
    @Mapping(target = "modelId", source = "model.id")
    public abstract VoiceRecordDto toDto(VoiceRecord entity);

    @Mapping(target = "number", source = "numberId", qualifiedByName = "getBlockedNumberRef")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "model", source = "dto.modelId", qualifiedByName = "getRecognitionModelRef")
    @Mapping(target = "recorder", source = "dto.recorderId", qualifiedByName = "getUserRef")
    public abstract VoiceRecord toEntity(UpdateVoiceRecordDto dto, long numberId);
}