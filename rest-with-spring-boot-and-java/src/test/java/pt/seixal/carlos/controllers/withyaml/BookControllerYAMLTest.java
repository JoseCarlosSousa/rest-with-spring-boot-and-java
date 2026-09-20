package pt.seixal.carlos.controllers.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.dto.wrappers.xml.PagedModelBook;
import pt.seixal.carlos.integrationtests.testcontainers.AbstractIntegrationTest;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerYAMLTest extends AbstractIntegrationTest {

	private BookDTO book;
	private static YAMLMapper objectMapper;

	@BeforeAll
	static void setUp() {
		objectMapper = new YAMLMapper();
	}

	@Test
	@Order(1)
	void createTest() throws JsonMappingException, JsonProcessingException {

		mockBook();
		setEspecification("book");

		book = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.spec(especification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.body(book, objectMapper)
				.when()
				.post()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(BookDTO.class, objectMapper);

		checkBook();
	}

	@Test
	@Order(2)
	void updateTest() throws JsonMappingException, JsonProcessingException {

		mockBook();
		setEspecification("book");
		book.setAuthor("Rui Oliveira");

		book = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.spec(especification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.body(book, objectMapper)
				.when()
				.put()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(BookDTO.class, objectMapper);

		checkBook();
		assertEquals("Rui Oliveira", book.getAuthor());
	}

	@Test
	@Order(3)
	void findByIdTest() throws JsonMappingException, JsonProcessingException {

		setEspecification("book");

		book = given()
				.config(RestAssuredConfig.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								.encodeContentTypeAs(MediaType.APPLICATION_YAML_VALUE, ContentType.TEXT)))
				.spec(especification)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.pathParam("id", 1L)
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(BookDTO.class, objectMapper);

		checkBook();
	}

	@Test
	@Order(4)
	void deleteTest() {
		setEspecification("book");

		given(especification)
				.pathParam("id", 1L)
				.when()
				.delete("{id}")
				.then()
				.statusCode(204);
	}

	@Test
	@Order(5)
	void findAllTest() throws JsonMappingException, JsonProcessingException {

		setEspecification("book");

		List<BookDTO> books = given(especification)
				.accept(MediaType.APPLICATION_YAML_VALUE)
				.queryParam("page", 1, "size", 10, "direction", "asc", "sort", "title")
				.when()
				.get()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_YAML_VALUE)
				.extract()
				.body()
				.as(PagedModelBook.class, objectMapper)
				.getContent();

		assertNotNull(books);

		for (int i = 0; i < books.size(); i++) {
			assertNotNull(books.get(i).getAuthor());
			assertNotNull(books.get(i).getTitle());
			assertNotNull(books.get(i).getPrice());
			assertNotNull(books.get(i).getLaunchDate());
		}
	}

	private void mockBook() {
		book = new BookDTO();
		book.setId(1L);
		book.setAuthor("Michael C. Feathers");
		book.setLaunchDate(generateLaunchDate());
		book.setPrice(49.00);
		book.setTitle("Working effectively with legacy code");
	}

	private void checkBook() {
		assertNotNull(book);
		assertNotNull(book.getId());
		assertNotNull(book.getAuthor());
		assertNotNull(book.getLaunchDate());
		assertNotNull(book.getPrice());
		assertNotNull(book.getTitle());
	}

	private Date generateLaunchDate() {
		String strDate = "2026-08-17";
		return Date.from(LocalDate.parse(strDate)
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant());
	}
}
