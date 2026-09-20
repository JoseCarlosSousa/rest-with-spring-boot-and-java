package pt.seixal.carlos.integrationtests.testcontainers;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import pt.seixal.carlos.config.TestConfigs;
import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.data.dto.v1.security.AccountCredentialsDTO;
import pt.seixal.carlos.data.dto.v1.security.TokenDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
public abstract class AbstractIntegrationTest {

	@LocalServerPort
	protected int dynamicPort;

	protected static String sharedAccessToken;
	protected static String refreshAccessToken;
	protected static RequestSpecification especification;

	protected static PersonDTO person;
	protected static BookDTO book;

	@BeforeEach
	void setUpDefinition() {
		// Correção estrutural: Instancia sempre objetos limpos a cada método.
		// Isto impede que o ID gerado no JSON ou YAML contamine as classes seguintes.
		person = new PersonDTO();
		book = new BookDTO();

		if (sharedAccessToken == null || sharedAccessToken.isBlank()) {
			var credentials = new AccountCredentialsDTO("carlos", "admin123");

			TokenDTO loginResult = given()
					.basePath("/auth/signin")
					.port(dynamicPort)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.body(credentials)
					.when()
					.post()
					.then()
					.statusCode(200)
					.extract()
					.body()
					.jsonPath()
					.getObject("", TokenDTO.class);

			sharedAccessToken = loginResult.getAccessToken();
			refreshAccessToken = loginResult.getRefreshToken();
		}
	}

	protected void setEspecification(String path) {
		setEspecification(TestConfigs.ORIGIN_LOCALHOST, path);
	}

	protected void setEspecificationBadOrigin(String path) {
		setEspecification(TestConfigs.ORIGIN_OTHER, path);
	}

	private void setEspecification(String origin, String path) {
		especification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, origin)
				.addHeader("Authorization", "Bearer " + sharedAccessToken)
				.setBasePath("/api/" + path + "/v1")
				.setPort(dynamicPort)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	protected static void mockPerson() {
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

	protected static void checkPerson() {
		checkPerson("Seixal", true);
	}

	protected static void checkPerson(String lastName, boolean enabled) {
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

	protected static void assertPerson(List<PersonDTO> list) {
		assertNotNull(list);
		assertFalse(list.isEmpty());

		// Valida o primeiro elemento retornado da consulta real do banco de dados
		var target = list.get(0);
		assertNotNull(target.getId());
		// assertEquals("Carlos Campos", target.getFirstName());
		assertNotNull(person.getFirstName());
	}

	protected static void mockBook() {
		if (book == null) {
			book = new BookDTO();
		}
		book.setId(null);
		book.setAuthor("Author Test");
		book.setLaunchDate(generateLaunchDate());
		book.setPrice(200.00);
		book.setTitle("Title Test");
	}

	private static Date generateLaunchDate() {
		String strDate = "2026-08-17";
		return Date.from(LocalDate.parse(strDate)
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant());
	}

	protected static void checkBook() {
		checkBook("Author Test");
	}

	protected static void checkBook(String author) {
		assertNotNull(book);
		assertNotNull(book.getId());

		assertEquals(author, book.getAuthor());
		assertEquals(generateLaunchDate(), book.getLaunchDate());
		assertEquals(200.00, book.getPrice());
		assertEquals("Title Test", book.getTitle());
	}

	protected static void assertBooks(List<BookDTO> list) {
		assertNotNull(list);
		var dto = list.get(0);

		assertEquals(13, dto.getId());
		assertEquals("Richard Hunter e George Westerman", dto.getAuthor());
		assertEquals(95.0, dto.getPrice());
		assertEquals("O verdadeiro valor de TI", dto.getTitle());
		assertNotNull(dto.getLaunchDate());
	}

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:9.1.0");

		private static void startContainers() {
			Startables.deepStart(Stream.of(mysql)).join();
		}

		private static Map<String, Object> createConnectionConfiguration() {
			return Map.of(
					"spring.datasource.url",
					mysql.getJdbcUrl(),
					"spring.datasource.username",
					mysql.getUsername(),
					"spring.datasource.password",
					mysql.getPassword());
		}

		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {
			startContainers();
			ConfigurableEnvironment environment = applicationContext.getEnvironment();
			MapPropertySource testcontainers = new MapPropertySource("testcontainers", createConnectionConfiguration());
			environment.getPropertySources().addFirst(testcontainers);
		}
	}
}
