package pt.seixal.carlos.controllers.withxml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

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
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import pt.seixal.carlos.config.TestConfigs;
import pt.seixal.carlos.dto.PersonDTO;
import pt.seixal.carlos.dto.wrappers.xml.PagedModelPerson;
import pt.seixal.carlos.integrationtests.testcontainers.AbstractIntegrationTest;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonControllerXMLTest  extends AbstractIntegrationTest {

	private static RequestSpecification especification;
	private static XmlMapper objectMapper;
	private static PersonDTO person;
	
	@BeforeAll
	static void setUp(){
	    objectMapper = new XmlMapper();
	    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
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
		
		PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
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
		
		PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
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
		
		PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
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
		
		PersonDTO createdPerson = objectMapper.readValue(content, PersonDTO.class);
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

		var content = given(especification)
				.accept(MediaType.APPLICATION_XML_VALUE)
				.queryParam("page", 1, "size", 10, "direction", "asc")
				.when()
					.get()
				.then()
					.statusCode(200)
					.contentType(MediaType.APPLICATION_XML_VALUE)
				.extract()
					.body()
						.asString();
		
		//List<PersonDTO> people = objectMapper.readValue(content, new TypeReference<List<PersonDTO>>() {});
		PagedModelPerson wrapper = objectMapper.readValue(content, PagedModelPerson.class); 
		List<PersonDTO> people = wrapper.getContent();
		
		PersonDTO person1 = people.get(0);
		assertEquals("Abey", person1.getFirstName());
		assertEquals("Lebreton", person1.getLastName());
		assertEquals("Apt 1341", person1.getAddress());
		assertEquals("Male", person1.getGender());
		assertTrue(person1.getEnabled());
		
	}	
	
	@Test
	@Order(7)
	void findByNameTest() throws JsonMappingException, JsonProcessingException {

		var content = given(especification)
				.accept(MediaType.APPLICATION_XML_VALUE)
				.pathParam("firstName", "and")
				.queryParam("page", 0, "size", 10, "direction", "asc")
				.when()
					.get("findPeopleByName/{firstName}")
				.then()
					.statusCode(200)
					.contentType(MediaType.APPLICATION_XML_VALUE)
				.extract()
					.body()
						.asString();
		
		//List<PersonDTO> people = objectMapper.readValue(content, new TypeReference<List<PersonDTO>>() {});
		PagedModelPerson wrapper = objectMapper.readValue(content, PagedModelPerson.class); 
		List<PersonDTO> people = wrapper.getContent();
		
		PersonDTO person1 = people.get(0);
		assertEquals("Aland", person1.getFirstName());
		assertEquals("Boyn", person1.getLastName());
		assertEquals("Apt 653", person1.getAddress());
		assertEquals("Male", person1.getGender());
		assertFalse(person1.getEnabled());
		
	}
	
	private void mockPerson() {
        person.setFirstName("Carlos");
        person.setLastName("Seixal");
        person.setAddress("Portugal");
        person.setGender("Male");
        person.setEnabled(true);
	}
}
