package pt.seixal.carlos.unitetests.mapper.mocks;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.model.Book;

public class MockBook {

	public Book mockEntity() {
		return mockEntity(0);
	}

	public BookDTO mockDTO() {
		return mockDTO(0);
	}

	public List<Book> mockEntityList() {
		var books = new ArrayList<Book>();
		for (int i = 0; i < 14; i++) {
			books.add(mockEntity(i));
		}
		return books;
	}

	public List<BookDTO> mockDTOList() {
		var books = new ArrayList<BookDTO>();
		for (int i = 0; i < 14; i++) {
			books.add(mockDTO(i));
		}
		return books;
	}

	public Book mockEntity(Integer number) {
		Book book = new Book();
		book.setAuthor("Author Test" + number);
		book.setLaunchDate(generateLaunchDate());
		book.setPrice(200.00 + number);
		book.setId(number.longValue());
		book.setTitle("Title Test" + number);
		return book;
	}

	public BookDTO mockDTO(Integer number) {
		BookDTO book = new BookDTO();
		book.setAuthor("Author Test" + number);
		book.setLaunchDate(generateLaunchDate());
		book.setPrice(200.00 + number);
		book.setId(number.longValue());
		book.setTitle("Title Test" + number);
		return book;
	}

	private Date generateLaunchDate() {
		String strDate = "2026-08-17";
		return Date.from(LocalDate.parse(strDate)
				.atStartOfDay(ZoneId.systemDefault())
				.toInstant());
	}

}