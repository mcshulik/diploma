package by.bsuir.whisper.server.dao;

import by.bsuir.whisper.server.model.VoiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoiceRecordRepository extends JpaRepository<VoiceRecord, Long> {
}