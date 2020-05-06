package co.edu.icesi.metrocali.atc.services.entities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.EventStates;
import co.edu.icesi.metrocali.atc.constants.OperatorTypes;
import co.edu.icesi.metrocali.atc.constants.SourceTypes;
import co.edu.icesi.metrocali.atc.constants.UserStates;
import co.edu.icesi.metrocali.atc.dtos.InEventMessage;
import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.EventRemarks;
import co.edu.icesi.metrocali.atc.entities.events.EventSource;
import co.edu.icesi.metrocali.atc.entities.events.EventTrack;
import co.edu.icesi.metrocali.atc.entities.events.Protocol;
import co.edu.icesi.metrocali.atc.entities.events.ProtocolTrack;
import co.edu.icesi.metrocali.atc.entities.events.State;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.exceptions.EventOwnerException;
import co.edu.icesi.metrocali.atc.repositories.EventManagmentRepository;
import co.edu.icesi.metrocali.atc.services.ResourcePlanning;
import co.edu.icesi.metrocali.atc.services.oprealtime.LocalRealtimeOperationStatus;

@Service
public class EventsService {
	
	private EventManagmentRepository eventManagmentRepository;
	
	private LocalRealtimeOperationStatus realtimeStatus;
	
	private OperatorsService operatorsService;
	
	private ResourcePlanning resourcePlanning;
	
	@Autowired
	public EventsService(EventManagmentRepository eventManagmentRepository,
			LocalRealtimeOperationStatus realtimeStatus,
			OperatorsService operatorsService,
			ResourcePlanning resourcePlanning) {
		this.eventManagmentRepository = eventManagmentRepository;
		this.realtimeStatus = realtimeStatus;
		this.operatorsService = operatorsService;
		this.resourcePlanning = resourcePlanning;
	}
	
	public List<State> retrieveAllStates() {
		List<State> states = 
				eventManagmentRepository.retrieveAllStates();
		if(states.isEmpty()) {
			throw new NoSuchElementException("No states were found.");
		}else {
			return states;
		}
	}
	
	public State retrieveUserState(UserStates state) {
		Optional<State> entity = 
				realtimeStatus.retrieveUserState(state.name());
		if(entity.isPresent()) {
			return entity.get();
		}else {
			entity = eventManagmentRepository.retrieveState(state.name());
			if(entity.isPresent()) {
				return entity.get();
			}else {
				throw new NoSuchElementException("No " + state 
						+ " user state was found.");
			}
		}
	}
	
	public List<State> retrieveAllUserStates(boolean shallowCopy) {
		if(!shallowCopy) {
			realtimeStatus.updateStates(
				eventManagmentRepository.retrieveAllStates()
			);
		}
		return realtimeStatus.retrieveAllUserStates();		
	}
	
	public State retrieveEventState(EventStates state) {
		Optional<State> entity = 
				realtimeStatus.retrieveEventState(state.name());
		if(entity.isPresent()) {
			return entity.get();
		}else {
			entity = eventManagmentRepository.retrieveState(state.name());
			if(entity.isPresent()) {
				return entity.get();
			}else {
				throw new NoSuchElementException();
			}
		}
	}
	
	public List<State> retrieveAllEventStates(boolean shallowCopy) {
		if(!shallowCopy) {
			realtimeStatus.updateStates(
				eventManagmentRepository.retrieveAllStates()
			);
		}
		return realtimeStatus.retrieveAllEventStates();			
	}
	
	private void createTrack(Event event, 
			String author, EventStates eventState) {
		
		Controller user = (Controller) operatorsService.retrieveOperator(
			author, OperatorTypes.Controller
		);
		
		State state = retrieveEventState(eventState);
		
		EventTrack lastEventTrack = event.getLastEventTrack();
		lastEventTrack.setEndTime(new Timestamp(System.currentTimeMillis()));
		
		EventTrack newEventTrack = new EventTrack(
			lastEventTrack.getPriority(),
			user, 
			state
		);
		
		event.addEventTrack(newEventTrack);
		persistEvent(event);	
	}
	
	public void changePriority(String accountName, 
			String eventCode, int priority) {
		Event event = realtimeStatus.retrieveEvent(eventCode);
		if(isEventOwner(event, accountName)) {
			createTrack(event, accountName, priority);
			realtimeStatus.addOrUpdateEvent(event);
			resourcePlanning.updateEventPriority(event);
		}else {
			throw new EventOwnerException(accountName 
					+ " controller doesn't own the event.");
		}
	}
	
