package co.edu.icesi.metrocali.atc.entities.events;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Step {

	//Attributes -----------------------------------
	private Integer id;
	
	private String description;

	private StepType stepType;
	//----------------------------------------------

}