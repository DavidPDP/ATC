package co.edu.icesi.metrocali.atc.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import co.edu.icesi.metrocali.atc.entities.policies.Role;
import co.edu.icesi.metrocali.atc.exceptions.bb.BlackboxException;

@Repository
public class RolesRepository {
	
	private RestTemplate blackboxApi;
	
	private String blackboxPoliciesApiURL;
	
	public RolesRepository(@Qualifier("blackboxApi") RestTemplate blackboxApi,
			@Value("${blackbox.apis.policies}") String blackboxPoliciesApiURL) {
		this.blackboxApi = blackboxApi;
		this.blackboxPoliciesApiURL = blackboxPoliciesApiURL;
	}
	
	public List<Role> retrieveAll(){
		try {
				
			List<Role> roles = 
				blackboxApi.exchange(blackboxPoliciesApiURL + "/roles", 
				HttpMethod.GET, null, 
				new ParameterizedTypeReference<List<Role>>() {}
			).getBody();
			
			return roles;
			
		} catch (HttpServerErrorException e) {
			e.printStackTrace();
			throw new BlackboxException("blacbox don't respond.");
		}
	}
	
	public void save(Role role) {
		try {
			
			HttpEntity<Role> request = new HttpEntity<>(role);
			blackboxApi.exchange(blackboxPoliciesApiURL + "/roles", 
					HttpMethod.POST, request, HttpStatus.class).getBody();
			
		} catch (HttpServerErrorException e) {
			
			HttpStatus errorCode = e.getStatusCode();
			
			throw new BlackboxException(
				"blackbox don't correctly respond.", e, errorCode
			);
			
		}
	}
	
	public void delete(String name) {
		try {
			
			blackboxApi.exchange(blackboxPoliciesApiURL 
				+ "/roles/" + name, HttpMethod.DELETE,
				null, HttpStatus.class).getBody();
			
		}catch (HttpServerErrorException e) {
			
			HttpStatus errorCode = e.getStatusCode();
			
			throw new BlackboxException(
				"blackbox don't correctly respond.", e, errorCode
			);
			
		}	
	}
	
}
