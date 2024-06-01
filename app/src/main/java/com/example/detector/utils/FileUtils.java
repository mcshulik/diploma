package com.example.detector.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import androidx.annotation.NonNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Paval Shlyk
 * @since 13/04/2024
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {
    public static final String TAG = "FileUtils";

    public static @NonNull String resolveAssetPath(Context context, String assetName) {
	File outfile = new File(context.getFilesDir(), assetName);
	if (!outfile.exists()) {
	    Log.d(TAG, "File not found - " + outfile.getAbsolutePath());
	}
	Log.d(TAG, "Returned asset path: " + outfile.getAbsolutePath());
	return outfile.getAbsolutePath();
    }

    public static void copyAssetFiles(Context context, String... files) {
	AssetManager assetManager = context.getAssets();
	try {
	    // Specify the destination directory in the app's data folder
	    String destFolder = context.getFilesDir().getAbsolutePath();
	    String[] assetFiles = assetManager.list("");
	    for (String fileName : files) {
		for (String assetFile : assetFiles) {
		    if (!assetFile.equals(fileName)) {
			continue;
		    }
		    File copiedFile = new File(destFolder, assetFile);
		    InputStream input = assetManager.open(assetFile);
		    FileOutputStream output = new FileOutputStream(copiedFile);
		    byte[] buffer = new byte[1024];
		    int read;
		    while ((read = input.read(buffer)) != -1) {
			output.write(buffer, 0, read);
		    }
		    input.close();
		    output.flush();
		    output.close();
		}
	    }
	} catch (IOException e) {
	    Log.e(TAG, "Failed to copy asset file");
	}
    }
}
