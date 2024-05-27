package com.example.detector.services.whisper.context;

import android.content.Context;
import com.example.detector.services.whisper.WhisperService;
import com.example.detector.services.whisper.engine.WhisperEngine;
import com.example.detector.services.whisper.engine.WhisperEngineConfig;
import com.example.detector.services.whisper.engine.impl.NativeWhisperEngine;
import com.example.detector.services.whisper.impl.WhisperServiceImpl;
import com.example.detector.utils.FileUtils;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ServiceScoped;
import dagger.hilt.components.SingletonComponent;
import lombok.val;

import javax.inject.Singleton;

import static com.example.detector.utils.FileUtils.resolveAssetPath;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@Module
@InstallIn(SingletonComponent.class)
public abstract class TenserFlowModule {
    public static final String I8N_MODEL_NAME = "whisper-tiny.tflite";
    public static final String I8N_LANG_VOC = "filters_vocab_multilingual.bin";

    public static final String EN_MODEL_NAME = "whisper-tiny-en.tflite";
    public static final String EN_LANG_VOC = "filters_vocab_en.bin";

    @Binds
    @Singleton
    public abstract WhisperService bindWhisperServer(
	WhisperServiceImpl whisperService
    );

    @Provides
    @Singleton
    public static WhisperEngine whisperEngine(
	@ApplicationContext Context context
    ) {
	FileUtils.copyAssetFiles(context, I8N_LANG_VOC, I8N_MODEL_NAME);
	val engineConfig = WhisperEngineConfig.builder()
			       .type(WhisperEngine.Type.JAVA)
			       .isMultiLang(true)
			       .modelPath(resolveAssetPath(context, I8N_MODEL_NAME))
			       .vocabPath(resolveAssetPath(context, I8N_LANG_VOC))
			       .build();
	return WhisperEngine.withConfig(engineConfig);
    }
}