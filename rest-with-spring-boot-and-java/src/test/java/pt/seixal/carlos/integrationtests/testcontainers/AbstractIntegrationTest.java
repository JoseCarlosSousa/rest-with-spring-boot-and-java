package pt.seixal.carlos.integrationtests.testcontainers;

import static io.restassured.RestAssured.given;

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
import pt.seixal.carlos.data.dto.v1.security.AccountCredentialsDTO;
import pt.seixal.carlos.data.dto.v1.security.TokenDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
public abstract class AbstractIntegrationTest {

	@LocalServerPort
	protected int dynamicPort = 8080;

	protected static String sharedAccessToken;
	protected static String refreshAccessToken;
	protected static RequestSpecification especification;

	@BeforeEach
	void setUpDefinition() {

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

	protected void setEspecificationPerson() {
		setEspecification(TestConfigs.ORIGIN_LOCALHOST, "person");
	}

	protected void setEspecificationBook() {
		setEspecification(TestConfigs.ORIGIN_LOCALHOST, "book");
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
