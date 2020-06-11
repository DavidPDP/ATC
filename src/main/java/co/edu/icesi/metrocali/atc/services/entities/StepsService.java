package co.edu.icesi.metrocali.atc.services.entities;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.entities.events.Step;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.repositories.StepsRepository;
import co.edu.icesi.metrocali.atc.services.realtime.RealtimeOperationStatus;

@Service
public class StepsService {

	private StepsRepository stepsRepository;
	
	private RealtimeOperationStatus realtimeOperationStatus;
	
	public StepsService(StepsRepository stepsRepository,
			RealtimeOperationStatus realtimeOperationStatus) {
		
		this.stepsRepository = stepsRepository;
		this.realtimeOperationStatus = realtimeOperationStatus;
		
	}
	
	public List<Step> retrieveAll(boolean shallow) {
		
		List<Step> states = Collections.emptyList();
		
		if(shallow) {
			//Shallow copy
			states = realtimeOperationStatus.retrieveAllSteps();
		}else {
			//Deep copy
			states = stepsRepository.retrieveAll();
			
			//Update operation status
			//realtimeOperationStatus.updateStates(states);
		}
		
		if(states.isEmpty()) {
			throw new ATCRuntimeException("No steps found.", 
					new NoSuchElementException()
			);
		}
		
		return states;
		
	}
	
	public Step retrieve(String description) {
		
		Step state = null;
		
		Optional<Step> shallowState = null;
			//realtimeOperationStatus.retrieveStep(description);
		
		if(shallowState.isPresent()) {
			//Shallow copy
			state = shallowState.get();
		}else {
			//Deep copy
			state =	stepsRepository.retrieve(description);
			
			//Update operation status
			//realtimeOperationStatus.addOrUpdateState(state);
		
		}
		
		return state;
		
	}
	
}
