package pt.seixal.carlos.controllers.docs;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.data.dto.v1.security.AccountCredentialsDTO;

public interface AuthControllerDocs {

	@Operation(summary = "Authenticates an user and return a token")
	@PostMapping("/signin")
	public ResponseEntity<?> signin(AccountCredentialsDTO credentials);

	@Operation(summary = "Refresh token for authenticated user and returns a token")
	@PutMapping("/refresh/{username}")
	public ResponseEntity<?> refresh(String username, String refreshToken);

	@PostMapping(value = "/createUser", consumes = {
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_YAML_VALUE })
	@Operation(summary = "Create a person", description = "Create a person in the database", tags = {
			"People" }, responses = {
					@ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = PersonDTO.class))),
					@ApiResponse(description = "No content", responseCode = "204", content = @Content),
					@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
					@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
					@ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
			})
	public AccountCredentialsDTO createUser(AccountCredentialsDTO credentials);
}
