package co.edu.icesi.metrocali.atc.services.realtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.Settings;
import co.edu.icesi.metrocali.atc.constants.StateType;
import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.EventTrack;
import co.edu.icesi.metrocali.atc.entities.events.Protocol;
import co.edu.icesi.metrocali.atc.entities.events.State;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.operators.Omega;
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
	
	private Map<String, Omega> omegas;
	
	private Map<String, Controller> controllers;
	
	private Map<String, State> operatorsState;

	private Map<String, State> eventsState;
	
	private Map<String, List<Event>> controllersEvents; 
	
	private Map<String,Event> events;
	
	private Map<String, Category> categories;
	
	private Map<String, Setting> settings;
	
	@Autowired
	public LocalRealtimeOperationStatus() {
		initMaps();
	}
	
	private void initMaps() {
		omegas = new HashMap<>();
		controllers = new HashMap<>();
		operatorsState = new HashMap<>();
		eventsState = new HashMap<>();
		controllersEvents = new HashMap<>();
		events = new HashMap<>();
		categories = new HashMap<>();
		settings = new HashMap<>();
	}
	
	//Recover Methods --------------------------------
	@Override
	public void recoverypoint(Map<String,
			List<? extends Recoverable>> entities) {
		
		recoverStates(entities.get("states"));
		recoverCategories(entities.get("categories"));
		recoverControllers(entities.get("controllers"));
		recoverEvents(entities.get("events"));
		
	}
	
	private void recoverCategories(List<? extends Recoverable> categories) {
		
		this.categories.clear();
		
		for (Recoverable recoverableCategory : categories) {
			Category category = (Category) recoverableCategory;
			this.categories.put(category.getName(), category);
		}
		
	}
	
	private void recoverStates(List<? extends Recoverable> states) {
		
		this.operatorsState.clear();
		this.eventsState.clear();
		
		for (Recoverable recoverableState : states) {
			State state = (State) recoverableState;
			if(state.getStateTypeName().equals(StateType.Users.name())) {
				this.operatorsState.put(state.getName(), state);
			}else {
				this.eventsState.put(state.getName(), state);
			}
		}
		
	}
	
	private void recoverControllers(
			List<? extends Recoverable> controllers) {
		
		this.controllers.clear();
		
		for (Recoverable recoverableController : controllers) {
			Controller controller = (Controller) recoverableController;
			this.controllers.put(controller.getAccountName(), controller);
		}
		
	}
	
	private void recoverEvents(List<? extends Recoverable> events) {
		for (Recoverable recoverableEvent : events) {
			Event event = (Event) recoverableEvent;
			this.events.put(event.getCode(), event);
		}
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
		
		this.operatorsState.clear();
		this.eventsState.clear();
		
		for (State state : states) {
			if(state.getStateTypeName().equals(StateType.Users.name())) {
				this.operatorsState.put(state.getName(), state);
			}else {
				this.eventsState.put(state.getName(), state);
			}
		}
		
	}
	
	@Override
	public void updateCategories(List<Category> categories) {
		
		this.categories.clear();
		
		for (Category category : categories) {
			this.categories.put(category.getName(), category);
		}
		
	}
	
	@Override
	public boolean addOrUpdateController(Controller controller) {
		controllers.put(controller.getAccountName(), controller);
		return true;
	}
	
	@Override
	public boolean addOrUpdateCategory(Category category) {
		categories.put(category.getName(), category);
		return true;
	}
	
	@Override
	public boolean addOrUpdateEventState(State state) {
		eventsState.put(state.getName(), state);
		return true;
	}
	
	@Override
	public boolean addOrUpdateUserState(State state) {
		operatorsState.put(state.getName(), state);
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
	public Optional<Setting> retrieveSetting(@NonNull Settings setting) {
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
	
	@Override
	public Optional<Controller> retrieveController(String accountName) {
		return Optional.ofNullable(controllers.getOrDefault(accountName, null));
	}
	
	@Override
	public Event retrieveEvent(String eventCode) {
		return events.get(eventCode);
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
	public List<State> retrieveAllUserStates() {
		if(operatorsState.isEmpty()) {
			return Collections.emptyList();
		}else {
			return new ArrayList<State>(operatorsState.values());
		}
	}
	
	/**
	 * Retrieves a specific user state that previously loaded.
	 * @param name the user state's business identifier.
	 * @return {@link Optional} with the specific user state searched.
	 */
	@Override
	public Optional<State> retrieveUserState(String name) {
		return Optional.of(operatorsState.get(name));
	}
	
	/**
	 * Retrieves all loaded event states.
	 * @return {@link List} with all loaded event states.
	 */
	@Override
	public List<State> retrieveAllEventStates() {
		if(eventsState.isEmpty()) {
			return Collections.emptyList();
		}else {
			return new ArrayList<State>(eventsState.values());
		}
	}
	
	/**
	 * Retrieves a specific event state that previously loaded.
	 * @param name the event state's business identifier.
	 * @return {@link Optional} with the specific event state searched.
	 */
	@Override
	public Optional<State> retrieveEventState(@NonNull String name) {
		return Optional.of(eventsState.get(name));
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
