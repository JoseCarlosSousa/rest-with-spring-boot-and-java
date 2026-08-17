package pt.seixal.carlos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.seixal.carlos.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}