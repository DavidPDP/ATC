package co.edu.icesi.metrocali.atc.services.realtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.State;
import co.edu.icesi.metrocali.atc.entities.events.Step;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.operators.ControllerWorkState;
import co.edu.icesi.metrocali.atc.entities.operators.Omega;
import co.edu.icesi.metrocali.atc.entities.policies.Role;
import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import co.edu.icesi.metrocali.atc.services.recovery.RecoveryPoint;

/**
 * Represents the concrete implementation of the operation 
 * real state. The collections are managed by the JVM 
 * and the key-value strategy is used for the retrieve of the 
 * instances.
 * 
 * @author <a href="mailto:
 * johan.ballesteros@outlook.com">Johan Ballesteros</a>
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
	private Map<String, ControllerWorkState> controllersEvents; 
	
	private Map<String,Event> events;
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
		
		states = new HashMap<>();
		categories = new HashMap<>();
		settings = new HashMap<>();
		roles = new HashMap<>();
		steps = new HashMap<>();
		
	}
	
	//Recover Method --------------------------------
	@Override
	public void preRecovery() {
		//there are no previous settings.
	}

	@Override
	public <T extends Recoverable> void recovery(
			Class<T> type, List<Recoverable> entities) {
		
		if(!entities.isEmpty()) {
			
			Map<String, T> entityCollection = resolve(type);

			for (Recoverable recoverable : entities) {
				
				entityCollection.put(
					recoverable.getKeyEntity(), 
					type.cast(recoverable)
				);
				
			}
		}
		
	}
	
	@Override
	public void postRecovery() {
		initOperators();
	}
	
	private void initOperators() {
			
		for (User operator : operators.values()) {
			operator.getLastUserTrack();
		}
		
	}
	//------------------------------------------------
	
	//operations on RAM ------------------------------
	/**
	 * allows to find the collection that is used to store 
	 * the entities of the type passed by parameter.<br><br>
     * <b>Note:</b> The uncheked warning is suppressed because the 
     * generic type cannot be statically related to the specific
     * type of the collection. But it should be noted that there 
     * is no heap pollution because with the verifications makes 
     * sure to deliver the corresponding type of collection 
     * avoiding the ClassCastException.
	 * @param type with the class of the entity that 
	 * stores the collection.
	 * @return the collection that stores the entity type.
	 */
	@SuppressWarnings("unchecked")
	private <T extends Recoverable> Map<String, T> 
		resolve(Class<T> type) {
		
		Map<String, T> entityCollection = 
			Collections.emptyMap();
		
		//TODO sort by descending demand
		if(Setting.class.isAssignableFrom(type)) {
			entityCollection = (Map<String, T>) settings;
		}else if(Event.class.isAssignableFrom(type)) {
			entityCollection = (Map<String, T>) events;
		}else if(Category.class.isAssignableFrom(type)) {
			entityCollection = (Map<String, T>) categories;
		}else if(State.class.isAssignableFrom(type)) {
			entityCollection = (Map<String, T>) states;
		}else if(Step.class.isAssignableFrom(type)) {
			entityCollection = (Map<String, T>) steps;
		}else if(Controller.class.isAssignableFrom(type)) {
			entityCollection = (Map<String, T>) controllers;
		}else if(Omega.class.isAssignableFrom(type)) {
			entityCollection = (Map<String, T>) omegas;
		}else if(Role.class.isAssignableFrom(type)) {
			entityCollection = (Map<String, T>) roles;
		}else if(User.class.isAssignableFrom(type)) {
			entityCollection = (Map<String, T>) operators;
		}else if(ControllerWorkState.class.isAssignableFrom(type)) {
			entityCollection = (Map<String, T>) controllersEvents;
		}else {
			
			throw new ATCRuntimeException(
				"There is no collection that supports that "
				+ "entity type."
			);
			
		}
		
		return entityCollection;
		
	}
	
	@Override
	public <T extends Recoverable> void store(
		Class<T> type, T entity) {
		
		Map<String, T> entityCollection = 
			(Map<String, T>) resolve(type);
		
		entityCollection.put(entity.getKeyEntity(), entity);
		
	}
	
	@Override
	public <T extends Recoverable> void store(
		Class<T> type, List<T> entities) {
		
		Map<String, T> entityCollection = 
			(Map<String, T>) resolve(type);
		
		for (T entity : entities) {
			entityCollection.put(entity.getKeyEntity(), entity);
		}
		
	}
	
	@Override
	public <T extends Recoverable> List<T> 
		retrieveAll(Class<T> type) {
	
		Map<String, T> entityCollection = 
				(Map<String, T>) resolve(type);
		
		List<T> resultValues = new ArrayList<T>(
			entityCollection.values());
		
		if(resultValues == null || resultValues.isEmpty()) {
			resultValues = Collections.emptyList();
		}
		
		return resultValues;
	
	}
	
	@Override
	public <T extends Recoverable> Optional<T> retrieve(
			Class<T> type, String entityKey) {
		
		Map<String, T> entityCollection = 
				(Map<String, T>) resolve(type);
		
		T entity = entityCollection.get(entityKey);
		
		return Optional.ofNullable(entity);
		
	}
	
	@Override
	public <T extends Recoverable> Optional<T> remove(
			Class<T> type, String entityKey) {
	
		Map<String, T> entityCollection = 
				(Map<String, T>) resolve(type);
		
		T entity = entityCollection.remove(entityKey);
		
		return Optional.ofNullable(entity);
	
	}
	
	@Override
	public <T extends Recoverable> void clear(Class<T> type) {
	
		Map<String, T> entityCollection = 
				(Map<String, T>) resolve(type);
		
		entityCollection.clear();
	
	} 
	
	@Override
	public <T extends Recoverable> List<T> filter(
			Class<T> type, String ... filters) {
		
		List<T> entitiesToFind = new ArrayList<>();
		
		//Retrieve concrete collection
		Map<String, T> entityCollection = 
			(Map<String, T>) resolve(type);
		
		//Retrieve filters patterns 
		Pattern[] patterns = createPatterns(filters);
		
		//Adds entities that match the pattern
		for (T entity : entityCollection.values()) {
			
			String entityFormat = entity.toString();
			
			if(hasPattern(entityFormat, patterns)) {
				entitiesToFind.add(entity);
			}
			
		}
		
		//Formats output
		if(entitiesToFind.isEmpty()) {
			entitiesToFind = Collections.emptyList();
		}
		
		return entitiesToFind;
		
	}
	
	private Pattern[] createPatterns(String ... filters) {
		
		String splitChar = "=";
		Map<String, List<String>> fieldsMap = new HashMap<>();
		
		//Groups the values ​​with the same field
		for (String filter : filters) {
			
			String[] filterValues = filter.split(splitChar);
			String field = filterValues[0];
			String value = filterValues[1];
			
			fieldsMap.computeIfAbsent(
				field, values -> new ArrayList<String>()
			).add(value);
			
		}
		
		Pattern[] patterns = new Pattern[fieldsMap.size()];
		
		//Creates each pattern
		int patternIndex = 0;
		for (Entry<String, List<String>> entry : 
			fieldsMap.entrySet()) {
			
			String pattern = 
				buildPattern(entry.getKey(), entry.getValue());
			
			patterns[patternIndex] = Pattern.compile(pattern);
			patternIndex++;
			
		}
		
		return patterns;
		
	}
	
	private String buildPattern(
			String field, List<String> values) {
		
		StringBuilder pattern = new StringBuilder();
		String splitChar = "=";
		
		pattern.append(field + splitChar + "(");
		
		for (String value : values) {
			pattern.append(value + "|");
		}
		
		pattern.setLength(pattern.length() - 1);
		pattern.append(")");
		
		return pattern.toString();
		
	}
	
	private boolean hasPattern(String input, Pattern[] patterns) {
		
		boolean satisfacePatterns = true;
		
		for (Pattern pattern : patterns) {
			
			Matcher matcher = pattern.matcher(input);
			
			if(!matcher.matches()) {
				satisfacePatterns = false;
				break;
			}
			
		}
		
		return satisfacePatterns;
		
	}
	//------------------------------------------------

}
