package pt.seixal.carlos.controllers.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
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

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import pt.seixal.carlos.config.TestConfigs;
import pt.seixal.carlos.dto.PersonDTO;
import pt.seixal.carlos.integrationtests.testcontainers.AbstractIntegrationTest;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerYAMLV2Test  extends AbstractIntegrationTest {

	private static RequestSpecification especification;
	private static YAMLMapper objectMapper;
	private static PersonDTO person;
	
	@BeforeAll
	static void setUp(){
	    objectMapper = new YAMLMapper();
	    
		person = new PersonDTO();
	}

	@Test
	@Order(1)
	void createTest() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		especification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_LOCALHOST)
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
		var createdPerson = given()
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
		
		person = createdPerson;
		
		assertNotNull(createdPerson);
		
		assertNotNull(createdPerson.getId());
		assertTrue(createdPerson.getId() > 0);
		
		assertEquals("Carlos", createdPerson.getFirstName());
		assertEquals("Seixal", createdPerson.getLastName());
		assertEquals("Portugal", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertTrue(createdPerson.getEnabled());
	}	
	
	@Test
	@Order(2)
	void updateTest() throws JsonMappingException, JsonProcessingException {
		
		person.setLastName("Seixal Updated");
		
		var createdPerson = given()
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
		
		person = createdPerson;
		
		assertNotNull(createdPerson);
		
		assertNotNull(createdPerson.getId());
		assertTrue(createdPerson.getId() > 0);
		
		assertEquals("Carlos", createdPerson.getFirstName());
		assertEquals("Seixal Updated", createdPerson.getLastName());
		assertEquals("Portugal", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertTrue(createdPerson.getEnabled());
	}
	
	@Test
	@Order(3)
	void findByIdTest() throws JsonMappingException, JsonProcessingException {

		var createdPerson = given()
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
		
		person = createdPerson;
		
		assertNotNull(createdPerson);
		
		assertNotNull(createdPerson.getId());
		assertTrue(createdPerson.getId() > 0);
		
		assertEquals("Carlos", createdPerson.getFirstName());
		assertEquals("Seixal Updated", createdPerson.getLastName());
		assertEquals("Portugal", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertTrue(createdPerson.getEnabled());
	}	
	
	@Test
	@Order(4)
	void disableTest() throws JsonMappingException, JsonProcessingException {

		var createdPerson = given()
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
		
		person = createdPerson;
		
		assertNotNull(createdPerson);
		
		assertNotNull(createdPerson.getId());
		assertTrue(createdPerson.getId() > 0);
		
		assertEquals("Carlos", createdPerson.getFirstName());
		assertEquals("Seixal Updated", createdPerson.getLastName());
		assertEquals("Portugal", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		assertFalse(createdPerson.getEnabled());
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
				.when()
					.get()
				.then()
					.statusCode(200)
					.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
					.body()
						.as(PersonDTO[].class, objectMapper);
		
		List<PersonDTO> people = Arrays.asList(response);
		assertNotNull(people);
		
		for (PersonDTO person : people) {
			assertNotNull(person.getId());
			assertTrue(person.getId() > 0);

			assertNotNull(person.getFirstName());
			assertNotNull(person.getLastName());
			assertNotNull(person.getAddress());
			assertNotNull(person.getGender());
		}
		
	}
	
	private void mockPerson() {
        person.setFirstName("Carlos");
        person.setLastName("Seixal");
        person.setAddress("Portugal");
        person.setGender("Male");
        person.setEnabled(true);
	}
}
