package co.edu.icesi.metrocali.atc.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.icesi.metrocali.atc.constants.PermissionLevel;
import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.services.entities.CategoriesService;

@RestController
@RequestMapping("/atc/categories")
public class HTTPRestCategoriesAPI {

	private CategoriesService categoriesService;
	
	public HTTPRestCategoriesAPI(
			CategoriesService categoriesService) {
		
		this.categoriesService = categoriesService;
		
	}
	
	//CRUD -------------------------------
	@PreAuthorize("hasRole('" + PermissionLevel.ADMIN + "') "
		+ "|| hasRole('" + PermissionLevel.OMEGA + "') "
		+ "|| hasRole('" + PermissionLevel.CONTROLLER + "') "
	)
	@GetMapping
	public ResponseEntity<List<Category>> retrieveAll(
			@RequestParam Boolean current) {
		
		List<Category> categories = 
				categoriesService.retrieveAllCategories(current);
		return ResponseEntity.ok(categories);
		
	}
	
	@GetMapping("/{name}")
	public ResponseEntity<Category> retrieve(
			@PathVariable String name) {
		
		Category category = 
				categoriesService.retrieveCategory(name);
		return ResponseEntity.ok(category);
		
	}
	
	@PreAuthorize("hasRole('" + PermissionLevel.ADMIN + "')")
	@PostMapping
	public ResponseEntity<HttpStatus> create(
			@RequestBody Category category) {
		
		categoriesService.create(category);
		return ResponseEntity.ok().build();
		
	}
	
	@PreAuthorize("hasRole('" + PermissionLevel.ADMIN + "')")
	@PatchMapping
	public ResponseEntity<HttpStatus> update(
			@RequestBody Category category) {
		
		categoriesService.update(category);
		return ResponseEntity.ok().build();
		
	}
	
	@PreAuthorize("hasRole('" + PermissionLevel.ADMIN + "')")
	@DeleteMapping("/{name}")
	public ResponseEntity<HttpStatus> delete(
			@PathVariable String name) {
		
		categoriesService.delete(name);
		return ResponseEntity.ok().build();
		
	}
	//-----------------------------------------------
}
