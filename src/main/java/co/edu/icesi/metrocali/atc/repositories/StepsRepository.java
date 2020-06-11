package co.edu.icesi.metrocali.atc.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import co.edu.icesi.metrocali.atc.entities.events.Step;

@Repository
public class StepsRepository {

	private RestTemplate blackboxApi;
	
	private String blackboxEventManagmentApiURL;
	
	public StepsRepository(
		@Qualifier("blackboxApi") RestTemplate blackboxApi,
		@Value("${blackbox.apis.event_managment}") 
		String blackboxEventManagmentApiURL) {
		
		this.blackboxApi = blackboxApi;
		this.blackboxEventManagmentApiURL = blackboxEventManagmentApiURL;
		
	}
		
	public List<Step> retrieveAll() {
				
		List<Step> steps = 
			blackboxApi.exchange(
				blackboxEventManagmentApiURL + "/steps", 
				HttpMethod.GET, null, 
				new ParameterizedTypeReference<List<Step>>() {}
			).getBody();
			
		return steps;
		
	}
	
	public Step retrieve(String name) {
		
		Step step = 
			blackboxApi.exchange(
				blackboxEventManagmentApiURL + "/steps/" + name,
				HttpMethod.GET, null, Step.class
			).getBody();
		
		return step;
		
	}
	
	public Step save(Step step) {
		
		HttpEntity<Step> requestBody = 
				new HttpEntity<>(step);
		
		Step persistedStep = 
			blackboxApi.exchange(
				blackboxEventManagmentApiURL + "/steps",
				HttpMethod.POST, requestBody, Step.class
			).getBody();
		
		return persistedStep;
		
	}
	
	public void delete(String description) {
		
		blackboxApi.exchange(
			blackboxEventManagmentApiURL + "/steps" + description,
			HttpMethod.DELETE, null, HttpStatus.class
		).getBody();
			
	}
	
}
