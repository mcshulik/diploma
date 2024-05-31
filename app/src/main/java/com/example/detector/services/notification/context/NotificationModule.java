package com.example.detector.services.notification.context;

import com.example.detector.services.notification.NotificationService;
import com.example.detector.services.notification.impl.NotificationServiceImpl;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import org.checkerframework.checker.signature.qual.SignatureBottom;

import javax.inject.Singleton;

/**
 * @author Paval Shlyk
 * @since 01/06/2024
 */
@Module
@InstallIn(SingletonComponent.class)
public abstract class NotificationModule {
    @Binds
    @Singleton
    public abstract NotificationService bindNotificationService(NotificationServiceImpl service);
}
