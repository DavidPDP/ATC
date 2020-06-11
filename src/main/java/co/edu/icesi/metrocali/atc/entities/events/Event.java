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
	
	@Override
	public String toString() {
		
		String text = "";
		
		for (EventTrack eventTrack : eventsTracks) {
			text += "[id: " + eventTrack.getId() + " code: " + 
				eventTrack.getCode() + "]";
		}
		
		return text;
	
//		"[id: " + id + " code: " + code + " creation: " 
//		+ creation + " description: " + description + " source: "
//		+ source + " sourceType: " + sourceType + " title: " 
//		+ title + " category: " + category.getName() + "]";		
	}
	
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
	
	@Override
	public String getKeyEntity() {
		return this.code;
	}
	
	//Aggregates -----------------------------------
	private List<EventTrack> eventsTracks = new ArrayList<>();
	
	private List<ProtocolTrack> protocolTracks = new ArrayList<>();
	//----------------------------------------------
	
	//Aggregates methods ---------------------------
	public void addEventTrack(EventTrack eventTrack) {
		this.eventsTracks.add(eventTrack);
	}
	
	public void addProtocolTrack(ProtocolTrack protocolTrack) {
		this.protocolTracks.add(protocolTrack);
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
	public int compareTo(Event o) {
		return this.getLastPriority() - o.getLastPriority();
	}
	//----------------------------------------------
	
}