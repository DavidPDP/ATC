package co.edu.icesi.metrocali.atc.services.oprealtime;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;

import co.edu.icesi.metrocali.atc.constants.Settings;
import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.State;
import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.entities.policies.User;

public interface RealtimeOperationStatus {

	public void recoverypoint(List<State> states, 
			List<Category> categories, List<Setting> settings);
	
	public List<User> retrieveAllControllers();
	
	public void assignEvent(Event event, String accountName);
	
	public Optional<Setting> retrieveSetting(@NonNull Settings setting);
	
}
