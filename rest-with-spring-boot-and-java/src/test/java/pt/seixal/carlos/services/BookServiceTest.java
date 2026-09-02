package pt.seixal.carlos.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.exceptions.RequiredObjectIsNullException;
import pt.seixal.carlos.model.Book;
import pt.seixal.carlos.repository.BookRepository;
import pt.seixal.carlos.unitetests.mapper.mocks.MockBook;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    MockBook input;

    @InjectMocks
    private BookService service;

    @Mock
    BookRepository repository;

    @BeforeEach
    void setUp() {
        input = new MockBook();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Disabled
    void findAll() {
        List<Book> list =input.mockEntityList();
        when(repository.findAll()).thenReturn(list);
        var books = new ArrayList<BookDTO>();  //service.findAll();

        assertNotNull(books);
        assertEquals(14, books.size());

        checkBookData(books, 1);
        checkBookData(books, 4);
    }

    private void checkBookData(List<BookDTO> books, int i){
        var book = books.get(i);
        checkHateoasLinks(book, i);
    }

    @Test
    void findById() {
        Book book = input.mockEntity(1);
        book.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(book));
        var result = service.findById(1L);

        checkHateoasLinks(result, 1);
    }

    @Test
    void create() {
        Book book = input.mockEntity(1);
        Book persisted = book;
        persisted.setId(1L);

        BookDTO dto = input.mockDTO(1);

        when(repository.save(book)).thenReturn(persisted);
        var result = service.create(dto);

        checkHateoasLinks(result, 1);
    }

    @Test
    void testCreateWithNullBook() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.create(null);
        });
        String expectedMessage = "It is not allowed to pass null as a required object";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void update() {

        Book book = input.mockEntity(1);
        Book persisted = book;
        persisted.setId(1L);

        BookDTO dto = input.mockDTO(1);

        when(repository.findById(1L)).thenReturn(Optional.of(book));
        when(repository.save(book)).thenReturn(persisted);

        var result = service.update(dto);

        checkHateoasLinks(result, 1);
    }
    
    @Test
    void testUpdateWithNullBook() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.update(null);
        });
        String expectedMessage = "It is not allowed to pass null as a required object";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void delete() {
        Book book = input.mockEntity(1);
        book.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(book));

        service.delete(1L);
        verify(repository, times(1)).findById(anyLong());
        verify(repository, times(1)).delete(any(Book.class));
        verifyNoMoreInteractions(repository);
    }

    private void checkHateoasLinks(BookDTO book, int i) {
        assertNotNull(book);
        assertNotNull(book.getId());

        checkLinks(book, "self", "api/book/v1/"+i, "GET");
        checkLinks(book, "findAll", "api/book/v1", "GET");
        checkLinks(book, "create", "api/book/v1", "POST");
        checkLinks(book, "update", "api/book/v1", "PUT");
        checkLinks(book, "delete", "api/book/v1/"+i, "DELETE");

        assertEquals("Author Test" + i, book.getAuthor());
        assertEquals(generateLaunchDate(), book.getLaunchDate());
        assertEquals(200.00 + i, book.getPrice());
        assertEquals("Title Test" + i, book.getTitle());
    }



    private void checkLinks(BookDTO book, String action, String strLink, String type) {
        assertNotNull(book.getLinks(), "The list of links must not be null.");

        assertTrue(book.getLinks().stream()
                        .anyMatch(link -> link.getRel().value().equals(action) &&
                                //link.getHref().endsWith(strLink) &&
                                link.getHref().contains(strLink) &&
                                link.getType().equals(type)), "No links match the criteria.");


    }

    private Date generateLaunchDate() {
        String strDate = "2026-08-17";
        return Date.from(LocalDate.parse(strDate)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
    }
}