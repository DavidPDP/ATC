package co.edu.icesi.metrocali.atc.entities.events;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Event implements Comparable<Event>, Recoverable{
	
	//Constructors ---------------------------------
	public Event() {}
	
	public Event(String description, String title, Long source,
		String sourceType, Category category, 
		List<EventTrack> eventTracks) {
		
		this.description = description;
		this.title = title;
		this.source = source;
		this.sourceType = sourceType;
		this.category = category;
		this.eventsTracks = eventTracks;
		
	}
	//----------------------------------------------
	
	//Attributes -----------------------------------
	private Long id;
	
	private String code;

	private Timestamp creation;

	private String description;

	private Long source; 
	
	private String sourceType;

	private String title;

	private Category category;
	//----------------------------------------------
	
	//Interface implementation ---------------------
	@Override
	@JsonIgnore
	public String getKeyEntity() {
		return this.code;
	}
	//----------------------------------------------
	
	//Aggregates -----------------------------------
	private List<EventTrack> eventsTracks = new ArrayList<>();
	
	private List<ProtocolTrack> protocolsTracks = new ArrayList<>();
	//----------------------------------------------
	
	//Aggregates methods ---------------------------
	public void addEventTrack(EventTrack eventTrack) {
		this.eventsTracks.add(eventTrack);
	}
	
	public void addProtocolTrack(ProtocolTrack protocolTrack) {
		this.protocolsTracks.add(protocolTrack);
	}
	//----------------------------------------------
	
	//Business methods -----------------------------
	@JsonIgnore
	public EventTrack getLastEventTrack() {
		return this.eventsTracks.get(this.eventsTracks.size() - 1);
	}
	
	@JsonIgnore
	public int getLastPriority() {
		return getLastEventTrack().getPriority();
	}
	
	@Override
	@JsonIgnore
	public int compareTo(Event o) {
		return this.getLastPriority() - o.getLastPriority();
	}
	
	@Override
	@JsonIgnore
	public String toString() {
		
		StringBuilder format = new StringBuilder();
		
		format.append("[");
		format.append("id: " + this.id + " ");
		format.append("code: " + this.code + " ");
		format.append("creation: " + this.creation + " ");
		format.append("description: " + this.creation + " ");
		format.append("source: " + this.source + ", ");
		format.append("sourceType: " + this.sourceType + " ");
		format.append("title: " + this.title + " ");
		format.append("category: " + this.category.getName() + " ");
		format.append("lastState: " + this.getLastEventTrack().getState().getName() + " ");
		format.append("lastUser: " + this.getLastEventTrack().getUser().getAccountName());
		format.append("]");
		
		return format.toString();
	
	}
	//----------------------------------------------
	
}