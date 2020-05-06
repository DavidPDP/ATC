package co.edu.icesi.metrocali.atc.services;

import java.util.Optional;
import java.util.PriorityQueue;

import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.policies.User;

/**
 * https://stackoverflow.com/questions/714796/priorityqueue-heap-update?noredirect=1&lq=1
 *
 */
@Service
public class ResourcePlanning {
	
	public static final int PRIORITY_TOP = 750;
			
	private PriorityQueue<Event> pendingEvents;

	private PriorityQueue<User> availableControllers;
	
	private Object eventLock;
	
	private Object controllerLock;
		
	public ResourcePlanning() {
		initPriorityQueues();
		initLocks();
	}
	
	private void initPriorityQueues() {
		this.pendingEvents = new PriorityQueue<>();
		this.availableControllers = new PriorityQueue<>();
	}
	
	private void initLocks() {
		this.eventLock = new Object();
		this.controllerLock = new Object();
	}
	
	public void addPendingEvent(Event event) {
		synchronized (eventLock) {
			pendingEvents.add(event);
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
	
	public void addAvailableController(User user) {
		synchronized (controllerLock) {
			availableControllers.add(user);
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
	
}
