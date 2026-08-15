package pt.seixal.carlos.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.seixal.carlos.exceptions.ResourceNotFoundException;
import pt.seixal.carlos.model.Person;
import pt.seixal.carlos.repository.PersonRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class PersonService {

    private final AtomicLong counter = new AtomicLong();
    private final Logger logger = Logger.getLogger(PersonService.class.getName());

    @Autowired
    PersonRepository repository;

    public List<Person> findAll() {
        logger.info("Finding all people!");
        return repository.findAll();
    }

    public Person findById(Long id) {
        logger.info("Finding one Person!");
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No record found for this id"));
    }

    public Person create(Person person) {
        logger.info("Creating Person");

        return repository.save(person);
    }

    public Person update(Person person) {
        logger.info("Edit Person");

        Person entity = repository.findById(person.getId()).orElseThrow(() -> new ResourceNotFoundException("No record found for this id"));
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        return repository.save(entity);
    }

    public void delete(Long id) {
        logger.info("delete Person");

        Person entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No record found for this id"));

        repository.delete(entity);
    }
}
