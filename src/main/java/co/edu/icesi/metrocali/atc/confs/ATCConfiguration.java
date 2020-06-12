package co.edu.icesi.metrocali.atc.confs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.icesi.metrocali.atc.exceptions.bb.BlackboxExceptionHandler;

@Configuration
public class ATCConfiguration {

	@Bean
	public RestTemplate blackboxApi() {
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(
				new BlackboxExceptionHandler());
		
		return restTemplate;
		
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
	
}
