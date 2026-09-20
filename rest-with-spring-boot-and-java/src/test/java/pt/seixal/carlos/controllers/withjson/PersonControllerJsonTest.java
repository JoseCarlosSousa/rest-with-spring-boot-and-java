package pt.seixal.carlos.controllers.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
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

		mockPerson();
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
		assertTrue(person.getEnabled());
		assertEquals("Sousa", person.getLastName());
	}

	@Test
	@Order(2)
	void updateTest() throws JsonMappingException, JsonProcessingException {

		mockPerson();
		setEspecification("person");
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

		checkPerson();
		assertTrue(person.getEnabled());
		assertEquals("Seixal Updated", person.getLastName());
	}

	@Test
	@Order(3)
	void findByIdTest() throws JsonMappingException, JsonProcessingException {

		setEspecification("person");

		person = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("id", 1L)
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.as(PersonDTO.class);

		checkPerson();
		assertTrue(person.getEnabled());
	}

	@Test
	@Order(4)
	void disableTest() throws JsonMappingException, JsonProcessingException {

		setEspecification("person");

		person = given(especification)
				.pathParam("id", 1L)
				.when()
				.patch("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.as(PersonDTO.class);

		checkPerson();
		assertFalse(person.getEnabled());
	}

	@Test
	@Order(5)
	void deleteTest() throws JsonMappingException, JsonProcessingException {

		setEspecification("person");

		given(especification)
				.pathParam("id", 1L)
				.when()
				.delete("{id}")
				.then()
				.statusCode(204);
	}

	@Test
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

		assertNotNull(people);
	}

	@Test
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

		assertNotNull(people);
	}

	private void mockPerson() {
		person = new PersonDTO();
		person.setId(1L);
		person.setFirstName("Carlos Campos");
		person.setLastName("Sousa");
		person.setAddress("Rua das Pretas");
		person.setGender("Male");
		person.setEnabled(true);
		person.setPhotoUrl("https://githubusercontent.com");
		person.setProfileUrl("https://wikipedia.org");
	}

	private void checkPerson() {
		assertNotNull(person);
		assertEquals("Carlos Campos", person.getFirstName());
		assertEquals("Sousa", person.getLastName());
		assertEquals("Rua das Pretas", person.getAddress());
		assertEquals("Male", person.getGender());
		assertNotNull(person.getPhotoUrl());
		assertNotNull(person.getProfileUrl());
	}
}
