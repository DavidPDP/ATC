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

import co.edu.icesi.metrocali.atc.constants.RecoveryPrecedence;
import co.edu.icesi.metrocali.atc.constants.SettingKey;
import co.edu.icesi.metrocali.atc.constants.StateValue;
import co.edu.icesi.metrocali.atc.constants.UserType;
import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.EventRemark;
import co.edu.icesi.metrocali.atc.entities.events.EventTrack;
import co.edu.icesi.metrocali.atc.entities.events.Protocol;
import co.edu.icesi.metrocali.atc.entities.events.ProtocolTrack;
import co.edu.icesi.metrocali.atc.entities.events.State;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.exceptions.EventOwnerException;
import co.edu.icesi.metrocali.atc.exceptions.bb.BadRequestException;
import co.edu.icesi.metrocali.atc.repositories.EventsRepository;
import co.edu.icesi.metrocali.atc.services.planning.ResourcePlanning;
import co.edu.icesi.metrocali.atc.services.realtime.RealtimeOperationStatus;
import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import co.edu.icesi.metrocali.atc.services.recovery.RecoveryService;

@Service
public class EventsService implements RecoveryService {
	
	private EventsRepository eventRepository;
	
	private RealtimeOperationStatus realtimeStatus;
	
	private OperatorsService operatorsService;
	
	private ResourcePlanning resourcePlanning;
	
	private CategoriesService categoriesService;
	
	private StatesService statesService;
	
	private SettingsService settingsService;
	
	public EventsService(
			EventsRepository eventRepository,
			RealtimeOperationStatus realtimeStatus,
			OperatorsService operatorsService,
			ResourcePlanning resourcePlanning,
			CategoriesService categoriesService,
			StatesService statesService,
			SettingsService settingsService) {
		
		this.eventRepository = eventRepository;
		this.realtimeStatus = realtimeStatus;
		this.operatorsService = operatorsService;
		this.resourcePlanning = resourcePlanning;
		this.categoriesService = categoriesService;
		this.statesService = statesService;
		this.settingsService = settingsService;
		
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
		
		Setting interval = 
			settingsService.retrieve(SettingKey.Recover_Time);

		List<Event> events = Collections.emptyList();
		
		try {
			
			events = eventRepository.retrieveAll(
				interval.getValue());
			
		}catch(BadRequestException e) {
			
			if(!e.getCode().equals(HttpStatus.NOT_FOUND)) {
				throw new ATCRuntimeException(
					"Could not retrieve any event", e);
			}
			
		}
		
		return new ArrayList<Recoverable>(events);
		
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
				realtimeStatus.retrieve(Event.class, code);
		
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
				realtimeStatus.retrieve(Event.class, code);
		
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
		
		event = eventRepository.save(event);
		
		//Update operation status
		realtimeStatus.store(Event.class, event);
		resourcePlanning.addPendingEvent(event);
		
		return event.getCode();
				
	}
	
