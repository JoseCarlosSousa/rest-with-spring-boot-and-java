package pt.seixal.carlos.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import pt.seixal.carlos.controllers.docs.PersonControllerDocs;
import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.file.exporter.MediaTypes;
import pt.seixal.carlos.services.PersonService;


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
	public ResponseEntity<Resource> exportPage(
        	@RequestParam(value = "page", defaultValue = "0") int page,
        	@RequestParam(value = "size", defaultValue = "12") int size,
        	@RequestParam(value = "direction", defaultValue = "asc") String direction, 
        	HttpServletRequest request) {
		
    	var sort = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
    	Pageable pageable = PageRequest.of(page, size, Sort.by(sort, "firstName"));
    	String acceptHeader = request.getHeader(HttpHeaders.ACCEPT);
    	
    	Resource file = service.exportPage(pageable, acceptHeader);
    	var contentType = acceptHeader != null ? acceptHeader : "application/octet-stream";
    	
		Map<String, String> extensionMap = Map.of(
				MediaTypes.CSV, ".csv",
				MediaTypes.XLSX, ".xlsx",
				MediaTypes.PDF, ".pdf"	
				);
    	var fileExtension = extensionMap.getOrDefault(contentType, "");
    	String dateSuffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    	var fileName = "people_exported_" + dateSuffix + fileExtension;
    	
    	return ResponseEntity.ok()
    			.contentType(MediaType.parseMediaType(contentType))
    			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
    			.body(file);
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
	public List<PersonDTO> massCreation(MultipartFile file) {
		return service.massCreation(file);
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
