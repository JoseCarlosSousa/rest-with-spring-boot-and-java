package pt.seixal.carlos.dto.wrappers.json;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import pt.seixal.carlos.data.dto.v1.BookDTO;

public class BookEmbeddedDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("books")
	private List<BookDTO> books;

	public BookEmbeddedDTO() {
	}

	public List<BookDTO> getBooks() {
		return books;
	}

	public void setBook(List<BookDTO> books) {
		this.books = books;
	}

}
