package pt.seixal.carlos.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static pt.seixal.carlos.mapper.ObjectMapper.parseObject;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import pt.seixal.carlos.controllers.PersonController;
import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.exceptions.BadRequestException;
import pt.seixal.carlos.exceptions.FileStorageException;
import pt.seixal.carlos.exceptions.RequiredObjectIsNullException;
import pt.seixal.carlos.exceptions.ResourceNotFoundException;
import pt.seixal.carlos.file.importer.contract.FileImporter;
import pt.seixal.carlos.file.importer.factory.FileImporterFactory;
import pt.seixal.carlos.model.Person;
import pt.seixal.carlos.repository.PersonRepository;

@Service
public class PersonService {

    private final Logger logger = LoggerFactory.getLogger(PersonService.class.getName());

    @Autowired
    PersonRepository repository;

    @Autowired
    FileImporterFactory importer;
    
    @Autowired
    PagedResourcesAssembler<PersonDTO> assembler;

    public PagedModel<EntityModel<PersonDTO>> findAll(Pageable pageable) {
        logger.info("Finding all people!");
        
        var people = repository.findAll(pageable);
        
		return buildPageModel(pageable, people);
    }


    public PagedModel<EntityModel<PersonDTO>> findByName(String firstName, Pageable pageable) {
        logger.info("Finding People!");
        
        var people = repository.findPeopleByName(firstName, pageable);
        
		return buildPageModel(pageable, people);
    }

    public PersonDTO findById(Long id) {
        logger.info("Finding one Person!");
        var dto = parseObject(getPerson(id), PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public PersonDTO create(PersonDTO person) {
        logger.info("Creating Person");
        if (person == null) throw new RequiredObjectIsNullException();

        var entity = parseObject(person, Person.class);
        var dto = parseObject(repository.save(entity), PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public List<PersonDTO> massCreation(MultipartFile file) {
        logger.info("Importing people from file!");
        if (file == null || file.isEmpty()) throw new BadRequestException("Please set a valid File!");
        
		try (InputStream inputStream = file.getInputStream()) {
			String filename = Optional.ofNullable(file.getOriginalFilename()).orElseThrow(()-> new BadRequestException("File name is missing"));
			
			FileImporter importer = this.importer.getImporter(filename);
			
			List<Person> entities = importer.importFile(inputStream)
					.stream()
					.map(dto -> repository.save(parseObject(dto, Person.class)))
					.toList();
			
			return entities.stream()
					.map(entity -> {
						var dto = parseObject(entity, PersonDTO.class);
						addHateoasLinks(dto);
						return dto;
					})
					.toList();
		} catch (Exception e) {
			throw new FileStorageException("Error importing file: " + e.getMessage());
		}
    }

    
    private Person getPerson(Long id) {
        logger.info("Getting Person with id: {}", id);
        return repository.findById(id).orElseThrow(() ->  new ResourceNotFoundException("No record found for this id"));
    }

    public PersonDTO update(PersonDTO person) {
        logger.info("Edit Person");

        if (person == null) throw new RequiredObjectIsNullException();

        Person entity = getPerson(person.getId());
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        var dto = parseObject(repository.save(entity), PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id) {
        logger.info("delete Person");

        var entity = getPerson(id);

        repository.delete(entity);
    }

    @Transactional
    public PersonDTO disablePerson(Long id) {
        logger.info("disable Person");

        getPerson(id);
        repository.disablePerson(id);
        var dto = parseObject(getPerson(id), PersonDTO.class);
        addHateoasLinks(dto);
        return dto;
    }


	private PagedModel<EntityModel<PersonDTO>> buildPageModel(Pageable pageable, Page<Person> people) {
		var peopleWithLinks = people.map(person -> {
			var dto = parseObject(person, PersonDTO.class);
			addHateoasLinks(dto);
			return dto;
		});
		
		Link findAllLink = WebMvcLinkBuilder.linkTo(
				WebMvcLinkBuilder.methodOn(PersonController.class)
				.findAll(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort().toString()))
				.withSelfRel();
		
        return assembler.toModel(peopleWithLinks, findAllLink);
	}
    
    private void addHateoasLinks(PersonDTO dto) {
        dto.add(linkTo(methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findAll(1, 12, "asc")).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).findByName("", 1, 12, "asc")).withRel("findByName").withType("GET"));
        dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class)).slash("massCreation").withRel("massCreation").withType("POST"));
        dto.add(linkTo(methodOn(PersonController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(PersonController.class).disablePerson(dto.getId())).withRel("disable").withType("PATH"));
        dto.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
