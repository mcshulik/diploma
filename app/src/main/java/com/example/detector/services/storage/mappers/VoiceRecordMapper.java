package com.example.detector.services.storage.mappers;

import com.example.detector.services.LocalRecognitionResult;
import com.example.detector.services.storage.model.VoiceRecord;
import lombok.NonNull;
import org.mapstruct.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

/**
 * @author Paval Shlyk
 * @since 27/05/2024
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class VoiceRecordMapper {
    @Mapping(target = "phoneNumberId", source = "numberId")
    @Mapping(target = "isSynchronized", constant = "false")
    @Mapping(target = "id", ignore = true)
    public abstract VoiceRecord toEntity(long numberId, @NonNull LocalRecognitionResult recognition);

    @Named("toDto")
    public abstract LocalRecognitionResult toDto(VoiceRecord record);

    @IterableMapping(qualifiedByName = "toDto")
    public abstract List<LocalRecognitionResult> toDtoList(List<VoiceRecord> entities);
}