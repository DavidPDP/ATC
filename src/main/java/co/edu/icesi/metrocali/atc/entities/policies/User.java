package co.edu.icesi.metrocali.atc.entities.policies;

import java.util.List;

import co.edu.icesi.metrocali.atc.entities.events.UsersTrack;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

	//Entities fields
	private Integer id;

	private String accountName;

	private String email;
	
	private String name;
	
	private String lastName;

	private String password;
	
	private List<Role> roles;
	
	private List<Setting> settings;
	
	private List<UsersTrack> userTracks;
	
	//Spring security fields
	private String username;
	
	private boolean enabled = true;
	
	private boolean accountNonExpired = true;
	
	private boolean credentialsNonExpired = true;
	
	private boolean accountNonLocked = true;
	
//	private List<GrantedAuthority> authorities = new ArrayList<>();
//	
//	public void fillSecurityFields() {
//		
//		if(this.roles != null && !this.roles.isEmpty()) {
//			
//			this.username = this.accountName;
//			
//			roles.stream().forEach(role -> {
//				this.authorities.add(
//					new SimpleGrantedAuthority("ROLE_" + role.getName())
//				);
//			});
//			
//		}else {
//			throw new ATCRuntimeException("The roles collection "
//					+ "not initialized.");
//		}
//		
//	}

}