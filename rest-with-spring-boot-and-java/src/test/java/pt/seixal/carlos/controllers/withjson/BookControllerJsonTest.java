package pt.seixal.carlos.controllers.withjson;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.dto.wrappers.json.WrapperBookDTO;
import pt.seixal.carlos.integrationtests.testcontainers.AbstractIntegrationTest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookControllerJsonTest extends AbstractIntegrationTest {

	private BookDTO book;
	private static ObjectMapper objectMapper;

	@BeforeAll
	static void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

	@Test
	@Order(1)
	void createTest() throws JsonMappingException, JsonProcessingException {

		mockBook();
		setEspecification("book");

		book = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(book)
				.when()
				.post()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.as(BookDTO.class);

		checkBook();
	}

	@Test
	@Order(2)
	void updateTest() throws JsonMappingException, JsonProcessingException {

		mockBook();
		setEspecification("book");
		book.setAuthor("Rui Oliveira");

		book = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(book)
				.when()
				.put()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.as(BookDTO.class);

		checkBook();
		assertEquals("Rui Oliveira", book.getAuthor());
	}

	@Test
	@Order(3)
	void findByIdTest() throws JsonMappingException, JsonProcessingException {

		setEspecification("book");

		book = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("id", 1L)
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.as(BookDTO.class);

		checkBook();
	}

	@Test
	@Order(4)
	void deleteTest() throws JsonMappingException, JsonProcessingException {

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
		var content = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.queryParam("page", 1, "size", 10)
				.when()
				.get()
				.then()
				.statusCode(200)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.extract()
				.body()
				.asString();

		WrapperBookDTO wrapper = objectMapper.readValue(content, WrapperBookDTO.class);
		List<BookDTO> books = wrapper.getEmbedded().getBooks();

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
		assertEquals(49.00, book.getPrice());
		assertEquals("Working effectively with legacy code", book.getTitle());
	}

	private Date generateLaunchDate() {
		String strDate = "2026-08-17";
		return Date.from(LocalDate.parse(strDate)
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant());
	}
}
