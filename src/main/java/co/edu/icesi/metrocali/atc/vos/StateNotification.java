package co.edu.icesi.metrocali.atc.vos;

import java.util.Optional;

import co.edu.icesi.metrocali.atc.constants.NotificationType;
import lombok.Value;

@Value
public class StateNotification {

	private NotificationType type;
	
	private Optional<String> addressee;
	
	private Object[] elementsInvolved;
	
	//Front
	//-----------
	//Handler
	//API
	
	
	//Servicios
	
	//Repo
	//Handler
	//-----------
	//BB
	
}
