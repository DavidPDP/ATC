package co.edu.icesi.metrocali.atc.services.realtime;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;

import co.edu.icesi.metrocali.atc.constants.SettingKey;
import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.Protocol;
import co.edu.icesi.metrocali.atc.entities.events.State;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.operators.Omega;
import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.entities.policies.User;

public interface RealtimeOperationStatus {
	
	public List<User> retrieveAllControllers();
	
	public void assignEvent(Event event, String accountName);
	
	public void updateSettings(List<Setting> settings);
	
	public void updateStates(List<State> states);
	
	public void updateCategories(List<Category> categories);
	
	public boolean addOrUpdateController(Controller controller);
	
	public boolean addOrUpdateCategory(Category category);
	
	public boolean addOrUpdateEventState(State state);
	
	public boolean addOrUpdateUserState(State state);
	
	public boolean addOrUpdateEvent(Event event);
	
	public List<Setting> retrieveAllSettings();
	
	public Optional<Setting> retrieveSetting(@NonNull SettingKey setting);
	
	public List<Omega> retrieveAllOmegas();
	
	public Optional<Controller> retrieveController(String accountName);
	
	public Event retrieveEvent(String eventCode);
	
	public List<Event> retrieveEventsByStates(String accountName, 
			List<String> states);
	
	public List<State> retrieveAllUserStates();
	
	public Optional<State> retrieveUserState(String name);
	
	public List<State> retrieveAllEventStates();
	
	public Optional<State> retrieveEventState(@NonNull String name);
	
	public List<Category> retrieveAllCategories();
	
	public Optional<Category> retrieveCategory(@NonNull String name);
	
	public Optional<Protocol> retrieveProtocolStep(@NonNull String eventCode,
			@NonNull String stepName);
	
}
