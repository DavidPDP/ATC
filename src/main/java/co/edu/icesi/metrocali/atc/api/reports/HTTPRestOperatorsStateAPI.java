package co.edu.icesi.metrocali.atc.api.reports;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.icesi.metrocali.atc.constants.UserType;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.operators.ControllerWorkState;
import co.edu.icesi.metrocali.atc.services.entities.ControllerWorkStateService;
import co.edu.icesi.metrocali.atc.services.entities.EntityServiceLookUp;
import co.edu.icesi.metrocali.atc.services.entities.OperatorsService;
import co.edu.icesi.metrocali.atc.vos.ComplexOutputMessage;
import lombok.NonNull;

@RestController
@RequestMapping("/atc/reports/operators_state")
public class HTTPRestOperatorsStateAPI {

	private ControllerWorkStateService controllerWorkStateService;
	
	private OperatorsService operatorsService;
	
	public HTTPRestOperatorsStateAPI(
		EntityServiceLookUp entityServiceLookUp) {
		
		this.controllerWorkStateService = 
			entityServiceLookUp.getEntityService(
				ControllerWorkStateService.class
			);
		
		this.operatorsService = 
			entityServiceLookUp.getEntityService(
				OperatorsService.class
			);
		
	}
	
	@GetMapping("/{accountName}/current_work")
	public ResponseEntity<ComplexOutputMessage> 
		currentWorkState(
			@PathVariable @NonNull String accountName) {
		
		ComplexOutputMessage bodyOutput = 
			new ComplexOutputMessage();
		
		//Retrieves the entities for the response
		Controller controller = 
			(Controller) operatorsService.retrieveOperator(
				accountName, UserType.Controller
			);
		
		ControllerWorkState workState = 
			controllerWorkStateService.retrieve(accountName);
		
		//Fills body response
		bodyOutput.addField("controller", controller);
		bodyOutput.addField("workState", workState);		
		
		return ResponseEntity.ok(bodyOutput);
		
	}
	
}
