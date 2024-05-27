package com.example.detector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.*;
import android.os.*;
import android.speech.RecognitionService;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.detector.asr.Recorder;
import com.example.detector.asr.RecorderListener;
import com.example.detector.asr.Whisper;
import com.example.detector.asr.WhisperListener;
import com.example.detector.services.network.NetworkService;
import com.example.detector.services.whisper.engine.WhisperEngine;
import com.example.detector.services.whisper.engine.WhisperEngineConfig;
import com.example.detector.services.storage.StorageService;
import com.example.detector.services.whisper.WhisperService;
import com.example.detector.utils.FileUtils;
import com.example.detector.utils.WaveUtil;
import dagger.hilt.android.AndroidEntryPoint;
import io.vertx.core.net.NetServer;
import lombok.NoArgsConstructor;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.File;

@AndroidEntryPoint
@NoArgsConstructor
public class WhisperActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private EditText phoneNumberEditText;
    private String phoneNum;
    volatile Button dialButton;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;
    private static final int PERMISSION_REQUEST_CODE = 123;
    private String outputFile;
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;
    private MediaPlayer mediaPlayer;
    private Recorder recorder;
    private TextView tvSpeech;
    private boolean isInitialized = false;
    @Inject
    private StorageService storageService;
    @Inject
    private WhisperService whisperService;
    @Inject
    private NetworkService networkService;

    private void init() {
	File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/CallRecordings");
	String directory = getFilesDir().getAbsolutePath();
	if (!folder.exists()) {
	    if (!folder.mkdirs()) {
		Looper.prepare();
		Toast.makeText(this, "Failed to create output directory. Will be saved in " + directory, Toast.LENGTH_LONG)
		    .show();
	    }
	}
	if (folder.exists()) {
	    directory = folder.getAbsolutePath();
	}
	recorder = Recorder.of(directory, this).get();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	dialButton = findViewById(R.id.btn_dial);
	final String[] permissions = getPermissions();
	ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
	if (!isInitialized) {
	    init();
	}
	tvSpeech = findViewById(R.id.tvSpeech);
	final Handler handler = new Handler(Looper.getMainLooper());
	whisper.setListener(new WhisperListener() {
	    @Override
	    public void onState(WhisperListener.State state, String message) {
		Log.d(TAG, "Update is received, Message: " + message);
		if (state == State.START) {
		    handler.post(() -> tvSpeech.setText(""));
		}
		if (state == State.DONE) {
		    handler.post(() -> tvSpeech.setText(""));
		}
	    }

	    @Override
	    public void onResult(String result) {
		Log.d(TAG, "Result: " + result);
		handler.post(() -> tvSpeech.append(result));
	    }
	});
	recorder.setListener(new RecorderListener() {
	    @Override
	    public void onStateUpdate(@NonNull RecorderListener.State state, String message) {
		Log.d(TAG, "Update is received, Message: " + message);
		if (state == State.RECORDING) {
		    Log.d(TAG, "Recording is started");
		    handler.post(() -> dialButton.setText(Recorder.ACTION_STOP));
		} else if (state == State.DONE) {
		    Log.d(TAG, "Recording is completed");
		    handler.post(() -> dialButton.setText(Recorder.ACTION_RECORD));
		    Looper.prepare();
		    Toast.makeText(WhisperActivity.this, message, Toast.LENGTH_LONG).show();
		}
	    }

	    @Override
	    public void onDataUpdate(float[] samples) {
		whisper.writeBuffer(samples);
	    }
	});
	checkRecordPermission();
	dialButton.setOnClickListener(v -> {
	    assert recorder != null;
	    if (recorder.isInProgress()) {
		handler.post(() -> tvSpeech.setText(phoneNum != null ? phoneNum : "aboba"));
		recorder.stop();
	    } else {
		recorder.start(WaveUtil.RECORDING_FILE);
	    }
	});
	String number = getIntent().getStringExtra("number");
	phoneNum = number == null
		       ? "Number is not available"
		       : number;
//        dialButton.setOnClickListener(v -> startCallRecording());

    }

    @NotNull
    private static String[] getPermissions() {
	final String[] permissions;
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
	    permissions = new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAPTURE_AUDIO_OUTPUT};
	} else {
	    permissions = new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAPTURE_AUDIO_OUTPUT};
	}
	return permissions;
    }

    private void startCallRecording() {
	// Создаем папку для записей, если она не существует
	File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/CallRecordings");
	if (!folder.exists()) {
	    if (folder.mkdirs()) ;
	    else
		dialButton.setText("error");
	} else
	    dialButton.setText("ok");

	String fileName = "Call_" + System.currentTimeMillis() + ".3gp";
	outputFile = folder.getAbsolutePath() + "/" + fileName;

	// Проверяем разрешение на звонок и запись аудио
	if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
		&& ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

	    // Устанавливаем слушателя для отслеживания состояний звонка
	    phoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String phoneNumber) {
		    super.onCallStateChanged(state, phoneNumber);
		    if (state == TelephonyManager.CALL_STATE_IDLE) {
			// Завершение звонка - остановить запись
			stopCallRecording();
		    } else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
			// Начало разговора - начать запись
			if (!isRecording) {
			    startRecording();
			}
		    }
		}
	    };

	    try {
		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager != null) {
		    telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		} else {
		    // Обработка ситуации, когда telephonyManager равен null
		}
	    } catch (SecurityException e) {
		// Обработка исключения SecurityException
		e.printStackTrace();
	    }

	    // Подготовка MediaRecorder
	    mediaRecorder = new MediaRecorder();
	    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
	    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	    mediaRecorder.setOutputFile(outputFile);
