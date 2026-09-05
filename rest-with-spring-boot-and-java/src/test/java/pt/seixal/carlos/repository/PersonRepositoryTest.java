package pt.seixal.carlos.repository;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pt.seixal.carlos.integrationtests.testcontainers.AbstractIntegrationTest;
import pt.seixal.carlos.model.Person;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonRepositoryTest extends AbstractIntegrationTest {

	
	@Autowired
	PersonRepository repository;
	private static Person person;
	
	
	@BeforeAll
	static void setUp() {
		person = new Person();
	}

	@Test
	@Order(1)
	void testFindPeopleByName () {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "firstName"));
		
		person = repository.findPeopleByName("and", pageable).getContent().get(0);
		
		assertNotNull(person);
		assertNotNull(person.getId());

		assertEquals("Aland", person.getFirstName());
		assertEquals("Boyn", person.getLastName());
		assertEquals("Apt 653", person.getAddress());
		assertEquals("Male", person.getGender());
		//assertTrue(person.getEnabled());
	}

	@Test
	@Order(2)
	void testDisablePerson() {
		Long id = person.getId();
		repository.disablePerson(id);
		
		var result = repository.findById(id);
		person = result.get();
		
		assertNotNull(person);
		assertNotNull(person.getId());

		assertEquals("Aland", person.getFirstName());
		assertEquals("Boyn", person.getLastName());
		assertEquals("Apt 653", person.getAddress());
		assertEquals("Male", person.getGender());
		assertFalse(person.getEnabled());
		
	}

}
