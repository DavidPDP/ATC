package co.edu.icesi.metrocali.atc.entities.evaluator;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.EventTrack;


/**
 * EvalEvent
 */

public class EvalEvent {

    private Event event;
    
    public EvalEvent(Event event){
       this.event=event;
    }

    @JsonIgnore
	public EventTrack getLastEventTrack() {
		return event.getLastEventTrack();
	}
    @JsonIgnore
    public long getId() {
        return event.getId();
    }
    @JsonProperty(value = "Description")
    public String getDescription() {
        return event.getDescription();
    }
    @JsonProperty(value = "Source")
    public Long getSource() {
        return event.getSource();
    }
    @JsonProperty(value = "Sourcetype")
    public String getSourceType() {
        return event.getSourceType();
    }
    @JsonProperty(value = "Title")
    public String getTitle() {
        return event.getTitle();
    }
    @JsonIgnore
    public Category getCategory() {
        return event.getCategory();
    }
    @JsonProperty(value = "Category")
    public String getCategoryName(){
        return event.getCategory().getName();
    }
    @JsonIgnore
    public List<EventTrack> getEventsTracks() {
        return event.getEventsTracks();
    }

}