package com.example.calltest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.media.MediaPlayer;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText phoneNumberEditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialButton = findViewById(R.id.btn_dial);

        dialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startCallRecording();
            }
        });
    }

    private void startCallRecording() {
        // Создаем папку для записей, если она не существует
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/CallRecordings");
        if (!folder.exists()) {
            if(folder.mkdirs());
            else
                dialButton.setText("error");
        }
        else
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
                    }
                    else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
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

        // Убираем слушателя при уничтожении активности
        if (telephonyManager != null && phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение получено, можно продолжить работу
                startCallRecording();
            } else {
                // Разрешение не было предоставлено
            }
        }
    }
}
