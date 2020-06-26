package co.edu.icesi.metrocali.atc.services.entities;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.SettingKey;
import co.edu.icesi.metrocali.atc.constants.StateValue;
import co.edu.icesi.metrocali.atc.constants.UserType;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.State;
import co.edu.icesi.metrocali.atc.entities.events.UserTrack;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.operators.Omega;
import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.exceptions.bb.BadRequestException;
import co.edu.icesi.metrocali.atc.exceptions.bb.BlackboxException;
import co.edu.icesi.metrocali.atc.exceptions.bb.ResourceNotFound;
import co.edu.icesi.metrocali.atc.repositories.OperatorsRepository;
import co.edu.icesi.metrocali.atc.services.planning.ResourcePlanning;
import co.edu.icesi.metrocali.atc.services.realtime.RealtimeOperationStatus;

/**
 * Manages the services of the operators. This includes 
 * the management of their entities and their reports.
 * For more information, consult the types of users 
 * ({@link UserType}) that are currently managed.
 * 
 * @author <a href="mailto:johan.ballesteros@outlook.com">Johan Ballesteros</a>
 * 
 * @see UserType 
 * @see UserDetailsService
 * 
 */
@Service
public class OperatorsService implements UserDetailsService{
	
	private RealtimeOperationStatus realtimeOpStatus;
	
	private OperatorsRepository operatorsRepository;
	
	private StatesService statesService;
	
	private SettingsService settingService;
	
	private ResourcePlanning resourcePlanning;
	
	private BCryptPasswordEncoder bcryptEncoder;
	
	public OperatorsService(
			RealtimeOperationStatus realtimeOpStatus,
			OperatorsRepository operatorsRepository,
			StatesService statesService,
			SettingsService settingService,
			ResourcePlanning resourcePlanning,
			BCryptPasswordEncoder bcryptEncoder) {
		
		this.realtimeOpStatus = realtimeOpStatus;
		this.operatorsRepository = operatorsRepository;
		this.statesService = statesService;
		this.settingService = settingService;
		this.resourcePlanning = resourcePlanning;
		this.bcryptEncoder = bcryptEncoder;
		
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) 
			throws UsernameNotFoundException {
		
		User user = operatorsRepository.retrieve(username);
		
		//Update operation status
		realtimeOpStatus.store(User.class, user);
		
		return user;
		
	}
	
	//Init operators in the system ---------------------
	public void registerOperator(String accountName, 
			UserType type) {
		
		//Retrieve user data from temporary sign-in memory
		Optional<User> operator = 
			realtimeOpStatus.retrieve(User.class, accountName);
		
		//Instantiate concrete user
		if(operator.isPresent()) {
				
			initUserTracks(operator.get());
			
			if(UserType.Controller.equals(type)) {
				
				Controller controller = new Controller();
				controller.fillUserData(operator.get());
				
				realtimeOpStatus.store(Controller.class, controller);
				resourcePlanning.addAvailableController(controller);
				
			}else if(UserType.Omega.equals(type)) {
				
				Omega omega = new Omega();
				omega.fillUserData(operator.get());
				
				realtimeOpStatus.store(Omega.class, omega);
				
			}else {
				throw new ATCRuntimeException("Operator type not found.", 
					new NoSuchElementException());
			}
			
			//Clear temporary memory
			realtimeOpStatus.remove(User.class, accountName);
			
		}else {
			throw new ATCRuntimeException("User not found.", 
				new NoSuchElementException());
		}
		
	}
	
	private void initUserTracks(User user) {
		
		List<UserTrack> tracksToPersist = new ArrayList<>();
		
		//Creates new trace
		State state = statesService.retrieve(StateValue.Available);
		UserTrack newTrack = new UserTrack(user, state);
		
		//Retrieves last tracks
		List<UserTrack> lastTracks = history(user.getAccountName());
		
		//Verifies if have open traces and closes them if yes 
		if(!lastTracks.isEmpty()) {
			user.setUserTracks(lastTracks);
			UserTrack lastTrack = user.removeLastUserTrack();
			
			if(lastTrack.getEndTime() == null) {
				
				lastTrack.setEndTime(
					new Timestamp(System.currentTimeMillis()));
				
				tracksToPersist.add(lastTrack);
				
			}
			
		}
		
		tracksToPersist.add(newTrack);
		
		List<UserTrack> persistedTracks = 
				operatorsRepository.save(tracksToPersist);
		
		//Update persisted tracks
		user.getUserTracks().addAll(persistedTracks);
		
	}
	
	public List<UserTrack> history(String accountName) {
		
		Setting intervalTime = 
			settingService.retrieve(SettingKey.Query_Time_Window);
		
		List<UserTrack> tracks = null;
		
		try {
			tracks = operatorsRepository.retrieveUserTrackHistory(
				accountName, intervalTime.getValue()
			);
		}catch (ResourceNotFound e) {
			tracks = Collections.emptyList();
		}
		
		return tracks;
		
	}
	
	public void changeState(UserType userType, 
			User user, StateValue nextState) {
		
		//Verify next state is valid
		State currentState = user.getLastUserTrack().getState();
		statesService.verifyNextState(currentState, nextState);
		
		//Creates trace
		createTrack(user, nextState);
		
		//Updates operation status
		resolveUpdate(userType, user);
		
	}
	
