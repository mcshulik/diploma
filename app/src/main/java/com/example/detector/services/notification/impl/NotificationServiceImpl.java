package com.example.detector.services.notification.impl;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.detector.R;
import com.example.detector.services.notification.NotificationService;
import com.example.detector.utils.ActivityChecker;
import com.example.utils.Result;
import com.example.utils.Results;
import dagger.hilt.android.qualifiers.ApplicationContext;
import lombok.val;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Paval Shlyk
 * @since 01/06/2024
 */
@Singleton

public class NotificationServiceImpl implements NotificationService {
    private static final String TAG = "NotificationServiceImpl";
    private static final String ATTENTION_CHANNEL_ID = "attention_channel_id";
    private static final int ATTENTION_NOTIFICATION_ID = 1;
    private final ReentrantLock lock = new ReentrantLock();
    private final NotificationManagerCompat notificationManager;
    private final NotificationCompat.Builder attentionNotificationBuilder;
    private final ActivityChecker checker;

    @Inject
    public NotificationServiceImpl(
	@ApplicationContext Context context,
	ActivityChecker checker
    ) {
	this.checker = checker;
	notificationManager = NotificationManagerCompat.from(context);
	attentionNotificationBuilder =
	    new NotificationCompat.Builder(context, ATTENTION_CHANNEL_ID)
		.setDefaults(NotificationCompat.DEFAULT_ALL)
		.setContentTitle("Attention")
		.setSmallIcon(R.drawable.ic_launcher_foreground)
		.setPriority(NotificationCompat.PRIORITY_HIGH)
		.setCategory(NotificationCompat.CATEGORY_CALL)
		.setAutoCancel(true)
		.setVibrate(new long[]{500, 2000})
		.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
	    CharSequence name = "Voice Call Channel";
	    String description = "Channel for voice call notifications";
	    int importance = NotificationManager.IMPORTANCE_HIGH;
	    NotificationChannel channel = new NotificationChannel(ATTENTION_CHANNEL_ID, name, importance);
	    channel.setVibrationPattern(new long[]{500, 2000});
	    channel.setDescription(description);
	    notificationManager.createNotificationChannel(channel);
	}
    }

    @Override
    public void notifyBlackNumber(String number) {
	val msg = String.format("The number %s is supposed to be black", number);
	lock.lock();
	val notification = attentionNotificationBuilder
			       .setContentText(msg)
			       .build();
	lock.unlock();
	if (checker.checkPermission(Manifest.permission.POST_NOTIFICATIONS).isErr()) {
	    Log.w(TAG, "Failed to access POST_NOTIFICATION permission");
	    return;
	}
	notificationManager.notify(ATTENTION_NOTIFICATION_ID, notification);
    }

    @Override
    public Result<?> checkPermissions() {
	if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
	    return checker.checkPermission(Manifest.permission.POST_NOTIFICATIONS);
	}
	return Results.ok();
    }

}
