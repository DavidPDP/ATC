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

import co.edu.icesi.metrocali.atc.entities.events.UserTrack;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.operators.Omega;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import co.edu.icesi.metrocali.atc.exceptions.bb.ResourceNotFound;

/**
 * Represents a repository(DDD) that manages the Policies Context's entities.
 * @author <a href="mailto:johan.ballesteros@outlook.com">Johan Ballesteros</a>
 */
@Repository
public class OperatorsRepository {

	private RestTemplate blackboxApi;
	
	private String blackboxPoliciesApiURL;
	
	private String blackboxEventManagmentApiURL;

	/**
	 * Builds a repository(DDD) that manages entities in the Policies Context.
	 * @param blackboxApi is the client technology to consume the Black-box subsystem's API.
	 * @param blackboxPoliciesApiURL is the URI where the Black-box subsystem's services are located.
	 * @param realtimeStatus is the concrete implementation of the shallow copy strategy.
	 */
	public OperatorsRepository(
			@Qualifier("blackboxApi") RestTemplate blackboxApi,
			@Value("${blackbox.apis.policies}") 
			String blackboxPoliciesApiURL,
			@Value("${blackbox.apis.event_managment}") 
			String blackboxEventManagmentApiURL) {
		
		this.blackboxApi = blackboxApi;
		this.blackboxPoliciesApiURL = blackboxPoliciesApiURL;
		this.blackboxEventManagmentApiURL = 
				blackboxEventManagmentApiURL;
		
	}
	
	public List<User> retrieveAll() {
		
		List<User> operators = null;
				
		try {
			
			operators =  
				blackboxApi.exchange(blackboxPoliciesApiURL + "/users", 
					HttpMethod.GET, null, 
					new ParameterizedTypeReference<List<User>>() {}
				).getBody();
			
		}catch (ResourceNotFound e) {
			operators = Collections.emptyList();
		}
		
		return operators;
		
	}
	
	/**
	 * Retrieves all controllers regardless of whether they are 
	 * active or not (deep copy).
	 * @return List of all registered users.
	 */
	public List<Controller> retrieveAllControllers() {
		
		List<Controller> controllers = null;
		
		try {
			
			controllers = 
				blackboxApi.exchange(
					blackboxPoliciesApiURL + "/users/types?type=Controller", 
					HttpMethod.GET, null, 
					new ParameterizedTypeReference<List<Controller>>() {}
				).getBody();
			
		}catch (ResourceNotFound e) {
			controllers = Collections.emptyList();
		}
		
		return controllers;
		
	}
	
	public List<Controller> retrieveOnlineControllers() {
		
		List<Controller> controllers = null;
		
		try {
			
			controllers = 
				blackboxApi.exchange(
					blackboxPoliciesApiURL + "/users/controllers/online",
					HttpMethod.GET, null, 
					new ParameterizedTypeReference<List<Controller>>() {}
				).getBody();
			
		}catch(ResourceNotFound e) {
			controllers = Collections.emptyList();
		}
		
		return controllers;
		
	}
	
	public List<Omega> retrieveAllOmegas() {
		
		List<Omega> omegas = null;
		
		try {
			
			omegas = 
				blackboxApi.exchange(
					blackboxPoliciesApiURL + "/users/types?type=Omega", 
					HttpMethod.GET, null, 
					new ParameterizedTypeReference<List<Omega>>() {}
				).getBody();
			
		}catch (ResourceNotFound e) {
			omegas = Collections.emptyList();
		}
		
		return omegas;
		
	}
	
	public List<User> retrieveAllAdmins() {
		
		List<User> admins = null;
		
		try {
			
			admins = 
				blackboxApi.exchange(
					blackboxPoliciesApiURL + "/users/types?type=Admin", 
					HttpMethod.GET, null, 
					new ParameterizedTypeReference<List<User>>() {}
				).getBody();
			
		}catch(ResourceNotFound e) {
			admins = Collections.emptyList();
		}
		
		return admins;
		
	}
	
	
	public User retrieve(String accountName) {
			
		User operator = 
			blackboxApi.exchange(
				blackboxPoliciesApiURL + "/users/" + accountName, 
				HttpMethod.GET, null, User.class
			).getBody();
		
		return operator;
		
	}
	
	public User save(User user) {

		HttpEntity<User> request = new HttpEntity<>(user);
		
		User persistedUser = 
			blackboxApi.exchange(
				blackboxPoliciesApiURL + "/users", 
				HttpMethod.POST, request, User.class
			).getBody();
		
		return persistedUser;
		
	}
	
	public UserTrack save(UserTrack userTrack) {
		
		HttpEntity<UserTrack> request = 
				new HttpEntity<>(userTrack);
		
		UserTrack persistedUserTrack = 
			blackboxApi.exchange(
				blackboxEventManagmentApiURL + "/users_track", 
				HttpMethod.POST, request, UserTrack.class
			).getBody();
		
		return persistedUserTrack;
		
	}
	
	public List<UserTrack> save(List<UserTrack> userTrack) {
		
		HttpEntity<List<UserTrack>> request = 
				new HttpEntity<>(userTrack);
		
		List<UserTrack> persistedUserTracks = 
			blackboxApi.exchange(
				blackboxEventManagmentApiURL + "/users_track/many", 
				HttpMethod.POST, request, 
				new ParameterizedTypeReference<List<UserTrack>>() {}
			).getBody();
		
		return persistedUserTracks;
		
	}
	
	public void deleteUser(String accountName) {
			
		blackboxApi.exchange(
			blackboxPoliciesApiURL + "/users/" + accountName,
			HttpMethod.DELETE, null, HttpStatus.class
		).getBody();
			
	}
	
	public List<UserTrack> retrieveUserTrackHistory(
			String accountName, String interval) {
		
		List<UserTrack> userTracks = null;
		
		try {
			
			userTracks = 
				blackboxApi.exchange(
					blackboxEventManagmentApiURL + "/users_track/" 
					+ accountName + "?interval=" + interval, 
					HttpMethod.GET, null, 
					new ParameterizedTypeReference<List<UserTrack>>() {}
				).getBody();
			
		}catch(ResourceNotFound e) {
			userTracks = Collections.emptyList();
		}
		
		return userTracks;
		
	}
	
}
