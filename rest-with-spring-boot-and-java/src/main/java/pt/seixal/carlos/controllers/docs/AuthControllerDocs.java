package pt.seixal.carlos.controllers.docs;

import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import io.swagger.v3.oas.annotations.Operation;
import pt.seixal.carlos.data.dto.v1.security.AccountCredentialsDTO;

@EnableMethodSecurity
public interface AuthControllerDocs {

	@Operation(summary = "Authenticates an user and return a token")
	@PostMapping("/signin")
	public ResponseEntity<?> signin(AccountCredentialsDTO credentials);

	@Operation(summary = "Refresh token for authenticated user and returns a token")
	@PutMapping("/refresh/{username}")
	public ResponseEntity<?> refresh(String username, String refreshToken);

}
