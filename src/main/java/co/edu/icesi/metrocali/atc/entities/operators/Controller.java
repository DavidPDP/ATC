package co.edu.icesi.metrocali.atc.entities.operators;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;

import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import lombok.Getter;
import lombok.Setter;

@JsonRootName("user")
@Getter @Setter
public class Controller extends User implements Comparable<Controller>{
	
	private static final long serialVersionUID = -6288917867363076425L;

	@JsonIgnore
	private Event lastEvent;
	
	private Integer workLoad = 0;
	
	public void increaseWorkLoad(int newWorkLoad) {
		this.workLoad += newWorkLoad;
	}
	
	public void decreaseWorkLoad(int workLoadResolved) {
		this.workLoad -= workLoadResolved;
	}

	@Override
	public int compareTo(Controller o) {
		return this.workLoad.compareTo(o.workLoad);
	}
	
}
