package pt.seixal.carlos.controllers.withyaml.withoutmapper;

import static io.restassured.RestAssured.given;

import java.util.List;

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
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.dto.wrappers.xml.PagedModelBook;
import pt.seixal.carlos.integrationtests.testcontainers.AbstractIntegrationTest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerYAMLTest2 extends AbstractIntegrationTest { // REMOVIDO: Anotação @SpringBootTest de porta fixa

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
		mockBook();
		setEspecification("book");

		String yamlBody = objectMapper.writeValueAsString(book);

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

		book = objectMapper.readValue(content, BookDTO.class);

		checkBook();
	}

	@Test
	@Order(2)
	void updateTest() throws JsonMappingException, JsonProcessingException {
		setEspecification("book");
		book.setAuthor("Rui Oliveira"); // Reaproveita o ID estável obtido do createTest()

		String yamlBody = objectMapper.writeValueAsString(book);

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

		book = objectMapper.readValue(content, BookDTO.class);

		checkBook("Rui Oliveira");
	}

	@Test
	@Order(3)
	void findByIdTest() throws JsonMappingException, JsonProcessingException {
		setEspecification("book");

		var content = given(especification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.pathParam("id", book.getId()) // Leitura segura do identificador ativo
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.asString();

		book = objectMapper.readValue(content, BookDTO.class);

		checkBook("Rui Oliveira");
	}

	@Test
	@Order(4)
	void deleteTest() {
		setEspecification("book");

		given(especification)
				.pathParam("id", book.getId())
				.when()
				.delete("{id}")
				.then()
				.statusCode(204);
	}

	@Test
	@Order(5)
	void findAllTest() throws JsonMappingException, JsonProcessingException {
		setEspecification("book");

		var content = given(especification)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.queryParam("page", 1, "size", 10, "direction", "asc")
				.when()
				.get()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.asString();

		PagedModelBook wrapper = objectMapper.readValue(content, PagedModelBook.class);
		List<BookDTO> books = wrapper.getContent();

		assertBooks(books);
	}
}
