package co.edu.icesi.metrocali.atc.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.icesi.metrocali.atc.entities.policies.Role;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.exceptions.bb.BadRequestException;
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
	public ResponseEntity<List<Role>> retrieveRoles() {
		try {
			return new ResponseEntity<List<Role>>(
				rolesService.retrieveAll(),
				HttpStatus.OK
			);
		}catch(BadRequestException e) {
			//e.printStackTrace();
			return new ResponseEntity<List<Role>>(HttpStatus.BAD_REQUEST);
		}catch(ATCRuntimeException e) {
			return new ResponseEntity<List<Role>>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping
	public ResponseEntity<HttpStatus> createRole(
			@RequestBody Role role) {
		try {
			this.rolesService.save(role);
			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
		}catch(BadRequestException e) {
			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
		}catch(ATCRuntimeException e) {
			return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/{name}")
	public ResponseEntity<HttpStatus> deleteRole(
			@PathVariable String name) {
		try {
			rolesService.delete(name);
			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
		}catch(BadRequestException e) {
			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
		}catch(ATCRuntimeException e) {
			return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	//------------------------------------------------
	
}
