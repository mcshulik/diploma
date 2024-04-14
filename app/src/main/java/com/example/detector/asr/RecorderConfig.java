package com.example.detector.asr;

import android.content.Context;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;

import java.io.File;

/**
 * @author Paval Shlyk
 * @since 14/04/2024
 */
@Accessors(fluent = true)
@Builder
@Value
public class RecorderConfig {
    @NonNull File directory;
    @NonNull Context context;
    @NonNull TelephonyManager telephony;
    @NonNull SubscriptionManager subscription;
}
