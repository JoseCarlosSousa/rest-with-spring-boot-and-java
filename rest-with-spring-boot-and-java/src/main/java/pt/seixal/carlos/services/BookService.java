package pt.seixal.carlos.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;
import pt.seixal.carlos.controllers.BookController;
import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.exceptions.RequiredObjectIsNullException;
import pt.seixal.carlos.exceptions.ResourceNotFoundException;
import pt.seixal.carlos.model.Book;
import pt.seixal.carlos.repository.BookRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static pt.seixal.carlos.mapper.ObjectMapper.parseObject;

@Service
public class BookService {

    private final Logger logger = LoggerFactory.getLogger(BookService.class.getName());

    @Autowired
    BookRepository repository;

    @Autowired
    PagedResourcesAssembler<BookDTO> assembler;
    
    public PagedModel<EntityModel<BookDTO>> findAll(Pageable pageable) {
        logger.info("Finding all books!");
        
        var books = repository.findAll(pageable);
        
		var peopleWithLinks = books.map(book -> {
			var dto = parseObject(book, BookDTO.class);
			addHateoasLinks(dto);
			return dto;
		});
		
		Link findAllLink = WebMvcLinkBuilder.linkTo(
				WebMvcLinkBuilder.methodOn(BookController.class)
				.findAll(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort().toString()))
				.withSelfRel();
		
        return assembler.toModel(peopleWithLinks, findAllLink);
    }

    public BookDTO findById(Long id) {
        logger.info("Finding one Book!");
        var dto = parseObject(getBook(id), BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public BookDTO create(BookDTO book) {
        logger.info("Creating Book");
        if (book == null) throw new RequiredObjectIsNullException();

        var entity = parseObject(book, Book.class);
        var dto = parseObject(repository.save(entity), BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    private Book getBook(Long id) {
        logger.info("Getting Book with id: {}", id);
        return repository.findById(id).orElseThrow(() ->  new ResourceNotFoundException("No record found for this id"));
    }

    public BookDTO update(BookDTO book) {
        logger.info("Edit Book");

        if (book == null) throw new RequiredObjectIsNullException();

        Book entity = getBook(book.getId());
        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());

        var dto = parseObject(repository.save(entity), BookDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id) {
        logger.info("delete Book");

        var entity = getBook(id);

        repository.delete(entity);
    }

    private void addHateoasLinks(BookDTO dto) {
        dto.add(linkTo(methodOn(BookController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).findAll(0,12,"asc")).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(BookController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(BookController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(BookController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}
