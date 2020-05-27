package co.edu.icesi.metrocali.atc.services.entities;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.exceptions.BlackboxException;

@Service
public class SettingsService {

	private RestTemplate blackboxApi;
	
	private String blackboxSettingsApiURL;
	
	@Autowired
	public SettingsService(
			@Qualifier("blackboxApi") RestTemplate blackboxApi,
			@Value("${blackbox.apis.settings}")
			String blackboxEventsApiURL) {
		this.blackboxApi = blackboxApi;
		this.blackboxSettingsApiURL = blackboxEventsApiURL;
	}
	
	public List<Setting> retrieveAllSettings() {
		try {
			return blackboxApi.exchange(blackboxSettingsApiURL, 
				HttpMethod.GET, null, 
				new ParameterizedTypeReference<List<Setting>>() {}).getBody();
		}catch (HttpServerErrorException e) {
			throw new BlackboxException("The Black-box subsystem failed to "
					+ "process the request.", e);
		}catch (HttpClientErrorException e) {
			throw new BlackboxException("Request failed, Black-box subsystem "
					+ "throw HTTP " + e.getRawStatusCode() + " code.", e);
		}
	}
	
}
