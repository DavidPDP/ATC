package co.edu.icesi.metrocali.atc.api;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.icesi.metrocali.atc.constants.NotificationType;
import co.edu.icesi.metrocali.atc.constants.UserType;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import co.edu.icesi.metrocali.atc.security.TokenProvider;
import co.edu.icesi.metrocali.atc.services.entities.OperatorsService;
import co.edu.icesi.metrocali.atc.services.notifications.NotificationManager;
import co.edu.icesi.metrocali.atc.vos.ComplexOutputMessage;
import co.edu.icesi.metrocali.atc.vos.ExternalConcerner;
import co.edu.icesi.metrocali.atc.vos.SessionToken;

@RestController
@RequestMapping("/atc")
public class HTTPRestAuthenticationAPI {
	
	private AuthenticationManager authenticationManager;
	
	private TokenProvider jwtTokenUtil;
	
	private OperatorsService operatorsService;
	
	private NotificationManager notificationManager;
	
	public HTTPRestAuthenticationAPI(
			AuthenticationManager authenticationManager,
			TokenProvider jwtTokenUtil,
			OperatorsService operatorsService,
			NotificationManager notificationManager) {
		
		this.authenticationManager = authenticationManager;
		this.jwtTokenUtil = jwtTokenUtil;
		this.operatorsService = operatorsService;
		this.notificationManager = notificationManager;
		
	}
	
	//Authentication --------------------------------
	@PostMapping("/sign_in/{operatorType}")
	public ResponseEntity<ComplexOutputMessage> signIn(
			@PathVariable UserType operatorType,
			@RequestBody User operator) {
		
		try {
			//Perform the authentication process
			Authentication authentication = 
				authenticationManager.authenticate(
		            new UsernamePasswordAuthenticationToken(
		            	operator.getAccountName(),
		            	operator.getPassword()
		            )
		        );
			
			//Inject into security context
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			//Token creation
			String token = jwtTokenUtil.generateToken(authentication);
			
			//Subscribe the controller in the notification service
			String channelId = "No available";
			
			if(operatorType.equals(UserType.Controller)) {
				
				channelId = UUID.randomUUID().toString();
				NotificationType[] concerns = 
					{NotificationType.New_Event_Assignment};
				
				ExternalConcerner concerner = new ExternalConcerner(
					channelId, operator.getAccountName(), 
					operatorType, concerns);
				
				notificationManager.subscribe(concerner);
				
			}
			
			//Register the controller in the system
			operatorsService.registerOperator(
				operator.getAccountName(), 
				operatorType
			);
			
			User userData = operatorsService.retrieveOperator(
				operator.getAccountName(), 
				operatorType
			);
			
			//Format output
			ComplexOutputMessage output = new ComplexOutputMessage();
			
			output.addField("token", token);
			output.addField("user", userData);
			output.addField("channel", channelId);
			
			return ResponseEntity.ok(output);
			
		}catch(AuthenticationException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(null);
			
		}
		
	}
	
	@DeleteMapping("/sign_out/{operatorType}/{accountName}")
	public void signOut(@PathVariable UserType operatorType,
			@PathVariable String accountName) {
		
		operatorsService.unregisterOperator(accountName, operatorType);
		
	}
	//-----------------------------------------------
	
	//Session ---------------------------------------
	//TODO refresh strategy
	@PostMapping("/refresh_session/{accountName}")
	public ResponseEntity<SessionToken> refreshSession(
			@PathVariable String operatorType,
			@RequestBody User controller) {
		return null;
	}
	//-----------------------------------------------
}
