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
@DatabaseView
    (
	viewName = "black_list",
	value = "select id, number, owner, isSynchronized " +
		    "from phone_number where numberType = 2"
    )
public class BlackNumber implements PhoneNumberProjection {
    private Long id;
    private String number;
    private String owner;
    private boolean isSynchronized;
}
