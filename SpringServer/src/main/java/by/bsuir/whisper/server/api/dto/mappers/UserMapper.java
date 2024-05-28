package by.bsuir.whisper.server.api.dto.mappers;

import by.bsuir.whisper.server.api.dto.mappers.config.CentralConfig;
import by.bsuir.whisper.server.api.dto.request.UpdateUserDto;
import by.bsuir.whisper.server.api.dto.response.UserDto;
import by.bsuir.whisper.server.context.PasswordGenerator;
import by.bsuir.whisper.server.model.User;
import org.mapstruct.*;

import java.util.List;

/**
 * @author Paval Shlyk
 * @since 24/05/2024
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.ERROR,
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    config = CentralConfig.class)
public abstract class UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hash", source = "generator.hash")
    @Mapping(target = "salt", source = "generator.salt")
    @Mapping(target = "email", source = "dto.email")
    @Mapping(target = "login", source = "dto.login")
    public abstract User toEntity(UpdateUserDto dto, PasswordGenerator generator);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hash", source = "generator.hash")
    @Mapping(target = "salt", source = "generator.salt")
    public abstract User partialUpdate(
	@MappingTarget User user,
	UpdateUserDto dto,
	PasswordGenerator generator
    );

    public abstract UserDto toDto(User user);

    public abstract List<UserDto> toDtoList(List<User> list);
}