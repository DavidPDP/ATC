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

import co.edu.icesi.metrocali.atc.entities.events.Category;

@Repository
public class CategoriesRepository {

	private RestTemplate blackboxApi;
	
	private String blackboxEventManagmentApiURL;
	
	public CategoriesRepository(
		@Qualifier("blackboxApi") RestTemplate blackboxApi,
		@Value("${blackbox.apis.event_managment}") 
			String blackboxEventManagmentApiURL) {
		
		this.blackboxApi = blackboxApi;
		this.blackboxEventManagmentApiURL = 
				blackboxEventManagmentApiURL;
		
	}
	
	public List<Category> retrieveAll() {
		
		List<Category> categories = 
			blackboxApi.exchange(
				blackboxEventManagmentApiURL + "/categories", 
				HttpMethod.GET, null, 
				new ParameterizedTypeReference<List<Category>>() {}
			).getBody();
			
		return categories;
		
	}
	
	public Category retrieve(String name) {
		
		Category category = 
			blackboxApi.exchange(
				blackboxEventManagmentApiURL + "/categories/" + name,
				HttpMethod.GET, null, Category.class
			).getBody();
		
		return category;
		
	}
	
	public Category save(Category category) {

		HttpEntity<Category> requestBody = 
				new HttpEntity<>(category);
		
		Category persistedCategory = 
			blackboxApi.exchange(
				blackboxEventManagmentApiURL + "/categories",
				HttpMethod.POST, requestBody, Category.class
			).getBody();
		
		return persistedCategory;
		
	}
	
	public void delete(String name) {
		
		blackboxApi.exchange(
			blackboxEventManagmentApiURL + "/categories" + name,
			HttpMethod.DELETE, null, HttpStatus.class
		).getBody();
			
	}
	
}
