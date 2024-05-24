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
@Table(name = "recognition_model")
public class RecognitionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    
}
