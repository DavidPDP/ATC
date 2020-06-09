package co.edu.icesi.metrocali.atc.repositories;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.EventTrack;
import co.edu.icesi.metrocali.atc.entities.events.State;
import co.edu.icesi.metrocali.atc.exceptions.bb.BlackboxException;

/**
 * Represents repository pattern from the DDD approach. 
 * This means that it handles a context (Aggregate Root) encapsulating 
 * the access to the child objects.
 * 
 * This repository is responsible for handling the event aggreagte root.
 */
@Repository
public class EventsRepository {
	
	private RestTemplate blackboxApi;
	
	private String blackboxEventsApiURL;
	
	private Object blackboxLock;
	
	@Autowired
	public EventsRepository(@Qualifier("blackboxApi") RestTemplate blackboxApi,
			@Value("${blackbox.apis.events}") String blackboxEventsApiURL) {
		this.blackboxApi = blackboxApi;
		this.blackboxEventsApiURL = blackboxEventsApiURL;
		blackboxLock = new Object();
	}
	
	/**
	 * Retrieves a specific state consuming the Black-box subsystem's API.
	 * @param name the state's business identifier.
	 * @return {@link Optional} with the specific state searched.
	 * @throws BlackboxException if Black-box throws Http 5xx - 4xx exception. 
	 */
	public Optional<State> retrieveState(String name) {
		try {
			return Optional.of(
				blackboxApi.exchange(blackboxEventsApiURL + "/states/name/" 
				+ name, HttpMethod.GET, null, State.class).getBody()
			);
		}catch (HttpServerErrorException e) {
			throw new BlackboxException("The Black-box subsystem failed to "
					+ "process the request.", e);
		}catch (HttpClientErrorException e) {
			throw new BlackboxException("Request failed, Black-box subsystem "
					+ "throw HTTP " + e.getRawStatusCode() + " code.", e);
		}
	}
	
	/**
	 * Retrieves a state list with all states persisted in Aviom.
	 * @return {@link List} list with all states founded.
	 * @throws BlackboxException if Black-box throws Http 5xx - 4xx exception.
	 */
	public List<State> retrieveAllStates(){
		try {
			
			List<State> states = 
				blackboxApi.exchange(blackboxEventsApiURL + "/states", 
				HttpMethod.GET, null, 
				new ParameterizedTypeReference<List<State>>() {}).getBody();
			
			return states != null ? states : Collections.emptyList();
			
		}catch (HttpServerErrorException e) {
			throw new BlackboxException("The Black-box subsystem failed to "
					+ "process the request.", e);
		}catch (HttpClientErrorException e) {
			throw new BlackboxException("Request failed, Black-box subsystem "
					+ "throw HTTP " + e.getRawStatusCode() + " code.", e);
		}
	}
	
//	public List<StateType> retrieveAllStateType() {
//		try {
//			return blackboxApi.exchange(blackboxEventsApiURL + "/states", 
//				HttpMethod.GET, null, 
//				new ParameterizedTypeReference<List<StateType>>() {}).getBody();
//		}catch (HttpServerErrorException e) {
//			throw new BlackboxException("The Black-box subsystem failed to "
//					+ "process the request.", e);
//		}catch (HttpClientErrorException e) {
//			throw new BlackboxException("Request failed, Black-box subsystem "
//					+ "throw HTTP " + e.getRawStatusCode() + " code.", e);
//		}
//	}
	
	/**
	 * Retrieves a category list with all categories persisted in Aviom.
	 * @return {@link List} list with all categories.
	 * @throws BlackboxException if Black-box throws Http 5xx - 4xx exception.
	 */
	public List<Category> retrieveAllCategories() {
		try {
			return blackboxApi.exchange(blackboxEventsApiURL + "/categories", 
				HttpMethod.GET, null, 
				new ParameterizedTypeReference<List<Category>>() {}).getBody();
		}catch (HttpServerErrorException e) {
			throw new BlackboxException("The Black-box subsystem failed to "
					+ "process the request.", e);
		}catch (HttpClientErrorException e) {
			throw new BlackboxException("Request failed, Black-box subsystem "
					+ "throw HTTP " + e.getRawStatusCode() + " code.", e);
		}
	}
	
