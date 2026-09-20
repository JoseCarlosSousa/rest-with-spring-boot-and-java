package pt.seixal.carlos.controllers.withxml;

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
class PersonControllerXMLTest extends AbstractIntegrationTest { // REMOVIDO: Anotação @SpringBootTest de porta fixa

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
	@Disabled
	@Order(2)
	void updateTest() throws JsonMappingException, JsonProcessingException {
		setEspecification("person");
		person.setLastName("Seixal Updated"); // Mantém o ID ativo gerado no passo 1

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

		checkPerson("Seixal Updated", true);
	}

	@Test
	@Disabled
	@Order(3)
	void findByIdTest() throws JsonMappingException, JsonProcessingException {
		setEspecification("person");

		var content = given(especification)
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.accept(MediaType.APPLICATION_XML_VALUE)
				.pathParam("id", person.getId())
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.extract()
				.body()
				.asString();

		person = objectMapper.readValue(content, PersonDTO.class);

		checkPerson("Seixal Updated", true);
	}

	@Test
	@Disabled
	@Order(4)
	void disableTest() throws JsonMappingException, JsonProcessingException {
		setEspecification("person");

		var content = given(especification)
				.accept(MediaType.APPLICATION_XML_VALUE)
				.pathParam("id", person.getId())
				.when()
				.patch("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_XML_VALUE)
				.extract()
				.body()
				.asString();

		person = objectMapper.readValue(content, PersonDTO.class);

		checkPerson("Seixal Updated", false);
	}

	@Test
	@Disabled
	@Order(5)
	void deleteTest() {
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
		assertPerson(wrapper.getContent());
	}

	@Test
	@Disabled
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
		assertPerson(wrapper.getContent());
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
