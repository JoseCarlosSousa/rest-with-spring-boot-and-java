package pt.seixal.carlos.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.seixal.carlos.controllers.docs.PersonControllerDocs;
import pt.seixal.carlos.services.PersonService;
import pt.seixal.carlos.data.dto.v1.PersonDTO;


@RestController
@RequestMapping("/api/person/v1")
@Tag(name="People", description="Endpoints for ManagingPeople")
public class PersonController implements PersonControllerDocs {

    @Autowired
    private PersonService service;

    @Override
    public ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findAll(
        	@RequestParam(value = "page", defaultValue = "0") int page,
        	@RequestParam(value = "size", defaultValue = "12") int size,
        	@RequestParam(value = "direction", defaultValue = "asc") String direction
    ){
    	var sort = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
    	Pageable pageable = PageRequest.of(page, size, Sort.by(sort, "firstName"));
    	return ResponseEntity.ok(service.findAll(pageable));
    }
    
    @Override
    public ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findByName(
    		@PathVariable(value = "firstName") String firstName,
        	@RequestParam(value = "page", defaultValue = "0") int page,
        	@RequestParam(value = "size", defaultValue = "12") int size,
        	@RequestParam(value = "direction", defaultValue = "asc") String direction
    ){
    	var sort = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
    	Pageable pageable = PageRequest.of(page, size, Sort.by(sort, "firstName"));
    	return ResponseEntity.ok(service.findByName(firstName, pageable));
    }

    //@CrossOrigin(origins = "http://localhost:8080")
    @Override
    public PersonDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    //@CrossOrigin(origins = {"http://localhost:8080", "http://localhost:8090"})
    @Override
    public PersonDTO create(@RequestBody PersonDTO person) {
        return service.create(person);
    }

    @Override
    public PersonDTO update(@RequestBody PersonDTO person) {
        return service.update(person);
    }

    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

	@Override
	public PersonDTO disablePerson(Long id) {
		return service.disablePerson(id);
	}
}
