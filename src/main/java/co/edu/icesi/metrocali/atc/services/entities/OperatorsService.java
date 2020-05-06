package co.edu.icesi.metrocali.atc.services.entities;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.OperatorTypes;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.operators.Omega;
import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.exceptions.BadRequestException;
import co.edu.icesi.metrocali.atc.exceptions.BlackboxException;
import co.edu.icesi.metrocali.atc.repositories.OperatorsRepository;
import co.edu.icesi.metrocali.atc.services.ResourcePlanning;
import co.edu.icesi.metrocali.atc.services.oprealtime.LocalRealtimeOperationStatus;

@Service
public class OperatorsService {
	
	private LocalRealtimeOperationStatus realtimeOpStatus;
	
	private OperatorsRepository operatorsRepository;
	
	private ResourcePlanning resourcePlanning;
	
	@Autowired
	public OperatorsService(LocalRealtimeOperationStatus realtimeOpStatus,
			OperatorsRepository operatorsRepository,
			ResourcePlanning resourcePlanning) {
		this.realtimeOpStatus = realtimeOpStatus;
		this.operatorsRepository = operatorsRepository;
		this.resourcePlanning = resourcePlanning;
	}
	
	public User retrieveOperator(String accountName, OperatorTypes type) {
		
		if(type.equals(OperatorTypes.Controller)) {
			return retrieveController(accountName);
		}else if(type.equals(OperatorTypes.Omega)) {
			return retrieveOmega(accountName);
		}else {
			return null;
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
		return null;
	}
	
	
	
	public void registerOperator(@NonNull String accountName, 
			@NonNull OperatorTypes type) {
		if(OperatorTypes.Controller.equals(type)) {
			Controller controller = retrieveController(accountName);
			realtimeOpStatus.addOrUpdateController(controller);
			resourcePlanning.addAvailableController(controller);
		}else if(OperatorTypes.Omega.equals(type)) {
			
		}
	}

	
	public List<Setting> retrieveAllSettings() {
		return operatorsRepository.retrieveAllSettings();
	}
	
	public void persistOperator(@NonNull User operator) {
		try {
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
	
}
