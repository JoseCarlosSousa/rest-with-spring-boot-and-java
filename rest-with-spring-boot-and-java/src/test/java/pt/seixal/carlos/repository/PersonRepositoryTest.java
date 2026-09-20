package pt.seixal.carlos.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import pt.seixal.carlos.integrationtests.testcontainers.AbstractIntegrationTest;
import pt.seixal.carlos.model.Person;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PersonRepositoryTest extends AbstractIntegrationTest {

	@Autowired
	PersonRepository repository;

	private static Person personInstance;

	@BeforeAll
	static void setUpAll() {
		personInstance = new Person();
	}

	@Test
	@Order(1)
	void testFindPeopleByName() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "firstName"));

		personInstance = repository.findPeopleByName("and", pageable).getContent().get(0);

		assertNotNull(personInstance);
		assertNotNull(personInstance.getId());

		assertEquals("Aland", personInstance.getFirstName());
		assertEquals("Boyn", personInstance.getLastName());
		assertEquals("Apt 653", personInstance.getAddress());
		assertEquals("Male", personInstance.getGender());
	}

	@Test
	@Order(2)
	@Transactional
	void testDisablePerson() {
		Long id = personInstance.getId();
		repository.disablePerson(id);

		var result = repository.findById(id);
		personInstance = result.get();

		assertNotNull(personInstance);
		assertNotNull(personInstance.getId());

		assertEquals("Aland", personInstance.getFirstName());
		assertEquals("Boyn", personInstance.getLastName());
		assertEquals("Apt 653", personInstance.getAddress());
		assertEquals("Male", personInstance.getGender());
		assertFalse(personInstance.getEnabled());
	}
}
