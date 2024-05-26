package com.example.detector.services.storage.model;

import androidx.room.DatabaseView;
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
@DatabaseView(viewName = "white_list", value = "select id, number, owner " +
						   "from phone_number where numberType = 1")
public class WhiteNumber implements PhoneNumberProjection {
    private Long id;
    private String number;
    private String owner;
}
