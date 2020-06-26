package co.edu.icesi.metrocali.atc.repositories;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import co.edu.icesi.metrocali.atc.entities.events.State;
import co.edu.icesi.metrocali.atc.exceptions.bb.ResourceNotFound;

@Repository
public class StatesRepository {

	private RestTemplate blackboxApi;
	
	private String blackboxEventManagmentApiURL;
	
	public StatesRepository(
		@Qualifier("blackboxApi") RestTemplate blackboxApi,
		@Value("${blackbox.apis.event_managment}") 
		String blackboxEventManagmentApiURL) {
		
		this.blackboxApi = blackboxApi;
		this.blackboxEventManagmentApiURL = blackboxEventManagmentApiURL;
		
	}
		
	public List<State> retrieveAll() {
				
		List<State> states = null;
		
		try {
			
			states = 
				blackboxApi.exchange(
					blackboxEventManagmentApiURL + "/states", 
					HttpMethod.GET, null, 
					new ParameterizedTypeReference<List<State>>() {}
				).getBody();
			
		}catch (ResourceNotFound e) {
			states = Collections.emptyList();
		}
			
			
		return states;
		
	}
	
	public State retrieve(String name) {
		
		State state = 
			blackboxApi.exchange(
				blackboxEventManagmentApiURL + "/states/" + name,
				HttpMethod.GET, null, State.class
			).getBody();
		
		return state;
		
	}
	
	public State save(State state) {
		
		HttpEntity<State> requestBody = 
				new HttpEntity<>(state);
		
		State persistedState = 
			blackboxApi.exchange(
				blackboxEventManagmentApiURL + "/states",
				HttpMethod.POST, requestBody, State.class
			).getBody();
		
		return persistedState;
		
	}
	
	public void delete(String name) {
		
		blackboxApi.exchange(
			blackboxEventManagmentApiURL + "/states" + name,
			HttpMethod.DELETE, null, HttpStatus.class
		).getBody();
			
	}
	
}
