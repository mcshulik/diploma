package by.bsuir.whisper.server.api.controllers;

import by.bsuir.whisper.server.api.dto.groups.Create;
import by.bsuir.whisper.server.api.dto.groups.Update;
import by.bsuir.whisper.server.api.dto.request.UpdateUserDto;
import by.bsuir.whisper.server.api.dto.response.PresenceDto;
import by.bsuir.whisper.server.api.dto.response.UserDto;
import by.bsuir.whisper.server.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Paval Shlyk
 * @since 24/05/2024
 */
@RestController
@RequestMapping("/api/v1.0/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> createUser(
	@RequestBody @Validated(Create.class) UpdateUserDto dto
    ) {
	UserDto entity = userService.create(dto);
	return ResponseEntity.status(HttpStatus.CREATED)
		   .body(entity);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(
	@PathVariable long userId,
	@RequestBody @Validated(Update.class) UpdateUserDto dto
    ) {
	UserDto entity = userService.update(userId, dto);
	return ResponseEntity.ok(entity);
    }

    @DeleteMapping("/{userId}")
    public PresenceDto deleteUser(
	@PathVariable long userId
    ) {
	return userService.delete(userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> findUser(
	@PathVariable long userId
    ) {
	return userService.find(userId)
		   .map(ResponseEntity::ok)
		   .orElseGet(() -> ResponseEntity.notFound().build());
    }
    //todo: add filtering parameters
    @GetMapping
    public List<UserDto> findUsers() {
	return userService.all();
    }
}
