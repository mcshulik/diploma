package com.example.detector.services.storage.model;

import androidx.room.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(
    tableName = "voice_recording",
    foreignKeys = {@ForeignKey(entity = PhoneNumber.class, parentColumns = "id", childColumns = "number_id")}
)
public class VoiceRecording {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long id;
    @ColumnInfo(name = "number_id")
    private Long phoneNumberId;

    private byte[] rawData;

    private boolean isSynchronized = false;
}
