package co.edu.icesi.metrocali.atc.entities.events;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class EventSource {

	@JsonProperty("source")
	private Long id;
	
	@NonNull
	@JsonIgnore
	private String value;
	
	@NonNull
	@JsonProperty("source_type")
	private String sourceType;
	
}
