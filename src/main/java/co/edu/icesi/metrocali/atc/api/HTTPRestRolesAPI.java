//package co.edu.icesi.metrocali.atc.api;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PatchMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import co.edu.icesi.metrocali.atc.entities.policies.User;
//import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
//import co.edu.icesi.metrocali.atc.exceptions.BadRequestException;
//import co.edu.icesi.metrocali.atc.repositories.RolesRepository;
//import co.edu.icesi.metrocali.atc.services.entities.RolesService;
//
//@RestController
//@RequestMapping("/atc/roles")
//public class HTTPRestRolesAPI {
//
//	private RolesService rolesService;
//	
//	@Autowired
//	public HTTPRestRolesAPI(RolesRepository rolesRepository) {
//		this.rolesRepository = rolesRepository;
//	}
//	
//	//CRUD Role --------------------------------------
//	@GetMapping
//	public ResponseEntity<HttpStatus> retrieveRoles(
//			@RequestBody User operator) {
//		try {
//			this.rolesRepository.persistOperator(operator);
//			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
//		}catch(BadRequestException e) {
//			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
//		}catch(ATCRuntimeException e) {
//			return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//	
//	@PostMapping
//	public ResponseEntity<HttpStatus> createRole(
//			@RequestBody User operator) {
//		try {
//			this.rolesRepository.persistOperator(operator);
//			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
//		}catch(BadRequestException e) {
//			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
//		}catch(ATCRuntimeException e) {
//			return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//	
//	@PatchMapping
//	public ResponseEntity<HttpStatus> updateRole(
//			@RequestBody User operator) {
//		try {
//			this.rolesRepository.persistOperator(operator);
//			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
//		}catch(BadRequestException e) {
//			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
//		}catch(ATCRuntimeException e) {
//			return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//	
//	@DeleteMapping
//	public ResponseEntity<HttpStatus> deleteRole(
//			@RequestBody User operator) {
//		try {
//			this.rolesRepository.persistOperator(operator);
//			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
//		}catch(BadRequestException e) {
//			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
//		}catch(ATCRuntimeException e) {
//			return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
//	//------------------------------------------------
//	
//}