	private void createTrack(Event event, 
			String author, int priority) {
		
		Controller user = (Controller) operatorsService.retrieveOperator(
			author, OperatorTypes.Controller
		);
		
		EventTrack lastEventTrack = event.getLastEventTrack();
		lastEventTrack.setEndTime(new Timestamp(System.currentTimeMillis()));
		
		EventTrack newEventTrack = new EventTrack(
			priority,
			user, 
			lastEventTrack.getState()
		);
		
		event.addEventTrack(newEventTrack);
		persistEvent(event);	
	}
	
	private EventSource createEventSource(
			@NonNull String sourceValue,
			@NonNull String sourceType) {
		
		EventSource eventSource = new EventSource(sourceValue,sourceType);
		
		if (SourceTypes.Bus.name()
				.equals(eventSource.getSourceType())) {
			eventSource.setId(1l);
		} else if (SourceTypes.Line.name()
				.equals(eventSource.getSourceType())) {
			eventSource.setId(2l);
		}
		
		return eventSource;
	}
	
	private List<EventTrack> initEventTracks(@NonNull String accountName, 
			@Positive int basePriority){
		
		Controller author = 
			(Controller) operatorsService.retrieveOperator(accountName, 
					OperatorTypes.Controller);
		
		EventTrack createdTrack = new EventTrack(
			basePriority,
			author,
			retrieveEventState(EventStates.Created)
		);
		createdTrack.setEndTime(new Timestamp(System.currentTimeMillis()));
		
		EventTrack pendingTrack = new EventTrack(
			basePriority,
			author,
			retrieveEventState(EventStates.Pending)
		);
		
		return new ArrayList<>(Arrays.asList(createdTrack,pendingTrack));
		
	}
	
	public String createEvent(InEventMessage message) {
		try {
			
			EventSource source = 
				createEventSource(
					message.getSourceValue(), 
					message.getSourceType().name()
			);
			
			Category category = 
					retrieveCategory(message.getCategoryName());
			
			List<EventTrack> eventTracks = 
				initEventTracks(message.getAccountName(),
					category.getBasePriority());
			
			Event event = 
				new Event(
					message.getDescription(),
					source,
					message.getTitle(),
					category,
					eventTracks
			);
			
			persistEvent(event);
						
			realtimeStatus.addOrUpdateEvent(event);
			resourcePlanning.addPendingEvent(event);
			
			return event.getCode();
			
		}catch (Exception e) {
			throw new ATCRuntimeException("failed!");
		}		
	}
	
	public void persistEvent(Event event) {
		eventManagmentRepository.persistEvent(event);
	}
	
