package pt.seixal.carlos.controllers.cors.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
	@Order(2)
	void creatWithWrongOrigin() throws JsonMappingException, JsonProcessingException {

		mockPerson();
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
				.pathParam("id", 1L)
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
				.pathParam("id", 1L)
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
		assertNotNull(person.getFirstName());
		assertNotNull(person.getLastName());
		assertNotNull(person.getAddress());
		assertNotNull(person.getGender());
		assertNotNull(person.getEnabled());
		assertNotNull(person.getPhotoUrl());
		assertNotNull(person.getProfileUrl());
	}

}
