package by.bsuir.whisper.server.api.controllers;

import by.bsuir.whisper.server.api.dto.groups.Create;
import by.bsuir.whisper.server.api.dto.request.UpdateVoiceRecordDto;
import by.bsuir.whisper.server.api.dto.response.BlockedNumberDto;
import by.bsuir.whisper.server.model.UpdateBlockedNumberDto;
import by.bsuir.whisper.server.services.BlackListService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * @author Paval Shlyk
 * @since 24/05/2024
 */
@RestController
@RequestMapping("/api/v1.0/black-list")
@RequiredArgsConstructor
public class BlackListController {
    private final BlackListService blackListService;

    @GetMapping
    public ResponseEntity<List<BlockedNumberDto>> getBlackList(
	@RequestParam(value = "member_name", required = false) String blockedName,
	@RequestParam(value = "user_id", required = false) Long userId
    ) {
	if (blockedName != null && userId != null) {
	    return blackListService
		       .getByOwnerAndUserId(blockedName, userId)
		       .map(dto -> ResponseEntity.ok(List.of(dto)))
		       .orElseGet(() -> ResponseEntity.notFound().build());
	}
	if (blockedName == null && userId == null) {
	    val list = blackListService.getBlackList();
	    return ResponseEntity.ok(list);
	}
	return ResponseEntity.badRequest().build();
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<BlockedNumberDto> getBlackNumber(
	@PathVariable @NotNull Long memberId
    ) {
	return blackListService
		   .getById(memberId)
		   .map(ResponseEntity::ok)
		   .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BlockedNumberDto> addBlackNumber(
	@RequestBody @Validated(Create.class) UpdateBlockedNumberDto dto
    ) {
	val entity = blackListService.create(dto);
	val uri = prepareBlackNumberURI(entity.id());
	return ResponseEntity
		   .created(uri)
		   .body(entity);
    }

    @PostMapping("/{memberId}/records")
    public ResponseEntity<?> addVoiceRecord(
	@PathVariable @NotNull Long memberId,
	@RequestBody @Validated(Create.class) UpdateVoiceRecordDto dto
    ) {
	val _ = blackListService.createVoiceRecord(memberId, dto);
	return ResponseEntity.accepted().build();
    }

    private URI prepareBlackNumberURI(long id) {
	val value = "/api/v1.0/black-list/" + id;
	return URI.create(value);
    }
}
