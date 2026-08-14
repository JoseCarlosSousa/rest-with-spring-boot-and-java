package pt.seixal.carlos;

import org.springframework.stereotype.Service;
import pt.seixal.carlos.model.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonService {

    private final AtomicLong counter = new AtomicLong();

    private Logger logger = Logger.getLogger(PersonService.class.getName());

    public List<Person> findAll() {
        List<Person> persons = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            persons.add(mokPerson(i));
        }

        return persons;
    }

    private Person mokPerson(int i) {
        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Carlos" + i);
        person.setLastName("Sousa" + i);
        person.setAddress("Rua das Pretas");
        person.setGender("Male");
        return person;
    }

    public Person findById(String id) {
        logger.info("Finding one Person!");

        Person person = new Person();
        person.setId(counter.incrementAndGet());
        person.setFirstName("Carlos");
        person.setLastName("Sousa");
        person.setAddress("Rua das Pretas");
        person.setGender("Male");
        return person;
    }
}
