package com.example.detector.services.storage.model;

import androidx.room.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(
    tableName = "voice_record",
    foreignKeys = {@ForeignKey(entity = PhoneNumber.class, parentColumns = "id", childColumns = "number_id")}
)
public class VoiceRecord {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long id;
    @ColumnInfo(name = "number_id")
    private long phoneNumberId;
    private float quality;
    private String speechText;
    private String recognizedText;

    private Long duration;
    private byte[] audio;

    @Builder.Default
    private boolean isSynchronized = false;
}
