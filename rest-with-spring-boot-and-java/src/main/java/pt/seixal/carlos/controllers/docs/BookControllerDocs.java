package pt.seixal.carlos.controllers.docs;

import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.file.exporter.MediaTypes;

public interface BookControllerDocs {

	@GetMapping(produces = {
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE })
	@Operation(summary = "Find all book", description = "Find all book in the database", tags = {
			"Book" }, responses = {
					@ApiResponse(description = "Success", responseCode = "200", content = {
							@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = BookDTO.class))) }),
					@ApiResponse(description = "No content", responseCode = "204", content = @Content),
					@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
					@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
					@ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
			})
	ResponseEntity<PagedModel<EntityModel<BookDTO>>> findAll(@RequestParam Map<String, String> allParams);

	@GetMapping(value = "/exportPage", produces = {
			MediaTypes.CSV,
			MediaTypes.XLSX,
			MediaTypes.PDF
	})
	@Operation(summary = "Export books", description = "Export a Page of books in XLSX and CSV format", tags = {
			"Book" }, responses = {
					@ApiResponse(description = "Success", responseCode = "200", content = {
							@Content(mediaType = MediaTypes.CSV),
							@Content(mediaType = MediaTypes.XLSX),
							@Content(mediaType = MediaTypes.PDF)
					}),
					@ApiResponse(description = "No content", responseCode = "204", content = @Content),
					@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
					@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
					@ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
			})
	ResponseEntity<Resource> exportPage(
			@RequestParam Map<String, String> allParams,
			HttpServletRequest request);

	@GetMapping(value = "/export/{id}", produces = {
			MediaTypes.CSV,
			MediaTypes.XLSX,
			MediaTypes.PDF
	})
	@Operation(summary = "Export book", description = "Export a Page of a book in XLSX, CSV or PDF format", tags = {
			"Book" }, responses = {
					@ApiResponse(description = "Success", responseCode = "200", content = {
							@Content(mediaType = MediaTypes.CSV),
							@Content(mediaType = MediaTypes.XLSX),
							@Content(mediaType = MediaTypes.PDF)
					}),
					@ApiResponse(description = "No content", responseCode = "204", content = @Content),
					@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
					@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
					@ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
			})
	ResponseEntity<Resource> export(@PathVariable("id") Long id, HttpServletRequest request);

	@GetMapping(value = "/{id}", produces = {
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE })
	@Operation(summary = "Find a book", description = "Find a book by ID", tags = { "Book" }, responses = {
			@ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = BookDTO.class))),
			@ApiResponse(description = "No content", responseCode = "204", content = @Content),
			@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
			@ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
	})
	BookDTO findById(@PathVariable("id") Long id);

	@PostMapping(consumes = {
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_YAML_VALUE })
	@Operation(summary = "Create a book", description = "Create a book in the database", tags = {
			"Book" }, responses = {
					@ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = BookDTO.class))),
					@ApiResponse(description = "No content", responseCode = "204", content = @Content),
					@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
					@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
					@ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
			})
	BookDTO create(@RequestBody BookDTO book);

	@PutMapping(consumes = {
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_YAML_VALUE })
	@Operation(summary = "Update Book", description = "Update a book in the database", tags = { "Book" }, responses = {
			@ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = BookDTO.class))),
			@ApiResponse(description = "No content", responseCode = "204", content = @Content),
			@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
			@ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
	})
	BookDTO update(@RequestBody BookDTO book);

	@DeleteMapping(value = "/{id}")
	@Operation(summary = "Delete a book", description = "Delete a book by ID", tags = { "Book" }, responses = {
			@ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = BookDTO.class))),
			@ApiResponse(description = "No content", responseCode = "204", content = @Content),
			@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
			@ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
	})
	ResponseEntity<?> delete(@PathVariable("id") Long id);
}