	/**
	 * Retrieves a specific category consuming the Black-box subsystem's API.
	 * @param name the category's business identifier.
	 * @return {@link Optional} with the specific category searched.
	 * @throws BlackboxException if Black-box throws Http 5xx - 4xx exception. 
	 */
	public Optional<Category> retrieveCategory(@NonNull String name) {
		try {
			return Optional.of(
				blackboxApi.exchange(blackboxEventsApiURL + "/categories/" 
				+ name, HttpMethod.GET, null, Category.class).getBody()
			);
		}catch (HttpServerErrorException e) {
			throw new BlackboxException("The Black-box subsystem failed to "
					+ "process the request.", e);
		}catch (HttpClientErrorException e) {
			throw new BlackboxException("Request failed, Black-box subsystem "
					+ "throw HTTP " + e.getRawStatusCode() + " code.", e);
		}
	}
	
	public void persistEvent(Event event) {
		synchronized (blackboxLock) {
			HttpEntity<Event> request = new HttpEntity<>(event);
			try {
				Event callback = blackboxApi.exchange(blackboxEventsApiURL + "/events", 
					HttpMethod.POST, request, Event.class)
					.getBody();
				System.out.println("Event: " + event);
				System.out.println("Callback: " + callback);
				mergeEventInfo(event, callback);
			}catch (HttpServerErrorException e) {
				throw new BlackboxException("The Black-box subsystem failed to "
						+ "process the request.", e);
			}catch (HttpClientErrorException e) {
				throw new BlackboxException("Request failed, Black-box subsystem "
						+ "throw HTTP " + e.getRawStatusCode() + " code.", e);
			}	
		}
	}
	
	private void mergeEventInfo(Event event, Event callback) {
		
		event.setId(callback.getId());
		event.setCode(callback.getCode());
		event.setCreation(callback.getCreation());
		
		mergeEventTrackInfo(
			event.getEventsTracks(), 
			callback.getEventsTracks()
		);
		
		event.setProtocolTracks(callback.getProtocolTracks());
		
	}
	
	private void mergeEventTrackInfo(List<EventTrack> eventTracks, 
			List<EventTrack> callbacks) {
		
		for (int i = 0; i < eventTracks.size(); i++) {
			EventTrack eventTrack = eventTracks.get(i);
			EventTrack callback = callbacks.get(i);
			eventTrack.setId(callback.getId());
			eventTrack.setStartTime(callback.getStartTime());
			eventTrack.setEndTime(callback.getEndTime());
		}
		
	}
	
	
	public List<EventTrack> persistEventTrack(List<EventTrack> eventTracks) {
		try {
			HttpEntity<List<EventTrack>> request = new HttpEntity<>(eventTracks);
			System.out.println("Hola: " + eventTracks);
			return blackboxApi.exchange(blackboxEventsApiURL 
						+ "/events/event_tracks", 
					HttpMethod.POST, request, 
					new ParameterizedTypeReference<List<EventTrack>>() {})
					.getBody();
			
		}catch (HttpServerErrorException e) {
			throw new BlackboxException("The Black-box subsystem failed to "
					+ "process the request.", e);
		}catch (HttpClientErrorException e) {
			throw new BlackboxException("Request failed, Black-box subsystem "
					+ "throw HTTP " + e.getRawStatusCode() + " code.", e);
		}
	}
	
	public List<Event> retrieveLastEvent(String interval){
				
		return blackboxApi.exchange(blackboxEventsApiURL 
			+ "/events/lasted/" + interval, HttpMethod.GET, null, 
			new ParameterizedTypeReference<List<Event>>() {})
			.getBody();
		
	}
}
