package pt.seixal.carlos.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.seixal.carlos.controllers.docs.PersonControllerDocs;
import pt.seixal.carlos.services.PersonService;
import pt.seixal.carlos.data.dto.v1.PersonDTO;

import java.util.List;

@RestController
@RequestMapping("/api/person/v1")
@Tag(name="People", description="Endpoints for ManagingPeople")
public class PersonController implements PersonControllerDocs {

    @Autowired
    private PersonService service;

    @Override
    public List<PersonDTO> findAll() {
        return service.findAll();
    }

    @Override
    public PersonDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

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
}
