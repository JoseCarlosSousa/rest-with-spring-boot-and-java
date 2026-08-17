package pt.seixal.carlos.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.seixal.carlos.controllers.docs.BookControllerDocs;
import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.services.BookService;

import java.util.List;

@RestController
@RequestMapping("/api/book/v1")
@Tag(name="Book", description="Endpoints for Managing Book")
public class BookController implements BookControllerDocs {

    @Autowired
    private BookService service;

    @Override
    public List<BookDTO> findAll() {
        return service.findAll();
    }

    @Override
    public BookDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @Override
    public BookDTO create(@RequestBody BookDTO book) {
        return service.create(book);
    }

    @Override
    public BookDTO update(@RequestBody BookDTO book) {
        return service.update(book);
    }

    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
