package pt.seixal.carlos.controllers.withxml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.dto.wrappers.xml.PagedModelPerson;
import pt.seixal.carlos.integrationtests.testcontainers.AbstractIntegrationTest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerXMLTest extends AbstractIntegrationTest {
	private PersonDTO person;

	private static XmlMapper objectMapper;

	@BeforeAll
	static void setUp() {
		objectMapper = new XmlMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	@Test
	@Order(1)
	void createTest() throws JsonMappingException, JsonProcessingException {

		mockPerson();
		setEspecification("person");

		var content = given(especification)
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.accept(MediaType.APPLICATION_XML_VALUE)
				.body(person)
				.when()
				.post()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.extract()
				.body()
				.asString();

		person = objectMapper.readValue(content, PersonDTO.class);

		checkPerson();
	}

	@Test
	@Order(2)
	void updateTest() throws JsonMappingException, JsonProcessingException {

		mockPerson();
		person.setId(1L);
		person.setLastName("Seixal Updated");

		setEspecification("person");

		var content = given(especification)
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.accept(MediaType.APPLICATION_XML_VALUE)
				.body(person)
				.when()
				.put()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.extract()
				.body()
				.asString();

		person = objectMapper.readValue(content, PersonDTO.class);

		checkPerson();
		assertEquals("Seixal Updated", person.getLastName());
	}

	@Test
	@Order(3)
	void findByIdTest() throws JsonMappingException, JsonProcessingException {

		setEspecification("person");

		var content = given(especification)
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.accept(MediaType.APPLICATION_XML_VALUE)
				.pathParam("id", 1L)
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.extract()
				.body()
				.asString();

		person = objectMapper.readValue(content, PersonDTO.class);

		checkPerson();
	}

	@Test
	@Order(4)
	void disableTest() throws JsonMappingException, JsonProcessingException {

		setEspecification("person");

		var content = given(especification)
				.accept(MediaType.APPLICATION_XML_VALUE)
				.pathParam("id", 1L)
				.when()
				.patch("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.extract()
				.body()
				.asString();

		person = objectMapper.readValue(content, PersonDTO.class);

		checkPerson();
		assertFalse(person.getEnabled());
	}

	@Test
	@Order(5)
	void deleteTest() {
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
				.accept(MediaType.APPLICATION_XML_VALUE)
				.queryParam("page", 0, "size", 5)
				.when()
				.get()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.extract()
				.body()
				.asString();

		PagedModelPerson wrapper = objectMapper.readValue(content, PagedModelPerson.class);
		assertNotNull(wrapper.getContent());
	}

	@Test
	@Order(7)
	void findByNameTest() throws JsonMappingException, JsonProcessingException {

		setEspecification("person");

		var content = given(especification)
				.accept(MediaType.APPLICATION_XML_VALUE)
				.pathParam("firstName", "carlos")
				.queryParam("page", 0, "size", 5)
				.when()
				.get("findPeopleByName/{firstName}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.extract()
				.body()
				.asString();

		PagedModelPerson wrapper = objectMapper.readValue(content, PagedModelPerson.class);
		assertNotNull(wrapper.getContent());
	}

	private void mockPerson() {
		person = new PersonDTO();
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
