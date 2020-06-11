package co.edu.icesi.metrocali.atc.services.planning;

import java.util.Optional;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import co.edu.icesi.metrocali.atc.services.entities.EventsService;

@Service
public class TaskEventAllocation extends TimerTask 
	implements EventAllocation{

	private ResourcePlanning resourcePlanning;
	
	private EventsService eventsService;
	
	@Autowired
	public TaskEventAllocation(ResourcePlanning resourcePlanning,
			EventsService eventsService) {
		this.resourcePlanning = resourcePlanning;
		this.eventsService = eventsService;
	}
	
	@Override
	public void allocation() {
		System.out.println("Query Allocation---------");
		Optional<Event> nextEvent = 
				resourcePlanning.showNextPendingEvent();
		Optional<User> nextController = 
				resourcePlanning.showNextAvailableController();
		
		if(nextEvent.isPresent() && nextController.isPresent()) {
			
			Event event = resourcePlanning.getNextPendingEvent();
			System.out.println(event);
			User controller = resourcePlanning.getNextAvailableController();
			
			eventsService.assignEvent(
				event, controller.getAccountName(), false
			);
			System.out.println("Allocation-----------");
		}
		
	}

	@Override
	public void run() {
		allocation();
	}

}
