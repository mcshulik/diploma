package com.example.detector.services.storage.mappers;

import com.example.detector.services.LocalPhoneNumber;
import com.example.detector.services.storage.model.BlackNumber;
import com.example.detector.services.storage.model.PhoneNumber;
import com.example.detector.services.storage.model.PhoneNumberProjection;
import org.mapstruct.*;

import javax.annotation.MatchesPattern;
import java.util.List;

/**
 * @author Paval Shlyk
 * @since 27/05/2024
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PhoneNumberMapper {
    @Mapping(target = "isSynchronized", source = "dto.synchronized")
    @Mapping(target = "isShared", constant = "true")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numberType", ignore = true)
    PhoneNumber toEntity(LocalPhoneNumber dto);

    @Mapping(target = "isSynchronized", ignore = true)
    @Named("toDto")
    LocalPhoneNumber toDto(BlackNumber number);

    @IterableMapping(qualifiedByName = "toDto")
    List<LocalPhoneNumber> toDtoList(List<BlackNumber> numbers);
}