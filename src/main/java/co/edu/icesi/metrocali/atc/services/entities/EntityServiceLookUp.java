package co.edu.icesi.metrocali.atc.services.entities;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class EntityServiceLookUp {
	
	private ApplicationContext context;
	
	public EntityServiceLookUp(ApplicationContext context) {
		this.context = context;
	}
	
	public <T> T getEntityService(Class<T> serviceType) {
		return context.getBean(serviceType);
	}
	
}
