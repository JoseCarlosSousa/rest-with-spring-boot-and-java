package pt.seixal.carlos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.seixal.carlos.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
