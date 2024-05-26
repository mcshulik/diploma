package com.example.detector.services.network.mappers;

import com.example.detector.services.LocalPhoneNumber;
import com.example.detector.services.network.model.ServerPhoneNumber;
import org.mapstruct.*;

import java.util.List;

/**
 * @author Paval Shlyk
 * @since 27/05/2024
 */
@Mapper(
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PhoneNumberMapper {
    @Mapping(target = "id", ignore = true)
    ServerPhoneNumber toServerDto(LocalPhoneNumber number);

    @Mapping(target = "isSynchronized", constant = "true")
    LocalPhoneNumber fromServerDto(ServerPhoneNumber dto);

    List<LocalPhoneNumber> fromServerDtoList(List<ServerPhoneNumber> numbers);
}