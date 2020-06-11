package co.edu.icesi.metrocali.atc.services.entities;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

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
			roles = realtimeOperationStatus.retrieveRoles();
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
	
	public void save(Role role) {
		
		rolesRepository.save(role);
		
		//Update operation status
		realtimeOperationStatus.persistRole(role);
		
	}
	
	public void delete(String name) {
		
		//Update operation status
		realtimeOperationStatus.removeRole(name);
		
		rolesRepository.delete(name);
		
	}
	
}
