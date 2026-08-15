package pt.seixal.carlos.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.data.dto.v2.PersonDTOV2;
import pt.seixal.carlos.exceptions.ResourceNotFoundException;
import pt.seixal.carlos.mapper.custom.PersonMapper;
import pt.seixal.carlos.model.Person;
import pt.seixal.carlos.repository.PersonRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static pt.seixal.carlos.mapper.ObjectMapper.parseListObjects;
import static pt.seixal.carlos.mapper.ObjectMapper.parseObject;

@Service
public class PersonService {

    private final AtomicLong counter = new AtomicLong();
    private final Logger logger = LoggerFactory.getLogger(PersonService.class.getName());

    @Autowired
    PersonRepository repository;
    @Autowired
    PersonMapper mapper;

    public List<PersonDTO> findAll() {
        logger.info("Finding all people!");
        return parseListObjects(repository.findAll(), PersonDTO.class);
    }

    public PersonDTO findById(Long id) {
        logger.info("Finding one Person!");
        return parseObject(getPerson(id), PersonDTO.class);
    }

    public PersonDTO create(PersonDTO person) {
        logger.info("Creating Person");

        var entity = parseObject(person, Person.class);
        return parseObject(repository.save(entity), PersonDTO.class);
    }

    public PersonDTOV2 createV2(PersonDTOV2 person) {
        logger.info("Creating PersonV2");

        var entity = mapper.convertDTOToEntity(person);
        return mapper.convertEntityToDTO(repository.save(entity));
    }

    private Person getPerson(Long id) {
        logger.info("Getting Person with id: {}", id);
        return repository.findById(id).orElseThrow(() ->  new ResourceNotFoundException("No record found for this id"));
    }

    public PersonDTO update(PersonDTO person) {
        logger.info("Edit Person");

        Person entity = getPerson(person.getId());
        entity.setFirstName(person.getFirstName());
        entity.setLastName(person.getLastName());
        entity.setAddress(person.getAddress());
        entity.setGender(person.getGender());

        return parseObject(repository.save(entity), PersonDTO.class);
    }

    public void delete(Long id) {
        logger.info("delete Person");

        var entity = getPerson(id);

        repository.delete(entity);
    }
}
