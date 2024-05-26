package com.example.detector.services.storage;

import android.content.Context;
import android.widget.Toast;
import androidx.datastore.core.Serializer;
import com.example.detector.services.StorageService;
import com.example.services.storage.WhitePhoneNumber;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class StorageServiceImpl implements StorageService {
    private final  @ApplicationContext Context context;
    @Override
    public void doStuff() {
	Toast.makeText(context, "The message", Toast.LENGTH_SHORT).show();

    }
    private static class SettingsSerializer implements Serializer<Object> {

        @Override
        public Object getDefaultValue() {
            return null;
        }

        @Nullable
        @Override
        public Object readFrom(@NotNull InputStream inputStream, @NotNull Continuation<? super Object> continuation) {
            return null;
        }

        @Nullable
        @Override
        public Object writeTo(Object o, @NotNull OutputStream outputStream, @NotNull Continuation<? super Unit> continuation) {
            return null;
        }
    }
}
