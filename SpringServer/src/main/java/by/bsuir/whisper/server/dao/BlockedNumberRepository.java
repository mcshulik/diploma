package by.bsuir.whisper.server.dao;

import by.bsuir.whisper.server.model.BlockedNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockedNumberRepository extends JpaRepository<BlockedNumber, Long> {
}