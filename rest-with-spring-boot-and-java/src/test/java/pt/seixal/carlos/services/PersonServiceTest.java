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
import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.exceptions.RequiredObjectIsNullException;
import pt.seixal.carlos.model.Person;
import pt.seixal.carlos.repository.PersonRepository;
import pt.seixal.carlos.unitetests.mapper.mocks.MockPerson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    MockPerson input;

    @InjectMocks
    private PersonService service;

    @Mock
    PersonRepository repository;

    @BeforeEach
    void setUp() {
        input = new MockPerson();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Disabled("REASON: Still Under Development")
    void findAll() {
        List<Person> list =input.mockEntityList();
        when(repository.findAll()).thenReturn(list);
        var persons = new ArrayList<PersonDTO>();//service.findAll();

        assertNotNull(persons);
        assertEquals(14, persons.size());

        checkPersonData(persons, 1);
        checkPersonData(persons, 4);
    }

    private void checkPersonData(List<PersonDTO> persons, int i){
        var person = persons.get(i);
        checkHateoasLinks(person, i);
    }

    @Test
    void findById() {
        Person person = input.mockEntity(1);
        person.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(person));
        var result = service.findById(1L);

        checkHateoasLinks(result, 1);
    }

    @Test
    void create() {
        Person person = input.mockEntity(1);
        Person persisted = person;
        persisted.setId(1L);

        PersonDTO dto = input.mockDTO(1);

        when(repository.save(person)).thenReturn(persisted);
        var result = service.create(dto);

        checkHateoasLinks(result, 1);
    }

    @Test
    void testCreateWithNullPerson() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.create(null);
        });
        String expectedMessage = "It is not allowed to pass null as a required object";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void update() {

        Person person = input.mockEntity(1);
        Person persisted = person;
        persisted.setId(1L);

        PersonDTO dto = input.mockDTO(1);

        when(repository.findById(1L)).thenReturn(Optional.of(person));
        when(repository.save(person)).thenReturn(persisted);

        var result = service.update(dto);

        checkHateoasLinks(result, 1);
    }
    
    @Test
    void testUpdateWithNullPerson() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class, () -> {
            service.update(null);
        });
        String expectedMessage = "It is not allowed to pass null as a required object";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void delete() {
        Person person = input.mockEntity(1);
        person.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(person));

        service.delete(1L);
        verify(repository, times(1)).findById(anyLong());
        verify(repository, times(1)).delete(any(Person.class));
        verifyNoMoreInteractions(repository);
    }

    private void checkHateoasLinks(PersonDTO person, int i) {
        assertNotNull(person);
        assertNotNull(person.getId());

        checkLinks(person, "self", "api/person/v1/"+i, "GET");
        checkLinks(person, "findAll", "api/person/v1", "GET");
        checkLinks(person, "create", "api/person/v1", "POST");
        checkLinks(person, "update", "api/person/v1", "PUT");
        checkLinks(person, "delete", "api/person/v1/"+i, "DELETE");

        assertEquals("First Name Test" + i, person.getFirstName());
        assertEquals("Last Name Test" + i, person.getLastName());
        assertEquals("Address Test" + i, person.getAddress());
        assertEquals(((i % 2)==0) ? "Male" : "Female", person.getGender());
    }

    private void checkLinks(PersonDTO person, String action, String strLink, String type) {
        assertNotNull(person.getLinks(), "The list of links must not be null.");

        assertTrue(person.getLinks().stream()
                        .anyMatch(link -> link.getRel().value().equals(action) &&
                                //link.getHref().endsWith(strLink) &&
                                link.getHref().contains(strLink) &&
                                link.getType().equals(type)), "No links match the criteria.");


    }
}