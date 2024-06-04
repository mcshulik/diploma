package com.example.detector.services.storage.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Paval Shlyk
 * @since 02/06/2024
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(
    tableName = "suspicious_keyword"
)
public class SuspiciousKeyword {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long id;
    @ColumnInfo(name = "keyword")
    private String keyword;
}
