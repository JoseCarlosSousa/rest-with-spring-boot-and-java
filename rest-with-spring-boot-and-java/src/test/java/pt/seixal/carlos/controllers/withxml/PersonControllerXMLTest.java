package pt.seixal.carlos.controllers.withxml;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.dto.wrappers.xml.PagedModelPerson;
import pt.seixal.carlos.integrationtests.testcontainers.AbstractIntegrationTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerXMLTest extends AbstractIntegrationTest { // REMOVIDO: Anotação @SpringBootTest de porta fixa

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
}
