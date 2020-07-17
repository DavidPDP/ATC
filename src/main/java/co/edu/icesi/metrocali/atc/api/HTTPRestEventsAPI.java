package co.edu.icesi.metrocali.atc.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.EventRemark;
import co.edu.icesi.metrocali.atc.entities.events.Step;
import co.edu.icesi.metrocali.atc.services.entities.EventsService;
import co.edu.icesi.metrocali.atc.vos.ComplexOutputMessage;

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
	
	@GetMapping
	public ResponseEntity<List<Event>> retrieveAll(
			@RequestParam Boolean current) {
		
		List<Event> events = eventService.retrieveAll(current);
		
		return ResponseEntity.ok(events);
		
	}
	
	//CRUD Event ----------------------------------
//	@PreAuthorize("hasRole('" + PermissionLevel.OMEGA + "') "
//		+ "|| hasRole('" + PermissionLevel.DATAGRAMS + "') "
//		+ "|| hasRole('" + PermissionLevel.IMPACT_MATRIX + "') "
//		+ "|| hasRole('" + PermissionLevel.ANALYTICS + "')"
//	)
	@PostMapping("/{authorName}")
	public ResponseEntity<ComplexOutputMessage> createEvent(
			@PathVariable String authorName, 
			@RequestBody Event event) {
			
		String code = 
			eventService.createEvent(authorName, event);
		
		ComplexOutputMessage responseBody = 
				new ComplexOutputMessage();
		
		responseBody.addField("code", code);
		
		return ResponseEntity.ok(responseBody);
			
	}
	//---------------------------------------------
	
	//Change Event state (Track) ------------------
	//@PreAuthorize("hasRole('" + PermissionLevel.CONTROLLER + "')")
	@PatchMapping("/accepted/{authorName}/{eventCode}")
	public ResponseEntity<HttpStatus> acceptEvent(
			@PathVariable String authorName, 
			@PathVariable String eventCode) {
		
		eventService.acceptEvent(authorName, eventCode);
		return ResponseEntity.ok().build();
		
	}
	
	//@PreAuthorize("hasRole('" + PermissionLevel.CONTROLLER + "')")
	@PatchMapping("/rejected/{authorName}/{eventCode}")
	public ResponseEntity<HttpStatus> rejectEvent(
			@PathVariable String authorName, 
			@PathVariable String eventCode,
			@RequestBody EventRemark remark) {

		eventService.rejectEvent(authorName, eventCode, remark);
		return ResponseEntity.ok().build();

	}
	
	//@PreAuthorize("hasRole('" + PermissionLevel.CONTROLLER + "')")
	@PatchMapping("/comt/{authorName}/{eventCode}")
	public ResponseEntity<HttpStatus> rejectEvent(
			@PathVariable String authorName, 
			@PathVariable String eventCode) {

		eventService.completeEvent(authorName, eventCode);
		return ResponseEntity.ok().build();

	}
	
	//@PreAuthorize("hasRole('" + PermissionLevel.CONTROLLER + "')")
	@PatchMapping("/on_holded/{authorName}/{eventCode}")
	public ResponseEntity<HttpStatus> putOnHoldEvent(
			@PathVariable String authorName,
			@PathVariable String eventCode,
			@RequestBody EventRemark remark){

		eventService.putOnHold(eventCode, authorName, remark);
		return ResponseEntity.ok().build();
	
	}
	
	//@PreAuthorize("hasRole('" + PermissionLevel.CONTROLLER + "')")
	@PatchMapping("/resume/{authorName}/{eventCode}")
	public ResponseEntity<HttpStatus> resumeEvent(
			@PathVariable String authorName,
			@PathVariable String eventCode){

		eventService.resumeEvent(eventCode, authorName);
		return ResponseEntity.ok().build();
	
	}
	
	//@PreAuthorize("hasRole('" + PermissionLevel.OMEGA + "')")
	@PatchMapping("/validated/{authorName}/{eventCode}")
	public ResponseEntity<HttpStatus> validateEvent(
			@PathVariable String authorName,
			@PathVariable String eventCode){

		eventService.verifyEvent(eventCode, authorName);
		return ResponseEntity.ok().build();

	}
	
	//@PreAuthorize("hasRole('" + PermissionLevel.OMEGA + "')")
	@PatchMapping("/send_back/{authorName}/{eventCode}")
	public ResponseEntity<HttpStatus> sendBackEvent(
			@PathVariable String authorName, 
			@PathVariable String eventCode,
			@RequestBody EventRemark remark) {

		eventService.sendBack(eventCode, authorName, remark);
		return ResponseEntity.ok().build();

	}
	//---------------------------------------------
	
	//CRUD Protocol step
	//@PreAuthorize("hasRole('" + PermissionLevel.CONTROLLER + "')")
	@PostMapping("/{eventCode}/protocols")
	public ResponseEntity<HttpStatus> completeProtocolStep(
			@PathVariable("eventCode") String eventCode,
			@RequestBody Step step) {

		eventService.completeProtocolStep(eventCode, 
				step.getDescription());
		return ResponseEntity.ok().build();

	}
	//---------------------------------------------
	
	//Retrive information -------------------------	
	@GetMapping("/controllers/{authorName}/history")
	public ResponseEntity<ComplexOutputMessage> history(
		@PathVariable String authorName){
		
		List<Event> assignedEvents = 
			eventService.retrieveAssignedEvents(authorName);
		List<Event> activeEvents = 
			eventService.retrieveActiveEvents(authorName);
		
		ComplexOutputMessage outputBody = 
			new ComplexOutputMessage();
		
		outputBody.addField("assignedEvents", assignedEvents);
		outputBody.addField("activeEvents", activeEvents);
		outputBody.addField("solvedEvents", null);
		
		return ResponseEntity.ok(outputBody);
		
	}
	//---------------------------------------------
	
	//CRUD Remark ---------------------------------
//	@PreAuthorize("hasRole('" + PermissionLevel.CONTROLLER + "') "
//		+ "|| hasRole('" + PermissionLevel.OMEGA + "') "
//		+ "|| hasRole('" + PermissionLevel.SUPERVISOR + "')"
//	)
	@PostMapping("/{authorName}/{eventCode}/event_tracks/last")
	public ResponseEntity<HttpStatus> createRemark(
			@PathVariable String authorName,
			@PathVariable String eventCode,
			@RequestBody JsonNode jsonContent){
//		try {
//			String content = jsonContent.get("content").asText(); 
//			eventService.createEventRemark(
//				eventCode, content, accountName
//			);
//			return new ResponseEntity<>(HttpStatus.OK);
//		}catch (Exception e) {
//			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//		}
		return null;
	}
	//---------------------------------------------
	
	//Update Priority Queue -----------------------
	//@PreAuthorize("hasRole('" + PermissionLevel.OMEGA + "')")
	@PatchMapping("/{accountName}/{eventCode}/priority/{priority}")
	public ResponseEntity<HttpStatus> updatePriority(
			@RequestParam String accountName,
			@RequestParam String eventCode,
			@RequestParam int priority,
			@RequestBody EventRemark remark) {

		eventService.changePriority(accountName, eventCode, priority);
		return ResponseEntity.ok().build();

	}
	//---------------------------------------------
	
}
