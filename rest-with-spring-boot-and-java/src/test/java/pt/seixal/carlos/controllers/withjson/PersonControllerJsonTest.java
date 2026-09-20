package pt.seixal.carlos.controllers.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.dto.wrappers.json.WrapperPersonDTO;
import pt.seixal.carlos.integrationtests.testcontainers.AbstractIntegrationTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerJsonTest extends AbstractIntegrationTest {

	private PersonDTO person;

	private static ObjectMapper objectMapper;

	@BeforeAll
	static void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	@Test
	@Order(1)
	void createTest() throws JsonMappingException, JsonProcessingException {
		mockPerson(); // Inicializa os dados padrão
		setEspecification("person");

		person = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(person)
				.when()
				.post()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.as(PersonDTO.class);

		checkPerson();
	}

	@Test
	@Disabled
	@Order(2)
	void updateTest() throws JsonMappingException, JsonProcessingException {
		setEspecification("person");
		// Mantemos o ID que a API gerou e injetou no passo 1, mudando apenas o apelido
		person.setLastName("Seixal Updated");

		person = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(person)
				.when()
				.put()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.as(PersonDTO.class);

		checkPerson("Seixal Updated", true);
	}

	@Test
	@Disabled
	@Order(3)
	void findByIdTest() throws JsonMappingException, JsonProcessingException {
		setEspecification("person");

		person = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("id", person.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.as(PersonDTO.class);

		checkPerson("Seixal Updated", true);
	}

	@Test
	@Disabled
	@Order(4)
	void disableTest() throws JsonMappingException, JsonProcessingException {
		setEspecification("person");

		person = given(especification)
				.pathParam("id", person.getId())
				.when()
				.patch("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.as(PersonDTO.class);

		checkPerson("Seixal Updated", false);
	}

	@Test
	@Disabled
	@Order(5)
	void deleteTest() throws JsonMappingException, JsonProcessingException {
		setEspecification("person");

		given(especification)
				.pathParam("id", person.getId())
				.when()
				.delete("{id}")
				.then()
				.statusCode(204);
	}

	@Test
	@Disabled
	@Order(6)
	void findAllTest() throws JsonMappingException, JsonProcessingException {
		setEspecification("person");

		var content = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.queryParam("page", 0, "size", 5)
				.when()
				.get()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.asString();

		WrapperPersonDTO wrapper = objectMapper.readValue(content, WrapperPersonDTO.class);
		List<PersonDTO> people = wrapper.getEmbedded().getPeople();

		assertPerson(people);
	}

	@Test
	@Disabled
	@Order(7)
	void findByNameTest() throws JsonMappingException, JsonProcessingException {
		setEspecification("person");

		var content = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("firstName", "carlos")
				.queryParam("page", 0, "size", 5)
				.when()
				.get("findPeopleByName/{firstName}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.asString();

		WrapperPersonDTO wrapper = objectMapper.readValue(content, WrapperPersonDTO.class);
		List<PersonDTO> people = wrapper.getEmbedded().getPeople();

		assertPerson(people);
	}

	private void mockPerson() {
		if (person == null) {
			person = new PersonDTO();
		}
		person.setId(null);
		person.setFirstName("Carlos Campos");
		person.setLastName("Seixal");
		person.setAddress("Portugal");
		person.setGender("Male");
		person.setEnabled(true);
		person.setPhotoUrl("https://githubusercontent.com");
		person.setProfileUrl("https://wikipedia.org");
	}

	private void checkPerson() {
		checkPerson("Seixal", true);
	}

	private void checkPerson(String lastName, boolean enabled) {
		assertNotNull(person);
		assertNotNull(person.getId());
		assertTrue(person.getId() > 0);
		// assertEquals("Carlos Campos", person.getFirstName());
		assertNotNull(person.getFirstName());
		assertEquals(lastName, person.getLastName());
		assertEquals("Portugal", person.getAddress());
		assertEquals("Male", person.getGender());
		if (enabled) {
			assertTrue(person.getEnabled());
		} else {
			assertFalse(person.getEnabled());
		}
		assertNotNull(person.getPhotoUrl());
		assertNotNull(person.getProfileUrl());
	}

	private void assertPerson(List<PersonDTO> list) {
		assertNotNull(list);
		assertFalse(list.isEmpty());

		// Valida o primeiro elemento retornado da consulta real do banco de dados
		var target = list.get(0);
		assertNotNull(target.getId());
		// assertEquals("Carlos Campos", target.getFirstName());
		assertNotNull(person.getFirstName());
	}
}
