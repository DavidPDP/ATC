package co.edu.icesi.metrocali.atc.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import co.edu.icesi.metrocali.atc.entities.policies.Setting;

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
				
		List<Setting> settings = 
			blackboxApi.exchange(blackboxPoliciesApiURL + "/settings", 
			HttpMethod.GET, null, 
			new ParameterizedTypeReference<List<Setting>>() {}
		).getBody();
			
		return settings;
		
	}
	
	public Optional<Setting> retrieve(@NonNull String key) {
		
		Setting setting = 
			blackboxApi.exchange(blackboxPoliciesApiURL + "/settings/"
			+ key, HttpMethod.GET, null, Setting.class
		).getBody();
		
		return Optional.ofNullable(setting);
		
	}
	
	public void save(@NonNull Setting setting) {
			
		HttpEntity<Setting> bodyRequest = new HttpEntity<>(setting);
		
		blackboxApi.exchange(blackboxPoliciesApiURL + "/settings", 
			HttpMethod.POST, bodyRequest, HttpStatus.class
		).getBody();
		
	}
	
	public void delete(@NonNull String key) {
			
		blackboxApi.exchange(blackboxPoliciesApiURL + "/settings/"
			+ key, HttpMethod.DELETE, null, HttpStatus.class
		).getBody();
			
	}
	
}