//	    mediaRecorder.setOnInfoListener(this::handleAboba);
	    // Подготовка и запуск записи будет произведена при начале разговора
	    dialButton.setText("Waiting for the call to start...");

	} else {
	    // Если разрешения не были предоставлены, запросим их
	    ActivityCompat.requestPermissions(this,
		new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.RECORD_AUDIO},
		PERMISSION_REQUEST_CODE);
	}
    }


    private void startRecording() {
	try {
	    mediaRecorder.prepare();
	    mediaRecorder.start();
	    isRecording = true;
	    dialButton.setText("Recording...");


	    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
		    && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
		int sampleRate = 44100; // Частота дискретизации 44.1 кГц (CD-качество)
		int channelConfig = AudioFormat.CHANNEL_IN_MONO; // Монофонический звук
		int audioFormat = AudioFormat.ENCODING_PCM_16BIT; // 16-битный PCM

		int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, bufferSize);

		// Настройка AudioTrack для воспроизведения обработанных данных
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO, audioFormat, bufferSize, AudioTrack.MODE_STREAM);


		byte[] buffer = new byte[bufferSize];

		Runnable runnable = () -> {
		    while (true) {
			int readBytes;
			readBytes = audioRecord.read(buffer, 0, bufferSize);
			// Здесь вы можете добавить код для обработки аудио данных в буфере перед записью
			audioTrack.write(buffer, 0, readBytes);
			// Здесь также вы можете записывать обработанные данные на диск, если нужно*/
		    }
		};

		Thread thread = new Thread(runnable);
		thread.start();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    private void stopCallRecording() {
	if (mediaRecorder != null && isRecording) {
	    try {
		//audioTrack.stop();
		//audioTrack.release();
		//audioRecord.stop();
		//audioRecord.release();
		mediaRecorder.stop();
		mediaRecorder.release();
	    } catch (RuntimeException e) {
		e.printStackTrace();
	    } finally {
		isRecording = false;
	    }
	}
    }

    @Override
    protected void onDestroy() {
	super.onDestroy();
	stopCallRecording();
	whisper.close();
	// Убираем слушателя при уничтожении активности
	if (telephonyManager != null && phoneStateListener != null) {
	    telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
	}
    }


    private void checkRecordPermission() {
	int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
	if (permission == PackageManager.PERMISSION_GRANTED) {
	    Log.d(TAG, "Record permission is granted");
	} else {
	    Log.d(TAG, "Requesting record permission");
	    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
	}
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
	super.onRequestPermissionsResult(requestCode, permissions, grantResults);

	if (requestCode == PERMISSION_REQUEST_CODE) {
	    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
		// Разрешение получено, можно продолжить работу
//		startCallRecording();
	    } else {
		// Разрешение не было предоставлено
	    }
	}
    }
}