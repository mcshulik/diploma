package by.bsuir.whisper.server.services.impl;

import by.bsuir.whisper.server.api.dto.mappers.UserMapper;
import by.bsuir.whisper.server.api.dto.request.UpdateUserDto;
import by.bsuir.whisper.server.api.dto.response.PresenceDto;
import by.bsuir.whisper.server.api.dto.response.UserDto;
import by.bsuir.whisper.server.api.exceptions.ResourceModifyingException;
import by.bsuir.whisper.server.context.CatchLevel;
import by.bsuir.whisper.server.context.PasswordGenerator;
import by.bsuir.whisper.server.dao.UserRepository;
import by.bsuir.whisper.server.model.User;
import by.bsuir.whisper.server.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Paval Shlyk
 * @since 28/05/2024
 */
@Slf4j
@Service
@CatchLevel(DataAccessException.class)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Function<String, PasswordGenerator> generatorBuilder;

    @Override
    public UserDto create(UpdateUserDto dto) throws ResourceModifyingException {
	val generator = generatorBuilder.apply(dto.password());
	User entity = userMapper.toEntity(dto, generator);
	User saved = userRepository.save(entity);
	return userMapper.toDto(saved);
    }

    @Override
    public UserDto update(long id, UpdateUserDto dto) throws ResourceModifyingException {
	val generator = generatorBuilder.apply(dto.password());
	User entity = userRepository
			  .findById(id)
			  .orElseThrow(() -> newUserNotFoundException(id));
	User _ = userMapper.partialUpdate(entity, dto, generator);
	return userMapper.toDto(entity);
    }

    @Override
    public PresenceDto delete(long id) throws ResourceModifyingException {
	if (userRepository.existsById(id)) {
	    userRepository.deleteById(id);
	    return PresenceDto.exists();
	}
	return PresenceDto.empty();
    }

    @Override
    public Optional<UserDto> find(long id) {
	return userRepository
		   .findById(id)
		   .map(userMapper::toDto);
    }

    @Override
    public List<UserDto> all() {
	return userMapper.toDtoList(userRepository.findAll());
    }

    private static ResourceModifyingException newUserNotFoundException(long id) {
	final String msg = STR."Failed to find user by id: \{id}";
	log.warn(msg);
	return new ResourceModifyingException(msg, 42);

    }
}
