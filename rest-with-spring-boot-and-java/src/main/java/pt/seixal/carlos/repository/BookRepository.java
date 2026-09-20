package pt.seixal.carlos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pt.seixal.carlos.model.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}