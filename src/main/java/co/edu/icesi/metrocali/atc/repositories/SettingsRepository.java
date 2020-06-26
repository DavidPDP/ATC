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

import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.exceptions.bb.ResourceNotFound;

@Repository
public class SettingsRepository {

	private RestTemplate blackboxApi;
	
	private String blackboxPoliciesApiURL;
	
	public SettingsRepository(
		@Qualifier("blackboxApi") RestTemplate blackboxApi,
		@Value("${blackbox.apis.policies}") String blackboxPoliciesApiURL) {
		
		this.blackboxApi = blackboxApi;
		this.blackboxPoliciesApiURL = blackboxPoliciesApiURL;
		
	}
	
	public List<Setting> retrieveAll(){
				
		List<Setting> settings = null;
		
		try {
			
			settings = 
				blackboxApi.exchange(blackboxPoliciesApiURL + "/settings", 
					HttpMethod.GET, null, 
					new ParameterizedTypeReference<List<Setting>>() {}
				).getBody();
			
		}catch(ResourceNotFound e) {
			settings = Collections.emptyList();
		}
			
		return settings;
		
	}
	
	public Setting retrieve(String key) {
		
		Setting setting = 
			blackboxApi.exchange(
				blackboxPoliciesApiURL + "/settings/" + key, 
				HttpMethod.GET, null, Setting.class
			).getBody();
		
		return setting;
		
	}
	
	public Setting save(Setting setting) {
			
		HttpEntity<Setting> bodyRequest = new HttpEntity<>(setting);
		
		Setting persistedSetting = 
			blackboxApi.exchange(
				blackboxPoliciesApiURL + "/settings", 
				HttpMethod.POST, bodyRequest, Setting.class
			).getBody();
		
		return persistedSetting;
		
	}
	
	public void delete(String key) {
			
		blackboxApi.exchange(
			blackboxPoliciesApiURL + "/settings/" + key, 
			HttpMethod.DELETE, null, HttpStatus.class
		).getBody();
			
	}
	
}
