package com.example.detector.services.storage.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
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
    tableName = "phone_number",
    indices = {@Index(value = "number", unique = true)}
)
public class PhoneNumber {
    public static final short WHITE_NUMBER = 1;
    public static final short BLACK_NUMBER = 2;
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long id;
    private String number;
    private String owner;
    private boolean isShared;
    private boolean isSynchronized;
    private short numberType;

    public PhoneNumber asWhite() {
	numberType = WHITE_NUMBER;
	return this;
    }

    public PhoneNumber asBlack() {
	numberType = BLACK_NUMBER;
	return this;
    }
}
