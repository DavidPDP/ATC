package co.edu.icesi.metrocali.atc.services.entities;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.repositories.EventsRepository;
import co.edu.icesi.metrocali.atc.services.realtime.LocalRealtimeOperationStatus;

@Service
public class CategoriesService {
	
	private EventsRepository categoriesRepository;
	
	private LocalRealtimeOperationStatus realtimeOperationStatus;
	
	@Autowired
	public CategoriesService(EventsRepository categoriesRepository,
			LocalRealtimeOperationStatus realtimeOperationStatus) {
		this.categoriesRepository = categoriesRepository;
		this.realtimeOperationStatus = realtimeOperationStatus;
	}
	
	/**
	 * Retrieves all categories with shallow/deep copy strategy.
	 * @param shallow the strategy 
	 * @return List
	 */
	public List<Category> retrieveAllCategories(boolean shallow){
		List<Category> categories = null;
		
		if(shallow) {
			categories = realtimeOperationStatus.retrieveAllCategories();
		}else {
			categories = categoriesRepository.retrieveAllCategories();
		}
		
		if(categories.isEmpty()) {
			throw new NoSuchElementException();
		}else {
			return categories;
		}
	}
	
	/**
	 * Retrieves a specific category with shallow/deep copy strategy.
	 * @param name the category's business identifier.
	 * @return {@link Category} with the specific category searched.
	 */
	public Category retrieveCategory(@NonNull String name) {
		Optional<Category> category = 
				realtimeOperationStatus.retrieveCategory(name);
		if(category.isPresent()) {
			//shallow copy
			return category.get();
		}else {
			//deep copy
			category = categoriesRepository.retrieveCategory(name);
			if(category.isPresent()) {
				return category.get();
			}else {
				throw new NoSuchElementException();
			}
		}
	}
}
