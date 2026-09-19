package pt.seixal.carlos.controllers.withyaml.withoutmapper;

import static io.restassured.RestAssured.given;

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
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.dto.wrappers.xml.PagedModelPerson;
import pt.seixal.carlos.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerYAMLTest2 extends AbstractIntegrationTest {

	private static YAMLMapper objectMapper;

	@BeforeAll
	static void setUp() {
		objectMapper = new YAMLMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		// Configura o REST Assured para conseguir codificar e enviar application/x-yaml
		// como texto puro
		io.restassured.RestAssured.config = io.restassured.config.RestAssuredConfig.config()
				.encoderConfig(io.restassured.config.EncoderConfig.encoderConfig()
						.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, io.restassured.http.ContentType.TEXT));
	}

	@Test
	@Order(1)
	void createTest() throws JsonMappingException, JsonProcessingException {

		mockPerson();
		setEspecification("person");

		String yamlBody = objectMapper.writeValueAsString(person);

		var content = given(especification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.body(yamlBody)
				.when()
				.post()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.asString();

		person = objectMapper.readValue(content, PersonDTO.class);

		checkPerson();
	}

	@Test
	@Order(2)
	void updateTest() throws JsonMappingException, JsonProcessingException {

		person.setLastName("Seixal Updated");
		String yamlBody = objectMapper.writeValueAsString(person);

		var content = given(especification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.body(yamlBody)
				.when()
				.put()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.asString();

		person = objectMapper.readValue(content, PersonDTO.class);

		checkPerson("Seixal Updated", true);
	}

	@Test
	@Order(3)
	void findByIdTest() throws JsonMappingException, JsonProcessingException {

		var content = given(especification)
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
				.asString();

		person = objectMapper.readValue(content, PersonDTO.class);

		checkPerson("Seixal Updated", true);
	}

	@Test
	@Order(4)
	void disableTest() throws JsonMappingException, JsonProcessingException {

		var content = given(especification)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.pathParam("id", person.getId())
				.when()
				.patch("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.asString();

		person = objectMapper.readValue(content, PersonDTO.class);

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

		var content = given(especification)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.queryParam("page", 0, "size", 5)
				.when()
				.get()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.asString();

		PagedModelPerson wrapper = objectMapper.readValue(content, PagedModelPerson.class);
		assertPerson(wrapper.getContent());

	}

	@Test
	@Order(7)
	void findByNameTest() throws JsonMappingException, JsonProcessingException {

		var content = given(especification)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.pathParam("firstName", "carlos")
				.queryParam("page", 0, "size", 5)
				.when()
				.get("findPeopleByName/{firstName}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.asString();

		PagedModelPerson wrapper = objectMapper.readValue(content, PagedModelPerson.class);
		assertPerson(wrapper.getContent());

	}

}