	private List<EventTrack> initEventTracks(String authorName, 
			int basePriority){
		
		Controller author = 
			(Controller) operatorsService.retrieveOperator(
				authorName, UserType.Controller
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
	public List<Event> retrieveAssignedEvents(
		@NonNull String accountName){
		
		List<Event> assignedEvents = 
			realtimeStatus.filter(Event.class,
				"lastUser=" + accountName,
				"lastState=" + StateValue.Assigned.name());
		
		if(!assignedEvents.isEmpty()) {
			return assignedEvents;
		}else {
			return null;
			//throw new IllegalArgumentException();
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
	public List<Event> retrieveActiveEvents(
		@NonNull String accountName){
		
		List<Event> activeEvents = 
			realtimeStatus.filter(Event.class,
				"lastUser=" + accountName,	
				"lastState=" + StateValue.In_Proccess.name(),
				"lastState=" + StateValue.On_Hold.name());
		
		if(!activeEvents.isEmpty()) {
			return activeEvents;
		}else {
			//throw new IllegalArgumentException();
			return null;
		}
	}
	
	public List<Event> retrieveSolvedEvents(
		@NonNull String accountName){
		
		List<Event> activeEvents = 
			realtimeStatus.filter(Event.class,
				"lastUser=" + accountName,	
				"lastState=" + StateValue.Verification.name(),
				"lastState=" + StateValue.On_Hold.name());
		
		if(!activeEvents.isEmpty()) {
			return activeEvents;
		}else {
			//throw new IllegalArgumentException();
			return null;
		}
	}
	
	//----------------------------------------------
	
	//Update event state methods -------------------
	public void assignEvent(Event event, 
			String authorName, boolean manual) {
		
		if(manual) {
			
		}
		
		Controller controller = 
			(Controller) operatorsService.retrieveOperator(
				authorName, UserType.Controller
			);
		
		createTrack(event, controller, StateValue.Assigned);
		
		realtimeStatus.store(Event.class, event);
		realtimeStatus.storeToList(
			Controller.class, controller, Event.class, event);
		
	}
	
	public void acceptEvent(String authorName, String code) {
		
		//Retrieves current data
		Event event = retrieveInRealTime(code);
		Controller controller = 
			(Controller) operatorsService.retrieveOperator(
				authorName, UserType.Controller
			);
		
		//Verifies that the user has permission to update
		verifyOwnership(event, controller.getAccountName());
		
		//Verifies next state
		statesService.verifyNextState(
			event.getLastEventTrack().getState(), 
			StateValue.In_Proccess
		);
		
		//Creates trace
		createTrack(event, controller, StateValue.In_Proccess);
		
		//Updates Controller state
		controller.setLastEvent(event);
		operatorsService.changeState(
			UserType.Controller, controller, 
			StateValue.Busy
		);
		
		//Update operation status
		realtimeStatus.store(Event.class, event);

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
			Controller author, StateValue eventState) {
		
		State state = statesService.retrieve(eventState);
		
		EventTrack lastEventTrack = event.getLastEventTrack();
		lastEventTrack.setEndTime(
				new Timestamp(System.currentTimeMillis()));
		
		EventTrack newEventTrack = new EventTrack(
			lastEventTrack.getPriority(),
			author, 
			state
		);
		
		event.addEventTrack(newEventTrack);
		event = eventRepository.save(event);	
		
	}
	
	public void rejectEvent(String authorName,
			String code, EventRemark inputFields) {
		
		//Retrieves current data
		Event event = retrieveInRealTime(code);
		Controller controller = 
			(Controller) operatorsService.retrieveOperator(
				authorName, UserType.Controller
			);
		
		//Verifies that the user has permission to update
		verifyOwnership(event, authorName);
		
		//Verify next state
		statesService.verifyNextState(
			event.getLastEventTrack().getState(), 
			StateValue.Pending
		);
		
		//Create trace and add justification
		EventRemark remark = new EventRemark(
				inputFields.getContent(), controller);
		
		event.getLastEventTrack().addEventRemark(remark);
		
		createTrack(event, controller, StateValue.Pending);
		
		//Update operation status
		realtimeStatus.store(Event.class, event);
		resourcePlanning.addPendingEvent(event);
		
		if(operatorsService.isAvailable(controller)) {
			
			operatorsService.changeState(
				UserType.Controller, controller, 
				StateValue.Available
			);
			
			resourcePlanning.addAvailableController(controller);
			
		}
		
	}
	
	public void completeEvent(String authorName, String code) {
		
		//Retrieves current data
		Event event = retrieveInRealTime(code);
		Controller controller = 
			(Controller) operatorsService.retrieveOperator(
				authorName, UserType.Controller
			);
		
		//Verifies that the user has permission to update
		verifyOwnership(event, authorName);

		//Verifies next state
		statesService.verifyNextState(
			event.getLastEventTrack().getState(), 
			StateValue.Verification
		);
		
		//Create trace
		createTrack(event, controller, StateValue.Verification);
		
		//Update operation status
		realtimeStatus.store(Event.class, event);
		
		if(operatorsService.isAvailable(controller)) {
			
			operatorsService.changeState(
				UserType.Controller, controller, 
				StateValue.Available
			);
			
			resourcePlanning.addAvailableController(controller);
			
		}

	}
	
	public void sendBack(String code, String authorName,
			EventRemark inputFields) {
		
		//Retrieves current data
		Event event = retrieveInRealTime(code);
		Controller controller = 
			(Controller) operatorsService.retrieveOperator(
				authorName, UserType.Controller
			);
		
		//Verifies that the user has permission to update
		verifyOwnership(event, authorName);

		//Verifies next state
		statesService.verifyNextState(
			event.getLastEventTrack().getState(), 
			StateValue.Pending
		);
		
		//Creates trace
		EventRemark remark = new EventRemark(
				inputFields.getContent(), controller);
		
		event.getLastEventTrack().addEventRemark(remark);
		
		createTrack(event, controller, StateValue.Pending);
		
		//Update operation status
		realtimeStatus.store(Event.class, event);
		resourcePlanning.addPendingEvent(event);

	}
	
	public void putOnHold(String code, String authorName,
			EventRemark inputFields) {
		
		//Retrieves current data
		Event event = retrieveInRealTime(code);
		Controller controller = 
			(Controller) operatorsService.retrieveOperator(
				authorName, UserType.Controller
			);
		
		//Verifies that the user has permission to update
		verifyOwnership(event, authorName);

		//Verifies next state
		statesService.verifyNextState(
			event.getLastEventTrack().getState(), 
			StateValue.On_Hold
		);
		
		//Creates trace
		EventRemark remark = new EventRemark(
				inputFields.getContent(), controller);
		
		event.getLastEventTrack().addEventRemark(remark);
		
		createTrack(event, controller, StateValue.On_Hold);
		
		//Updates operation status
		realtimeStatus.store(Event.class, event);
		
		if(operatorsService.isAvailable(controller)) {
			
			operatorsService.changeState(
				UserType.Controller, controller, 
				StateValue.Available
			);
			
			resourcePlanning.addAvailableController(controller);
			
		}
		
	}
	
	public void resumeEvent(String code, String authorName) {
		
		//Retrieves current data
		Event event = retrieveInRealTime(code);
		Controller controller = 
			(Controller) operatorsService.retrieveOperator(
				authorName, UserType.Controller
			);
		
		//Verifies that the user has permission to update
		verifyOwnership(event, authorName);
		
		//Verifies next state
		statesService.verifyNextState(
			event.getLastEventTrack().getState(), 
			StateValue.In_Proccess
		);
		
		//Create trace
		createTrack(event, controller, StateValue.In_Proccess);
		
		//Update operation status
		realtimeStatus.store(Event.class, event);
		
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
		realtimeStatus.store(Event.class, event);
		
	}
	//----------------------------------------------
	
	//Business methods -----------------------------
	public void changePriority(String authorName, 
			String code, int priority) {
		
		//Verify that the user has permission to update
		Event event = retrieveInRealTime(code);
		verifyOwnership(event, authorName);
	
		//Create trace
		createTrack(event, authorName, priority);
		
		//Update operation status
		realtimeStatus.store(Event.class, event);
		resourcePlanning.updateEventPriority(event);

	}
	
	private void createTrack(Event event, 
			String author, int priority) {
		
		Controller user = 
			(Controller) operatorsService.retrieveOperator(
				author, UserType.Controller
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
	
	public void completeProtocolStep(String code, String stepCode) {
		
		Event event = retrieveInRealTime(code);
		
		//Find protocol
		List<Protocol> eventProtocols = 
			event.getCategory().getProtocols();
		
		Optional<Protocol> stepProtocol = 
			eventProtocols.stream()
				.filter(p -> 
					p.getStep().getCode().equals(stepCode)
				).findAny();
		
		if(!stepProtocol.isPresent()) {
			
			throw new ATCRuntimeException(
				"Step with code" + stepCode + " does not exist "
				+ "for this event. Review the event protocols "
				+ "to make sure which steps are valid."
			);
			
		}else {
			
			//Create trace
			ProtocolTrack track = 
				new ProtocolTrack(true, stepProtocol.get());
			
			event.addProtocolTrack(track);
			event = eventRepository.save(event);
			
			//Update operation status
			realtimeStatus.store(Event.class, event);
		}
		
	}
	//----------------------------------------------
	
}
