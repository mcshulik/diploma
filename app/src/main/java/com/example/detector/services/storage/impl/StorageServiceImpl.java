package com.example.detector.services.storage.impl;

import android.util.Pair;
import com.example.detector.services.LocalPhoneNumber;
import com.example.detector.services.LocalRecognitionResult;
import com.example.detector.services.storage.PhoneNumberDao;
import com.example.detector.services.storage.StorageService;
import com.example.detector.services.storage.VoiceRecordDao;
import com.example.detector.services.storage.mappers.PhoneNumberMapper;
import com.example.detector.services.storage.mappers.VoiceRecordMapper;
import com.example.detector.services.storage.model.BlackNumber;
import com.example.detector.services.storage.model.PhoneNumber;
import com.example.detector.services.storage.model.VoiceRecord;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.val;

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
    private final VoiceRecordDao voiceRecordingDao;
    private final VoiceRecordMapper voiceRecordMapper;
    private final PhoneNumberMapper phoneNumberMapper;

    @Override
    public void addWhiteNumber(String number) {
	val entity = PhoneNumber.builder()
			 .number(number)
			 .isShared(false)
			 .isSynchronized(true)
			 .owner(PRIVATE_PHONE_NUMBER)
			 .build()
			 .asWhite();
	Long id = phoneNumberDao
		      .insert(entity)
		      .blockingGet();
    }

    @Override
    public void addBlackNumber(
	LocalPhoneNumber number,
	Maybe<LocalRecognitionResult> maybeRecognition
    ) {
	PhoneNumber entity = toEntity(number);
	Single<Long> numberSingle = phoneNumberDao
					.insert(entity.asBlack());
	if (number.isSynchronized()) {
	    val _id = numberSingle.blockingGet();
	    return;
	}
	val _id = numberSingle
		      .zipWith(
			  maybeRecognition.toSingle(),
			  (numberId, recognition) -> {
			      val voiceEntity = toVoiceEntity(numberId, recognition);
			      return voiceRecordingDao.insert(voiceEntity);
			  }).blockingGet();

    }

    @Override
    public Single<Boolean> isWhiteNumber(String number) {
	return phoneNumberDao
		   .existsWhiteNumber(number);
    }

    @Override
    public Maybe<LocalPhoneNumber> findBlackNumber(String number) {
	return phoneNumberDao
		   .findBlackNumber(number)
		   .map(this::toDto)
		   .toMaybe();
    }

    @Override
    public Flowable<Pair<LocalPhoneNumber, List<LocalRecognitionResult>>> findBlackNumbers() {
	return phoneNumberDao
		   .notSynchronizedBlackList()
		   .flatMap(blackNumber -> {
		       val recordsFlow = voiceRecordingDao
					     .allNotSynchronizedByBlackNumberId(blackNumber.getId())
					     .<List<VoiceRecord>>collect(ArrayList::new, List::add)
					     .map(this::toVoiceDtoList);
		       return Single.just(blackNumber)
				  .map(this::toDto)
				  .zipWith(recordsFlow, Pair::create)
				  .toFlowable();
		   });
    }

    private PhoneNumber toEntity(LocalPhoneNumber dto) {
	return phoneNumberMapper.toEntity(dto);
    }

    private LocalPhoneNumber toDto(BlackNumber entity) {
	return phoneNumberMapper.toDto(entity);
    }

    private VoiceRecord toVoiceEntity(long id, LocalRecognitionResult dto) {
	return voiceRecordMapper.toEntity(id, dto);
    }

    private List<LocalRecognitionResult> toVoiceDtoList(List<VoiceRecord> entities) {
	return voiceRecordMapper.toDtoList(entities);
    }
}
