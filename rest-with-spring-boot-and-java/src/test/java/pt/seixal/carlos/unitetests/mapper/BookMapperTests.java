package pt.seixal.carlos.unitetests.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.model.Book;
import pt.seixal.carlos.unitetests.mapper.mocks.MockBook;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pt.seixal.carlos.mapper.ObjectMapper.parseListObjects;
import static pt.seixal.carlos.mapper.ObjectMapper.parseObject;

public class BookMapperTests {
    MockBook inputObject;

    @BeforeEach
    public void setUp() {
        inputObject = new MockBook();
    }

    @Test
    public void parseEntityToDTOTest() {
        BookDTO output = parseObject(inputObject.mockEntity(), BookDTO.class);
        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("Author Test0", output.getAuthor());
        assertEquals(generateLaunchDate(), output.getLaunchDate());
        assertEquals(200.00, output.getPrice());
        assertEquals("Title Test0", output.getTitle());
    }

    @Test
    public void parseEntityListToDTOListTest() {
        List<BookDTO> outputList = parseListObjects(inputObject.mockEntityList(), BookDTO.class);

        BookDTO outputZero = outputList.get(0);
        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("Author Test0", outputZero.getAuthor());
        assertEquals(generateLaunchDate(), outputZero.getLaunchDate());
        assertEquals(200.00, outputZero.getPrice());
        assertEquals("Title Test0", outputZero.getTitle());

        BookDTO outputSeven = outputList.get(7);
        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("Author Test7", outputSeven.getAuthor());
        assertEquals(generateLaunchDate(), outputSeven.getLaunchDate());
        assertEquals(207.00, outputSeven.getPrice());
        assertEquals("Title Test7", outputSeven.getTitle());

        BookDTO outputTwelve = outputList.get(12);
        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("Author Test12", outputTwelve.getAuthor());
        assertEquals(generateLaunchDate(), outputTwelve.getLaunchDate());
        assertEquals(212.00, outputTwelve.getPrice());
        assertEquals("Title Test12", outputTwelve.getTitle());
    }

    @Test
    public void parseDTOToEntityTest() {
        Book output = parseObject(inputObject.mockDTO(), Book.class);
        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("Author Test0", output.getAuthor());
        assertEquals(generateLaunchDate(), output.getLaunchDate());
        assertEquals(200.00, output.getPrice());
        assertEquals("Title Test0", output.getTitle());
    }

    @Test
    public void parserDTOListToEntityListTest() {
        List<Book> outputList = parseListObjects(inputObject.mockDTOList(), Book.class);
        Book outputZero = outputList.get(0);

        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("Author Test0", outputZero.getAuthor());
        assertEquals(generateLaunchDate(), outputZero.getLaunchDate());
        assertEquals(200.00, outputZero.getPrice());
        assertEquals("Title Test0", outputZero.getTitle());

        Book outputSeven = outputList.get(7);

        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("Author Test7", outputSeven.getAuthor());
        assertEquals(generateLaunchDate(), outputSeven.getLaunchDate());
        assertEquals(207.00, outputSeven.getPrice());
        assertEquals("Title Test7", outputSeven.getTitle());

        Book outputTwelve = outputList.get(12);

        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("Author Test12", outputTwelve.getAuthor());
        assertEquals(generateLaunchDate(), outputTwelve.getLaunchDate());
        assertEquals(212.00, outputTwelve.getPrice());
        assertEquals("Title Test12", outputTwelve.getTitle());
    }

    private Date generateLaunchDate() {
        String strDate = "2026-08-17";
        return Date.from(LocalDate.parse(strDate)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
    }
}