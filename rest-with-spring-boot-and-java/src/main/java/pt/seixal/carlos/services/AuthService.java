package pt.seixal.carlos.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pt.seixal.carlos.data.dto.v1.security.AccountCredentialsDTO;
import pt.seixal.carlos.data.dto.v1.security.TokenDTO;
import pt.seixal.carlos.repository.UserRepository;
import pt.seixal.carlos.security.jwt.JwtTokenProvider;

@Service
public class AuthService {

	private final Logger logger = LoggerFactory.getLogger(AuthService.class);

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenProvider tokenProvider;

	@Autowired
	private UserRepository repository;

	public ResponseEntity<TokenDTO> signIn(AccountCredentialsDTO credentials) {
		logger.info("Login with credentials");
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword()));

		var user = repository.findByUsername(credentials.getUsername());
		if (user == null) {
			logger.error("Username " + credentials.getUsername() + " not found!");
			throw new UsernameNotFoundException("Username " + credentials.getUsername() + " not found!");
		}
		var token = tokenProvider.createAccessToken(credentials.getUsername(), user.getRoles());

		return ResponseEntity.ok(token);
	}

	public ResponseEntity<TokenDTO> refresh(String username, String refreshToken) {
		logger.info("Login with refreshToken");
		var user = repository.findByUsername(username);
		TokenDTO token;
		if (user != null) {
			token = tokenProvider.refreshToken(refreshToken);
		} else {
			logger.error("Username {} not found!", username);
			throw new UsernameNotFoundException("Username " + username + " not found!");
		}

		return ResponseEntity.ok(token);
	}

}
