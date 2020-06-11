package co.edu.icesi.metrocali.atc.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.icesi.metrocali.atc.entities.events.State;
import co.edu.icesi.metrocali.atc.services.entities.StatesService;

@RestController
@RequestMapping("/atc/states")
public class HTTPRestStatesAPI {

	private StatesService statesService;
	
	public HTTPRestStatesAPI(StatesService statesService) {
		
		this.statesService = statesService;
		
	}
	
	@GetMapping
	public ResponseEntity<List<State>> retrieveAll() {
		return ResponseEntity.ok(statesService.retrieveAll(true));
	}
	
	@GetMapping("/{name}")
	public ResponseEntity<State> retrieve(
			@NonNull @PathVariable String name) {
		//statesService.retrieve(name)
		return ResponseEntity.ok(null);
	}
	
	@GetMapping("/operators/{accountName}")
	public ResponseEntity<State> retrievej(
			@NonNull @PathVariable String accountName) {
		return null;
	}
}
