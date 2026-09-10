package pt.seixal.carlos.data.dto.v1.request;

import java.util.Objects;

public class EmailRequestDTO {

	private String to;
	private String subject;
	private String body;
	
	public EmailRequestDTO() {
		super();
	}
	
	public String getTo() {
		return to;
	}
	
	public void setTo(String to) {
		this.to = to;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public String getBody() {
		return body;
	}
	
	public void setBody(String body) {
		this.body = body;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(body, subject, to);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmailRequestDTO other = (EmailRequestDTO) obj;
		return Objects.equals(body, other.body) && Objects.equals(subject, other.subject)
				&& Objects.equals(to, other.to);
	}

}
