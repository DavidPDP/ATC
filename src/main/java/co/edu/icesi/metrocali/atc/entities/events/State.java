package co.edu.icesi.metrocali.atc.entities.events;

import java.util.ArrayList;
import java.util.List;

import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class State implements Recoverable{

	//Attributes -----------------------------------
	private Integer id;
	
	private String name;
	
	private StateType stateType;
	//----------------------------------------------
	
	@Override
	public String getKeyEntity() {
		return this.name;
	}
	
	//Aggregates -----------------------------------
	private List<State> nextStates = new ArrayList<>();
	//----------------------------------------------
	
	//Aggregates methods ---------------------------
	public void addProtocol(State state) {
		this.nextStates.add(state);
	}
	//----------------------------------------------
	
}
