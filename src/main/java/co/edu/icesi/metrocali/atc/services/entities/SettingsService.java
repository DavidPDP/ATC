package co.edu.icesi.metrocali.atc.services.entities;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.SettingKey;
import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.exceptions.bb.ExternalApiResponseException;
import co.edu.icesi.metrocali.atc.repositories.SettingsRepository;
import co.edu.icesi.metrocali.atc.services.realtime.RealtimeOperationStatus;

@Service
public class SettingsService {

	private SettingsRepository settingsRepository;
	
	private RealtimeOperationStatus realtimeOpStatus;
	
	public SettingsService(
			RealtimeOperationStatus realtimeOpStatus,
			SettingsRepository settingsRepository) {
		
		this.realtimeOpStatus = realtimeOpStatus;
		this.settingsRepository = settingsRepository;
		
	}
	
	public List<Setting> retrieveAll(boolean shallow) {
		
		List<Setting> settings = null;
		
		if(shallow) {
			settings = realtimeOpStatus.retrieveAllSettings();
		}else {
			settings = settingsRepository.retrieveAll();
		}
		
		if(settings == null || settings.isEmpty()) {
			throw new ATCRuntimeException("No settings found.",
				new NoSuchElementException());
		}
		
		return settings;
		
	}
	
	public Setting retrieve(@NonNull SettingKey key) {
		
		Optional<Setting> setting = 
				realtimeOpStatus.retrieveSetting(key);
		
		if(setting.isPresent()) {
			//Shallow strategy
			return setting.get();
		}else {
			//Deep strategy
			setting = settingsRepository.retrieve(key.name());
			
			if(setting.isPresent()) {
				return setting.get();
			}else {
				throw new ATCRuntimeException(
					key + "setting doesn't exist.", 
					new NoSuchElementException()
				);
			}
			
		}
		
	}

	public boolean isValidValue(@NonNull SettingKey key, 
			@NonNull String value) {
		
		boolean isValid = false;
		
		Setting setting = retrieve(key);
		String groupType = resolveGroup(setting.getType());
		
		if(groupType.equals("Interval")) {
			isValid = isValidInterval(setting.getValue(), value);
		}
		
		return isValid;
		
	}

	private String resolveGroup(@NonNull String type) {
		
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
			@NonNull String limit, @NonNull String value) {

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
	
	public void save(@NonNull Setting setting) {
		
		try {
			settingsRepository.save(setting);
		}catch (ExternalApiResponseException e) {
			throw new ATCRuntimeException(
				"could not save setting.", e);
		}
		
	}
	
	public void delete(@NonNull String key) {
		try {
			settingsRepository.delete(key);
		}catch (ExternalApiResponseException e) {
			throw new ATCRuntimeException(
				"could not delete setting.", e);
		}
	}
	
	
}
