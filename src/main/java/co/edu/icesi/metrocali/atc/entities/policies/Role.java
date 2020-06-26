package co.edu.icesi.metrocali.atc.entities.policies;

import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Role implements Recoverable{
	
	//Attributes -----------------------------------
	private Integer id;

	private String name;
	//----------------------------------------------

	@Override
	public String getKeyEntity() {
		return this.name;
	}

}