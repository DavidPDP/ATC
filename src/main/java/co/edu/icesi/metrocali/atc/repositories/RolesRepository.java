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

import co.edu.icesi.metrocali.atc.entities.policies.Role;
import co.edu.icesi.metrocali.atc.exceptions.bb.ResourceNotFound;

@Repository
public class RolesRepository {
	
	private RestTemplate blackboxApi;
	
	private String blackboxPoliciesApiURL;
	
	public RolesRepository(
			@Qualifier("blackboxApi") RestTemplate blackboxApi,
			@Value("${blackbox.apis.policies}")
			String blackboxPoliciesApiURL) {
		
		this.blackboxApi = blackboxApi;
		this.blackboxPoliciesApiURL = blackboxPoliciesApiURL;
		
	}
	
	public List<Role> retrieveAll(){
				
		List<Role> roles = null;
		
		try {
			
			roles =
				blackboxApi.exchange(blackboxPoliciesApiURL + "/roles", 
					HttpMethod.GET, null, 
					new ParameterizedTypeReference<List<Role>>() {}
				).getBody();
			
		}catch(ResourceNotFound e) {
			roles = Collections.emptyList();
		}
		
		return roles;
		
	}
	
	public Role retrieve(String name) {
		
		Role role = 
			blackboxApi.exchange(
				blackboxPoliciesApiURL + "/roles/" + name, 
				HttpMethod.POST, null, Role.class
			).getBody();
		
		return role; 
			
	}
	
	public Role save(Role role) {
			
		HttpEntity<Role> request = new HttpEntity<>(role);
		
		Role persistedRole = 
			blackboxApi.exchange(
				blackboxPoliciesApiURL + "/roles", 
				HttpMethod.POST, request, Role.class
			).getBody();
		
		return persistedRole; 
			
	}
	
	public void delete(String name) {
			
		blackboxApi.exchange(
			blackboxPoliciesApiURL + "/roles/" + name,
			HttpMethod.DELETE, null, HttpStatus.class
		).getBody();
			
	}
	
}
