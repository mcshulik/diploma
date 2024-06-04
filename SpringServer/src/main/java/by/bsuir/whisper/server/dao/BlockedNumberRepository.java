package by.bsuir.whisper.server.dao;

import by.bsuir.whisper.server.model.BlockedNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockedNumberRepository extends JpaRepository<BlockedNumber, Long> {
    Optional<BlockedNumber> findByNumberAndOwner(String number, String owner);

    Optional<BlockedNumber> findByNumber(String number);
}