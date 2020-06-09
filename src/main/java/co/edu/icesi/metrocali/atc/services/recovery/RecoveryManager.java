package co.edu.icesi.metrocali.atc.services.recovery;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.SettingKey;
import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.repositories.SettingsRepository;
import co.edu.icesi.metrocali.atc.services.entities.EventsService;
import co.edu.icesi.metrocali.atc.services.entities.OperatorsService;
import co.edu.icesi.metrocali.atc.services.realtime.LocalRealtimeOperationStatus;

@Service
public class RecoveryManager {
	
	private EventsService eventsService;
	
	private LocalRealtimeOperationStatus realtimeStatus;
	
	private OperatorsService operatorsService;
	
	private SettingsRepository settingsService;
	
	private List<RecoveryPoint> recoveryPoints;
	
	@Autowired
	public RecoveryManager(EventsService eventsService,
			LocalRealtimeOperationStatus realtimeStatus,
			OperatorsService operatorsService,
			SettingsRepository settingsService,
			List<RecoveryPoint> recoveryPoints) {
		
		this.eventsService = eventsService;
		this.realtimeStatus = realtimeStatus;
		this.operatorsService = operatorsService;
		this.settingsService = settingsService;
		this.recoveryPoints = recoveryPoints;
		
	}
	
	private List<RecoveryPoint> retrieveRecoveryPoints(){
		return recoveryPoints;
	}
	
	public void loadSystemSettings() {
		realtimeStatus.updateSettings(
			settingsService.retrieveAll()
		);
	}
	
	public void recoveryRealTimeStatus(){
		
		Optional<Setting> recoverTime = 
			realtimeStatus.retrieveSetting(SettingKey.Recover_Time);
		
		if(recoverTime.isPresent()) {
			
			//init recoverable entities -----------------
			//TODO should check if they want to make everything dynamic.
			HashMap<String, List<? extends Recoverable>> entities = 
				new HashMap<>();
			
			entities.put("states", eventsService.retrieveAllStates());
			
			entities.put("categories", 
				eventsService.retrieveAllCategories(false));
			
			entities.put("controllers", 
				operatorsService.retrieveOnlineControllers());
			
			entities.put("events",
				eventsService.retrieveLastEvents(
					(String) recoverTime.get().getValue()
				)
			);
			//-------------------------------------------
			
			//Run recovery points -----------------------
			List<RecoveryPoint> recoveryPoints = 
					retrieveRecoveryPoints();
			
			for (RecoveryPoint recoveryPoint : recoveryPoints) {
				recoveryPoint.recoverypoint(entities);
			}
			//-------------------------------------------
			
		}else {
			throw new ATCRuntimeException("the setting Recover_Time"
					+ " was not found.");
		}		
		
	}
	
}
