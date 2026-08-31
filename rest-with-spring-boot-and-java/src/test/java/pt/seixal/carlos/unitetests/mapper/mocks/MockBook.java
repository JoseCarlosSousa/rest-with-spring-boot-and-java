package pt.seixal.carlos.unitetests.mapper.mocks;

import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.model.Book;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockBook {

    public Book mockEntity() {
        return mockEntity(0);
    }
    
    public BookDTO mockDTO() {
        return mockDTO(0);
    }
    
    public List<Book> mockEntityList() {
        var persons = new ArrayList<Book>();
        for (int i = 0; i < 14; i++) {
            persons.add(mockEntity(i));
        }
        return persons;
    }

    public List<BookDTO> mockDTOList() {
        var persons = new ArrayList<BookDTO>();
        for (int i = 0; i < 14; i++) {
            persons.add(mockDTO(i));
        }
        return persons;
    }
    
    public Book mockEntity(Integer number) {
        Book person = new Book();
        person.setAuthor("Author Test" + number);
        person.setLaunchDate(generateLaunchDate());
        person.setPrice(200.00 + number);
        person.setId(number.longValue());
        person.setTitle("Title Test" + number);
        return person;
    }

    public BookDTO mockDTO(Integer number) {
        BookDTO person = new BookDTO();
        person.setAuthor("Author Test" + number);
        person.setLaunchDate(generateLaunchDate());
        person.setPrice(200.00 + number);
        person.setId(number.longValue());
        person.setTitle("Title Test" + number);
        return person;
    }

    private Date generateLaunchDate() {
        String strDate = "2026-08-17";
        return Date.from(LocalDate.parse(strDate)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
    }

}