package pt.seixal.carlos.config;

import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 💡 ENVIRONMENT CONFIGURATION DOCUMENTATION (macOS + Eclipse)
 * ============================================================================
 * For this Spring Boot project (specifically JavaMailSender and Integration Tests) 
 * to run properly, Eclipse must be able to read system environment variables.
 * 
 * 1. OS-LEVEL ENVIRONMENT VARIABLES SETUP:
 *    The required variables were permanently added to the '~/.zshrc' file:
 *    export EMAIL_USERNAME="your_email@gmail.com"
 *    export EMAIL_PASSWORD="your_google_app_password"
 * 
 * 2. MAC-SPECIFIC ECLIPSE ISSUE:
 *    When Eclipse is launched normally via the Finder or Launchpad GUI, it runs
 *    as an isolated process and DOES NOT inherit variables defined in '~/.zshrc'.
 *    This caused the Spring Boot ApplicationContext to fail during JUnit/Testcontainers 
 *    runs, throwing 'ApplicationContext failure threshold exceeded'.
 * 
 * 3. AUTOMATED SOLUTION USING AUTOMATOR:
 *    A custom macOS Application shortcut ("Eclipse Vars.app") was built using 
 *    Automator and placed on the Desktop. Its internal shell script runs:
 *    
 *    source ~/.zshrc
 *    open -a Eclipse
 * 
 * ⚠️ CRITICAL RULES: 
 *    - ALWAYS launch Eclipse using this custom Desktop shortcut app.
 * ============================================================================
 */


@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfig {
	
    private String host;
    private int port;
    private String username;
    private String password;
    private String from;
    private boolean ssl;
    
    
	public EmailConfig() {}


	public String getHost() {
		return host;
	}


	public void setHost(String host) {
		this.host = host;
	}


	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getFrom() {
		return from;
	}


	public void setFrom(String from) {
		this.from = from;
	}


	public boolean isSsl() {
		return ssl;
	}


	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}


	@Override
	public int hashCode() {
		return Objects.hash(from, host, password, Integer.valueOf(port), Boolean.valueOf(ssl), username);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmailConfig other = (EmailConfig) obj;
		return Objects.equals(from, other.from) && Objects.equals(host, other.host)
				&& Objects.equals(password, other.password) && port == other.port && ssl == other.ssl
				&& Objects.equals(username, other.username);
	}
}
