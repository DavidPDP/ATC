package co.edu.icesi.metrocali.atc.entities.policies;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import co.edu.icesi.metrocali.atc.constants.OperatorType;
import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class User implements UserDetails, 
	CredentialsContainer, Recoverable{
 	
	private static final long serialVersionUID = -4730778099134900170L;

	private Integer id;

	private String accountName;

	private String email;
	
	private String name;
	
	private String lastName;

	@JsonInclude(value=Include.NON_EMPTY)
	private String password;
	
	private List<Role> roles;

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		
		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        
		this.roles.forEach(role -> {
            authorities.add(
            	new SimpleGrantedAuthority("ROLE_" + role.getName())
            );
		});
		
		return authorities;
		
	}

	@Override
	@JsonIgnore
	public String getUsername() {
		return this.accountName;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	@JsonIgnore
	public boolean isEnabled() {
		return true;
	}

	@Override
	@JsonIgnore
	public void eraseCredentials() {
		this.password = null;
	}
	
	/**
	 * Responsible for init a user's mandatory information. 
	 * This is useful for dynamism of instantiating concrete/child 
	 * classes at runtime.<br><br>
	 * 
	 * <b>Example:</b> query a user and then, at runtime, 
	 * instantiate it as a concrete user (Controller, Omega, 
	 * Supervisor, etc.).
	 * 
	 * @param user data encapsulator.
	 * 
	 * @see OperatorType
	 */
	public void fillUserData(User user) {
		setId(user.getId());
		setAccountName(user.getAccountName());
		setEmail(user.getEmail());
		setName(user.getName());
		setLastName(user.getLastName());
		setPassword(user.getPassword());
		setRoles(user.getRoles());
	}
	
}