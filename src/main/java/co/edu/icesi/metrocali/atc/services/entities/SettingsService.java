package co.edu.icesi.metrocali.atc.services.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.RecoveryPrecedence;
import co.edu.icesi.metrocali.atc.constants.SettingKey;
import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.repositories.SettingsRepository;
import co.edu.icesi.metrocali.atc.services.realtime.RealtimeOperationStatus;
import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import co.edu.icesi.metrocali.atc.services.recovery.RecoveryService;

@Service
public class SettingsService implements RecoveryService {

	private SettingsRepository settingsRepository;
	
	private RealtimeOperationStatus realtimeOperationStatus;
	
	public SettingsService(
			RealtimeOperationStatus realtimeOperationStatus,
			SettingsRepository settingsRepository) {
		
		this.realtimeOperationStatus = realtimeOperationStatus;
		this.settingsRepository = settingsRepository;
		
	}
	
	@Override
	public Class<? extends Recoverable> getType(){
		return Setting.class;
	}
	
	@Override
	public RecoveryPrecedence getRecoveryPrecedence() {
		return RecoveryPrecedence.First;
	}

	@Override
	public List<Recoverable> recoveryEntities() {
		
		List<Setting> settings = 
			settingsRepository.retrieveAll();
		
		return new ArrayList<Recoverable>(settings);
		
	}
	
	//CRUD -----------------------------------------
	public List<Setting> retrieveAll(boolean shallow) {
		
		List<Setting> settings = Collections.emptyList();
		
		if(shallow) {
			settings = 
				realtimeOperationStatus.retrieveAll(Setting.class);
		}else {
			settings = settingsRepository.retrieveAll();
		}
		
		if(settings.isEmpty()) {
			
			throw new ATCRuntimeException("No settings found.",
				new NoSuchElementException()
			);
			
		}
		
		return settings;
		
	}
	
	public Setting retrieve(SettingKey key) {
		
		Optional<Setting> setting = 
			realtimeOperationStatus.retrieve(
				Setting.class, key.name()
			);
		
		if(setting.isPresent()) {
			//Shallow strategy
			return setting.get();
		}else {
			//Deep strategy
			return settingsRepository.retrieve(key.name());			
		}
		
	}
	
	public void save(Setting setting) {
		
		Setting persistedSetting = 
				settingsRepository.save(setting);
		
		//Update operation status
		realtimeOperationStatus.store(
				Setting.class, persistedSetting);
		
	}
	
	public void delete(String key) {

		//Update operation status
		realtimeOperationStatus.remove(Setting.class, key);
		
		settingsRepository.delete(key);

	}
	//----------------------------------------------
	
	//Business methods -----------------------------
	public boolean isValidValue(SettingKey key, 
			@NonNull String value) {
		
		boolean isValid = false;
		
		Setting setting = retrieve(key);
		String groupType = resolveGroup(setting.getType());
		
		if(groupType.equals("Interval")) {
			isValid = isValidInterval(setting.getValue(), value);
		}
		
		return isValid;
		
	}

	private String resolveGroup(String type) {
		
		String group = "";
		
		switch (type) {
		case "Timestamp": case "Date":
			group = "Interval";
			break;
		case "Integer": case "Float":
			group = "Numeric";
		default:
			throw new ATCRuntimeException(
				type + " setting type is not supported.", 
				new NoSuchElementException());
		}
		
		return group;
	}
	
	private boolean isValidInterval(
			String limit, String value) {

		float valueMin = convertIntervalInMinutes(value);
		float limitMin = convertIntervalInMinutes(limit);

		return valueMin < limitMin ? true : false;

	}

	private float convertIntervalInMinutes(@NonNull String value) {

		float intervalMin = 0;

		Pattern pattern = Pattern.compile(
				"^([1-9]*)\\s(min|hour|hours|day|days)$");
		Matcher matcher = pattern.matcher(value);

		if (matcher.matches()) {

			int amountTime = Integer.parseInt(matcher.group(1));
			String unitTime = matcher.group(2);

			switch (unitTime) {
			case "min":
				intervalMin = amountTime;
			case "hour":
			case "hours":
				intervalMin = amountTime / 60;
			case "day":
			case "days":
				intervalMin = amountTime / 1440;
			}

			return intervalMin;

		} else {
			throw new ATCRuntimeException(
				"The input value does not satisfy with "
				+ "the allowed interval format.",
				new IllegalArgumentException(
					"format: ^([1-9]*)\\\\s(min|hour|"
					+ "hours|day|days)$")
				);
		}

	}
	//----------------------------------------------

}
