package co.edu.icesi.metrocali.atc.entities.operators;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import co.edu.icesi.metrocali.atc.constants.StateValue;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ControllerWorkState implements Recoverable{

	@JsonIgnore
	private String accountName;
	
	@JsonIgnoreProperties({"category","eventsTracks","protocolsTracks"})
	private Map<StateValue, Map<String, Event>> events = new HashMap<>();
	
	public ControllerWorkState(String accountName) {
		this.accountName = accountName;
	}
	
	@Override
	@JsonIgnore
	public String getKeyEntity() {
		return accountName;
	}
	
	public void addEvent(StateValue state, Event event) {
		
		events.computeIfAbsent(state, 
			events -> new HashMap<String,Event>()
		).put(event.getCode(), event);

	}
	
	@JsonIgnore
	public Map<String, Event> getEventsByState(StateValue state) {
		return events.get(state);
	}
	
	@JsonIgnore
	public Event removeEvent(StateValue state, String eventCode) {
		return events.get(state).remove(eventCode);
	}

}
