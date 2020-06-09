package co.edu.icesi.metrocali.atc.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.icesi.metrocali.atc.constants.OperatorType;
import co.edu.icesi.metrocali.atc.constants.SettingKey;
import co.edu.icesi.metrocali.atc.entities.events.UserTrack;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.exceptions.bb.BadRequestException;
import co.edu.icesi.metrocali.atc.services.entities.OperatorsService;
import co.edu.icesi.metrocali.atc.services.entities.SettingsService;

@RestController
@RequestMapping("/atc/operators")
public class HTTPRestOperatorsAPI {

	private OperatorsService operatorsService;
	
	private SettingsService settingsService;
	
	public HTTPRestOperatorsAPI(OperatorsService operatorsService,
			SettingsService settingsService) {
		
		this.operatorsService = operatorsService;
		this.settingsService = settingsService;
		
	}
	
	//CRUD Operator ----------------------------------
	@GetMapping
	public ResponseEntity<List<User>> retrieveAllOperators(){
		
		return ResponseEntity.ok(
			operatorsService.retrieveAllOperators()
		);
		
	}
	
	@PostMapping
	public ResponseEntity<HttpStatus> createOperator(
			@RequestBody User operator) {
		try {
			operatorsService.persistOperator(operator);
			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
		}catch(BadRequestException e) {
			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
		}catch(ATCRuntimeException e) {
			return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PatchMapping
	public ResponseEntity<HttpStatus> updateOperator(
			@RequestBody User operator) {
		try {
			operatorsService.persistOperator(operator);
			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
		}catch(BadRequestException e) {
			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
		}catch(ATCRuntimeException e) {
			return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/{accountName}")
	public ResponseEntity<HttpStatus> deleteOperator(
			@PathVariable String accountName) {
		try {
			operatorsService.deleteOperator(accountName);
			return new ResponseEntity<HttpStatus>(HttpStatus.OK);
		}catch(BadRequestException e) {
			e.printStackTrace();
			return new ResponseEntity<HttpStatus>(HttpStatus.BAD_REQUEST);
		}catch(ATCRuntimeException e) {
			e.printStackTrace();
			return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	//------------------------------------------------
	
	//Controllers -------------------------------
	@GetMapping("/controllers/{accountName}")
	public ResponseEntity<Controller> retrieveController(
			@PathVariable String accountName) {
		return new ResponseEntity<Controller>(
			(Controller) operatorsService.retrieveOperator(
				accountName, OperatorType.Controller
			),
			HttpStatus.OK
		);
		
	}
	
	@GetMapping("/controllers/{accountName}/history/{interval}")
	public ResponseEntity<List<UserTrack>> retrieveControllerHistory(
			@PathVariable String accountName,
			@PathVariable String interval){
		
		return ResponseEntity.ok(
			operatorsService.retrieveUserTrackHistory(
				accountName, interval
			)
		);
		
	}
	//------------------------------------------------
	
	//Settings ---------------------------------------
	@GetMapping("/settings/{current}")
	public ResponseEntity<List<Setting>> retrieveAllSettings(
		@PathVariable boolean current) {
		
		return ResponseEntity.ok(
			settingsService.retrieveAll(current)
		);
		
	}
	
	@GetMapping("/settings/{key}")
	public ResponseEntity<Setting> retrieveSetting(
		@PathVariable String key) {
	
		return ResponseEntity.ok(
			settingsService.retrieve(SettingKey.valueOf(key))
		);
		
	}
	
	@PostMapping("/settings")
	public ResponseEntity<HttpStatus> createSetting(
		@RequestBody Setting setting) {
		
		settingsService.save(setting);
		return ResponseEntity.ok().build();
		
	}
	
	@DeleteMapping("/settings/{key}")
	public ResponseEntity<Setting> deleteSetting(
		@PathVariable String key) {
		
		settingsService.delete(key);
		return ResponseEntity.ok().build();
		
	}
	//------------------------------------------------
}
