package pt.seixal.carlos.security.jwt;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import pt.seixal.carlos.data.dto.v1.security.TokenDTO;

@Service
public class JwtTokenProvider {

	private final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

	@Value("${security.jwt.token.secret-key:secret}")
	private String secretKey = "secret";

	@Value("${security.jwt.token.expire-length:3600000}")
	private long validInMilliseconds = 3600000;

	@Value("${security.jwt.token.refresh-expire-length:86400000}")
	private long refreshValidInMilliseconds = 86400000;

	@Autowired
	private UserDetailsService userDetailsService;

	Algorithm algorithm = null;

	private final String BEARER = "Bearer ";

	@PostConstruct
	protected void init() {
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
		algorithm = Algorithm.HMAC256(secretKey.getBytes());
	}

	public TokenDTO createAccessToken(String username, List<String> roles) {
		var now = new Date();
		var validity = new Date(now.getTime() + validInMilliseconds);
		var accessToken = getAccessToken(username, roles, now, validity);
		var refreshToken = getRefreshToken(username, roles, now);

		return new TokenDTO(username, true, now, validity, accessToken, refreshToken);
	}

	public TokenDTO refreshToken(String refreshToken) {
		var tmpToken = refreshToken;
		if (checkTokenContainsBears(refreshToken)) {
			tmpToken = refreshToken.substring(BEARER.length()).trim();
		}

		var decodedJWT = decodedToken(tmpToken);

		var username = decodedJWT.getSubject();
		List<String> roles = decodedJWT.getClaim("roles").asList(String.class);

		return createAccessToken(username, roles);
	}

	private String getRefreshToken(String username, List<String> roles, Date now) {
		var refreshTokenValidity = new Date(now.getTime() + refreshValidInMilliseconds);
		return JWT.create()
				.withClaim("roles", roles)
				.withIssuedAt(now)
				.withExpiresAt(refreshTokenValidity)
				.withSubject(username)
				.sign(algorithm);
	}

	private String getAccessToken(String username, List<String> roles, Date now, Date validity) {
		var issuerUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
		return JWT.create()
				.withClaim("roles", roles)
				.withIssuedAt(now)
				.withExpiresAt(validity)
				.withSubject(username)
				.withIssuer(issuerUrl)
				.sign(algorithm);
	}

	public Authentication getAuthentication(String token) {
		var decodedJWT = decodedToken(token);
		var userDetails = this.userDetailsService.loadUserByUsername(decodedJWT.getSubject());
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public String resolveToken(HttpServletRequest request) {
		var bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (checkTokenContainsBears(bearerToken)) {
			return bearerToken.substring(BEARER.length());
		}
		return null;
	}

	public boolean validateToken(String token) {
		try {
			var decodedJWT = decodedToken(token);
			if (decodedJWT.getExpiresAt().before(new Date())) {
				return false;
			}
			return true;
		} catch (TokenExpiredException e) {
			logger.warn("O token JWT fornecido já expirou.");
		} catch (JWTVerificationException e) {
			logger.warn("O token JWT fornecido é inválido.");
		} catch (Exception e) {
			logger.error("Erro inesperado ao validar o token JWT.");
		}
		return false;
	}

	private DecodedJWT decodedToken(String token) {
		JWTVerifier verifier = JWT.require(algorithm).build();
		return verifier.verify(token);
	}

	private boolean checkTokenContainsBears(String token) {
		return StringUtils.isNotBlank(token) && token.startsWith(BEARER);
	}

}
