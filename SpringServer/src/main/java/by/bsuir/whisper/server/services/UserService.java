package by.bsuir.whisper.server.services;

import by.bsuir.whisper.server.api.dto.request.UpdateUserDto;
import by.bsuir.whisper.server.api.dto.response.PresenceDto;
import by.bsuir.whisper.server.api.dto.response.UserDto;
import by.bsuir.whisper.server.api.exceptions.ResourceModifyingException;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

/**
 * @author Paval Shlyk
 * @since 24/05/2024
 */
public interface UserService {
    UserDto create(@Valid UpdateUserDto dto) throws ResourceModifyingException;

    UserDto update(long id, @Valid UpdateUserDto dto) throws ResourceModifyingException;

    PresenceDto delete(long id) throws ResourceModifyingException;

    Optional<UserDto> find(long id);

    //return all users
    List<UserDto> all();
}
