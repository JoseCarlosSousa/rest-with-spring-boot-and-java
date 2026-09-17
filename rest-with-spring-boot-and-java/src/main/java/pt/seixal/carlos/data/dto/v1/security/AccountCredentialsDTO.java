package pt.seixal.carlos.data.dto.v1.security;

import java.io.Serializable;
import java.util.Objects;

public class AccountCredentialsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String username;
	private String password;
	private String fullName;
	private String role;

	public AccountCredentialsDTO() {
	}

	public AccountCredentialsDTO(String username, String password, String fullName, String role) {
		this.username = username;
		this.password = password;
		this.fullName = fullName;
		this.role = role;
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

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fullName, password, role, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		AccountCredentialsDTO other = (AccountCredentialsDTO) obj;
		return Objects.equals(fullName, other.fullName) && Objects.equals(password, other.password)
				&& Objects.equals(role, other.role) && Objects.equals(username, other.username);
	}

}
