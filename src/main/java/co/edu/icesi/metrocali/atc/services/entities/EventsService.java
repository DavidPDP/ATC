package co.edu.icesi.metrocali.atc.services.entities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.OperatorType;
import co.edu.icesi.metrocali.atc.constants.RecoveryPrecedence;
import co.edu.icesi.metrocali.atc.constants.SettingKey;
import co.edu.icesi.metrocali.atc.constants.StateValue;
import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.EventRemark;
import co.edu.icesi.metrocali.atc.entities.events.EventTrack;
import co.edu.icesi.metrocali.atc.entities.events.State;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.exceptions.EventOwnerException;
import co.edu.icesi.metrocali.atc.exceptions.bb.BadRequestException;
import co.edu.icesi.metrocali.atc.repositories.EventsRepository;
import co.edu.icesi.metrocali.atc.services.planning.ResourcePlanning;
import co.edu.icesi.metrocali.atc.services.realtime.LocalRealtimeOperationStatus;
import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import co.edu.icesi.metrocali.atc.services.recovery.RecoveryService;

@Service
public class EventsService implements RecoveryService {
	
	private EventsRepository eventRepository;
	
	private LocalRealtimeOperationStatus realtimeStatus;
	
	private OperatorsService operatorsService;
	
	private ResourcePlanning resourcePlanning;
	
	private CategoriesService categoriesService;
	
	private StatesService statesService;
	
	public EventsService(
			EventsRepository eventRepository,
			LocalRealtimeOperationStatus realtimeStatus,
			OperatorsService operatorsService,
			ResourcePlanning resourcePlanning,
			CategoriesService categoriesService,
			StatesService statesService) {
		
		this.eventRepository = eventRepository;
		this.realtimeStatus = realtimeStatus;
		this.operatorsService = operatorsService;
		this.resourcePlanning = resourcePlanning;
		this.categoriesService = categoriesService;
		this.statesService = statesService;
		
	}
	
	@Override
	public Class<? extends Recoverable> getType(){
		return Event.class;
	}
	
	@Override
	public RecoveryPrecedence getRecoveryPrecedence() {
		return RecoveryPrecedence.Second;
	}

	@Override
	public List<Recoverable> recoveryEntities() {
		
		Optional<Setting> interval = 
			realtimeStatus.retrieveSetting(
				SettingKey.Recover_Time
			);
		
		if(interval.isPresent()) {

			List<Event> events = Collections.emptyList();
			
			try {
				events = eventRepository.retrieveAll(
					interval.get().getValue()
				);
			}catch(BadRequestException e) {
				
				if(!e.getCode().equals(HttpStatus.NOT_FOUND)) {
					throw new ATCRuntimeException(
						"Could not retrieve any event", e);
				}
				
			}
			
			return new ArrayList<Recoverable>(events);
			
		}else {
			throw new ATCRuntimeException(
				"The setting Recover_Time is not loaded at "
				+ "the time of the request.");
		}
		
	}
	
	
	//CRUD -----------------------------------------
	public List<Event> retrieveAll(boolean shallow) {
		
		List<Event> events = Collections.emptyList();
		
		if(shallow) {
			//events = realtimeStatus.retrieveAllEvents();
		}
		
		return events;
		
	}
	
	public Event retrieve(String code) {
		
		Event event = null;
		
		Optional<Event> shallowEvent = 
				realtimeStatus.retrieveEvent(code);
		
		if(shallowEvent.isPresent()) {
			//Shallow copy
			event = shallowEvent.get();
		}else {
			//Deep copy
			event = eventRepository.retrieve(code);
		}
		
		return event;
		
	}
	
	private Event retrieveInRealTime(String code) {
		
		Event event = null;
		
		Optional<Event> shallowEvent = 
				realtimeStatus.retrieveEvent(code);
		
		if(shallowEvent.isPresent()) {
			event = shallowEvent.get();
		}else {
			throw new ATCRuntimeException("aaaa");
		}
		
		return event;
		
	}
	
	public String createEvent(String authorName, Event inputFields) {
		
		//Resolve event source
		//TODO realtime sgco model
		Long source = 1l;
		
		Category category = 
			categoriesService.retrieve(
				inputFields.getCategory().getName()
			);
		
		//Check for manual priority
		Integer priority = null;
		
		if(!inputFields.getEventsTracks().isEmpty()) {
			priority = inputFields.getLastPriority();
		}else {
			priority = category.getBasePriority();
		}
		
		List<EventTrack> eventTracks = 
			initEventTracks(authorName, priority);
		
		//Fill event attributes
		Event event = 
			new Event(
				inputFields.getDescription(),
				inputFields.getTitle(),
				source,
				inputFields.getSourceType(),
				category, 
				eventTracks
			);
		System.out.println("Size: " + event.getEventsTracks().size()
				+ "P1: " + event.toString());
		event = eventRepository.save(event);
		System.out.println("Size: " + event.getEventsTracks().size()
				+ "P2: " + event.toString());
		
		//Update operation status
		realtimeStatus.addOrUpdateEvent(event);
		resourcePlanning.addPendingEvent(event);
		
		return event.getCode();
				
	}
	
