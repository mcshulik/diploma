package com.example.detector.services.network.impl;

import android.util.Log;
import android.util.Pair;
import androidx.annotation.Nullable;
import com.example.detector.services.LocalPhoneNumber;
import com.example.detector.services.LocalRecognitionResult;
import com.example.detector.services.UserInfo;
import com.example.detector.services.network.NetworkService;
import com.example.detector.services.network.exceptions.UnknownDataFormat;
import com.example.detector.services.network.mappers.PhoneNumberMapper;
import com.example.detector.services.network.mappers.RecognitionResultMapper;
import com.example.detector.services.network.model.ServerPhoneNumber;
import com.example.detector.services.network.model.ServerRecognitionResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import dagger.hilt.android.scopes.ServiceScoped;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.var;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class NetworkServiceImpl implements NetworkService {
    private static final String TAG = "NetworkServiceImpl";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    private static final String JSON_TYPE_HEADER = "application/json";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private final Gson jsonParser;
    private final UserInfo userInfo;
    private final OkHttpClient client;
    private final PhoneNumberMapper numberMapper;
    private final RecognitionResultMapper resultMapper;

    @Override
    public Maybe<List<LocalPhoneNumber>> tryAccessBlackList() {
	val request = new Request.Builder()
			  .url("http://10.0.2.2:8081/api/v1.0/black-list")
			  .get()
			  .build();

	return sendRequest(request, ServerPhoneNumber[].class)
		   .map(Arrays::asList)
		   .map(numberMapper::fromServerDtoList)
		   .subscribeOn(Schedulers.io());
    }

    @Override
    public Maybe<?> trySendBlackList(Pair<LocalPhoneNumber, List<LocalRecognitionResult>>... numbers) {
	var single = (Single<?>) Single.just(42);

	for (Pair<LocalPhoneNumber, List<LocalRecognitionResult>> numberAndResults : numbers) {

	    val number = (ServerPhoneNumber) numberMapper.toServerDto(numberAndResults.first);
	    val results = numberAndResults.second;
	    val numberRequest = new Request.Builder()
				    .url("http://10.0.2.2:8081/api/v1.0/black-list")
				    .post(ofBody(number))
				    .addHeader(CONTENT_TYPE_HEADER, JSON_TYPE_HEADER)
				    .build();

	    val requestSingle = sendRequest(numberRequest, ServerPhoneNumber.class)
				    .flatMapSingle(response -> {

					if (results.isEmpty()) {
					    return Single.just(response);
					}

					val serverResults = resultMapper
								.toServerDtoList(results, userInfo)
								.toArray(new ServerRecognitionResult[0]);
					long resourceId = response.getId();
					val resultsRequest = new Request.Builder()
								 .url("http://10.0.2.2:8081/api/v1.0/black-list/" + resourceId + "/records")
								 .post(ofBody(serverResults))
								 .build();
					val request = sendRequest(resultsRequest, null);

					return Single.just(response)
						   .zipWith(request.toSingle(), Pair::create);
				    }).toSingle();
	    single = single
			 .zipWith(requestSingle, (magic, tuple) -> 42);
	}
	return single
		   .toMaybe()
		   .subscribeOn(Schedulers.io());
    }

    private <T> Optional<T> ofJson(ResponseBody body, Class<T> clazz) {
	try {
	    val value = jsonParser.fromJson(body.charStream(), clazz);
	    return Optional.of(value);
	} catch (JsonSyntaxException | JsonIOException e) {
	    Log.w(TAG, "Failed to parse server JSON");
	}
	//if we here then all is terrible
	return Optional.empty();
    }

    private <T> RequestBody ofBody(T value) {
	String json = jsonParser.toJson(value);
	return RequestBody.create(json, JSON_MEDIA_TYPE);
    }

    private <T> Maybe<T> sendRequest(
	Request request,
	@Nullable Class<T> clazz
    ) {
	val hasBody = clazz != null;
	return Maybe.create(emitter -> {
	    client
		.newCall(request)
		.enqueue(new Callback() {
		    @Override
		    public void onFailure(@NotNull Call call, @NotNull IOException e) {
			Log.w(TAG, "Server is busy on request:" + request, e);
			emitter.onComplete();
		    }

		    @Override
		    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
			if (!response.isSuccessful() || (hasBody && response.body() == null)) {
			    emitter.onError(new UnknownDataFormat());
			    return;
			}

			if (!hasBody) {
			    emitter.onComplete();
			    return;
			}
			val parsingResult = ofJson(response.body(), clazz);
			if (!parsingResult.isPresent()) {
			    emitter.onError(new UnknownDataFormat());
			    return;
			}
			val value = parsingResult.get();
			emitter.onSuccess(value);
		    }
		});
	});
    }
}
