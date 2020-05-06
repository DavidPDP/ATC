package co.edu.icesi.metrocali.atc.entities.events;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class Category {

	private Integer id;

	@JsonProperty("base_priority")
	private Integer basePriority;
	
	@NonNull
	private String name;
	
	private String parent;

	@JsonProperty("protocols")
	private List<Protocol> protocols;

}