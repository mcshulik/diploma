package com.example.detector.asr;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.telecom.InCallService;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.MimeTypeFilter;
import com.example.detector.utils.FileUtils;
import com.example.detector.utils.WaveUtil;
import com.google.common.base.Optional;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.var;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;

public class Recorder {
    public static final String TAG = "Recorder";
    public static final String ACTION_STOP = "Stop";
    public static final String ACTION_RECORD = "Record";
    public static final String MSG_RECORDING = "Recording...";
    public static final String MSG_RECORDING_DONE = "Recording done...!";

    private final Context mContext;
    private final TelephonyManager telephony;
    private final SubscriptionManager subscription;
    private final AtomicBoolean mInProgress = new AtomicBoolean(false);
    private Thread mExecutorThread = null;
    private RecorderListener mListener = null;
    private final File directory;
    @RequiresApi(api = Build.VERSION_CODES.S)
    private Integer subscriptionId;

    private Recorder(RecorderConfig config) {
	mContext = config.context();
	directory = config.directory();
	telephony = config.telephony();
	subscription = config.subscription();
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
	    telephony.registerTelephonyCallback(mContext.getMainExecutor(), callStateListener);
	    this.subscriptionId = telephony.getSubscriptionId();
	    IntentFilter filter = new IntentFilter();
	    filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
	    mContext.registerReceiver(new BroadCastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
		    super.onReceive(context, intent);
		    String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
		    System.out.println(number);//this f*** method is invoked two times. One of them return null. Other real number
		}
	    }, filter);
	} else {
	    telephony.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
    }

    public static Optional<Recorder> of(@NonNull String directory, @NonNull Context context) {
	if (!new File(directory).exists()) {
	    return Optional.absent();
	}
	if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
		&& ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
		&& ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
	    var telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	    var subscription = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
	    var config = RecorderConfig.builder()
			     .directory(new File(directory))
			     .telephony(telephony).subscription(subscription)
			     .context(context)
			     .build();
	    try {

		return Optional.of(new Recorder(config));
	    } catch (Throwable t) {
		throw t;
	    }
	}
	return Optional.absent();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private static abstract class CallStateListener extends TelephonyCallback implements TelephonyCallback.CallStateListener {
	@Override
	abstract public void onCallStateChanged(int state);
    }

    private final CallStateListener callStateListener = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) ?
							    new CallStateListener() {
								@Override
								public void onCallStateChanged(int state) {
								    try {
									String number = telephony.getLine1Number();
									handleSession(state, number);
								    } catch (
									  SecurityException ignored) {
									throw new IllegalStateException("Already checked");
								    }

								}
							    }
							    : null;

    private final PhoneStateListener phoneStateListener = (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) ?
							      new PhoneStateListener() {
								  @Override
								  public void onCallStateChanged(int state, String phoneNumber) {
								      handleSession(state, phoneNumber);
								  }
							      }
							      : null;

    public void setListener(RecorderListener listener) {
	mListener = listener;
    }

    public void start(@NonNull String fileName) {
	if (mInProgress.get()) {
	    Log.d(TAG, "Recording is already in progress...");
	    return;
	}

	mExecutorThread = new Thread(() -> {
	    mInProgress.set(true);
	    threadFunction(fileName);
	    mInProgress.set(false);
	});
	mExecutorThread.start();
    }

    private void handleSession(int state, String phoneNumber) {
	Log.d(TAG, "The phone number is " + phoneNumber);
	sendUpdate(phoneNumber);
	if (state == TelephonyManager.CALL_STATE_IDLE) {
	    stop();
	} else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
	    if (!isInProgress()) {
		sendUpdate(phoneNumber);
		String fileName = "Call_" + System.currentTimeMillis() + ".3gp";
		String fullName = directory.getAbsolutePath() + "/" + fileName;
		start(fullName);
	    }
	}
    }

    public void stop() {
	mInProgress.set(false);
	try {
	    if (mExecutorThread != null) {
		mExecutorThread.join();
		mExecutorThread = null;
	    }
	} catch (InterruptedException e) {
	    throw new RuntimeException(e);
	}
    }

    public boolean isInProgress() {
	return mInProgress.get();
    }

    private void sendUpdate(String message) {
	if (mListener != null)
	    mListener.onUpdateReceived(message);
    }

    private void sendData(float[] samples) {
	if (mListener != null)
	    mListener.onDataReceived(samples);
    }

    private void threadFunction(String fileName) {
	try {
	    sendUpdate(MSG_RECORDING);
	    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
		return;
	    }
	    int channels = 1;
	    int bytesPerSample = 2;
	    int sampleRateInHz = 16000;
	    int channelConfig = AudioFormat.CHANNEL_IN_MONO; // as per channels
	    int audioFormat = AudioFormat.ENCODING_PCM_16BIT; // as per bytesPerSample
	    int audioSource = MediaRecorder.AudioSource.VOICE_CALL;
	    int bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
	    AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSize);
	    audioRecord.startRecording();

	    int bufferSize1Sec = sampleRateInHz * bytesPerSample * channels;
	    int bufferSize30Sec = bufferSize1Sec * 30;
	    ByteBuffer buffer30Sec = ByteBuffer.allocateDirect(bufferSize30Sec);
	    ByteBuffer bufferRealtime = ByteBuffer.allocateDirect(bufferSize1Sec * 5);

	    int timer = 0;
	    int totalBytesRead = 0;
	    byte[] audioData = new byte[bufferSize];
	    while (mInProgress.get() && (totalBytesRead < bufferSize30Sec)) {
		sendUpdate(MSG_RECORDING + timer + "s");

		int bytesRead = audioRecord.read(audioData, 0, bufferSize);
		if (bytesRead > 0) {
		    buffer30Sec.put(audioData, 0, bytesRead);
		    bufferRealtime.put(audioData, 0, bytesRead);
		} else {
		    Log.d(TAG, "AudioRecord error, bytes read: " + bytesRead);
		    break;
		}

		// Update timer after every second
		totalBytesRead = totalBytesRead + bytesRead;
		int timer_tmp = totalBytesRead / bufferSize1Sec;
		if (timer != timer_tmp) {
		    timer = timer_tmp;

		    // Transcribe realtime buffer after every 3 seconds
		    if (timer % 3 == 0) {
			// Flip the buffer for reading
			bufferRealtime.flip();
			bufferRealtime.order(ByteOrder.nativeOrder());

			// Create a sample array to hold the converted data
			float[] samples = new float[bufferRealtime.remaining() / 2];

			// Convert ByteBuffer to short array
			for (int i = 0; i < samples.length; i++) {
			    samples[i] = (float) (bufferRealtime.getShort() / 32768.0);
			}

			// Reset the ByteBuffer for writing again
			bufferRealtime.clear();

			// Send samples for transcription
			sendData(samples);
		    }
		}
	    }

	    audioRecord.stop();
	    audioRecord.release();

	    // Save 30 seconds of recording buffer in wav file
	    WaveUtil.createWaveFile(fileName, buffer30Sec.array(), sampleRateInHz, channels, bytesPerSample);
	    Log.d(TAG, "Recorded file: " + fileName);
	    sendUpdate(MSG_RECORDING_DONE);
	} catch (Exception e) {
	    Log.e(TAG, "Error...", e);
	    sendUpdate(e.getMessage());
	}
    }
}
