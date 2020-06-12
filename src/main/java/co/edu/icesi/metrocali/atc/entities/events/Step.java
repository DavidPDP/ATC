package co.edu.icesi.metrocali.atc.entities.events;

import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Step implements Recoverable {

	//Attributes -----------------------------------
	private Integer id;
	
	private String code;
	
	private String description;

	private StepType stepType;
	//----------------------------------------------

	@Override
	public String getKeyEntity() {
		return this.code;
	}

}