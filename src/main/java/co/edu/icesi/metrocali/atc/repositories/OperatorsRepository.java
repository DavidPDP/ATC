package co.edu.icesi.metrocali.atc.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import co.edu.icesi.metrocali.atc.dtos.InUserInfo;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.operators.Omega;
import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import co.edu.icesi.metrocali.atc.exceptions.BlackboxException;

/**
 * Represents a repository(DDD) that manages the Policies Context's entities.
 * @author <a href="mailto:johan.ballesteros@outlook.com">Johan Ballesteros</a>
 */
@Repository
public class OperatorsRepository {

	private RestTemplate blackboxApi;
	
	private String blackboxPoliciesApiURL;

	/**
	 * Builds a repository(DDD) that manages entities in the Policies Context.
	 * @param blackboxApi is the client technology to consume the Black-box subsystem's API.
	 * @param blackboxPoliciesApiURL is the URI where the Black-box subsystem's services are located.
	 * @param realtimeStatus is the concrete implementation of the shallow copy strategy.
	 */
	@Autowired
	public OperatorsRepository(@Qualifier("blackboxApi") RestTemplate blackboxApi,
			@Value("${blackbox.apis.policies}") String blackboxPoliciesApiURL) {
		this.blackboxApi = blackboxApi;
		this.blackboxPoliciesApiURL = blackboxPoliciesApiURL;
	}
		
	/**
	 * Retrieves all controllers regardless of whether they are 
	 * active or not (deep copy).
	 * @return List of all registered users.
	 */
	public List<Controller> retrieveAllControllers() {
		return blackboxApi.exchange(blackboxPoliciesApiURL + "/users/controllers", 
				HttpMethod.GET, null, 
				new ParameterizedTypeReference<List<Controller>>() {}).getBody();
	}
	
	/**
	 * Retrieves all controllers regardless of whether they are 
	 * active or not (deep copy).
	 * @return List of all registered users.
	 */
	public List<Omega> retrieveAllOmegas() {
		return blackboxApi.exchange(blackboxPoliciesApiURL + "/users/omegas", 
				HttpMethod.GET, null, 
				new ParameterizedTypeReference<List<Omega>>() {}).getBody();
	}
	
	/**
	 * Retrieves user with the deep copy strategy.
	 * @param id is the user's identification.
	 * @return the searched user.
	 */
	protected Controller retrieveController(Integer id) {
		Controller controller = blackboxApi.exchange(blackboxPoliciesApiURL + "/users/" + id, 
				HttpMethod.GET, null, Controller.class).getBody();
		return controller;
	}
	
	public Optional<InUserInfo> retriveOperatorInfo(String accountName) {
		
		return Optional.ofNullable(
			blackboxApi.exchange(
				blackboxPoliciesApiURL + "/users/" + accountName, 
				HttpMethod.GET, null, InUserInfo.class
			).getBody()
		);

	}
	
	/**
	 * Retrieves the user entity, consuming the Black-box API.
	 * @param accountName is the user's account name.
	 * @return the searched user.
	 */
	public Optional<Controller> retrieveController(String accountName) {
		try {
			
			Optional<Controller> controller = Optional.of(
				blackboxApi.exchange(blackboxPoliciesApiURL 
				+ "/users/accountName/" + accountName, 
				HttpMethod.GET, null, Controller.class).getBody()
			);
			
			return controller;
			
		} catch (HttpServerErrorException e) {
			throw new BlackboxException("blacbox don't respond.");
		}
	}
	
	public void saveUser(User user) {
		try {
			
			HttpEntity<User> request = new HttpEntity<>(user);
			blackboxApi.exchange(blackboxPoliciesApiURL + "/users/", 
					HttpMethod.POST, request, HttpStatus.class).getBody();
			
		} catch (HttpServerErrorException e) {
			
			HttpStatus errorCode = e.getStatusCode();
			
			throw new BlackboxException(
				"blackbox don't correctly respond.", e, errorCode
			);
			
		}
		
	}
	
	public void deleteUser(String accountName) {
		try {
			
			blackboxApi.exchange(blackboxPoliciesApiURL 
				+ "/users/" + accountName, HttpMethod.DELETE,
				null, HttpStatus.class).getBody();
			
		}catch (HttpServerErrorException e) {
			
			HttpStatus errorCode = e.getStatusCode();
			
			throw new BlackboxException(
				"blackbox don't correctly respond.", e, errorCode
			);
			
		}
	}
	
	public List<User> retrieveAllOnlineControllers() {
		return blackboxApi.exchange(blackboxPoliciesApiURL + "/users/controllers/active", 
			HttpMethod.GET, null, 
			new ParameterizedTypeReference<List<User>>() {}).getBody();
	}
	
	public List<Setting> retrieveAllSettings(){
		return blackboxApi.exchange(blackboxPoliciesApiURL + "/settings", 
			HttpMethod.GET, null, 
			new ParameterizedTypeReference<List<Setting>>() {}).getBody();
	}
	
}
