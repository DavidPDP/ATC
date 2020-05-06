package co.edu.icesi.metrocali.atc.entities.events;

import java.sql.Timestamp;

import co.edu.icesi.metrocali.atc.entities.policies.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersRemark {

	private Long id;

	private String content;

	private Timestamp creation;

	private User user;
	
	private UsersTrack usersTrack;

}