package co.edu.icesi.metrocali.atc.entities.operators;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;

import co.edu.icesi.metrocali.atc.entities.events.EventRemarks;
import co.edu.icesi.metrocali.atc.entities.events.EventTrack;
import co.edu.icesi.metrocali.atc.entities.events.UsersRemark;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import lombok.Getter;
import lombok.Setter;

@JsonRootName("user")
@Getter
@Setter
public class Controller extends User{
		
	private List<UsersRemark> userRemarks;
	
	private List<EventRemarks> eventRemarks;
	
	private List<EventTrack> eventTracks;
	
	@JsonIgnore
	private int workLoad;
	
	public void increaseWorkLoad(int newWorkLoad) {
		this.workLoad += newWorkLoad;
	}
	
	public void decreaseWorkLoad(int workLoadResolved) {
		this.workLoad -= workLoadResolved;
	}
	
}
