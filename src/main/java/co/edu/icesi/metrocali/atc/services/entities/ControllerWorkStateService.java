package co.edu.icesi.metrocali.atc.services.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.RecoveryPrecedence;
import co.edu.icesi.metrocali.atc.constants.StateValue;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.EventTrack;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.operators.ControllerWorkState;
import co.edu.icesi.metrocali.atc.exceptions.bb.ResourceNotFound;
import co.edu.icesi.metrocali.atc.services.realtime.RealtimeOperationStatus;
import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import co.edu.icesi.metrocali.atc.services.recovery.RecoveryService;

@Service
public class ControllerWorkStateService implements RecoveryService{

	private RealtimeOperationStatus realtimeOperationStatus;
	
	public ControllerWorkStateService(
		RealtimeOperationStatus realtimeOperationStatus) {
		
		this.realtimeOperationStatus = 
			realtimeOperationStatus;
		
	}
	
	@Override
	public Class<? extends Recoverable> getType() {
		return ControllerWorkState.class;
	}

	@Override
	public RecoveryPrecedence getRecoveryPrecedence() {
		return RecoveryPrecedence.Third;
	}

	@Override
	public List<Recoverable> recoveryEntities() {
		
		List<ControllerWorkState> controllersWorkState = 
			new ArrayList<>();
		
		List<Controller> controllers = 
			realtimeOperationStatus.retrieveAll(Controller.class);
		
		List<Event> events = 
			realtimeOperationStatus.retrieveAll(Event.class);
		
		for (Controller controller : controllers) {
				
			ControllerWorkState workState = 
				new ControllerWorkState(
					controller.getAccountName()
				);
			
			realtimeOperationStatus.store(
				ControllerWorkState.class, workState);
			
		}
		
		for (Event event : events) {
			
			List<EventTrack> eventTracks = 
				event.getEventsTracks();
			
			for (EventTrack eventTrack : eventTracks) {
				
				for (ControllerWorkState workState : 
					controllersWorkState) {
					
					if(eventTrack.getUser()
						.getAccountName().equals(
							workState.getAccountName())) {
						
						
						workState.addEvent(
							StateValue.valueOf(eventTrack.getState().getName()), 
							event
						);
						
					}
					
				}
				
			}
			
		}
		
		return new ArrayList<Recoverable>(controllersWorkState);
		
	}
	
	public ControllerWorkState retrieve(String accountName) {
		
		ControllerWorkState workState = null;
		
		Optional<ControllerWorkState> shallowWorkState =
			realtimeOperationStatus.retrieve(
				ControllerWorkState.class, accountName);
		
		if(shallowWorkState.isPresent()) {
			//Shallow load
			workState = shallowWorkState.get();
		}else {
			throw new ResourceNotFound("aaa");
		}
		
		return workState;
		
	}
	
	public boolean hasEventsInProcess(String accountName) {
		
		Optional<ControllerWorkState> workState = 
			realtimeOperationStatus.retrieve(
				ControllerWorkState.class, accountName
			);
		
		if(workState.isPresent()) {
			
			Map<String, Event> events = workState.get().getEventsByState(
				StateValue.Processing
			);
			
			int eventsInProcess = events != null ? events.size() : 0;
				
			return eventsInProcess > 0 ? false : true;
			
		}else {
			throw new ResourceNotFound("asdas");
		}
		
	}
	
	public void create(ControllerWorkState workState) {
		
		realtimeOperationStatus.store(
			ControllerWorkState.class, workState);
		
	}
	
	public void addToState(String accountName, 
		Event event, StateValue state) {
		
		Optional<ControllerWorkState> workState = 
			realtimeOperationStatus.retrieve(
				ControllerWorkState.class, accountName
			);
		
		if(workState.isPresent()) {
			workState.get().addEvent(state, event);
		}else {
			throw new ResourceNotFound("asd");
		}
		
	}
	
	public void updateState(String accountName, String eventCode, 
		StateValue currentState, StateValue nextState) {
		
		Optional<ControllerWorkState> workState = 
			realtimeOperationStatus.retrieve(
				ControllerWorkState.class, accountName
			);
		
		if(workState.isPresent()) {
			
			//remove from old state
			Event event = 
				workState.get().removeEvent(currentState, eventCode);
			
			//add to new state
			workState.get().addEvent(nextState, event);
			
		}else {
			throw new ResourceNotFound("asd");
		}
		
	}
	
	public Event removeState(String accountName, String eventCode, 
		StateValue state) {
		
		Optional<ControllerWorkState> workState = 
			realtimeOperationStatus.retrieve(
				ControllerWorkState.class, accountName
			);
		
		if(workState.isPresent()) {
			return workState.get().removeEvent(state, eventCode);
		}else {
			throw new ResourceNotFound("asd");
		}
		
	}
	
	public void delete(String accountName) {
		
		realtimeOperationStatus.remove(
			ControllerWorkState.class, accountName);
		
	}

}
