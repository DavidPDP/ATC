package co.edu.icesi.metrocali.atc.entities.events;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Event implements Comparable<Event>{
	
	private Long id;
	
	private String code;

	private Timestamp creation;

	@NonNull
	private String description;

	@JsonUnwrapped
	@NonNull
	private EventSource eventSource;

	@NonNull
	private String title;

	@NonNull
	private Category category;

	@NonNull
	@JsonProperty("event_tracks")
	private List<EventTrack> eventsTracks;
	
	@JsonProperty("protocol_tracks")
	private List<ProtocolTrack> protocolTracks = new ArrayList<>();
	
	public void addEventTrack(@NonNull EventTrack eventTrack) {
		this.eventsTracks.add(eventTrack);
	}
	
	public void addProtocolTrack(@NonNull ProtocolTrack protocolTrack) {
		this.protocolTracks.add(protocolTrack);
	}
	
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
}