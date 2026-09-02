package pt.seixal.carlos.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.seixal.carlos.controllers.docs.BookControllerDocs;
import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.services.BookService;


@RestController
@RequestMapping("/api/book/v1")
@Tag(name="Book", description="Endpoints for Managing Book")
public class BookController implements BookControllerDocs {

    @Autowired
    private BookService service;

    @Override
    public ResponseEntity<PagedModel<EntityModel<BookDTO>>> findAll(
        	@RequestParam(value = "page", defaultValue = "0") int page,
        	@RequestParam(value = "size", defaultValue = "12") int size,
        	@RequestParam(value = "direction", defaultValue = "asc") String direction
    ){
    	var sort = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
    	Pageable pageable = PageRequest.of(page, size, Sort.by(sort, "title"));
    	return ResponseEntity.ok(service.findAll(pageable));
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
