package co.edu.icesi.metrocali.atc.entities.operators;

import com.fasterxml.jackson.annotation.JsonRootName;

import co.edu.icesi.metrocali.atc.entities.policies.User;
import lombok.Getter;
import lombok.Setter;

@JsonRootName("user")
@Getter @Setter
public class Omega extends User{
		
	private static final long serialVersionUID = -1978628684483032461L;
	
}
