package co.edu.icesi.metrocali.atc.services.oprealtime;

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

/**
 * Represents the concrete implementation of the operation 
 * real state. The collections are managed by the JVM 
 * and the key-value strategy is used for the retrieve of the 
 * instances.
 * @author <a href="mailto:johan.ballesteros@outlook.com">Johan Ballesteros</a>
 */
@Service
public class LocalRealtimeOperationStatus implements RealtimeOperationStatus {
	
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
	
	public void print() {
		System.out.println("Controller: ------------------");
		for (Controller controller : controllers.values()) {
			System.out.println(controller.getAccountName());
		}
		System.out.println("Controller Events: -------------");
		for (Map.Entry<String, List<Event>> entry: 
			controllersEvents.entrySet()) {
			System.out.println(entry.getKey());
			for (Event event : entry.getValue()) {
				System.out.println(event.getCode());
				System.out.println(event.getLastEventTrack().getState().getName());
				System.out.println("Track: ");
				for (EventTrack track : event.getEventsTracks()) {
					System.out.println(track.getId());
				}
			}
		}
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
	
	//TODO create abstract class ATCEntity and refactor this method with Map parameter
	@Override
	public void recoverypoint(List<State> states, 
			List<Category> categories, List<Setting> settings) {
		updateStates(states);
		updateCategories(categories);
		//updateSettings(settings);
	}
	
	private void updateSettings(List<Setting> settings) {
		
		this.settings.clear();
		
		for (Setting setting : settings) {
			this.settings.put(setting.getKey(), setting);
		}
		
	}
	
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
	
	private void updateCategories(List<Category> categories) {
		
		this.categories.clear();
		
		for (Category category : categories) {
			this.categories.put(category.getName(), category);
		}
		
	}
	
	public Optional<Setting> retriveSetting(@NonNull Settings setting) {
		return Optional.ofNullable(
			this.settings.get(setting.name())
		);
	}
	
	public List<User> retrieveAllControllers(){
		return new ArrayList<>(controllers.values());
	}
	
	public List<Omega> retrieveAllOmegas(){
		return new ArrayList<>(omegas.values());
	}
	
	public Optional<Controller> retrieveController(String accountName) {
		return Optional.ofNullable(controllers.getOrDefault(accountName, null));
	}
	
	public boolean addOrUpdateController(Controller controller) {
		controllers.put(controller.getAccountName(), controller);
		return true;
	}
	
	public boolean addOrUpdateCategory(Category category) {
		categories.put(category.getName(), category);
		return true;
	}
	
	public boolean addOrUpdateEventState(State state) {
		eventsState.put(state.getName(), state);
		return true;
	}
	
	public boolean addOrUpdateUserState(State state) {
		operatorsState.put(state.getName(), state);
		return true;
	}
	
	public boolean addOrUpdateEvent(Event event) {
		events.put(event.getCode(), event);
		return true;
	}
	
	public Event retrieveEvent(String eventCode) {
		return events.get(eventCode);
	}
	
	public void assignEvent(Event event, String accountName) {
		controllersEvents.computeIfAbsent(accountName, 
			user -> new ArrayList<>()).add(event);
	}
	
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
	public Optional<State> retrieveUserState(String name) {
		return Optional.of(operatorsState.get(name));
	}
	
	/**
	 * Retrieves all loaded event states.
	 * @return {@link List} with all loaded event states.
	 */
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
	public Optional<State> retrieveEventState(@NonNull String name) {
		return Optional.of(eventsState.get(name));
	}
	
	/**
	 * Retrieves all loaded categories.
	 * @return {@link List} with all loaded categories.
	 */
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
	public Optional<Category> retrieveCategory(@NonNull String name) {
		return Optional.of(categories.get(name));
	}

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
	
	public Optional<Setting> retrieveSetting(@NonNull Settings setting) {
		return Optional.ofNullable(this.settings.get(setting.name()));
	}
	
}
