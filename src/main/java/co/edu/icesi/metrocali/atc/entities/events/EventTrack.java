package co.edu.icesi.metrocali.atc.entities.events;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import co.edu.icesi.metrocali.atc.entities.policies.User;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EventTrack {

	//Constructors ---------------------------------
	public EventTrack() {}
	
	public EventTrack(Integer priority, User user,
			State state) {
		
		this.priority = priority;
		this.user = user;
		this.state = state;
		
	}
	//----------------------------------------------
	
	//Attributes -----------------------------------
	private Long id;
	
	private String code;

	private Timestamp startTime;
	
	private Timestamp endTime;

	private Integer priority;
	
	private User user;
	
	private State state;
	//----------------------------------------------
	
	//Aggregates -----------------------------------
	private List<EventRemark> eventsRemarks = new ArrayList<>();
	//----------------------------------------------
	
	//Aggregates methods ---------------------------
	public void addEventRemark(EventRemark eventRemark) {
		System.out.println("HOLA");
		this.eventsRemarks.add(eventRemark);
	}
	//----------------------------------------------
	
}