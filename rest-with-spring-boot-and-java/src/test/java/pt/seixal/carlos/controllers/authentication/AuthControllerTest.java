package pt.seixal.carlos.controllers.authentication;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import pt.seixal.carlos.data.dto.v1.security.TokenDTO;
import pt.seixal.carlos.integrationtests.testcontainers.AbstractIntegrationTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerTest extends AbstractIntegrationTest {

	@LocalServerPort
	protected int dynamicPort;

	@BeforeAll
	static void setUp() {
	}

	@Test
	@Order(1)
	void testSignin() {
		assertNotNull(sharedAccessToken);
		assertNotNull(refreshAccessToken);

	}

	@Test
	@Order(2)
	void testRefresh() {

		var newTokenDTO = given()
				.basePath("/auth/refresh")
				.port(dynamicPort)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.pathParam("username", "carlos")
				.header("Authorization", "Bearer " + refreshAccessToken)
				.when()
				.put("{username}")
				.then()
				.statusCode(200)
				.extract()
				.body()
				.jsonPath()
				.getObject("", TokenDTO.class);

		assertNotNull(newTokenDTO.getAccessToken());
		assertNotNull(newTokenDTO.getRefreshToken());

		sharedAccessToken = newTokenDTO.getAccessToken();
		refreshAccessToken = newTokenDTO.getRefreshToken();
	}
}
