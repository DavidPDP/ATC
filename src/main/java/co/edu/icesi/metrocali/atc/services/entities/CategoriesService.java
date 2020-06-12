package co.edu.icesi.metrocali.atc.services.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.RecoveryPrecedence;
import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.events.Protocol;
import co.edu.icesi.metrocali.atc.entities.events.Step;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.repositories.CategoriesRepository;
import co.edu.icesi.metrocali.atc.services.realtime.RealtimeOperationStatus;
import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import co.edu.icesi.metrocali.atc.services.recovery.RecoveryService;

@Service
public class CategoriesService implements RecoveryService {
	
	private CategoriesRepository categoriesRepository;
	
	private RealtimeOperationStatus realtimeOperationStatus;
	
	public CategoriesService(CategoriesRepository categoriesRepository,
			RealtimeOperationStatus realtimeOperationStatus) {
		
		this.categoriesRepository = categoriesRepository;
		this.realtimeOperationStatus = realtimeOperationStatus;
		
	}
	
	@Override
	public Class<? extends Recoverable> getType() {
		return Category.class;
	}
	
	@Override
	public RecoveryPrecedence getRecoveryPrecedence() {
		return RecoveryPrecedence.Second;
	}

	@Override
	public List<Recoverable> recoveryEntities() {
		
		List<Category> categories = 
			categoriesRepository.retrieveAll();
		
		return new ArrayList<Recoverable>(categories);
		
	}
	
	//CRUD -----------------------------------------
	/**
	 * Retrieves all categories with shallow/deep copy strategy.
	 * @param shallow the strategy 
	 * @return List
	 */
	public List<Category> retrieveAll(boolean shallow){
		
		List<Category> categories = Collections.emptyList();
		
		if(shallow) {
			categories = realtimeOperationStatus.retrieveAllCategories();
		}else {
			categories = categoriesRepository.retrieveAll();
		}
		
		if(categories.isEmpty()) {
			
			throw new ATCRuntimeException("No categories found", 
				new NoSuchElementException()
			);
			
		}
		
		return categories;
		
	}
	
	/**
	 * Retrieves a specific category with shallow/deep copy strategy.
	 * @param name the category's business identifier.
	 * @return {@link Category} with the specific category searched.
	 */
	public Category retrieve(String name) {
		
		Optional<Category> category = 
				realtimeOperationStatus.retrieveCategory(name);
		
		if(category.isPresent()) {
			//shallow copy
			return category.get();
		}else {
			//deep copy
			return categoriesRepository.retrieve(name);
		}
	}
	
	public void create(Category category) {
		
		for (Protocol protocol : category.getProtocols()) {
			
			String stepCode = protocol.getStep().getCode();
			Optional<Step> step = 
				realtimeOperationStatus.retrieveStep(stepCode);
			
			if(step.isPresent()) {
				protocol.setStep(step.get());
			}else {
				throw new ATCRuntimeException("The step is not "
					+ "loaded at the time of making the request.");
			}
			
		}
		
		Category persistedCategory = 
				categoriesRepository.save(category);
		
		//Update operation state
		realtimeOperationStatus
			.addOrUpdateCategory(persistedCategory);
		
	}
	
	public void update(Category category) {
		
		
		
	}
	
	public void delete(String name) {
		
		//Update operation state
		realtimeOperationStatus.removeCategory(name);
		
		categoriesRepository.delete(name);
		
	}
	//----------------------------------------------

}
