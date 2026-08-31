package pt.seixal.carlos.integrationtests.swagger;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import pt.seixal.carlos.config.TestConfigs;
import pt.seixal.carlos.integrationtests.testcontainers.AbstractIntegrationTest;

import static io.restassured.RestAssured.given;
import static junit.framework.TestCase.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class SwaggerIntegrationTest extends AbstractIntegrationTest {

	@Test
	void shouldDisplaySwaggerUIPage() {
		var content = given()
				.basePath("/swagger-ui/index.html")
					.port(TestConfigs.SERVER_PORT)
				.when()
					.get()
				.then()
					.statusCode(200)
				.extract()
					.body()
						.asString();
		
		
		assertTrue(content.contains("Swagger UI"));
	}
}
