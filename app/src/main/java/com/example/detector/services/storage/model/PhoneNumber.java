package com.example.detector.services.storage;

import androidx.room.DatabaseView;
import androidx.room.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@Data
@Entity(tableName = "phone_number")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhoneNumberEntity {
    public static final short WHITE_NUMBER = 1;
    public static final short BLACK_NUMBER = 2;
    private String number;
    private String owner;
    private boolean isShared;
    private boolean isSynchronized;
    private short numberType;
}
