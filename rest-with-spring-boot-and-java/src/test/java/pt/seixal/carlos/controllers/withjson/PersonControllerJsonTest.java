package pt.seixal.carlos.controllers.withjson;

import static io.restassured.RestAssured.given;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.dto.wrappers.json.WrapperPersonDTO;
import pt.seixal.carlos.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerJsonTest extends AbstractIntegrationTest {

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
	}

	@Test

	@Order(2)
	void updateTest() throws JsonMappingException, JsonProcessingException {

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
	@Order(3)
	void findByIdTest() throws JsonMappingException, JsonProcessingException {

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
	@Order(4)
	void disableTest() throws JsonMappingException, JsonProcessingException {

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
	@Order(5)
	void deleteTest() throws JsonMappingException, JsonProcessingException {

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
	@Order(7)
	void findByNameTest() throws JsonMappingException, JsonProcessingException {

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
}
