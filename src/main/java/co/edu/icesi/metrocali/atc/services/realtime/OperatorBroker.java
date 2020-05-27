package co.edu.icesi.metrocali.atc.services.realtime;

import java.util.List;
import java.util.Map;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import co.edu.icesi.metrocali.atc.entities.policies.User;

/**
 * Is responsible of making the communications between the system's 
 * front-end clients through web sockets.
 */
@Component
public class OperatorBroker {
	
	private Map<String, List<User>> channels;
	
	public void refreshOmegas() {
		
	}
	
	public void sendNotificationToController(@NonNull String accountName) {
		
	}
	
	
	
}
