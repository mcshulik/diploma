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
import io.reactivex.rxjava3.schedulers.Schedulers;
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
    public Single<?> addBlackNumber(
	LocalPhoneNumber number,
	Maybe<LocalRecognitionResult> maybeRecognition
    ) {
	PhoneNumber entity = toEntity(number);
	Single<Long> numberSingle = phoneNumberDao
					.insert(entity.asBlack())
					.subscribeOn(Schedulers.io());
	if (number.isSynchronized()) {
	    return numberSingle;
	}
	return numberSingle
		   .flatMap(entityId -> {
		       val result = maybeRecognition.blockingGet();
		       if (result == null) {
			   return Single.just(42);
		       }
		       val voiceEntity = toVoiceEntity(entityId, result);
		       return voiceRecordingDao
				  .insert(voiceEntity);
		   });
    }

    @Override
    public void synchronizeNumbers(List<LocalPhoneNumber> numbers) {
	for (LocalPhoneNumber number : numbers) {
	    BlackNumber black = phoneNumberDao.findBlackNumber(number.getNumber())
				    .blockingGet();
	    if (black != null) {
		//number already present locally
		phoneNumberDao.syncBlackNumber(black.getId());
	    } else {
		val entity = PhoneNumber.builder()
				 .number(number.getNumber())
				 .owner(number.getOwner())
				 .isShared(true)
				 .isSynchronized(true)
				 .build()
				 .asBlack();
		Long l = phoneNumberDao.insert(entity).blockingGet();
	    }
	}
    }

    @Override
    public Single<Boolean> isWhiteNumber(String number) {
	return phoneNumberDao
		   .existsWhiteNumber(number);
    }

    @Override
    public Single<Boolean> isBlackNumber(String number) {
	return phoneNumberDao
		   .existsBlackNumber(number);
    }

    @Override
    public Maybe<LocalPhoneNumber> findBlackNumber(String number) {
	return phoneNumberDao
		   .findBlackNumber(number)
		   .map(this::toDto);
    }

    @Override
    public Flowable<Pair<LocalPhoneNumber, List<LocalRecognitionResult>>> notSyncBlackNumbers() {
	return phoneNumberDao
		   .notSynchronizedBlackList()
		   .subscribeOn(Schedulers.io())
		   .flatMap(blackNumber -> {
//		       val recordsFlow = voiceRecordingDao
//					     .allNotSynchronizedByBlackNumberId(blackNumber.getId())
//					     .<List<VoiceRecord>>collect(ArrayList::new, List::add)
//					     .map(this::toVoiceDtoList);

		       return Single.just(blackNumber)
				  .map(this::toDto)
				  .map(number -> {
				      List<LocalRecognitionResult> list = new ArrayList<>();
				      return Pair.create(number, list);
				  })
				  .toFlowable();
		   });
    }

    @Override
    public Single<List<LocalPhoneNumber>> allBlackNumbers() {
	return phoneNumberDao
		   .allBlackList()
		   .subscribeOn(Schedulers.io())
		   .map(phoneNumberMapper::toDtoList);
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
