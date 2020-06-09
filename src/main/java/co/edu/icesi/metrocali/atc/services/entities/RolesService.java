package co.edu.icesi.metrocali.atc.services.entities;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.entities.policies.Role;
import co.edu.icesi.metrocali.atc.repositories.RolesRepository;

@Service
public class RolesService {

	private RolesRepository rolesRepository;
	
	public RolesService(RolesRepository rolesRepository) {
		this.rolesRepository = rolesRepository;
	}
	
	public List<Role> retrieveAll() {
		return rolesRepository.retrieveAll();
	}
	
	public void save(Role role) {
		rolesRepository.save(role);
	}
	
	public void delete(String name) {
		rolesRepository.delete(name);
	}
	
}
