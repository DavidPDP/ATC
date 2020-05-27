package co.edu.icesi.metrocali.atc.services.recovery;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.Settings;
import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.services.entities.EventsService;
import co.edu.icesi.metrocali.atc.services.entities.OperatorsService;
import co.edu.icesi.metrocali.atc.services.entities.SettingsService;
import co.edu.icesi.metrocali.atc.services.realtime.LocalRealtimeOperationStatus;

@Service
public class RecoveryManager {
	
	private EventsService eventsService;
	
	private LocalRealtimeOperationStatus realtimeStatus;
	
	private OperatorsService operatorsService;
	
	private SettingsService settingsService;
	
	@Autowired
	public RecoveryManager(EventsService eventsService,
			LocalRealtimeOperationStatus realtimeStatus,
			OperatorsService operatorsService,
			SettingsService settingsService) {
		
		this.eventsService = eventsService;
		this.realtimeStatus = realtimeStatus;
		this.operatorsService = operatorsService;
		this.settingsService = settingsService;
		
	}
	
	public void loadSystemSettings() {
		realtimeStatus.updateSettings(
			settingsService.retrieveAllSettings()
		);
	}
	
	public void recoveryRealTimeStatus(){
		
		Optional<Setting> recoverTime = 
			realtimeStatus.retrieveSetting(Settings.Recover_Time);
		
		if(recoverTime.isPresent()) {
			
			HashMap<String, List<? extends Recoverable>> entities = 
				new HashMap<>();
			
			entities.put("states", eventsService.retrieveAllStates());
			
			entities.put("categories", 
				eventsService.retrieveAllCategories(false));
			
			entities.put("controllers", 
				operatorsService.retrieveOnlineControllers());
			
			entities.put("events",
				eventsService.retrieveLastEvents(
					(String) recoverTime.get().getInstanceValue()
				)
			);
			
			realtimeStatus.recoverypoint(entities);
			realtimeStatus.print();
		}else {
			throw new ATCRuntimeException("the setting Recover_Time"
					+ " was not found");
		}		
		
	}
	
}
