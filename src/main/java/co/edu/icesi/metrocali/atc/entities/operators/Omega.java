package co.edu.icesi.metrocali.atc.entities.operators;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;

import co.edu.icesi.metrocali.atc.entities.events.EventRemarks;
import co.edu.icesi.metrocali.atc.entities.events.UsersRemark;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import lombok.Getter;
import lombok.Setter;

@JsonRootName("user")
@Getter
@Setter
public class Omega extends User{
		
	private List<UsersRemark> userRemarks;
	
	private List<EventRemarks> eventRemarks;
	
}
