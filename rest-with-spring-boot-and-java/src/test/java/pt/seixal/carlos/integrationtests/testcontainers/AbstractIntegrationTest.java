package pt.seixal.carlos.integrationtests.testcontainers;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
import pt.seixal.carlos.dto.AccountCredentialsDTO;
import pt.seixal.carlos.dto.BookDTO;
import pt.seixal.carlos.dto.PersonDTO;
import pt.seixal.carlos.dto.TokenDTO;

@TestInstance(Lifecycle.PER_CLASS)
@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
public abstract class AbstractIntegrationTest {

	protected static String sharedAccessToken;
	protected static String refreshAccessToken;
	protected static RequestSpecification especification;

	protected static PersonDTO person;
	protected static BookDTO book;

	@BeforeAll
	void setUpDefinition() {
		person = new PersonDTO();
		book = new BookDTO();

		if (sharedAccessToken == null || sharedAccessToken.isBlank()) {
			var credentials = new AccountCredentialsDTO("leandro", "admin123");

			TokenDTO loginResult = given()
					.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.body(credentials)
					.when()
					.post()
					.then()
					.statusCode(200)
					.extract()
					.body()
					.jsonPath()
					.getObject("body", TokenDTO.class);

			sharedAccessToken = loginResult.getAccessToken();
			refreshAccessToken = loginResult.getRefreshToken();
		}
	}

	protected static void setEspecification(String path) {
		setEspecification(TestConfigs.ORIGIN_LOCALHOST, path);
	}

	protected static void setEspecificationBadOrigin(String path) {
		setEspecification(TestConfigs.ORIGIN_OTHER, path);
	}

	private static void setEspecification(String origin, String path) {
		especification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_ORIGIN, origin)
				.addHeader("Authorization", "Bearer " + sharedAccessToken)
				.setBasePath("/api/" + path + "/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}

	protected static void mockPerson() {
		person.setFirstName("Carlos");
		person.setLastName("Seixal");
		person.setAddress("Portugal");
		person.setGender("Male");
		person.setEnabled(true);
		person.setPhotoUrl(
				"https://raw.githubusercontent.com/leandrocgsi/rest-with-spring-boot-and-java-erudio/refs/heads/main/photos/01_senna.jpg");
		person.setProfileUrl("https://en.wikipedia.org/wiki/Ayrton_Senna");
	}

	protected static void checkPerson() {
		checkPerson("Seixal", true);
	}

	protected static void checkPerson(String lastName, boolean enabled) {
		assertNotNull(person);
		assertNotNull(person.getId());
		assertTrue(person.getId() > 0);
		assertEquals("Carlos", person.getFirstName());
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

	protected static void mockBook() {
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

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.1.0");

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
