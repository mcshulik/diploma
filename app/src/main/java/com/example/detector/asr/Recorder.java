package com.example.detector.asr;

import android.Manifest;
import android.content.*;
import android.content.pm.PackageManager;
import android.media.*;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.detector.MainActivity;
import com.example.detector.asr.RecorderListener.State;
import com.example.detector.utils.WaveUtil;
import com.google.common.base.Optional;
import com.google.errorprone.annotations.ThreadSafe;
import lombok.NonNull;
import lombok.var;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@ThreadSafe
public class Recorder {
    public static final String TAG = "Recorder";
    public static final String ACTION_STOP = "Stop";
    public static final String ACTION_RECORD = "Record";
    public static final String MSG_RECORDING = "Recording...";
    public static final String MSG_RECORDING_DONE = "Recording done...!";
    private final Context context;
    private final TelephonyManager telephony;
    private final AtomicBoolean mInProgress = new AtomicBoolean(false);
    private Thread mExecutorThread = null;
    private RecorderListener mListener = null;
    private final File directory;
    private final AtomicReference<String> currentPhoneNumber = new AtomicReference<>();

    private Optional<String> getCurrentPhoneNumber() {
	return Optional.fromNullable(currentPhoneNumber.get());
    }

    private Recorder(RecorderConfig config) {
	context = config.context();
	directory = config.directory();
	telephony = config.telephony();
	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
	    telephony.registerTelephonyCallback(context.getMainExecutor(), callStateListener);
	    IntentFilter filter = new IntentFilter();
	    filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
	    context.registerReceiver(new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
		    String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
		    String extraState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		    if (intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER) && TelephonyManager.EXTRA_STATE_RINGING.equals(extraState)) {
			assert number != null;
			currentPhoneNumber.set(number);
//			Intent msg = new Intent(context, MainActivity.class);
//			msg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			msg.putExtra("number", number);
//			context.startActivity(msg);
		    }
		}
	    }, filter);
	} else {
	    telephony.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
    }

    public static Optional<Recorder> of(@NonNull String directory, @NonNull Context context) {
	if (!new File(directory).exists()) {
	    Log.e(TAG, "Output directory is not present");
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
		Log.e(TAG, t.getMessage());
		throw t;
	    }
	}
	Log.w(TAG, "Permissions are not granted");
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
									String number = currentPhoneNumber.get();
									if (state == TelephonyManager.CALL_STATE_OFFHOOK && number == null) {
									    Log.d(TAG, "Phone number is not available for offhook");
									}
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
								      if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
									  assert phoneNumber != null;
									  handleSession(state, phoneNumber);
								      }
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
	Log.d(TAG, "Recording is starting...");
	String fullName = directory.getAbsolutePath() + "/" + fileName;
	mExecutorThread = new Thread(() -> {
	    mInProgress.set(true);
	    threadFunction(fullName);
	    mInProgress.set(false);
	});
	mExecutorThread.start();
    }

    private void handleSession(int state, @Nullable String phoneNumber) {
	Log.d(TAG, "The phone number is " + phoneNumber);
	if (state == TelephonyManager.CALL_STATE_IDLE) {
	    stop();
	} else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
	    if (canStartRecording()) {
		sendState(State.START, phoneNumber);
		String fileName = "Call_" + System.currentTimeMillis() + "(" + phoneNumber + ".3gp";
		start(fileName);
	    }
	}
    }

    private boolean canStartRecording() {
	return currentPhoneNumber.get() != null && !isInProgress();
    }

    public void stop() {
	mInProgress.set(false);
	currentPhoneNumber.set(null);
	sendState(State.STOP);
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

    private void sendState(@NonNull State state, String message) {
	if (mListener != null) {
	    mListener.onStateUpdate(state, message);
	}
    }

    private void sendState(@NonNull State state) {
	if (mListener != null) {
	    mListener.onStateUpdate(state, null);
	}
    }

    private void sendData(float[] samples) {
	if (mListener != null)
	    mListener.onDataUpdate(samples);
    }

    private static final ThreadLocal<float[]> SAMPLES = new ThreadLocal<>();

    private static float[] getBufferedSamples(int size) {
	float[] samples = SAMPLES.get();
	if (samples == null || samples.length != size) {
	    Log.d(TAG, "Allocate new samples buffer");
	    samples = new float[size];
	    SAMPLES.set(samples);
	}
	return samples;
    }

    private void threadFunction(String fileName) {
	try {
	    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
		return;
	    }
	    int channels = 1;
	    int bytesPerSample = 2;
//	    int sampleRateInHz = 16000;
	    int sampleRateInHz = 44100;
	    int channelConfig = AudioFormat.CHANNEL_IN_MONO; // as per channels
	    int audioFormat = AudioFormat.ENCODING_PCM_16BIT; // as per bytesPerSample
	    int audioSource = MediaRecorder.AudioSource.VOICE_CALL;
	    int bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
//	    MediaRecorder recorder = new MediaRecorder();
//	    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//	    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//	    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//	    recorder.setOutputFile(fileName);
//	    recorder.prepare();
//	    recorder.start();
	    AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSize);
	    audioRecord.startRecording();

	    final int bufferSize1Sec = sampleRateInHz * bytesPerSample * channels;
	    final int defaultBufferSize = bufferSize1Sec * 30;
//	    int bufferSize30Sec = bufferSize1Sec * 30;
	    var output = new ByteArrayOutputStream(defaultBufferSize);
//	    ByteBuffer buffer30Sec = ByteBuffer.allocateDirect(bufferSize30Sec);
	    ByteBuffer bufferRealtime = ByteBuffer.allocateDirect(bufferSize1Sec * 5);
	    int timer = 0;
	    int totalBytesRead = 0;
	    byte[] audioData = new byte[bufferSize];
	    while (mInProgress.get()) {
		sendState(State.RECORDING, timer + "s");

		int bytesRead = audioRecord.read(audioData, 0, bufferSize);
		if (bytesRead > 0) {
		    output.write(audioData, 0, bytesRead);
//		    buffer30Sec.put(audioData, 0, bytesRead);
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

		    // Transcribe realtime buffer after every 2 seconds
		    if (timer % 2 == 0) {
			// Flip the buffer for reading
			bufferRealtime.flip();
			bufferRealtime.order(ByteOrder.nativeOrder());

			// Create a sample array to hold the converted data
			float[] samples = getBufferedSamples(bufferRealtime.remaining() / 2);
//			float[] samples = new float[bufferRealtime.remaining() / 2];

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
	    WaveUtil.createWaveFile(fileName, output.toByteArray(), sampleRateInHz, channels, bytesPerSample);
	    Log.d(TAG, "Recorded file: " + fileName);
	    sendState(State.DONE, "File saved at " + fileName);
	} catch (Exception e) {
	    Log.e(TAG, "Error...", e);
	    sendState(State.STOP, e.getMessage());
	}
    }
}
