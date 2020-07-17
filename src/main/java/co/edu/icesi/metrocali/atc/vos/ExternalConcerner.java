package co.edu.icesi.metrocali.atc.vos;

import co.edu.icesi.metrocali.atc.constants.NotificationType;
import co.edu.icesi.metrocali.atc.constants.UserType;
import lombok.ToString;
import lombok.Value;

@ToString
@Value
public class ExternalConcerner {

	private String id;
	
	private String accountName;
	
	private UserType userType;
	
	private NotificationType[] concerns;
	
}
