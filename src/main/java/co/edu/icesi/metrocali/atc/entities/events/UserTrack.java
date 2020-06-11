package co.edu.icesi.metrocali.atc.entities.events;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import co.edu.icesi.metrocali.atc.entities.policies.User;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserTrack {

	//Attributes -----------------------------------
	private Integer id;

	private Timestamp endTime;

	private Timestamp startTime;

	private User user;
	
	private State state;
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