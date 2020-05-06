package co.edu.icesi.metrocali.atc.entities.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class Protocol {

	private Integer id;
	
	@JsonProperty("step_order")
	private Integer stepOrder;
	
	private Step step;

}