package co.edu.icesi.metrocali.atc.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import co.edu.icesi.metrocali.atc.constants.PermissionLevels;
import co.edu.icesi.metrocali.atc.dtos.InEventMessage;
import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.EventRemarks;
import co.edu.icesi.metrocali.atc.entities.events.Step;
import co.edu.icesi.metrocali.atc.services.entities.EventsService;

/**
 * Represents the event API. Responsible for handling 
 * requests to event services. The API can be implemented in 
 * multiple technologies, which is why mandatory services are 
 * defined. It is recommended to think about communication based 
 * on gRPC, SSE/Websocket and the HTTP/2 protocol. For more 
 * information (see Effective Java, by Josh Bloch pp. 213).
 * 
 * The Events API concrete implementation. It contains all the 
 * services that deal with the events and their states. It uses REST 
 * technology over the HTTP protocol. For more information 
 * see ATC API Documentation<br>
 * 
 * <b>TODO</b> implement more efficient options.
 * @author <a href="mailto:johan.ballesteros@outlook.com">Johan Ballesteros</a>
 */
@RestController
@RequestMapping("/atc/events")
public class HTTPRestEventsAPI {
	
	private EventsService eventService;
	
	@Autowired
	public HTTPRestEventsAPI(EventsService eventsService) {
		this.eventService = eventsService;
	}
	
	//CRUD Event ----------------------------------
	@PreAuthorize("hasRole('" + PermissionLevels.OMEGA + "') "
		+ "|| hasRole('" + PermissionLevels.DATAGRAMS + "') "
		+ "|| hasRole('" + PermissionLevels.IMPACT_MATRIX + "') "
		+ "|| hasRole('" + PermissionLevels.ANALYTICS + "')"
	)
	@PostMapping
	public ResponseEntity<String> createEvent(
			@RequestBody InEventMessage message) {
		try {
			
			String eventCode = eventService.createEvent(message);
			return new ResponseEntity<String>(eventCode, HttpStatus.OK);
			
		}catch(Exception e) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}	
	}
	//---------------------------------------------
	
	//Change Event state (Track) ------------------
	@PreAuthorize("hasRole('" + PermissionLevels.CONTROLLER + "')")
	@PatchMapping("/accepted/{accountName}/{eventCode}")
	public ResponseEntity<HttpStatus> acceptEvent(
			@PathVariable("accountName") String accountName, 
			@PathVariable("eventCode") String eventCode) {
		try {
			eventService.acceptEvent(accountName, eventCode);
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('" + PermissionLevels.CONTROLLER + "')")
	@PatchMapping("/rejected/{accountName}/{eventCode}")
	public ResponseEntity<HttpStatus> rejectEvent(
			@PathVariable("accountName") String accountName, 
			@PathVariable("eventCode") String eventCode,
			@RequestBody EventRemarks remark) {
		try {
			eventService.rejectEvent(accountName, eventCode, remark);
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('" + PermissionLevels.CONTROLLER + "')")
	@PatchMapping("/completed/{accountName}/{eventCode}")
	public ResponseEntity<HttpStatus> completeEvent(
			@PathVariable("accountName") String accountName,
			@PathVariable("eventCode") String eventCode) {
		try {
			eventService.completeEvent(accountName, eventCode);
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('" + PermissionLevels.CONTROLLER + "')")
	@PatchMapping("/on_holded/{accountName}/{eventCode}")
	public ResponseEntity<HttpStatus> putOnHoldEvent(
			@PathVariable("accountName") String accountName,
			@PathVariable("eventCode") String eventCode,
			@RequestBody EventRemarks remark){
		try {
			eventService.completeEvent(accountName, eventCode);
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('" + PermissionLevels.OMEGA + "')")
	@PatchMapping("/validated/{accountName}/{eventCode}")
	public ResponseEntity<HttpStatus> validateEvent(
			@PathVariable("accountName") String accountName,
			@PathVariable("eventCode") String eventCode){
		try {
			eventService.completeEvent(accountName, eventCode);
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PreAuthorize("hasRole('" + PermissionLevels.OMEGA + "')")
	@PatchMapping("/send_back/{accountName}/{eventCode}")
	public ResponseEntity<HttpStatus> sendBackEvent(
			@PathVariable("accountName") String accountName, 
			@PathVariable("eventCode") String eventCode,
			@RequestBody EventRemarks remark) {
		try {
			eventService.sendBack(accountName, eventCode, remark);
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	//---------------------------------------------
	
	//CRUD Protocol step
	@PreAuthorize("hasRole('" + PermissionLevels.CONTROLLER + "')")
	@PostMapping("/{eventCode}/protocols")
	public ResponseEntity<HttpStatus> completeProtocolStep(
			@PathVariable("eventCode") String eventCode,
			@RequestBody Step step) {
		try {
			eventService.completeProtocolStep(eventCode, 
					step.getDescription());
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	//---------------------------------------------
	
	//Retrive information -------------------------	
	@GetMapping("/{accountName}/assigned_events")
	public ResponseEntity<List<Event>> retrieveAssignedEvents(
			@PathVariable String accountName) {
		try {
			return new ResponseEntity<List<Event>>(
				eventService.retrieveAssignedEvents(accountName),
				HttpStatus.OK
			);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
		
	@GetMapping("/{accountName}/active_events")
	public ResponseEntity<List<Event>> retrieveActiveEvents(
			@PathVariable String accountName) {
		try {
			return new ResponseEntity<List<Event>>(
				eventService.retrieveActiveEvents(accountName),
				HttpStatus.OK
			);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping("/categories")
	public ResponseEntity<List<Category>> retrieveCategories(){
		return new ResponseEntity<List<Category>>(
				eventService.retrieveAllCategories(true), HttpStatus.OK);
	}
	//---------------------------------------------
	
	//CRUD Remark ---------------------------------
	@PreAuthorize("hasRole('" + PermissionLevels.CONTROLLER + "') "
		+ "|| hasRole('" + PermissionLevels.OMEGA + "') "
		+ "|| hasRole('" + PermissionLevels.SUPERVISOR + "')"
	)
	@PostMapping("/{accountName}/{eventCode}/event_tracks/last")
	public ResponseEntity<HttpStatus> createRemark(
			@PathVariable("accountName") String accountName,
			@PathVariable("eventCode") String eventCode,
			@RequestBody JsonNode jsonContent){
		try {
			String content = jsonContent.get("content").asText(); 
			eventService.createEventRemark(
				eventCode, content, accountName
			);
			return new ResponseEntity<>(HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	//---------------------------------------------
	
	//Update Priority Queue -----------------------
	@PreAuthorize("hasRole('" + PermissionLevels.OMEGA + "')")
	@PatchMapping("/{accountName}/{eventCode}/priority/{priority}")
	public ResponseEntity<HttpStatus> updatePriority(
			@RequestParam String accountName,
			@RequestParam String eventCode,
			@RequestParam int priority,
			@RequestBody EventRemarks remark) {
		try {
			eventService.changePriority(accountName, eventCode, priority);
			return ResponseEntity.ok().build();
		}catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	//---------------------------------------------
	
}
