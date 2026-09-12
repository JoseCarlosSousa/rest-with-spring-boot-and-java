package pt.seixal.carlos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import pt.seixal.carlos.controllers.docs.AuthControllerDocs;
import pt.seixal.carlos.data.dto.v1.security.AccountCredentialsDTO;
import pt.seixal.carlos.services.AuthService;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for Authentication")
public class AuthController implements AuthControllerDocs {

	@Autowired
	AuthService service;

	@Override
	public ResponseEntity<?> signin(@RequestBody AccountCredentialsDTO credentials) {

		if (credentialsInvalid(credentials)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
		}
		var token = service.signIn(credentials);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
		}
		return ResponseEntity.ok().body(token);
	}

	@Override
	public ResponseEntity<?> refresh(
			@PathVariable("username") String username,
			@RequestHeader("Authorization") String refreshToken) {

		if (checkIfInvalid(username, refreshToken)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
		}
		var token = service.refresh(username, refreshToken);
		if (token == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
		}
		return ResponseEntity.ok().body(token);
	}

	@Override
	public AccountCredentialsDTO createUser(@RequestBody AccountCredentialsDTO credentials) {
		return service.create(credentials);
	}

	private boolean checkIfInvalid(String value1, String value2) {
		return StringUtils.isBlank(value1) || StringUtils.isBlank(value1);
	}

	private boolean credentialsInvalid(AccountCredentialsDTO credentials) {
		return credentials == null || checkIfInvalid(credentials.getUsername(), credentials.getPassword());
	}

}
