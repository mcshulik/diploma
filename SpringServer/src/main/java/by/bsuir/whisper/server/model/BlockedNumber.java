package by.bsuir.whisper.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "blocked_number")
public class BlockedNumber {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "number", nullable = false, unique = true, length = 100)
    private String number;

    @Column(name = "owner", nullable = true)
    private String owner;

    @Column(name = "approve_count", nullable = false)
    @Builder.Default
    private Long approveCount = 0L;

    //The time when
    @Column(name = "registration_time", nullable = false)
    private Timestamp registrationTime;

    @PrePersist
    public void beforeInsert() {
	this.registrationTime = Timestamp.from(Instant.now());
    }
    public void approve() {
        approveCount += 1;
    }
}