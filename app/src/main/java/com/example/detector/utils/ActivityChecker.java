package com.example.detector.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import com.example.utils.Result;
import com.example.utils.Results;
import dagger.hilt.android.qualifiers.ApplicationContext;
import lombok.val;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Paval Shlyk
 * @since 01/06/2024
 */
@Singleton
public class ActivityChecker {
    private final Context context;

    @Inject
    public ActivityChecker(@ApplicationContext Context context) {
	this.context = context;
    }

    public Result<?> checkPermission(String... permissions) {
	for (val permission : permissions) {
	    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
		return Results.withCause("Permission " + permission + " is not not granted");
	    }
	}
	return Results.ok();
    }
}
