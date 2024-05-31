package com.example.detector.services.notification;

import com.example.utils.Result;

/**
 * @author Paval Shlyk
 * @since 01/06/2024
 */
public interface NotificationService {
    void notifyBlackNumber(String number);
    //return Ok if all permission granted
    Result<?> checkPermissions();
}
