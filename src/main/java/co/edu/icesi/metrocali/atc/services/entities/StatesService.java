package co.edu.icesi.metrocali.atc.services.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.RecoveryPrecedence;
import co.edu.icesi.metrocali.atc.constants.StateValue;
import co.edu.icesi.metrocali.atc.entities.events.State;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.exceptions.InvalidStateException;
import co.edu.icesi.metrocali.atc.repositories.StatesRepository;
import co.edu.icesi.metrocali.atc.services.realtime.RealtimeOperationStatus;
import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import co.edu.icesi.metrocali.atc.services.recovery.RecoveryService;

@Service
public class StatesService implements RecoveryService {

	private StatesRepository statesRepository;
	
	private RealtimeOperationStatus realtimeOperationStatus;
	
	public StatesService(StatesRepository statesRepository,
			RealtimeOperationStatus realtimeOperationStatus) {
		
		this.statesRepository = statesRepository;
		this.realtimeOperationStatus = realtimeOperationStatus;
		
	}
	
	@Override
	public Class<? extends Recoverable> getType(){
		return State.class;
	}
	
	@Override
	public RecoveryPrecedence getRecoveryPrecedence() {
		return RecoveryPrecedence.Second;
	}

	@Override
	public List<Recoverable> recoveryEntities() {
		
		List<State> states = statesRepository.retrieveAll();
		
		return new ArrayList<Recoverable>(states);
		
	}
	
	
	public List<State> retrieveAll(boolean shallow) {
		
		List<State> states = Collections.emptyList();
		
		if(shallow) {
			//Shallow copy
			states = 
				realtimeOperationStatus.retrieveAll(State.class);
		}else {
			//Deep copy
			states = statesRepository.retrieveAll();
			
			//Update operation status
			realtimeOperationStatus.store(State.class, states);
		}
		
		if(states.isEmpty()) {
			throw new ATCRuntimeException("No states found.", 
					new NoSuchElementException()
			);
		}
		
		return states;
		
	}
	
	public State retrieve(StateValue stateValue) {
		
		State state = null;
		
		Optional<State> shallowState = 
			realtimeOperationStatus.retrieve(
				State.class, stateValue.name()
			);
		
		if(shallowState.isPresent()) {
			//Shallow copy
			state = shallowState.get();
		}else {
			//Deep copy
			state =	statesRepository.retrieve(stateValue.name());
			
			//Update operation status
			realtimeOperationStatus.store(State.class, state);
		
		}
		
		return state;
		
	}
	
	public void verifyNextState(State current, StateValue nextValue) {
		
		List<State> validStates = current.getNextStates();
		
		if(!canGoNextState(validStates, nextValue)) {
			
			String exceptionMessage = "";
			String validStatesNames = "";
			
			validStates.forEach(
				vs -> validStatesNames.concat(vs.getName() + " "));
			
			if(validStatesNames.isEmpty()) {
				exceptionMessage = "no valid next status found. "
					+ "This may mean that there is no rule "
					+ "mapping for this current state. "
					+ "Contact system admin.";
			}else {
				exceptionMessage = 
					"It is not possible to go to the next state. "
					+ "From the current state (" 
					+ current.getName() + ") it can only "
					+ "change to: " + validStatesNames 
					+ " states.";
			}
			
			throw new InvalidStateException(exceptionMessage);
			
		}
		
	}
	
	private boolean canGoNextState(List<State> possibleNextStates, 
			StateValue nextState) {
		
		boolean isValidNextState = false;
		
		for (State state : possibleNextStates) {
			if(nextState.name().equals(state.getName())) {
				isValidNextState = true;
				break;
			}
		}
		
		return isValidNextState;
		
	}
	
}
