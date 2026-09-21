package pt.seixal.carlos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "pt.seixal.carlos.model")
public class Startup {

	public static void main(String[] args) {
		SpringApplication.run(Startup.class, args);
	}

}
