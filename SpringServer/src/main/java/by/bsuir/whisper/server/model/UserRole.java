package by.bsuir.whisper.server.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * @author Paval Shlyk
 * @since 24/05/2024
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_role")
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Short id;
}
