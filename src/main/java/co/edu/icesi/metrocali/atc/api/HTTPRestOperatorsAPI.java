package co.edu.icesi.metrocali.atc.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.icesi.metrocali.atc.entities.policies.User;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.exceptions.BadRequestException;
import co.edu.icesi.metrocali.atc.services.entities.OperatorsService;

@RestController
@RequestMapping("/atc/operators")
public class HTTPRestOperatorsAPI {

	private OperatorsService operatorsService;
	
	@Autowired
	public HTTPRestOperatorsAPI(OperatorsService operatorsService) {
		this.operatorsService = operatorsService;
	}
	
	//CRUD Operator ----------------------------------
	@PostMapping
	public ResponseEntity<HttpStatus> createOperator(
			@RequestBody User operator) {
		try {
			operatorsService.persistOperator(operator);
			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
		}catch(BadRequestException e) {
			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
		}catch(ATCRuntimeException e) {
			return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PatchMapping
	public ResponseEntity<HttpStatus> updateOperator(
			@RequestBody User operator) {
		try {
			operatorsService.persistOperator(operator);
			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
		}catch(BadRequestException e) {
			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
		}catch(ATCRuntimeException e) {
			return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/{accountName}")
	public ResponseEntity<HttpStatus> deleteOperator(
			@PathVariable String accountName) {
		try {
			operatorsService.deleteOperator(accountName);
			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
		}catch(BadRequestException e) {
			e.printStackTrace();
			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
		}catch(ATCRuntimeException e) {
			e.printStackTrace();
			return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	//------------------------------------------------
	
}
