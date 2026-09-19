package pt.seixal.carlos.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static pt.seixal.carlos.mapper.ObjectMapper.parseObject;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import pt.seixal.carlos.controllers.PersonController;
import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.exceptions.BadRequestException;
import pt.seixal.carlos.exceptions.FileStorageException;
import pt.seixal.carlos.exceptions.RequiredObjectIsNullException;
import pt.seixal.carlos.exceptions.ResourceNotFoundException;
import pt.seixal.carlos.file.exporter.contract.FileExporter;
import pt.seixal.carlos.file.exporter.factory.FileExporterFactory;
import pt.seixal.carlos.file.importer.contract.FileImporter;
import pt.seixal.carlos.file.importer.factory.FileImporterFactory;
import pt.seixal.carlos.model.Person;
import pt.seixal.carlos.repository.PersonRepository;

@Service
public class PersonService {

	private final Logger logger = LoggerFactory.getLogger(PersonService.class);

	@Autowired
	PersonRepository repository;

	@Autowired
	FileImporterFactory importer;

	@Autowired
	FileExporterFactory exporter;

	@Autowired
	PagedResourcesAssembler<PersonDTO> assembler;

	public PagedModel<EntityModel<PersonDTO>> findAll(Map<String, String> params) {
		logger.info("Finding all people!");

		return buildPageModel(params, repository.findAll(getPageable(params)));
	}

	public PagedModel<EntityModel<PersonDTO>> findByName(String firstName, Map<String, String> params) {
		logger.info("Finding People!");
		return buildPageModel(params, repository.findPeopleByName(firstName, getPageable(params)));
	}

	public Resource exportPage(Map<String, String> params, String acceptHeader) {
		logger.info("Finding all people!");

		var people = repository.findAll(getPageable(params))
				.map(person -> parseObject(person, PersonDTO.class))
				.getContent();

		FileExporter exporter = this.exporter.getExporter(acceptHeader);

		try {
			return exporter.exportPeople(people);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error exporting file: " + e.getMessage(), e);
		}
	}

	public Resource exportPerson(Long id, String acceptHeader) {
		logger.info("Exporting data of one Person!");

		var dto = parseObject(getPerson(id), PersonDTO.class);

		FileExporter exporter = this.exporter.getExporter(acceptHeader);

		try {
			return exporter.exportPerson(dto);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error exporting file: " + e.getMessage(), e);
		}
	}

	public PersonDTO findById(Long id) {
		logger.info("Finding one Person!");
		var dto = parseObject(getPerson(id), PersonDTO.class);
		addHateoasLinks(dto);
		return dto;
	}

	public PersonDTO create(PersonDTO person) {
		logger.info("Creating Person");
		if (person == null) {
			throw new RequiredObjectIsNullException();
		}

		var entity = parseObject(person, Person.class);
		var dto = parseObject(repository.save(entity), PersonDTO.class);
		addHateoasLinks(dto);
		return dto;
	}

	public List<PersonDTO> massCreation(MultipartFile file) {
		logger.info("Importing people from file!");
		if (file == null || file.isEmpty()) {
			throw new BadRequestException("Please set a valid File!");
		}

		try (InputStream inputStream = file.getInputStream()) {
			String filename = Optional.ofNullable(file.getOriginalFilename())
					.orElseThrow(() -> new BadRequestException("File name is missing"));

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
		return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No record found for this id"));
	}

	public PersonDTO update(PersonDTO person) {
		logger.info("Edit Person");

		if (person == null) {
			throw new RequiredObjectIsNullException();
		}

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

	private Pageable getPageable(Map<String, String> params) {
		int page = params.containsKey("page") ? Integer.parseInt(params.get("page")) : 0;
		int size = params.containsKey("size") ? Integer.parseInt(params.get("size")) : 12;

		Sort sort = Sort.unsorted();

		if (params.containsKey("direction") && params.containsKey("sort")) {

			String sortColumn = params.get("sort");

			switch (params.get("direction").toLowerCase()) {
			case "desc":
				sort = Sort.by(Direction.DESC, sortColumn);
			case "asc":
				sort = Sort.by(Direction.ASC, sortColumn);
			}
		}

		Pageable pageable = PageRequest.of(page, size, sort);
		return pageable;
	}

	private PagedModel<EntityModel<PersonDTO>> buildPageModel(Map<String, String> params, Page<Person> people) {
		var peopleWithLinks = people.map(person -> {
			var dto = parseObject(person, PersonDTO.class);
			addHateoasLinks(dto, params);
			return dto;
		});

		Link findAllLink = linkTo(
				methodOn(PersonController.class).findAll(params)).withSelfRel();

		return assembler.toModel(peopleWithLinks, findAllLink);
	}

	private void addHateoasLinks(PersonDTO dto) {
		addHateoasLinks(dto, null);
	}

	private void addHateoasLinks(PersonDTO dto, Map<String, String> params) {
		dto.add(linkTo(methodOn(PersonController.class).findById(dto.getId())).withSelfRel().withType("GET"));
		dto.add(linkTo(methodOn(PersonController.class).findAll(params))
				.withRel("findAll").withType("GET"));

		dto.add(linkTo(methodOn(PersonController.class).findByName("and", params)).withRel("findByName")
				.withType("GET"));
		dto.add(linkTo(methodOn(PersonController.class).create(dto)).withRel("create").withType("POST"));
		dto.add(linkTo(methodOn(PersonController.class)).slash("massCreation").withRel("massCreation")
				.withType("POST"));
		dto.add(linkTo(methodOn(PersonController.class).update(dto)).withRel("update").withType("PUT"));
		dto.add(linkTo(methodOn(PersonController.class).disablePerson(dto.getId())).withRel("disable")
				.withType("PATH"));
		dto.add(linkTo(methodOn(PersonController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
		dto.add(linkTo(methodOn(PersonController.class).exportPage(params, null)).withRel("exportPage")
				.withType("GET").withTitle("Export People"));
	}
}
