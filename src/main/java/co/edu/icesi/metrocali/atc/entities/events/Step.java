package co.edu.icesi.metrocali.atc.entities.events;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Step {

	private Integer id;
	
	private String description;

	private StepType stepType;

}