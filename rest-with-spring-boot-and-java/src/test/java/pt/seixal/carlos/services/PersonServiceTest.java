package pt.seixal.carlos.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.test.util.ReflectionTestUtils;

import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.exceptions.RequiredObjectIsNullException;
import pt.seixal.carlos.model.Person;
import pt.seixal.carlos.repository.PersonRepository;
import pt.seixal.carlos.unitetests.mapper.mocks.MockPerson;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

	MockPerson input;

	@InjectMocks
	private PersonService service;

	@Mock
	PersonRepository repository;

	@Mock
	private PagedResourcesAssembler<Person> assembler;

	@BeforeEach
	void setUp() {
		input = new MockPerson();
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(service, "assembler", assembler);
	}

	@Test
	@SuppressWarnings("unchecked")
	void findAll() {
		List<Person> list = input.mockEntityList();
		Page<Person> page = new PageImpl<>(list);

		when(repository.findAll(any(Pageable.class))).thenReturn(page);

		List<EntityModel<PersonDTO>> entityModels = new java.util.ArrayList<>();
		for (int k = 1; k < list.size(); k++) {
			PersonDTO dto = input.mockDTO(k);

			dto.add(Link.of("/api/person/v1/" + k, "self").withType("GET"));
			dto.add(Link.of("/api/person/v1?page=0&size=12&direction=asc", "findAll").withType("GET"));
			dto.add(Link.of("/api/person/v1", "create").withType("POST"));
			dto.add(Link.of("/api/person/v1", "update").withType("PUT"));
			dto.add(Link.of("/api/person/v1/" + k, "delete").withType("DELETE"));

			entityModels.add(EntityModel.of(dto));
		}

		PagedModel<EntityModel<PersonDTO>> pagedModel = PagedModel
				.of(
						entityModels,
						new PageMetadata(10, 1, list.size()));

		when(assembler.toModel(
				any(Page.class),
				any(Link.class))).thenReturn(pagedModel);

		var parms = Map.of("page", "1", "size", "10", "direction", "asc", "sort", "firstName");
		var people = service.findAll(parms).getContent()
				.stream().map(EntityModel::getContent).toList();

		assertNotNull(people);

		checkHateoasLinks(people.get(0), 1);
		checkHateoasLinks(people.get(3), 4);
	}

	@Test
	void findById() {
		Person person = input.mockEntity(1);
		person.setId(1L);
		when(repository.findById(1L)).thenReturn(Optional.of(person));
		var result = service.findById(1L);

		checkHateoasLinks(result, 1);
	}

	@Test
	void create() {
		Person person = input.mockEntity(1);
		Person persisted = person;
		persisted.setId(1L);

		PersonDTO dto = input.mockDTO(1);

		when(repository.save(person)).thenReturn(persisted);
		var result = service.create(dto);

		checkHateoasLinks(result, 1);
	}

	@Test
	void testCreateWithNullPerson() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.create(null);
		});
		String expectedMessage = "It is not allowed to pass null as a required object";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void update() {

		Person person = input.mockEntity(1);
		Person persisted = person;
		persisted.setId(1L);

		PersonDTO dto = input.mockDTO(1);

		when(repository.findById(1L)).thenReturn(Optional.of(person));
		when(repository.save(person)).thenReturn(persisted);

		var result = service.update(dto);

		checkHateoasLinks(result, 1);
	}

	@Test
	void testUpdateWithNullPerson() {
		Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
			service.update(null);
		});
		String expectedMessage = "It is not allowed to pass null as a required object";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
	}

	@Test
	void delete() {
		Person person = input.mockEntity(1);
		person.setId(1L);
		when(repository.findById(1L)).thenReturn(Optional.of(person));

		service.delete(1L);
		verify(repository, times(1)).findById(anyLong());
		verify(repository, times(1)).delete(any(Person.class));
		verifyNoMoreInteractions(repository);
	}

	private void checkHateoasLinks(PersonDTO person, int i) {
		assertNotNull(person);
		assertNotNull(person.getId());

		checkLinks(person, "self", "api/person/v1/" + i, "GET");
		checkLinks(person, "findAll", "api/person/v1", "GET");
		checkLinks(person, "create", "api/person/v1", "POST");
		checkLinks(person, "update", "api/person/v1", "PUT");
		checkLinks(person, "delete", "api/person/v1/" + i, "DELETE");

		assertEquals("First Name Test" + i, person.getFirstName());
		assertEquals("Last Name Test" + i, person.getLastName());
		assertEquals("Address Test" + i, person.getAddress());
		assertEquals(i % 2 == 0 ? "Male" : "Female", person.getGender());
	}

	private void checkLinks(PersonDTO person, String action, String strLink, String type) {
		assertNotNull(person.getLinks(), "The list of links must not be null.");

		assertTrue(person.getLinks().stream()
				.anyMatch(link -> link.getRel().value().equals(action) &&
				// link.getHref().endsWith(strLink) &&
						link.getHref().contains(strLink) &&
						link.getType().equals(type)),
				"No links match the criteria.");

	}
}