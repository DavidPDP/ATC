package co.edu.icesi.metrocali.atc.services.realtime;

import java.util.List;
import java.util.Optional;

import org.springframework.lang.NonNull;

import co.edu.icesi.metrocali.atc.constants.SettingKey;
import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.Protocol;
import co.edu.icesi.metrocali.atc.entities.events.State;
import co.edu.icesi.metrocali.atc.entities.events.Step;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.operators.Omega;
import co.edu.icesi.metrocali.atc.entities.policies.Role;
import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.entities.policies.User;

public interface RealtimeOperationStatus {
	
//	public List<?> retrieve(Class<?> type);
//	
//	public void persist(Class<?> type, Object entity);
	
	public void removeStep(String code);
	
	public void addOrUpdateStep(Step step);
	
	public void updateSteps(List<Step> steps);
	
	public Optional<Step> retrieveStep(String code);
	
	public List<Step> retrieveAllSteps();
	
	public void persistRole(Role role);
	
	public List<Role> retrieveRoles();
	
	public void removeRole(String name);
	
	public List<User> retrieveAllControllers();
	
	public void assignEvent(Event event, String accountName);
	
	public void updateSettings(List<Setting> settings);
	
	public void updateStates(List<State> states);
	
	public void updateCategories(List<Category> categories);
	
	public boolean addOrUpdateController(Controller controller);
	
	public boolean addOrUpdateCategory(Category category);
	
	public boolean addOrUpdateState(State state);
	
	public boolean addOrUpdateEvent(Event event);
	
	public List<Setting> retrieveAllSettings();
	
	public Optional<Setting> retrieveSetting(@NonNull SettingKey setting);
	
	public List<Omega> retrieveAllOmegas();
	
	public Optional<Controller> retrieveController(String accountName);
	
	public Optional<Event> retrieveEvent(String eventCode);
	
	public List<Event> retrieveEventsByStates(String accountName, 
			List<String> states);
	
	public List<State> retrieveAllStates();
	
	public Optional<State> retrieveState(String name);
	
	public List<Category> retrieveAllCategories();
	
	public Optional<Category> retrieveCategory(@NonNull String name);
	
	public Optional<Protocol> retrieveProtocolStep(@NonNull String eventCode,
			@NonNull String stepName);
	
	public void removeCategory(String name);
	
	public void addOrUpdateSetting(Setting setting);
	
	public void removeSetting(String key);
}
