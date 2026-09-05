package pt.seixal.carlos.dto.wrappers.xml;

import java.io.Serializable;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import pt.seixal.carlos.dto.PersonDTO;

@XmlRootElement
public class PagedModelPerson implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@XmlElement(name = "content")
	public List<PersonDTO> content;

	public PagedModelPerson() {
	}

	public List<PersonDTO> getContent() {
		return content;
	}

	public void setContent(List<PersonDTO> content) {
		this.content = content;
	}
	
}