	private List<EventTrack> initEventTracks(String authorName, 
			int basePriority){
		
		Controller author = 
			(Controller) operatorsService.retrieveOperator(
				authorName, OperatorType.Controller
			);
		
		State state = 
			statesService.retrieve(StateValue.Pending);
		
		EventTrack eventTrack = 
			new EventTrack(
				basePriority,
				author,
				state
			);
		
		return new ArrayList<>(Arrays.asList(eventTrack));
		
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
				Arrays.asList(StateValue.Assigned.name())
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
				Arrays.asList(StateValue.In_Proccess.name(),
					StateValue.On_Hold.name())
		);
		
		if(!activeEvents.isEmpty()) {
			return activeEvents;
		}else {
			throw new IllegalArgumentException();
		}
	}
	//----------------------------------------------
	
	//Update event state methods -------------------
	public void acceptEvent(String authorName, String code) {
		
		//Verify that the user has permission to update
		Event event = retrieveInRealTime(code);
		verifyOwnership(event, authorName);
		
		//Create trace
		System.out.println("Size: " + event.getEventsTracks().size()
				+ " P1: " + event.toString());
		createTrack(event, authorName, StateValue.In_Proccess);
		System.out.println("Size: " + event.getEventsTracks().size()
				+ " P2: " + event.toString());
		
		realtimeStatus.addOrUpdateEvent(event);
		realtimeStatus.assignEvent(event, authorName);
	
	}
	
	private void verifyOwnership(Event event, 
			String authorName) {
		
		String lastOwner = 
			event.getLastEventTrack().getUser().getAccountName();
		
		if(!lastOwner.equals(authorName)) {
			throw new EventOwnerException(authorName 
					+ " controller doesn't own the event.");
		}
		
	}
	
	private void createTrack(Event event, 
			String author, StateValue eventState) {
		
		Controller user = 
			(Controller) operatorsService.retrieveOperator(
				author, OperatorType.Controller
			);
		
		State state = statesService.retrieve(eventState);
		
		EventTrack lastEventTrack = event.getLastEventTrack();
		lastEventTrack.setEndTime(
				new Timestamp(System.currentTimeMillis()));
		
		EventTrack newEventTrack = new EventTrack(
			lastEventTrack.getPriority(),
			user, 
			state
		);
		
		event.addEventTrack(newEventTrack);
		event = eventRepository.save(event);	
		
	}
	
	public void assignEvent(Event event, 
			String authorName, boolean manual) {
		
		if(manual) {
			
		}
		
		createTrack(event, authorName, StateValue.Assigned);
		
		realtimeStatus.addOrUpdateEvent(event);
		realtimeStatus.assignEvent(event, authorName);
		
	}
	
	public void rejectEvent(String authorName,
			String code, EventRemark inputFields) {
		
		//Verify that the user has permission to update
		Event event = retrieveInRealTime(code);
		verifyOwnership(event, authorName);
		
		//Create trace and add justification
		Controller author = 
			(Controller) operatorsService.retrieveOperator(
				authorName, OperatorType.Controller
			);
		
		EventRemark remark = new EventRemark(
				inputFields.getContent(), author);
		
		event.getLastEventTrack().addEventRemark(remark);
		
		createTrack(event, authorName, StateValue.Pending);
		
		//Update operation status
		realtimeStatus.addOrUpdateEvent(event);
		resourcePlanning.addPendingEvent(event);
		
		checkAvailabilityAssignment(author);
		
	}
	
	private void checkAvailabilityAssignment(Controller controller) {
		//TODO
		resourcePlanning.addAvailableController(controller);
	}
	
	public void completeEvent(String authorName, String code) {
		
		//Verify that the user has permission to update
		Event event = retrieveInRealTime(code);
		verifyOwnership(event, authorName);

		//Create trace
		createTrack(event, authorName, StateValue.Verification);
		
		//Update operation status
		realtimeStatus.addOrUpdateEvent(event);
		
		Controller controller = 
			(Controller) operatorsService.retrieveOperator(
				authorName, OperatorType.Controller
			);
		
		checkAvailabilityAssignment(controller);

	}
	
	public void sendBack(String code, String authorName,
			EventRemark inputFields) {
		
		//Verify that the user has permission to update
		Event event = retrieveInRealTime(code);
		verifyOwnership(event, authorName);

		//Create trace
		Controller author = 
				(Controller) operatorsService.retrieveOperator(
					authorName, OperatorType.Controller
				);
		
		EventRemark remark = new EventRemark(
				inputFields.getContent(), author);
		
		event.getLastEventTrack().addEventRemark(remark);
		
		createTrack(event, authorName, StateValue.Pending);
		
		//Update operation status
		realtimeStatus.addOrUpdateEvent(event);
		resourcePlanning.addPendingEvent(event);

	}
	
	public void putOnHold(String code, String authorName,
			EventRemark inputFields) {
		
		//Verify that the user has permission to update
		Event event = retrieveInRealTime(code);
		verifyOwnership(event, authorName);

		//Create trace
		Controller author = 
				(Controller) operatorsService.retrieveOperator(
					authorName, OperatorType.Controller
				);
		
		EventRemark remark = new EventRemark(
				inputFields.getContent(), author);
		
		event.getLastEventTrack().addEventRemark(remark);
		
		createTrack(event, authorName, StateValue.On_Hold);
		
		//Update operation status
		realtimeStatus.addOrUpdateEvent(event);
		resourcePlanning.addAvailableController(author);
		
	}
	
	public void resumeEvent(String code, String authorName) {
		
		Event event = retrieveInRealTime(code);
		
		//Create trace
		createTrack(event, authorName, StateValue.In_Proccess);
		
		//Update operation status
		realtimeStatus.addOrUpdateEvent(event);
		
	}
	
	public void verifyEvent(String code, String authorName) {
		
		Event event = retrieveInRealTime(code);
		
		//No trace is created. Only the end time is 
		//created for the verified status.
		EventTrack lastEventTrack = event.getLastEventTrack();
		lastEventTrack.setEndTime(
				new Timestamp(System.currentTimeMillis()));
		event = eventRepository.save(event);
		
		//Update operation status
		realtimeStatus.addOrUpdateEvent(event);
		
	}
	
	public void changePriority(String authorName, 
			String code, int priority) {
		
		//Verify that the user has permission to update
		Event event = retrieveInRealTime(code);
		verifyOwnership(event, authorName);
	
		//Create trace
		createTrack(event, authorName, priority);
		
		//Update operation status
		realtimeStatus.addOrUpdateEvent(event);
		resourcePlanning.updateEventPriority(event);

	}
	
	private void createTrack(Event event, 
			String author, int priority) {
		
		Controller user = 
			(Controller) operatorsService.retrieveOperator(
				author, OperatorType.Controller
			);
		
		EventTrack lastEventTrack = event.getLastEventTrack();
		lastEventTrack.setEndTime(
				new Timestamp(System.currentTimeMillis()));
		
		EventTrack newEventTrack = new EventTrack(
			priority,
			user, 
			lastEventTrack.getState()
		);
		
		event.addEventTrack(newEventTrack);
		event = eventRepository.save(event);
		
	}
	//----------------------------------------------
	
	//Business methods -----------------------------
	public void completeProtocolStep(String code, String stepName) {
//		Event event = realtimeStatus.retrieveEvent(eventCode);
//		createProtocolTrack(event, stepName, true);
//		realtimeStatus.addOrUpdateEvent(event);
	}
	
//	private void createProtocolTrack(Event event, 
//			String stepName, boolean done) {
//		
//		Optional<Protocol> protocol = 
//			realtimeStatus.retrieveProtocolStep(
//				event.getCode(), stepName
//		);
//		
//		if(protocol.isPresent()) {
//			
//			ProtocolTrack protocolTrack = 
//				new ProtocolTrack(done, protocol.get());
//			
//			event.addProtocolTrack(protocolTrack);
//			
//			//persistEvent(event);
//			
//		}else {
//			throw new ATCRuntimeException("invalid protocol");
//		}
//		
//	}
//	
//	public void createEventRemark(String code,
//			String content, String accountName) {
//		Event event = realtimeStatus.retrieveEvent(eventCode);
//		Optional<Controller> author = 
//				realtimeStatus.retrieveController(accountName);
//		EventRemark eventRemark = new EventRemark(content, author.get());
//		event.getLastEventTrack().addEventRemark(eventRemark);
//		persistEvent(event);
//		realtimeStatus.addOrUpdateEvent(event);
//	}
//	
//	public List<Event> retrieveLastEvents(@NonNull String interval){
//		return eventManagmentRepository.retrieveLastEvent(interval);
//	}
	//----------------------------------------------
	
}
