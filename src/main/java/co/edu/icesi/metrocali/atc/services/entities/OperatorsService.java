package co.edu.icesi.metrocali.atc.services.entities;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.OperatorType;
import co.edu.icesi.metrocali.atc.constants.SettingKey;
import co.edu.icesi.metrocali.atc.entities.events.EventTrack;
import co.edu.icesi.metrocali.atc.entities.events.UserTrack;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.operators.Omega;
import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.exceptions.bb.BadRequestException;
import co.edu.icesi.metrocali.atc.exceptions.bb.BlackboxException;
import co.edu.icesi.metrocali.atc.repositories.OperatorsRepository;
import co.edu.icesi.metrocali.atc.services.planning.ResourcePlanning;
import co.edu.icesi.metrocali.atc.services.realtime.LocalRealtimeOperationStatus;

/**
 * Manages the services of the operators. This includes 
 * the management of their entities and their reports.
 * For more information, consult the types of users 
 * ({@link OperatorType}) that are currently managed.
 * 
 * @author <a href="mailto:johan.ballesteros@outlook.com">Johan Ballesteros</a>
 * 
 * @see OperatorType 
 * @see UserDetailsService
 * 
 */
@Service
public class OperatorsService implements UserDetailsService{
	
	private LocalRealtimeOperationStatus realtimeOpStatus;
	
	private OperatorsRepository operatorsRepository;
	
	private ResourcePlanning resourcePlanning;
	
	private BCryptPasswordEncoder bcryptEncoder;
	
	public OperatorsService(LocalRealtimeOperationStatus realtimeOpStatus,
			OperatorsRepository operatorsRepository,
			ResourcePlanning resourcePlanning,
			BCryptPasswordEncoder bcryptEncoder) {
		
		this.realtimeOpStatus = realtimeOpStatus;
		this.operatorsRepository = operatorsRepository;
		this.resourcePlanning = resourcePlanning;
		this.bcryptEncoder = bcryptEncoder;
		
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) 
			throws UsernameNotFoundException {
		
		Optional<User> user = 
			operatorsRepository.retrieveOperator(username);
		
		if(user.isPresent()) {
			System.out.println("HOLAAAAA LOAD");
			realtimeOpStatus.addOperator(user.get());
			return user.get();
			
		}else {
			throw new UsernameNotFoundException("Invalid username.");
		}
		
	}
	
	//Init/end operators in the system ---------------------
	public void registerOperator(@NonNull String accountName, 
			@NonNull OperatorType type) {
		
		//Retrieve user data from temporary sign-in memory
		Optional<User> operator = 
			realtimeOpStatus.retrieveOperator(accountName);
		
		//
		
		//Instantiate concrete user
		if(operator.isPresent()) {
			
			if(OperatorType.Controller.equals(type)) {
				
				Controller controller = new Controller();
				controller.fillUserData(operator.get());
				
				realtimeOpStatus.addOrUpdateController(controller);
				resourcePlanning.addAvailableController(controller);
				
			}else if(OperatorType.Omega.equals(type)) {
				
				Omega omega = new Omega();
				omega.fillUserData(operator.get());
				
				realtimeOpStatus.addOrUpdateOmega(omega);
				
			}else {
				throw new ATCRuntimeException("Operator type not found.", 
					new NoSuchElementException());
			}
			
			//Clear temporary memory
			realtimeOpStatus.removeOperator(accountName);
			
		}else {
			throw new ATCRuntimeException("User not found.", 
				new NoSuchElementException());
		}
		
	}
	
	public void unregisterOperator(@NonNull String accountName, 
			@NonNull OperatorType type) {
		
		if(OperatorType.Controller.equals(type)) {
			
			realtimeOpStatus.removeController(accountName);
			
		}else if(OperatorType.Omega.equals(type)) {
			
			realtimeOpStatus.removeOmega(accountName);
			
		}
		
	}
	//--------------------------------------------------
	
	//CRUD Operator ----------------------------------
	public List<User> retrieveAllOperators() {
		return operatorsRepository.retrieveAllOperators();
	}
	
	
	public User retrieveOperator(String accountName, OperatorType type) {
		
		if(type.equals(OperatorType.Controller)) {
			return retrieveController(accountName);
		}else if(type.equals(OperatorType.Omega)) {
			return retrieveOmega(accountName);
		}else {
			throw new NoSuchElementException();
		}
		
	}
	
	/**
	 * Retrieves user info with the shallow/deep copy strategy.
	 * @param accountName is the user's account name.
	 * @return the searched user info.
	 */
	private Controller retrieveController(@NonNull String accountName) {
		Optional<Controller> controller = 
				realtimeOpStatus.retrieveController(accountName);
		if(controller.isPresent()) {
			// shallow strategy
			return controller.get();
		}else {
			// deep strategy
			controller = operatorsRepository.retrieveController(accountName);
			if(controller.isPresent()) {
				realtimeOpStatus.addOrUpdateController(controller.get());
				return controller.get();
			}else {
				throw new NoSuchElementException(accountName 
						+ " account name doen't exists");
			}
		}
	}
	
	/**
	 * Retrieves user info with the shallow/deep copy strategy.
	 * @param accountName is the user's account name.
	 * @return the searched user info.
	 */
	public Omega retrieveOmega(String accountName) {
		
		Optional<Omega> omega = realtimeOpStatus.retrieveOmega(accountName);
		
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
	
	
	public void persistOperator(@NonNull User operator) {
		try {
			// Encrypt password
			String encryptedPassword = bcryptEncoder.encode(operator.getPassword());
			operator.setPassword(encryptedPassword);
			operatorsRepository.saveUser(operator);
		}catch(BlackboxException e) {
			
			if(e.getErrorCode().equals(HttpStatus.BAD_REQUEST)) {
				throw new BadRequestException("the request does "
					+ "not contain the mandatory information, see "
					+ "the API documentation.", e);
			}else {
				throw new ATCRuntimeException(
					"the request couldn't be processed.", e);
			}
			
		}
	}
	
	public List<Setting> retrieveAllSettings() {
		return operatorsRepository.retrieveAllSettings();
	}
	
	
	public void deleteOperator(@NonNull String accountName) {
		try {
			operatorsRepository.deleteUser(accountName);
		}catch(BlackboxException e) {
			
			if(e.getErrorCode().equals(HttpStatus.BAD_REQUEST)) {
				throw new BadRequestException(
					"the user doesn't exist", e);
			}else {
				throw new ATCRuntimeException(
					"the request couldn't be processed.", e);
			}
			
		}
	}
	
	public List<Controller> retrieveOnlineControllers(){
		return operatorsRepository.retrieveAllOnlineControllers();
	}
		
	public List<EventTrack> retrieveEventTrackHistory() {
		return null;
	}
	
	public List<UserTrack> retrieveUserTrackHistory(
			@NonNull String accountName, @NonNull String interval) {
		
		Optional<Setting> intervalTime = 
			realtimeOpStatus.retrieveSetting(SettingKey.User_Track_Time);
		
		if(intervalTime.isPresent()) {
			
			return operatorsRepository.retrieveUserTrackHistory(
				accountName, intervalTime.get().getValue()
			);
			
		}else {
			throw new ATCRuntimeException("User track time not found.",
				new NoSuchElementException());
		}
		
	}
}
