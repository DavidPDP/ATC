package co.edu.icesi.metrocali.atc.entities.events;

import java.sql.Timestamp;

import co.edu.icesi.metrocali.atc.entities.policies.User;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EventRemark {

	//Constructors ---------------------------------
	public EventRemark() {}
	
	public EventRemark(String content, User user) {
		
		this.content = content;
		this.user = user;
		
	}
	//----------------------------------------------
	
	//Attributes -----------------------------------
	private Long id;
	
	private String code;

	private String content;

	private Timestamp creation;

	private User user;
	//----------------------------------------------
	
}