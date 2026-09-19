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
import pt.seixal.carlos.data.dto.v1.security.TokenDTO;
import pt.seixal.carlos.services.AuthService;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints for Authentication")
public class AuthController implements AuthControllerDocs {

	@Autowired
	AuthService service;

	@Override
	public ResponseEntity<?> signin(@RequestBody AccountCredentialsDTO credentials) {

		ResponseEntity<TokenDTO> token = null;
		if (!credentialsInvalid(credentials)) {
			token = service.signIn(credentials);
		}
		return token != null ? token : ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
	}

	@Override
	public ResponseEntity<?> refresh(
			@PathVariable("username") String username,
			@RequestHeader("Authorization") String refreshToken) {

		ResponseEntity<TokenDTO> token = null;
		if (!checkIfInvalid(username, refreshToken)) {
			token = service.refresh(username, refreshToken);
		}
		return token != null ? token : ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
	}

	private boolean checkIfInvalid(String username, String value) {
		return StringUtils.isBlank(username) || StringUtils.isBlank(value);
	}

	private boolean credentialsInvalid(AccountCredentialsDTO credentials) {
		return credentials == null || checkIfInvalid(credentials.getUsername(), credentials.getPassword());
	}

}
