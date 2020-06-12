package co.edu.icesi.metrocali.atc.services.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.RecoveryPrecedence;
import co.edu.icesi.metrocali.atc.entities.events.Step;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.repositories.StepsRepository;
import co.edu.icesi.metrocali.atc.services.realtime.RealtimeOperationStatus;
import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import co.edu.icesi.metrocali.atc.services.recovery.RecoveryService;

@Service
public class StepsService implements RecoveryService {

	private StepsRepository stepsRepository;
	
	private RealtimeOperationStatus realtimeOperationStatus;
	
	public StepsService(StepsRepository stepsRepository,
			RealtimeOperationStatus realtimeOperationStatus) {
		
		this.stepsRepository = stepsRepository;
		this.realtimeOperationStatus = realtimeOperationStatus;
		
	}
	
	@Override
	public Class<? extends Recoverable> getType() {
		return Step.class;
	}

	@Override
	public RecoveryPrecedence getRecoveryPrecedence() {
		return RecoveryPrecedence.Second;
	}

	@Override
	public List<Recoverable> recoveryEntities() {
		
		List<Step> steps = 
			stepsRepository.retrieveAll();
		
		return new ArrayList<Recoverable>(steps);
		
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
			realtimeOperationStatus.updateSteps(states);
		}
		
		if(states.isEmpty()) {
			throw new ATCRuntimeException("No steps found.", 
					new NoSuchElementException()
			);
		}
		
		return states;
		
	}
	
	public Step retrieve(String code) {
		
		Step step = null;
		
		Optional<Step> shallowState = 
			realtimeOperationStatus.retrieveStep(code);
		
		if(shallowState.isPresent()) {
			//Shallow copy
			step = shallowState.get();
		}else {
			//Deep copy
			step =	stepsRepository.retrieve(code);
			
			//Update operation status
			realtimeOperationStatus.addOrUpdateStep(step);
		
		}
		
		return step;
		
	}
	
	public void save(Step step) {
		
		Step persistedStep = 
				stepsRepository.save(step);
		
		//Update operation state
		realtimeOperationStatus
			.addOrUpdateStep(persistedStep);
		
	}
	
	public void delete(String code) {
		
		//Update operation state
		realtimeOperationStatus.removeStep(code);
		
		stepsRepository.delete(code);
		
	}
	
}
