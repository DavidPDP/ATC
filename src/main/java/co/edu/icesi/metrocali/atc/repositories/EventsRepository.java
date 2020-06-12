package co.edu.icesi.metrocali.atc.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import co.edu.icesi.metrocali.atc.entities.events.Event;

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
	
	private String blackboxEventManagmentApiURL;
	
	public EventsRepository(
			@Qualifier("blackboxApi") RestTemplate blackboxApi,
			@Value("${blackbox.apis.event_managment}") 
			String blackboxEventManagmentApiURL) {
		
		this.blackboxApi = blackboxApi;
		this.blackboxEventManagmentApiURL = 
				blackboxEventManagmentApiURL;
		
	}
	
	public Event save(Event event) {
		
		HttpEntity<Event> request = new HttpEntity<>(event);

		Event persistedEvent = 
			blackboxApi.exchange(
				blackboxEventManagmentApiURL + "/events", 
				HttpMethod.POST, request, Event.class
			).getBody();
		
		return persistedEvent;
			
	}
	
	public List<Event> retrieveAll(String interval){
		
		System.out.println(interval);
		
		List<Event> events = 
			blackboxApi.exchange(blackboxEventManagmentApiURL 
				+ "/events?interval=" + interval, HttpMethod.GET,
				null, new ParameterizedTypeReference<List<Event>>() {}
		    ).getBody();
		
		return events;
		
	}
	
	public Event retrieve(String code) {
		
		Event event = 
			blackboxApi.exchange(
				blackboxEventManagmentApiURL + "/events/" + code,
				HttpMethod.GET, null, Event.class
			).getBody();
				
		return event;
		
	}
}
