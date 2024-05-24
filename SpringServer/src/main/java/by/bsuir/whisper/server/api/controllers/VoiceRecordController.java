package by.bsuir.whisper.server.api.controllers;

import by.bsuir.whisper.server.services.RecordingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Paval Shlyk
 * @since 24/05/2024
 */
@RestController
@RequestMapping("/api/v1.0/records")
@RequiredArgsConstructor
public class VoiceRecordController {
   private final RecordingService recordingService;
}
