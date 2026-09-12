package pt.seixal.carlos;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

@SpringBootApplication
@EntityScan(basePackages = "pt.seixal.carlos.model")
public class Startup {

	public static void main(String[] args) {
		SpringApplication.run(Startup.class, args);

		generateHashedPassword();
	}

	private static void generateHashedPassword() {

		PasswordEncoder pbkdf2Encoder = new Pbkdf2PasswordEncoder("", 8, 18500,
				Pbkdf2PasswordEncoder.SecretKeyFactoryAlgorithm.PBKDF2WithHmacSHA256);
		Map<String, PasswordEncoder> encoders = new HashMap<>();
		encoders.put("pbkdf2", pbkdf2Encoder);
		var passwordEncoder = new DelegatingPasswordEncoder("pbkdf2", encoders);
		passwordEncoder.setDefaultPasswordEncoderForMatches(pbkdf2Encoder);
		System.out.println(passwordEncoder.encode("admin123"));
		System.out.println(passwordEncoder.encode("admin124"));
	}

}