	/**
	 * Retrieves events in the assigned state that the controller 
	 * currently has. This means that only the current status of the 
	 * operation is consulted (shallow strategy).
	 * @param accountName the business identifier of the controller.
	 * @return List with all events in the assigned state.
	 * @throws IllegalArgumentException if the controller doesn't exist.
	 */
	public List<Event> retrieveAssignedEvents(@NonNull String accountName){
		List<Event> assignedEvents = 
			realtimeStatus.retrieveEventsByStates(accountName, 
				Arrays.asList(EventStates.Assigned.name())
		);
		if(!assignedEvents.isEmpty()) {
			return assignedEvents;
		}else {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Retrieves events in the active state (In_Process - On_Hold) 
	 * that the controller currently has. This means that only the 
	 * current status of the operation is consulted (shallow strategy).
	 * @param accountName the business identifier of the controller.
	 * @return List with all events in the assigned state.
	 * @throws IllegalArgumentException if the controller doesn't exist.
	 */
	public List<Event> retrieveActiveEvents(@NonNull String accountName){
		List<Event> activeEvents = 
			realtimeStatus.retrieveEventsByStates(accountName, 
				Arrays.asList(EventStates.In_Proccess.name(),
					EventStates.On_Hold.name())
		);
		if(!activeEvents.isEmpty()) {
			return activeEvents;
		}else {
			throw new IllegalArgumentException();
		}
	}
	
	private boolean isEventOwner(@NonNull Event event, 
			@NonNull String accountName) {
		return event.getLastEventTrack().getUser()
				.getAccountName().equals(accountName);
	}
	
	public void acceptEvent(@NonNull String accountName, 
			@NonNull String eventCode) {
		Event event = realtimeStatus.retrieveEvent(eventCode);
		if(isEventOwner(event, accountName)) {
			createTrack(event, accountName, EventStates.Accepted);
			createTrack(event, accountName, EventStates.In_Proccess);
			realtimeStatus.addOrUpdateEvent(event);
			realtimeStatus.assignEvent(event, accountName);
		}else {
			throw new EventOwnerException(accountName 
				+ " controller doesn't own the event.");
		}
	}
	
	public void rejectEvent(@NonNull String accountName,
			@NonNull String eventCode) {
		Event event = realtimeStatus.retrieveEvent(eventCode);
		if(isEventOwner(event, accountName)) {
			createTrack(event, accountName, EventStates.Rejected);
			realtimeStatus.print();
			realtimeStatus.addOrUpdateEvent(event);
			resourcePlanning.addPendingEvent(event);
			resourcePlanning.addAvailableController(
				realtimeStatus.retrieveController(accountName).get());
		}else {
			throw new EventOwnerException(accountName
				+ " controller doesn't own the event.");
		}
	}
	
	public void completeEvent(@NonNull String accountName,
			@NonNull String eventCode) {
		Event event = realtimeStatus.retrieveEvent(eventCode);
		if(isEventOwner(event, accountName)) {
			createTrack(event, accountName, EventStates.Solved);
			realtimeStatus.addOrUpdateEvent(event);
		}else {
			throw new EventOwnerException(accountName 
					+ " controller doesn't own the event.");
		}
	}
	
	private void createProtocolTrack(@NonNull Event event, 
			@NonNull String stepName, boolean done) {
		
		Optional<Protocol> protocol = 
			realtimeStatus.retrieveProtocolStep(
				event.getCode(), stepName
		);
		
		if(protocol.isPresent()) {
			
			ProtocolTrack protocolTrack = 
				new ProtocolTrack(done, protocol.get());
			
			event.addProtocolTrack(protocolTrack);
			
			persistEvent(event);
			
		}else {
			throw new ATCRuntimeException("invalid protocol");
		}
		
	}
	
	public void completeProtocolStep(@NonNull String eventCode,
			@NonNull String stepName) {
		Event event = realtimeStatus.retrieveEvent(eventCode);
		createProtocolTrack(event, stepName, true);
		realtimeStatus.addOrUpdateEvent(event);
	}
	
	public void createEventRemark(@NonNull String eventCode,
			@NonNull String content, @NonNull String accountName) {
		Event event = realtimeStatus.retrieveEvent(eventCode);
		Optional<Controller> author = 
				realtimeStatus.retrieveController(accountName);
		EventRemarks eventRemark = new EventRemarks(content, author.get());
		event.getLastEventTrack().addEventRemark(eventRemark);
		persistEvent(event);
		realtimeStatus.addOrUpdateEvent(event);
	}
	
	/**
	 * Retrieves all categories with shallow/deep copy strategy.
	 * @param shallow the strategy 
	 * @return List
	 */
	public List<Category> retrieveAllCategories(boolean shallow){
		List<Category> categories = null;
		
		if(shallow) {
			categories = realtimeStatus.retrieveAllCategories();
		}else {
			categories = eventManagmentRepository.retrieveAllCategories();
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
				realtimeStatus.retrieveCategory(name);
		if(category.isPresent()) {
			//shallow copy
			return category.get();
		}else {
			//deep copy
			category = eventManagmentRepository.retrieveCategory(name);
			if(category.isPresent()) {
				return category.get();
			}else {
				throw new NoSuchElementException(name 
						+ " category doesn't exist.");
			}
		}
	}
	
	public void assignEvent(@NonNull Event event, @NonNull String accountName) {
		createTrack(event, accountName, EventStates.Assigned);
		realtimeStatus.assignEvent(event, accountName);
	}
	
	public void sendBack(@NonNull String eventCode, 
			@NonNull String accountName) {
		Event event = realtimeStatus.retrieveEvent(eventCode);
		if(isEventOwner(event, accountName)) {
			createTrack(event, accountName, EventStates.Send_Back);
			createTrack(event, accountName, EventStates.Pending);
			realtimeStatus.addOrUpdateEvent(event);
			resourcePlanning.addPendingEvent(event);
		}else {
			throw new EventOwnerException(accountName 
					+ " controller doesn't own the event.");
		}
	}
	
}
