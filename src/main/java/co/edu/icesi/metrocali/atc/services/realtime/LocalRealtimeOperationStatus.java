package co.edu.icesi.metrocali.atc.services.realtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.SettingKey;
import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.EventTrack;
import co.edu.icesi.metrocali.atc.entities.events.Protocol;
import co.edu.icesi.metrocali.atc.entities.events.State;
import co.edu.icesi.metrocali.atc.entities.events.Step;
import co.edu.icesi.metrocali.atc.entities.events.UserTrack;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.operators.Omega;
import co.edu.icesi.metrocali.atc.entities.policies.Role;
import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import co.edu.icesi.metrocali.atc.services.recovery.RecoveryPoint;

/**
 * Represents the concrete implementation of the operation 
 * real state. The collections are managed by the JVM 
 * and the key-value strategy is used for the retrieve of the 
 * instances.
 * @author <a href="mailto:johan.ballesteros@outlook.com">Johan Ballesteros</a>
 */
@Service
public class LocalRealtimeOperationStatus 
	implements RealtimeOperationStatus, RecoveryPoint {
	
	//Operators -----------------------------
	/**
	 * It stores the operators that sign-in the system.
	 * Data is temporary while authentication completes.
	 */
	private Map<String, User> operators;
	
	private Map<String, Omega> omegas;
	
	private Map<String, Controller> controllers;
	
	//---------------------------------------
		
	//Events --------------------------------
	private Map<String, List<Event>> controllersEvents; 
	
	private Map<String,Event> events;
	//---------------------------------------
	
	//Tracks --------------------------------
	private Map<String, List<UserTrack>> operatorTracks;
	
	private Map<String, List<EventTrack>> controllerEventTracks;
	//---------------------------------------
	
	//Static entities -----------------------
	private Map<String, State> states;
	
	private Map<String, Category> categories;
	
	private Map<String, Step> steps;
	
	private Map<String, Setting> settings;
	
	private Map<String, Role> roles;
	//---------------------------------------
	
	public LocalRealtimeOperationStatus() {
		initMaps();
	}
	
	private void initMaps() {
		
		operators = new HashMap<>();
		omegas = new HashMap<>();
		controllers = new HashMap<>();
		
		controllersEvents = new HashMap<>();
		events = new HashMap<>();
		
		operatorTracks = new HashMap<>();
		controllerEventTracks = new HashMap<>();
		
		states = new HashMap<>();
		categories = new HashMap<>();
		settings = new HashMap<>();
		roles = new HashMap<>();
		
	}
	
	//Recover Methods --------------------------------
	@Override
	public <T extends Recoverable> void recoverypoint(
			Class<T> type, List<Recoverable> entities) {
		
		Map<String, T> entityCollection = resolve(type);
	
		entityCollection.clear();

		for (Recoverable recoverable : entities) {
			
			entityCollection.put(
				recoverable.getKeyEntity(), 
				type.cast(recoverable)
			);
			
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private <T> Map<String, T> resolve(Class<T> type) {
		
		Map<String, T> entityCollection = 
			Collections.emptyMap();
		
		if(type.getName().equals(Setting.class.getName())) {
			entityCollection = (Map<String, T>) settings;
		}else if(type.getName().equals(Event.class.getName())) {
			entityCollection = (Map<String, T>) events;
		}else if(type.getName().equals(Category.class.getName())) {
			entityCollection = (Map<String, T>) categories;
		}else if(type.getName().equals(State.class.getName())) {
			entityCollection = (Map<String, T>) states;
		}
		
		return entityCollection;
		
	}
	//------------------------------------------------
		
	//Add and update operation status methods --------
	
	
	
	
	@Override
	public void assignEvent(Event event, String accountName) {
		controllersEvents.computeIfAbsent(accountName, 
			user -> new ArrayList<>()).add(event);
	}
	
	@Override
	public void updateSettings(List<Setting> settings) {
		
		this.settings.clear();
		
		for (Setting setting : settings) {
			this.settings.put(setting.getKey(), setting);
		}
		
	}
	
	@Override
	public void updateStates(List<State> states) {
		
		this.states.clear();
		
		for (State state : states) {
			
			this.states.put(state.getName(), state);

		}
		
	}
	
	@Override
	public void updateCategories(List<Category> categories) {
		
		this.categories.clear();
		
		for (Category category : categories) {
			this.categories.put(category.getName(), category);
		}
		
	}
	
	public void addOperator(User operator) {
		operators.put(operator.getAccountName(), operator);
	}
	
	public void removeOperator(String accountName) {
		operators.remove(accountName);
	}
	
	public List<Role> retrieveRoles(){
		return new ArrayList<>(roles.values());
	}
	
	public void persistRole(Role role) {
		roles.put(role.getName(), role);
	}
	
	public void removeRole(String name) {
		roles.remove(name);
	}
	
//	public List<?> retrieve(Class<?> type) {
//		
//		Map<?,?> entityMap = resolveType(type);
//		
//		return new ArrayList<>(entityMap.values());
//		
//	}
//		
//	private Map<?,?> resolveType(Class<?> type) {
//		
//		Map<?,?> entityMap = Collections.emptyMap();
//		
//		if(type.isInstance(Role.class)) {
//			entityMap = roles;
//		}
//		
//		return entityMap;
//	}
	
	@Override
	public boolean addOrUpdateController(Controller controller) {
		controllers.put(controller.getAccountName(), controller);
		return true;
	}
	
	public void removeController(@NonNull String accountName) {
		controllers.remove(accountName);
	}
	
	public void addOrUpdateOmega(Omega omega) {
		omegas.put(omega.getAccountName(), omega);
	}
	
	public void removeOmega(@NonNull String accountName) {
		omegas.remove(accountName);
	}
	
	public void removeCategory(String name) {
		categories.remove(name);
	}
	
	public void removeSetting(String key) {
		settings.remove(key);
	}
	
	public void addOrUpdateSetting(Setting setting) {
		settings.put(setting.getKey(), setting);
	}
	
	@Override
	public boolean addOrUpdateCategory(Category category) {
		categories.put(category.getName(), category);
		return true;
	}
	
	@Override
	public boolean addOrUpdateState(State state) {
		states.put(state.getName(), state);
		return true;
	}
	
	@Override
	public boolean addOrUpdateEvent(Event event) {
		events.put(event.getCode(), event);
		return true;
	}
	
	//------------------------------------------------
	
	//Retrieve methods -------------------------------
	@Override
	public List<Setting> retrieveAllSettings(){
		return (List<Setting>) settings.values();
	}
	
	@Override
	public Optional<Setting> retrieveSetting(@NonNull SettingKey setting) {
		return Optional.ofNullable(
			this.settings.get(setting.name())
		);
	}
	
	@Override
	public List<User> retrieveAllControllers(){
		return new ArrayList<>(controllers.values());
	}
	
	@Override
	public List<Omega> retrieveAllOmegas(){
		return new ArrayList<>(omegas.values());
	}
	
	public Optional<User> retrieveOperator(String accountName) {
		return Optional.ofNullable(operators.get(accountName));
	}
	
	@Override
	public Optional<Controller> retrieveController(String accountName) {
		return Optional.ofNullable(controllers.getOrDefault(accountName, null));
	}
	
	public Optional<Omega> retrieveOmega(String accountName) {
		return Optional.ofNullable(omegas.getOrDefault(accountName, null));
	}
	
	@Override
	public Optional<Event> retrieveEvent(String eventCode) {
		return Optional.ofNullable(events.get(eventCode));
	}
	
	@Override
	public List<Event> retrieveEventsByStates(String accountName, 
			List<String> states) {
		if(controllersEvents.isEmpty()) {
			return Collections.emptyList();
		}else {
			return controllersEvents.get(accountName)
				.stream()
				.filter(event -> states.contains(
					event.getLastEventTrack().getState().getName()
				))
				.collect(Collectors.toList());
		}
	}
	
	/**
	 * Retrieves all loaded user states.
	 * @return {@link List} with all loaded user states.
	 */
	@Override
	public List<State> retrieveAllStates() {
		if(states.isEmpty()) {
			return Collections.emptyList();
		}else {
			return new ArrayList<State>(states.values());
		}
	}
	
	/**
	 * Retrieves a specific user state that previously loaded.
	 * @param name the user state's business identifier.
	 * @return {@link Optional} with the specific user state searched.
	 */
	@Override
	public Optional<State> retrieveState(String name) {
		return Optional.of(states.get(name));
	}
	
	/**
	 * Retrieves all loaded categories.
	 * @return {@link List} with all loaded categories.
	 */
	@Override
	public List<Category> retrieveAllCategories(){
		if(categories.isEmpty()) {
			return Collections.emptyList();
		}else {
			return new ArrayList<>(categories.values());
		}
	}
	
	/**
	 * Retrieves a specific category that previously loaded.
	 * @param name the category's business identifier.
	 * @return {@link Optional} with the specific category searched.
	 */
	@Override
	public Optional<Category> retrieveCategory(@NonNull String name) {
		return Optional.of(categories.get(name));
	}

	@Override
	public Optional<Protocol> retrieveProtocolStep(@NonNull String eventCode,
			@NonNull String stepName) {
		System.out.println("PASO: " + stepName);
		Category category = events.get(eventCode).getCategory();
		System.out.println("CATEGORY: " + category);
		List<Protocol> protocols = category.getProtocols();
		
		return protocols.stream()
				.filter(p -> p.getStep().getDescription().equals(stepName))
				.findAny();
		
	}
	
	public List<Step> retrieveAllSteps() {
		return new ArrayList<>(steps.values());
	}
	
	//------------------------------------------------
	
	public void print() {
		for (Map.Entry<String,Event> mevent : this.events.entrySet()) {
			for (EventTrack track : mevent.getValue().getEventsTracks()) {
				System.out.println(track.getCode());
			}
		}
//		System.out.println("Controller: ------------------");
//		for (Controller controller : controllers.values()) {
//			System.out.println(controller.getAccountName());
//		}
//		System.out.println("Controller Events: -------------");
//		for (Map.Entry<String, List<Event>> entry: 
//			controllersEvents.entrySet()) {
//			System.out.println(entry.getKey());
//			for (Event event : entry.getValue()) {
//				System.out.println(event.getCode());
//				System.out.println(event.getLastEventTrack().getState().getName());
//				System.out.println("Track: ");
//				for (EventTrack track : event.getEventsTracks()) {
//					System.out.println(track.getId());
//				}
//			}
//		}
	}
	
	
	
}
