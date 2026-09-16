package pt.seixal.carlos.dto.wrappers.xml;

import java.io.Serializable;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import pt.seixal.carlos.dto.BookDTO;

@XmlRootElement
public class PagedModelBook implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "content")
	public List<BookDTO> content;

	public PagedModelBook() {
	}

	public List<BookDTO> getContent() {
		return content;
	}

	public void setContent(List<BookDTO> content) {
		this.content = content;
	}

}
