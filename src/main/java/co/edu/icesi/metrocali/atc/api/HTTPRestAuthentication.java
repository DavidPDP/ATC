package co.edu.icesi.metrocali.atc.api;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.icesi.metrocali.atc.constants.OperatorTypes;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import co.edu.icesi.metrocali.atc.security.TokenProvider;
import co.edu.icesi.metrocali.atc.services.entities.OperatorsService;
import co.edu.icesi.metrocali.atc.vos.SessionToken;

@RestController
@RequestMapping("/atc")
public class HTTPRestAuthentication {
	
	private AuthenticationManager authenticationManager;
	
	private TokenProvider jwtTokenUtil;
	
	private OperatorsService operatorsService;
	
	public HTTPRestAuthentication(AuthenticationManager authenticationManager,
			TokenProvider jwtTokenUtil,
			OperatorsService operatorsService) {
		
		this.authenticationManager = authenticationManager;
		this.jwtTokenUtil = jwtTokenUtil;
		this.operatorsService = operatorsService;
		
	}
	
	@PostMapping("/sign_in/controllers")
	public ResponseEntity<SessionToken> signInController(@RequestBody User controller) {
		return signInUser(controller, OperatorTypes.Controller);
	}
	
	@PostMapping("/sign_in/omegas")
	public ResponseEntity<SessionToken> signInOmega(@RequestBody User omega) {
		return signInUser(omega, OperatorTypes.Omega);
	}
	
	private ResponseEntity<SessionToken> signInUser(@NonNull User user, 
			@NonNull OperatorTypes type) {
		
		try {
			
			// Perform the authentication process
			Authentication authentication = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(
	            	user.getAccountName(),
	            	user.getPassword()
	            )
	        );
			
			// Inject into security context
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			// Token creation
			String token = jwtTokenUtil.generateToken(authentication);
			
			// Register the controller in the system
			this.operatorsService.registerOperator(
				user.getAccountName(), type);
			
			return ResponseEntity.ok(new SessionToken(token));
			
		}catch(AuthenticationException e) {
			
			return ResponseEntity.badRequest().body(null);
			
		}
		
	}
	
	@DeleteMapping("/sign_out/controllers")
	public void signOutController(@RequestBody User controller) {
		
	}
	
	@DeleteMapping("/sign_out/omegas")
	public void signOutOmega(@RequestBody User omega) {
		
	}
	
}
