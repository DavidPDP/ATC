package co.edu.icesi.metrocali.atc.entities.events;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class EventTrack {

	private Long id;
	
	private String code;

	@JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
	private Timestamp startTime;
	
	private Timestamp endTime;

	@NonNull
	private Integer priority;
	
	private List<EventRemarks> eventsRemarks = new ArrayList<>();
	
	@NonNull
	private Controller user;
	
	@NonNull
	private State state;
	
	public void addEventRemark(EventRemarks eventRemark) {
		System.out.println(eventRemark);
		this.eventsRemarks.add(eventRemark);
	}
	
	@Override
	public String toString() {
		String state = this.getState() != null ? this.getState().getName() : "";
		return "id: " + this.id + " - code: " + this.code 
				+ " - startTime: " + this.startTime + " - endTime: " 
				+ this.endTime + " - state: " + state;
	}
	
}