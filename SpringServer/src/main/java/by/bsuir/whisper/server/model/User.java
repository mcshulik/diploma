package by.bsuir.whisper.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "login", unique = true, length = 40)
    private String login;

    @Column(name = "hash", length = 64)
    @Size(min = 64, max = 64)
    private String hash;

    @Size(min = 64, max = 64)
    private String salt;
}