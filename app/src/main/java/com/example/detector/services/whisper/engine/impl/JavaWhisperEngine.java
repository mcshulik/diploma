package com.example.detector.services.whisper.engine.impl;

import android.util.Log;
import com.example.detector.services.whisper.engine.ResourceNotFoundException;
import com.example.detector.services.whisper.engine.WhisperEngine;
import com.example.detector.services.whisper.engine.WhisperEngineConfig;
import com.example.detector.utils.WhisperUtil;
import lombok.val;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Optional;

@NotThreadSafe
public class JavaWhisperEngine implements WhisperEngine {
    private final String TAG = "WhisperEngineJava";
    private final Interpreter interpreter;
    private final WhisperUtil params;

    public JavaWhisperEngine(WhisperEngineConfig config) {
	WhisperUtil params = new WhisperUtil();
	Interpreter interpreter;
	try {
	    interpreter = loadModel(config.modelPath());
	} catch (IOException e) {
	    throw new ResourceNotFoundException("Failed to load model");
	}
	boolean isLoaded = params.loadFiltersAndVocab(config.isMultiLang(), config.vocabPath());
	if (!isLoaded) {
	    throw new ResourceNotFoundException("Failed to found vocab files");
	}
	this.interpreter = interpreter;
	this.params = params;
    }

    @Override
    public void interrupt() {

    }

    @Override
    public Optional<String> transcribeBuffer(float[] samples) {
	final float[] melSpectrogram = getMelSpectrogram(samples);
	String msg = runInference(melSpectrogram);
	if (msg.isEmpty()) {
	    return Optional.empty();
	}
	return Optional.of(msg);
    }

    // Load TFLite model
    private Interpreter loadModel(String modelPath) throws IOException {
	try (val inputStream = new FileInputStream(modelPath)) {
	    FileChannel fileChannel = inputStream.getChannel();
	    long startOffset = 0;
	    long declaredLength = fileChannel.size();
	    ByteBuffer model = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
	    // Set the number of threads for inference
	    Interpreter.Options options = new Interpreter.Options();
	    options.setNumThreads(Runtime.getRuntime().availableProcessors());
	    return new Interpreter(model, options);
	}
    }

    private float[] getMelSpectrogram(float[] samples) {
	int fixedInputSize = WhisperUtil.WHISPER_SAMPLE_RATE * WhisperUtil.WHISPER_CHUNK_SIZE;
	float[] inputSamples = new float[fixedInputSize];
	int copyLength = Math.min(samples.length, fixedInputSize);
	System.arraycopy(samples, 0, inputSamples, 0, copyLength);

	int cores = Runtime.getRuntime().availableProcessors();
	return params.getMelSpectrogram(inputSamples, inputSamples.length, cores);
    }

    private String runInference(float[] inputData) {
	// Create input tensor
	Tensor inputTensor = interpreter.getInputTensor(0);
	TensorBuffer inputBuffer = TensorBuffer.createFixedSize(inputTensor.shape(), inputTensor.dataType());
	Log.d(TAG, "Input Tensor Dump ===>");
	printTensorDump(inputTensor);

	// Create output tensor
	Tensor outputTensor = interpreter.getOutputTensor(0);
	TensorBuffer outputBuffer = TensorBuffer.createFixedSize(outputTensor.shape(), DataType.FLOAT32);
	Log.d(TAG, "Output Tensor Dump ===>");
	printTensorDump(outputTensor);

	// Load input data
	int inputSize = inputTensor.shape()[0] * inputTensor.shape()[1] * inputTensor.shape()[2] * Float.BYTES;
	ByteBuffer inputBuf = ByteBuffer.allocateDirect(inputSize);
	inputBuf.order(ByteOrder.nativeOrder());
	for (float input : inputData) {
	    inputBuf.putFloat(input);
	}

	// To test mel data as a input directly
//        try {
//            byte[] bytes = Files.readAllBytes(Paths.get("/data/user/0/com.example.tfliteaudio/files/mel_spectrogram.bin"));
//            inputBuf = ByteBuffer.wrap(bytes);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

	inputBuffer.loadBuffer(inputBuf);

	// Run inference
	interpreter.run(inputBuffer.getBuffer(), outputBuffer.getBuffer());

	// Retrieve the results
	int outputLen = outputBuffer.getIntArray().length;
	Log.d(TAG, "output_len: " + outputLen);
	StringBuilder result = new StringBuilder();
	for (int i = 0; i < outputLen; i++) {
	    int token = outputBuffer.getBuffer().getInt();
	    if (token == params.getTokenEOT())
		break;

	    // Get word for token and Skip additional token
	    if (token < params.getTokenEOT()) {
		String word = params.getWordFromToken(token);
		Log.d(TAG, "Adding token: " + token + ", word: " + word);
		result.append(word);
	    } else {
		if (token == params.getTokenTranscribe())
		    Log.d(TAG, "It is Transcription...");

		if (token == params.getTokenTranslate()) {
		    Log.d(TAG, "It is Translation...");
		}

		String word = params.getWordFromToken(token);
		Log.d(TAG, "Skipping token: " + token + ", word: " + word);
	    }
	}

	return result.toString();
    }

    private void printTensorDump(Tensor tensor) {
	Log.d(TAG, "  shape.length: " + tensor.shape().length);
	for (int i = 0; i < tensor.shape().length; i++)
	    Log.d(TAG, "    shape[" + i + "]: " + tensor.shape()[i]);
	Log.d(TAG, "  dataType: " + tensor.dataType());
	Log.d(TAG, "  name: " + tensor.name());
	Log.d(TAG, "  numBytes: " + tensor.numBytes());
	Log.d(TAG, "  index: " + tensor.index());
	Log.d(TAG, "  numDimensions: " + tensor.numDimensions());
	Log.d(TAG, "  numElements: " + tensor.numElements());
	Log.d(TAG, "  shapeSignature.length: " + tensor.shapeSignature().length);
	Log.d(TAG, "  quantizationParams.getScale: " + tensor.quantizationParams().getScale());
	Log.d(TAG, "  quantizationParams.getZeroPoint: " + tensor.quantizationParams().getZeroPoint());
	Log.d(TAG, "==================================================================");
    }

    @Override
    public void close() throws Exception {
	interpreter.close();
    }
}