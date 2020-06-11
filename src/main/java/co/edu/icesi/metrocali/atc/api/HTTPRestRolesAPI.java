package co.edu.icesi.metrocali.atc.api;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.icesi.metrocali.atc.entities.policies.Role;
import co.edu.icesi.metrocali.atc.services.entities.RolesService;

@RestController
@RequestMapping("/atc/roles")
public class HTTPRestRolesAPI {

	private RolesService rolesService;
	
	public HTTPRestRolesAPI(RolesService rolesService) {
		this.rolesService = rolesService;
	}
	
	//CRUD Role --------------------------------------
	@GetMapping
	public ResponseEntity<List<Role>> retrieveAll(
			@RequestParam @NonNull Boolean current) {
		
		List<Role> roles = rolesService.retrieveAll(current);
		return ResponseEntity.ok(roles); 
				
	}
	
	@PostMapping
	public ResponseEntity<HttpStatus> create(
			@RequestBody @NonNull Role role) {

		rolesService.save(role);
		return ResponseEntity.ok().build();
		
	}
	
	@DeleteMapping("/{name}")
	public ResponseEntity<HttpStatus> delete(
			@PathVariable @NotBlank String name) {
		
		rolesService.delete(name);
		return ResponseEntity.ok().build();
	
	}
	//------------------------------------------------
	
}
