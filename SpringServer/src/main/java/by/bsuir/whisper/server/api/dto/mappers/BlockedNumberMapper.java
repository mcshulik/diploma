package by.bsuir.whisper.server.api.dto.mappers;

import by.bsuir.whisper.server.api.dto.response.BlockedNumberDto;
import by.bsuir.whisper.server.model.BlockedNumber;
import by.bsuir.whisper.server.model.UpdateBlockedNumberDto;
import org.mapstruct.*;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class BlockedNumberMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "approveCount", ignore = true)
    @Mapping(target = "registrationTime", ignore = true)
    abstract BlockedNumber toEntity(UpdateBlockedNumberDto updateBlockedNumberDto);

    abstract BlockedNumberDto toDto(BlockedNumber blockedNumber);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "approveCount", ignore = true)
    @Mapping(target = "registrationTime", ignore = true)
    abstract BlockedNumber partialUpdate(
	UpdateBlockedNumberDto dto, @MappingTarget BlockedNumber entity
    );
}