	private void createTrack(User user, StateValue userState) {
		
		List<UserTrack> tracksToPersist = new ArrayList<>();
		
		//Closes last track
		UserTrack lastUserTrack = user.removeLastUserTrack();
		lastUserTrack.setEndTime(
				new Timestamp(System.currentTimeMillis()));
		
		tracksToPersist.add(lastUserTrack);
		
		//Creates new trace
		State state = statesService.retrieve(userState);
		UserTrack newUsertrack = new UserTrack(user, state);
		
		tracksToPersist.add(newUsertrack);
		
		List<UserTrack> persistedTrack = 
				operatorsRepository.save(tracksToPersist);
		
		//Update persisted tracks
		user.getUserTracks().addAll(persistedTrack);
		
	}
	
	private void resolveUpdate(UserType userType, User user) {
		if(userType.equals(UserType.Controller)) {
			realtimeOpStatus.store(
				Controller.class, (Controller) user);
		}else if(userType.equals(UserType.Omega)) {
			realtimeOpStatus.store(
					Omega.class, (Omega) user);
		}else if(userType.equals(UserType.Supervisor)) {
			
		}else {
			throw new ATCRuntimeException(
				"The " + userType.name() 
				+ " type is not supported."
			);
		}
	}
	
	public void unregisterOperator(String accountName, 
			UserType type) {
				
		if(UserType.Controller.equals(type)) {
			
			realtimeOpStatus.remove(Controller.class, accountName);
			
		}else if(UserType.Omega.equals(type)) {
			
			realtimeOpStatus.remove(Omega.class, accountName);
			
		}
		
	}
	//--------------------------------------------------
	
	//CRUD Operator ----------------------------------
	public List<User> retrieveAllOperators() {
		
		return operatorsRepository.retrieveAll();
		
	}
	
	public List<Controller> retrieveAllControllers() {
	
		return realtimeOpStatus.retrieveAll(Controller.class);
		
	}
	
	public List<Omega> retrieveAllOmegas() {
	
		return realtimeOpStatus.retrieveAll(Omega.class);
		
	}
	
	
	public User retrieveOperator(String accountName, 
			UserType type) {
		
		if(type.equals(UserType.Controller)) {
			return retrieveController(accountName);
		}else if(type.equals(UserType.Omega)) {
			return retrieveOmega(accountName);
		}else {
			throw new NoSuchElementException();
		}
		
	}
	
	public boolean isAvailable(Controller controller) {
		
		boolean isAvailable = false;
		
		List<Event> inProcessEvents = 
			realtimeOpStatus.filter(Event.class,
				"lastUser=" + controller.getAccountName(),	
				"lastState=" + StateValue.In_Proccess.name()
			);
		
		if(inProcessEvents.isEmpty()) {
			isAvailable = true;
		}
		
		return isAvailable;
		
	}
	
	/**
	 * Retrieves user info with the shallow/deep copy strategy.
	 * @param accountName is the user's account name.
	 * @return the searched user info.
	 */
	private Controller retrieveController(String accountName) {
		Optional<Controller> controller = 
				realtimeOpStatus.retrieve(
					Controller.class, accountName);
		if(controller.isPresent()) {
			// shallow strategy
			return controller.get();
		}else {
			// deep strategy
			Controller s = (Controller) operatorsRepository.retrieve(accountName);
			realtimeOpStatus.store(Controller.class, controller.get());
			return s;
		}
	}
	
	/**
	 * Retrieves user info with the shallow/deep copy strategy.
	 * @param accountName is the user's account name.
	 * @return the searched user info.
	 */
	private Omega retrieveOmega(String accountName) {
		
		Optional<Omega> omega = 
			realtimeOpStatus.retrieve(Omega.class, accountName);
		
		if(omega.isPresent()) {
			//Shallow strategy
			return omega.get();
		}else {
			return null;
			//omega = operatorsRepository.retrieveOperator(accountName);
		}
		
//		Optional<Omega> omega = realtimeOpStatus.retrieveOmega(accountName);
//		if(omega.isPresent()) {
//			// shallow strategy
//			return omega.get();
//		}else {
//			// deep strategy
//			omega = operatorsRepository.retrieveOmega(accountName);
//			if(omega.isPresent()) {
//				realtimeOpStatus.addOrUpdateController(omega.get());
//				return omega.get();
//			}else {
//				throw new NoSuchElementException();
//			}
//		}
	//	return null;
	}
	
	
	public void persistOperator(User operator) {
		
		// Encrypt password
		String encryptedPassword = bcryptEncoder.encode(operator.getPassword());
		operator.setPassword(encryptedPassword);
		operatorsRepository.save(operator);
			
	}
	
	
	public void deleteOperator(String accountName) {
		try {
			operatorsRepository.deleteUser(accountName);
		}catch(BlackboxException e) {
			
			if(e.getCode().equals(HttpStatus.BAD_REQUEST)) {
				throw new BadRequestException(
					"the user doesn't exist", e);
			}else {
				throw new ATCRuntimeException(
					"the request couldn't be processed.", e);
			}
			
		}
	}
	
	public List<Controller> retrieveOnlineControllers(){
		return operatorsRepository.retrieveOnlineControllers();
	}
	
	public List<Controller> retrieveOOControllers() {
		return realtimeOpStatus.retrieveAll(Controller.class);
	}

}
