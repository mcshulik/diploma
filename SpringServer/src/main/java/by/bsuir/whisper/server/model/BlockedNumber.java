package by.bsuir.whisper.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.TimerTask;

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
    private Long approveCount;

    //The time when
    @Column(name = "registration_time", nullable = false)
    private Timestamp registrationTime;

    @PrePersist
    public void beforeInsert() {
	this.registrationTime = Timestamp.from(Instant.now());
    }
}