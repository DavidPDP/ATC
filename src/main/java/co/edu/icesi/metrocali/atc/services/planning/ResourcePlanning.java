package co.edu.icesi.metrocali.atc.services.planning;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.NotificationType;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import co.edu.icesi.metrocali.atc.services.entities.EventsService;
import co.edu.icesi.metrocali.atc.services.notifications.StateChangeConcerner;
import co.edu.icesi.metrocali.atc.services.notifications.events.EventStateChangeConcerner;
import co.edu.icesi.metrocali.atc.services.notifications.events.EventStateChangeGenerator;
import co.edu.icesi.metrocali.atc.vos.StateNotification;

/**
 * https://stackoverflow.com/questions/714796/priorityqueue-heap-update?noredirect=1&lq=1
 *
 */
@Service
public class ResourcePlanning implements EventStateChangeGenerator {
	
	public static final int PRIORITY_TOP = 750;
	
	//Attributes -----------------------------------
	private Object eventLock;
	
	private Object controllerLock;
	//----------------------------------------------
	
	//Aggregates -----------------------------------
	private PriorityQueue<Event> pendingEvents;

	private PriorityQueue<Controller> availableControllers;
	
	private List<EventStateChangeConcerner> concerners;
	//----------------------------------------------
	
	//Dependencies ---------------------------------
	private EventsService eventsService;
	//----------------------------------------------
	
	//Constructors ---------------------------------
	public ResourcePlanning(
			List<EventStateChangeConcerner> concerners,
			@Lazy EventsService eventsService) {
		
		this.concerners = concerners;
		this.eventsService = eventsService;
		
		initPriorityQueues();
		initLocks();
		initConcerners(concerners);
		
	}
	
	private void initPriorityQueues() {
		this.pendingEvents = new PriorityQueue<>();
		this.availableControllers = new PriorityQueue<>();
	}
	
	private void initLocks() {
		this.eventLock = new Object();
		this.controllerLock = new Object();
	}
	
	private void initConcerners(
		List<EventStateChangeConcerner> concerners) {
		
		concerners = new ArrayList<>();
		
		for (EventStateChangeConcerner concerner : concerners) {
			attach(concerner);
		}
		
	}
	//----------------------------------------------
	
	//Interface implementation ---------------------
	@Override
	public void attach(StateChangeConcerner stateChangeConcerner) {
		
		this.concerners.add(
			(EventStateChangeConcerner) stateChangeConcerner
		);
		
	}

	@Override
	public void detach(StateChangeConcerner stateChangeConcerner) {
		this.concerners.remove(stateChangeConcerner);
	}

	@Override
	public void notifyNewStateChange(StateNotification notification) {
		
		for (EventStateChangeConcerner concerner : concerners) {
			concerner.update(notification);
		}
		
	}
	//----------------------------------------------
	
	//Events Queue methods -------------------------
	public void addPendingEvent(Event event) {
		
		Object[] elementsInvolved = {event};
		
		StateNotification stateEvent = 
			new StateNotification(
				NotificationType.New_Event_Entity, 
				Optional.ofNullable(null),
				elementsInvolved
			);
		
		synchronized (eventLock) {
			pendingEvents.add(event);
			notifyNewStateChange(stateEvent);
			allocation();
		}
		
	}
	
	public Event getNextPendingEvent() {
		synchronized (eventLock) {
			return pendingEvents.poll();
		}
	}
	
	public Optional<Event> showNextPendingEvent() {
		
		synchronized (eventLock) {
			return Optional.ofNullable(pendingEvents.peek());
		}
		
	}
	
	public void updateEventPriority(Event event) {
		
		synchronized (eventLock) {
			
			boolean isRemoved = pendingEvents.removeIf(
				e -> e.getCode().equals(event.getCode())
			);
			
			if(isRemoved) {
				pendingEvents.add(event);
			}
			
		}
	}
	
	//----------------------------------------------
	
	//Controllers Queue methods --------------------
	public void addAvailableController(Controller user) {
		
		Object[] elementsInvolved = {user};
		
		StateNotification stateEvent = 
			new StateNotification(
				NotificationType.New_Available_Controller,
				Optional.ofNullable(null),
				elementsInvolved
			);
		
		synchronized (controllerLock) {
			
			availableControllers.add(user);
			notifyNewStateChange(stateEvent);
			allocation();
			
		}
		
	}
	
	public User getNextAvailableController() {
		
		synchronized (controllerLock) {
			return availableControllers.poll();
		}
		
	}
	
	public Optional<User> showNextAvailableController() {
		
		synchronized (controllerLock) {
			return Optional.ofNullable(availableControllers.peek());
		}
		
	}
	
	public void updateControllerPriority(Controller controller) {
		
		synchronized (controllerLock) {
			
			boolean isRemoved = availableControllers.removeIf(
				c -> c.getAccountName().equals(
						controller.getAccountName())
			);
			
			if(isRemoved) {
				availableControllers.add(controller);
			}
			
		}
	}
	
	//----------------------------------------------
	
	//Event Allocation -----------------------------
	public void allocation() {
		System.out.println("Query Allocation---------");
		Optional<Event> nextEvent = showNextPendingEvent();
		Optional<User> nextController = 
				showNextAvailableController();
		
		if(nextEvent.isPresent() && nextController.isPresent()) {
						
			Event event = getNextPendingEvent();
			User controller = getNextAvailableController();
			
			Object[] elementsInvolved = 
				{event,controller};
			
			Optional<String> addressee = Optional.of(
				controller.getAccountName()
			);
			
			StateNotification stateEvent = 
				new StateNotification(
					NotificationType.New_Event_Assignment, 
					addressee,
					elementsInvolved
				);
			
			eventsService.assignEvent(
				event, controller.getAccountName(), false
			);
			
			notifyNewStateChange(stateEvent);
			
			System.out.println("Allocation-----------");
		}
		
	}
	//----------------------------------------------
	
}
