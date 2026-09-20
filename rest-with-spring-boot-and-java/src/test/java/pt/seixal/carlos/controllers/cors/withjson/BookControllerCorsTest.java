package pt.seixal.carlos.controllers.cors.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookControllerCorsTest extends AbstractIntegrationTest {

	private BookDTO book;

	@Test
	@Order(1)
	void create() throws JsonMappingException, JsonProcessingException {

		mockBook();
		setEspecification("book");

		book = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(book)
				.when()
				.post()
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(BookDTO.class);

		checkBook();
	}

	@Test
	@Order(2)
	void creatWithWrongOrigin() throws JsonMappingException, JsonProcessingException {

		mockBook();
		setEspecificationBadOrigin("book");

		var content = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(book)
				.when()
				.post()
				.then()
				.statusCode(403)
				.extract()
				.body()
				.asString();

		assertEquals("Invalid CORS request", content);
	}

	@Test
	@Order(3)
	void findById() throws JsonMappingException, JsonProcessingException {

		setEspecification("book");

		book = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("id", 1L)
				.when()
				.get("{id}")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.as(BookDTO.class);

		checkBook();

	}

	@Test
	@Order(4)
	void findByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {

		setEspecificationBadOrigin("book");

		var content = given(especification)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("id", 1L)
				.when()
				.get("{id}")
				.then()
				.statusCode(403)
				.extract()
				.body()
				.asString();

		assertEquals("Invalid CORS request", content);
	}

	private void mockBook() {
		book = new BookDTO();
		book.setId(1L);
		book.setAuthor("Author Test");
		book.setLaunchDate(generateLaunchDate());
		book.setPrice(200.00);
		book.setTitle("Title Test");
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
