package co.edu.icesi.metrocali.atc.api;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.icesi.metrocali.atc.entities.events.Step;
import co.edu.icesi.metrocali.atc.services.entities.StepsService;

@RestController
@RequestMapping("/atc/steps")
public class HTTPRestStepsAPI {

	private StepsService stepsService;
	
	public HTTPRestStepsAPI(StepsService stepsService) {
		
		this.stepsService = stepsService;
		
	}
	
	//CRUD -------------------------------
	@GetMapping
	public ResponseEntity<List<Step>> retrieveAll(
			@RequestParam @NonNull Boolean current) {
		
		List<Step> steps = stepsService.retrieveAll(current);
		
		return ResponseEntity.ok(steps);
		
	}
	
	@GetMapping("/{code}")
	public ResponseEntity<Step> retrieve(
			@PathVariable String code) {
		
		Step step = stepsService.retrieve(code);
		return ResponseEntity.ok(step);
		
	}
	
	//@PreAuthorize("hasRole('" + PermissionLevel.ADMIN + "')")
	@PostMapping
	public ResponseEntity<HttpStatus> save(
			@RequestBody @NonNull Step step) {
		
		stepsService.save(step);
		return ResponseEntity.ok().build();
		
	}
	
	//@PreAuthorize("hasRole('" + PermissionLevel.ADMIN + "')")
	@DeleteMapping("/{code}")
	public ResponseEntity<HttpStatus> delete(
			@PathVariable @NotBlank String code) {
		
		stepsService.delete(code);
		return ResponseEntity.ok().build();
		
	}
	//-----------------------------------------------
	
}
