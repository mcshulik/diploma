package com.example.detector.services.network.mappers;

import com.example.detector.services.LocalRecognitionResult;
import com.example.detector.services.UserInfo;
import com.example.detector.services.network.model.ServerRecognitionResult;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Paval Shlyk
 * @since 27/05/2024
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RecognitionResultMapper {
    @Mapping(target = "recorderId", source = "userInfo.id")
    @Mapping(target = "modelId", constant = "1L")
    @Named("toServerDto")
    ServerRecognitionResult toServerDto(
	LocalRecognitionResult result,
	UserInfo userInfo
    );

    LocalRecognitionResult fromServerDto(ServerRecognitionResult result);
    @IterableMapping(qualifiedByName = "toServerDto")
    ArrayList<ServerRecognitionResult> toServerDtoList(List<LocalRecognitionResult> results, UserInfo userInfo);
}