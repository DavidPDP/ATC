package co.edu.icesi.metrocali.atc.services.oprealtime;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.OperatorTypes;
import co.edu.icesi.metrocali.atc.services.entities.CategoriesService;
import co.edu.icesi.metrocali.atc.services.entities.EventsService;
import co.edu.icesi.metrocali.atc.services.entities.OperatorsService;

@Service
public class RecoveryPoint {
	
	private EventsService eventsService;
	
	private CategoriesService categoriesService;
	
	private OperatorsService operatorsService;
	
	private LocalRealtimeOperationStatus realtimeStatus;
	
	@Autowired
	public RecoveryPoint(EventsService eventsService,
			LocalRealtimeOperationStatus realtimeStatus,
			CategoriesService categoriesService,
			OperatorsService operatorsService) {
		
		this.eventsService = eventsService;
		this.realtimeStatus = realtimeStatus;
		this.categoriesService = categoriesService;
		this.operatorsService = operatorsService;
		
	}
	
	public void recoveryRealTimeStatus(){
		
		realtimeStatus.recoverypoint(
			eventsService.retrieveAllStates(),
			categoriesService.retrieveAllCategories(false),
			null//operatorsService.retrieveAllSettings()
		);
		
	}
	
}
