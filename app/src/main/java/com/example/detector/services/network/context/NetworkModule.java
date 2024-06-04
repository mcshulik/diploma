package com.example.detector.services.network.context;

import com.example.detector.services.UserInfo;
import com.example.detector.services.network.NetworkService;
import com.example.detector.services.network.impl.NetworkServiceImpl;
import com.example.detector.services.network.mappers.PhoneNumberMapper;
import com.example.detector.services.network.mappers.RecognitionResultMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import org.mapstruct.factory.Mappers;

import javax.inject.Singleton;

/**
 * @author Paval Shlyk
 * @since 27/05/2024
 */
@Module
@InstallIn(SingletonComponent.class)
public abstract class NetworkModule {
    @Provides
    public static Gson jsonParser() {
	return new GsonBuilder().create();
    }

    @Provides
    public static UserInfo userInfo() {
	return UserInfo.builder()
		   .id(1)
		   .build();
    }
    @Provides
    public static ServerInfo serverInfo() {
	return ServerInfo.builder()
//		   .baseUrl("http://192.168.64.100:8081/api/v1.0")
		   .baseUrl("http://192.168.50.14:8081/api/v1.0")
		   .build();
    }
    @Provides
    public static OkHttpClient webClient() {
	return new OkHttpClient.Builder()
		   .addInterceptor(chain -> chain.proceed(chain.request()))
		   .build();
    }

    @Provides
    public static PhoneNumberMapper numberMapper() {
	return Mappers.getMapper(PhoneNumberMapper.class);
    }

    @Provides
    public static RecognitionResultMapper recognitionMapper() {
	return Mappers.getMapper(RecognitionResultMapper.class);
    }

    @Binds
    @Singleton
    public abstract NetworkService bindNetworkServer(
	NetworkServiceImpl networkService
    );
}
