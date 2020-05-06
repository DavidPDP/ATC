package co.edu.icesi.metrocali.atc.entities.events;

import java.sql.Timestamp;
import java.util.List;

import co.edu.icesi.metrocali.atc.entities.policies.User;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class UsersTrack {

	private Integer id;

	private Timestamp endTime;

	private Timestamp startTime;

	private List<UsersRemark> usersRemarks;

	@NonNull
	private User user;
	
	@NonNull
	private State state;
	
	public UsersRemark addUsersRemark(UsersRemark usersRemark) {
		getUsersRemarks().add(usersRemark);
		usersRemark.setUsersTrack(this);
		return usersRemark;
	}

	public UsersRemark removeUsersRemark(UsersRemark usersRemark) {
		getUsersRemarks().remove(usersRemark);
		usersRemark.setUsersTrack(null);
		return usersRemark;
	}

}