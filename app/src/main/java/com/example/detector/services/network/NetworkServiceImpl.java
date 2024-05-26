package com.example.detector.services.network;

import com.example.detector.services.NetworkService;
import com.example.detector.services.IncomingPhoneNumber;
import dagger.hilt.android.scopes.ServiceScoped;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@ServiceScoped
public class NetworkServiceImpl implements NetworkService {
    private final OkHttpClient client;

    @Override
    public Optional<List<IncomingPhoneNumber>> tryAccessWhiteList() {
	return Optional.empty();
    }

    @Override
    public Optional<List<IncomingPhoneNumber>> tryAccessBlackList() {
	return Optional.empty();
    }

    @Override
    public boolean trySendWhiteList(List<IncomingPhoneNumber> numbers) {
	return false;
    }

    @Override
    public boolean trySendBlackList(List<IncomingPhoneNumber> numbers) {
	return false;
    }
}
