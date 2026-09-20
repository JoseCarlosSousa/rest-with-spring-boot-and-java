package pt.seixal.carlos.controllers.cors.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

	private static PersonDTO person;

	@Test
	@Order(1)
	void create() throws JsonMappingException, JsonProcessingException {

		person = new PersonDTO();
		person.setFirstName("Carlos Campos");
		person.setLastName("Sousa");
		person.setAddress("Rua das Pretas");
		person.setGender("Male");
		person.setEnabled(true);
		person.setPhotoUrl("https://githubusercontent.com");
		person.setProfileUrl("https://wikipedia.org");

		setEspecificationPerson();

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
	void findById() throws JsonMappingException, JsonProcessingException {

		setEspecificationPerson();

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
	@Order(3)
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

	@Test
	@Order(4)
	void creatWithWrongOrigin() throws JsonMappingException, JsonProcessingException {

		person = new PersonDTO();
		person.setFirstName("Carlos Campos");
		person.setLastName("Sousa");
		person.setAddress("Rua das Pretas");
		person.setGender("Male");
		person.setEnabled(true);
		person.setPhotoUrl("https://githubusercontent.com");
		person.setProfileUrl("https://wikipedia.org");

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
