package pt.seixal.carlos.controllers.cors.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonControllerCorsTest extends AbstractIntegrationTest {

	private PersonDTO person;

	@Test
	@Disabled
	@Order(1)
	void create() throws JsonMappingException, JsonProcessingException {

		mockPerson();
		setEspecification("person");

		person = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(person)
				.when()
				.post()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(PersonDTO.class);

		checkPerson();
	}

	@Test
	@Disabled
	@Order(2)
	void creatWithWrongOrigin() throws JsonMappingException, JsonProcessingException {

		setEspecificationBadOrigin("person");

		var content = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(person)
				.when()
				.post()
				.then()
				.statusCode(403)
				.extract()
				.body()
				.asString();

		assertEquals("Invalid CORS request", content);
	}

	@Test
	@Disabled
	@Order(3)
	void findById() throws JsonMappingException, JsonProcessingException {

		setEspecification("person");

		person = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("id", person.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(PersonDTO.class);

		checkPerson();

	}

	@Test
	@Disabled
	@Order(4)
	void findByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		setEspecificationBadOrigin("person");

		var content = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("id", person.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(403)
				.extract()
				.body()
				.asString();

		assertEquals("Invalid CORS request", content);
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
