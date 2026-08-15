package pt.seixal.carlos.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.seixal.carlos.exceptions.ResourceNotFoundException;
import pt.seixal.carlos.model.Person;
import pt.seixal.carlos.repository.PersonRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PersonService {

    private final AtomicLong counter = new AtomicLong();
    private final Logger logger = LoggerFactory.getLogger(PersonService.class.getName());

    @Autowired
    PersonRepository repository;

    public List<Person> findAll() {
        logger.info("Finding all people!");
        return repository.findAll();
    }

    public Person findById(Long id) {
        logger.info("Finding one Person!");
        return getPerson(id);
    }

    public Person create(Person person) {
        logger.info("Creating Person");

        return repository.save(person);
    }

    private Person getPerson(Long id) {
        logger.info("Getting Person with id: {}", id);
        return repository.findById(id).orElseThrow(() -> {
            logger.error("Person not found with id: {}", id);
            return new ResourceNotFoundException("No record found for this id");
        });
    }

    public Person update(Person person) {
        logger.info("Edit Person");

        Person entity = getPerson(person.getId());
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
