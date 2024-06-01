package by.bsuir.whisper.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.sql.Timestamp;

/**
 * @author Paval Shlyk
 * @since 24/05/2024
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "voice_record")
@Entity
public class VoiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "duration", nullable = false)
    private Timestamp duration;

    //The estimation quality of the text
    //This field is set automatically by model
    @Column(name = "quality", nullable = false)
    private Float quality;

    //This text is set automatically by model
    @Column(name = "recognition_text", nullable = false)
    private String recognitionText;

    //The real text of speech
    //That's set manually by user
    @Column(name = "speech_text", nullable = false)
    private String speechText;

    ///consider removing this field (that will be too huge?)
    @Column(name = "audio", nullable = false)
    private byte[] audio;

    //the user which had recorded the speech
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User recorder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recognition_model_id", nullable = false)
    private RecognitionModel model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "number_id", nullable = false)
    private BlockedNumber number;
}
