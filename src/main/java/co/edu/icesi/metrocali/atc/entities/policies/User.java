package co.edu.icesi.metrocali.atc.entities.policies;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import co.edu.icesi.metrocali.atc.entities.events.UsersTrack;
import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User implements UserDetails, CredentialsContainer, Recoverable{
 	
	private static final long serialVersionUID = -4730778099134900170L;

	private Integer id;

	private String accountName;

	private String email;
	
	private String name;
	
	private String lastName;

	private String password;
	
	private List<Role> roles;
	
	private List<Setting> settings;
	
	private List<UsersTrack> userTracks;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        
		this.roles.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
		});
		
		return authorities;
		
	}

	@Override
	public String getUsername() {
		return this.accountName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void eraseCredentials() {
		this.password = null;
	}

}