package co.edu.icesi.metrocali.atc.services.entities;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.UserType;
import co.edu.icesi.metrocali.atc.entities.policies.Role;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.repositories.RolesRepository;
import co.edu.icesi.metrocali.atc.services.realtime.RealtimeOperationStatus;

@Service
public class RolesService {

	private RolesRepository rolesRepository;
	
	private RealtimeOperationStatus realtimeOperationStatus; 
	
	public RolesService(RolesRepository rolesRepository,
		RealtimeOperationStatus realtimeOperationStatus) {
		
		this.rolesRepository = rolesRepository;
		this.realtimeOperationStatus = realtimeOperationStatus;
	}
	
	public List<Role> retrieveAll(boolean shallow) {
		
		List<Role> roles = Collections.emptyList();
		
		if(shallow) {
			roles = realtimeOperationStatus.retrieveAll(Role.class);
		}else {
			roles = rolesRepository.retrieveAll();
		}
		
		if(roles.isEmpty()) {
			
			throw new ATCRuntimeException("No roles found", 
				new NoSuchElementException()
			);
			
		}
		
		return roles;
		
	}
	
	public Role retrieve(UserType userType) {
		
		Role role = null;
		
		Optional<Role> shallowRole = 
			realtimeOperationStatus.retrieve(
				Role.class, userType.name()
			);
		
		if(shallowRole.isPresent()) {
			role = shallowRole.get();
		}else {
			role = rolesRepository.retrieve(userType.name());
		}
		
		return role;
		
	}
	
	public void save(Role role) {
		
		rolesRepository.save(role);
		
		//Update operation status
		realtimeOperationStatus.store(Role.class, role);
		
	}
	
	public void delete(String name) {
		
		//Update operation status
		realtimeOperationStatus.remove(Role.class, name);
		
		rolesRepository.delete(name);
		
	}
	
}
