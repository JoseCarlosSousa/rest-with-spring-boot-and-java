package pt.seixal.carlos.controllers.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.dto.wrappers.xml.PagedModelPerson;
import pt.seixal.carlos.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerYAMLTest extends AbstractIntegrationTest {

	private static YAMLMapper objectMapper;

	@BeforeAll
	static void setUp() {
		objectMapper = new YAMLMapper();
	}

	@Test
	@Order(1)
	void createTest() throws JsonMappingException, JsonProcessingException {

		mockPerson();
		setEspecification("person");

		person = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.spec(especification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.body(person, objectMapper)
				.when()
				.post()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(PersonDTO.class, objectMapper);

		checkPerson();
	}

	@Test
	@Order(2)
	void updateTest() throws JsonMappingException, JsonProcessingException {

		person.setLastName("Seixal Updated");

		person = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.spec(especification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.body(person, objectMapper)
				.when()
				.put()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(PersonDTO.class, objectMapper);

		checkPerson("Seixal Updated", true);
	}

	@Test
	@Order(3)
	void findByIdTest() throws JsonMappingException, JsonProcessingException {

		person = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.spec(especification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.pathParam("id", person.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(PersonDTO.class, objectMapper);

		checkPerson("Seixal Updated", true);
	}

	@Test
	@Order(4)
	void disableTest() throws JsonMappingException, JsonProcessingException {

		person = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.spec(especification)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.pathParam("id", person.getId())
				.when()
				.patch("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(PersonDTO.class, objectMapper);

		checkPerson("Seixal Updated", false);
	}

	@Test
	@Order(5)
	void deleteTest() {

		given(especification)
				.pathParam("id", person.getId())
				.when()
				.delete("{id}")
				.then()
				.statusCode(204);
	}

	@Test
	@Order(6)
	void findAllTest() throws JsonMappingException, JsonProcessingException {

		var response = given(especification)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.queryParam("page", 1, "size", 10, "direction", "asc")
				.when()
				.get()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(PagedModelPerson.class, objectMapper);

		// List<PersonDTO> people = Arrays.asList(response);
		List<PersonDTO> people = response.getContent();
		assertNotNull(people);

		PersonDTO person1 = people.get(0);
		assertEquals("Abey", person1.getFirstName());
		assertEquals("Lebreton", person1.getLastName());
		assertEquals("Apt 1341", person1.getAddress());
		assertEquals("Male", person1.getGender());
		assertTrue(person1.getEnabled());

	}

	@Test
	@Order(6)
	void findByNameTest() throws JsonMappingException, JsonProcessingException {

		var response = given(especification)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.pathParam("firstName", "and")
				.queryParam("page", 0, "size", 10, "direction", "asc")
				.when()
				.get("findPeopleByName/{firstName}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(PagedModelPerson.class, objectMapper);

		// List<PersonDTO> people = Arrays.asList(response);
		List<PersonDTO> people = response.getContent();
		assertNotNull(people);

		PersonDTO person1 = people.get(0);
		assertEquals("Aland", person1.getFirstName());
		assertEquals("Boyn", person1.getLastName());
		assertEquals("Apt 653", person1.getAddress());
		assertEquals("Male", person1.getGender());
		assertFalse(person1.getEnabled());

	}
}
