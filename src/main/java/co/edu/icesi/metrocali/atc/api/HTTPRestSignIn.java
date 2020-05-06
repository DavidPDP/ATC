package co.edu.icesi.metrocali.atc.api;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.icesi.metrocali.atc.constants.OperatorTypes;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import co.edu.icesi.metrocali.atc.services.entities.OperatorsService;

@RestController
@RequestMapping("/atc")
public class HTTPRestSignIn {
	
	private OperatorsService operatorsService;
	
	public HTTPRestSignIn(OperatorsService operatorsService) {
		this.operatorsService = operatorsService;
	}
	
	@PostMapping("/sign_in/controllers")
	public void signInController(@RequestBody User controller) {
		this.operatorsService.registerOperator(
			controller.getAccountName(), OperatorTypes.Controller);
	}
	
	@PostMapping("/sign_in/omegas")
	public void signInOmega(@RequestBody User omega) {
		
	}
	
	@DeleteMapping("/sign_out/controllers")
	public void signOutController(@RequestBody User controller) {
		
	}
	
	@DeleteMapping("/sign_out/omegas")
	public void signOutOmega(@RequestBody User omega) {
		
	}
	
}
