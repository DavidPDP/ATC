package co.edu.icesi.metrocali.atc.entities.events;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import co.edu.icesi.metrocali.atc.entities.policies.Role;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserTrack {

	//Constructors ---------------------------------
	public UserTrack() {}
	
	public UserTrack(User user, State state, Role role) {
		
		this.user = user;
		this.state = state;
		this.role = role;
		
	}
	//----------------------------------------------
	
	//Attributes -----------------------------------
	private Integer id;

	private Timestamp endTime;

	private Timestamp startTime;
	
	private User user;
	
	private State state;
	
	private Role role;
	//----------------------------------------------

	//Aggregates -----------------------------------
	private List<UserRemark> usersRemarks = new ArrayList<>();
	//----------------------------------------------
	
	//Aggregates methods ---------------------------
	public void addUsersRemark(UserRemark userRemark) {
		this.usersRemarks.add(userRemark);
	}
	//----------------------------------------------

}