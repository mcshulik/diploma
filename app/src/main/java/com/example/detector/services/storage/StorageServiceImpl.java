package com.example.detector.services.storage;

import com.example.detector.services.IncomingPhoneNumber;
import com.example.detector.services.StorageService;
import com.example.detector.services.storage.model.PhoneNumber;
import com.example.detector.services.storage.model.PhoneNumberProjection;
import com.example.detector.services.storage.model.VoiceRecording;
import io.reactivex.rxjava3.core.Single;
import lombok.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Paval Shlyk
 * @since 26/05/2024
 */
@RequiredArgsConstructor(onConstructor_ = {@Inject})
@Builder
public class StorageServiceImpl implements StorageService {
    private static final String PRIVATE_PHONE_NUMBER = "Owner is hidden";
    private static final String TAG = "StorageServiceImpl";
    private final PhoneNumberDao phoneNumberDao;
    private final VoiceRecordingDao voiceRecordingDao;

    @Override
    public void addPrivateWhiteNumber(String number) {
	val entity = PhoneNumber.builder()
			 .number(number)
			 .isShared(false)
			 .isSynchronized(true)
			 .owner(PRIVATE_PHONE_NUMBER)
			 .build()
			 .asWhite();
	Single<Long> single = phoneNumberDao
				  .insert(entity);

    }

    @Override
    public void addWhiteNumber(IncomingPhoneNumber number, boolean isNew) {
	val entity = toEntity(number, isNew);
	Single<Long> single = phoneNumberDao
				  .insert(entity.asWhite());
    }

    @Override
    public void addBlackNumber(IncomingPhoneNumber number, boolean isNew) {
	val entity = toEntity(number, isNew);
	Single<Long> single = phoneNumberDao
				  .insert(entity.asBlack());
	if (isNew && number.hasAudio()) {
	    val id = single.blockingGet();
	    val recordingEntity = VoiceRecording.builder()
				      .phoneNumberId(id)
				      .rawData(number.getAudio())
				      .build();
	    Single<Long> single1 = voiceRecordingDao.insert(recordingEntity);
	}
    }

    @Override
    public boolean isWhiteNumber(String number) {
	return phoneNumberDao
		   .existsWhiteNumber(number)
		   .blockingGet();
    }

    @Override
    public boolean isBlackNumber(String number) {
	return phoneNumberDao
		   .existsBlackNumber(number)
		   .blockingGet();
    }

    @Override
    public List<IncomingPhoneNumber> getNewWhiteNumbers() {
	return phoneNumberDao
		   .notSynchronizedWhiteList()
		   .map(this::toDtoList)
		   .blockingGet();
    }

    @Override
    public List<IncomingPhoneNumber> getNewBlackNumbers() {
	return phoneNumberDao
		   .notSynchronizedBlackList()
		   .map(this::toDtoList)
		   .blockingGet();
    }

    private PhoneNumber toEntity(IncomingPhoneNumber dto, boolean isSynchronized) {
	return PhoneNumber.builder()
		   .isShared(true)
		   .isSynchronized(isSynchronized)
		   .number(dto.getNumber())
		   .owner(dto.getOwner())
		   .build();
    }

    private <T extends PhoneNumberProjection> IncomingPhoneNumber toDto(T entity) {
	return IncomingPhoneNumber.builder()
		   .number(entity.getNumber())
		   .owner(entity.getOwner())
		   .build();
    }

    private <T extends PhoneNumberProjection> List<IncomingPhoneNumber> toDtoList(List<T> list) {
	val dtoList = new ArrayList<IncomingPhoneNumber>();
	for (T projection : list) {
	    dtoList.add(toDto(projection));
	}
	return dtoList;
    }
}
