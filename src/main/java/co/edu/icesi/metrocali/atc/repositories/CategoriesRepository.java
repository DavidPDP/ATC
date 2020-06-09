package co.edu.icesi.metrocali.atc.repositories;

import java.util.Collections;
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

import co.edu.icesi.metrocali.atc.entities.events.Category;

@Repository
public class CategoriesRepository {

	private RestTemplate blackboxApi;
	
	private String blackboxCategoriesApiURL;
	
	public CategoriesRepository(
		@Qualifier("blackboxApi") RestTemplate blackboxApi,
		@Value("${blackbox.apis.categories}") 
			String blackboxCategoriesApiURL) {
		
		this.blackboxApi = blackboxApi;
		this.blackboxCategoriesApiURL = blackboxCategoriesApiURL;
		
	}
	
	public List<Category> retrieveAll() {
		
		List<Category> categories = 
			blackboxApi.exchange(blackboxCategoriesApiURL, 
			HttpMethod.GET, null, 
			new ParameterizedTypeReference<List<Category>>() {}
		).getBody();
			
		return categories == null || categories.isEmpty() ? 
			Collections.emptyList() : categories;
		
	}
	
	public Optional<Category> retrieve(@NonNull String name) {
		
		Category category = blackboxApi.exchange(
			blackboxCategoriesApiURL + "/" + name,
			HttpMethod.GET, null, Category.class
		).getBody();
		
		return Optional.ofNullable(category);
		
	}
	
	public void save(@NonNull Category category) {
		System.out.println("HOLA");
		HttpEntity<Category> requestBody = 
				new HttpEntity<>(category);
		
		Category persistedCategory = blackboxApi.exchange(
			blackboxCategoriesApiURL, HttpMethod.POST, 
			requestBody, Category.class
		).getBody();
		
		category.merge(persistedCategory);
		
	}
	
	public void delete(@NonNull String name) {
		
		blackboxApi.exchange(
			blackboxCategoriesApiURL + "/" + name,
			HttpMethod.DELETE, null, HttpStatus.class
		).getBody();
			
	}
	
	
	
}
