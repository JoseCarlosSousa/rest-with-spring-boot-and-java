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
import pt.seixal.carlos.data.dto.v1.UserDTO;
import pt.seixal.carlos.file.exporter.MediaTypes;

public interface UserControllerDocs {

	@GetMapping(produces = {
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE })
	@Operation(summary = "Find all users", description = "Find all users in the database", tags = {
			"User" }, responses = {
					@ApiResponse(description = "Success", responseCode = "200", content = {
							@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))) }),
					@ApiResponse(description = "No content", responseCode = "204", content = @Content),
					@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
					@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
					@ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
			})
	ResponseEntity<PagedModel<EntityModel<UserDTO>>> findAll(@RequestParam Map<String, String> allParams);

	@GetMapping(value = "/exportPage", produces = {
			MediaTypes.CSV,
			MediaTypes.XLSX,
			MediaTypes.PDF
	})
	@Operation(summary = "Export users", description = "Export a Page of users in PDF, Excel or CSV format", tags = {
			"User" }, responses = {
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
	@Operation(summary = "Export User", description = "Export a users in PDF, Excel or CSV format", tags = {
			"User" }, responses = {
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
	@Operation(summary = "Find a user", description = "Find a user by ID", tags = { "User" }, responses = {
			@ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = UserDTO.class))),
			@ApiResponse(description = "No content", responseCode = "204", content = @Content),
			@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
			@ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
	})
	UserDTO findById(@PathVariable("id") Long id);

	@PostMapping(value = "/create", consumes = {
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_YAML_VALUE })
	@Operation(summary = "Create a user", description = "Create a user in the database", tags = {
			"User" }, responses = {
					@ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = UserDTO.class))),
					@ApiResponse(description = "No content", responseCode = "204", content = @Content),
					@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
					@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
					@ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
			})
	public UserDTO create(UserDTO credentials);

	@PutMapping(consumes = {
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_YAML_VALUE }, produces = {
					MediaType.APPLICATION_JSON_VALUE,
					MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_YAML_VALUE })
	@Operation(summary = "Update User", description = "Update a user in the database", tags = { "User" }, responses = {
			@ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = UserDTO.class))),
			@ApiResponse(description = "No content", responseCode = "204", content = @Content),
			@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
			@ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
	})
	UserDTO update(@RequestBody UserDTO user);

	@DeleteMapping(value = "/{id}")
	@Operation(summary = "Delete a book", description = "Delete a book by ID", tags = { "User" }, responses = {
			@ApiResponse(description = "Success", responseCode = "200", content = @Content(schema = @Schema(implementation = UserDTO.class))),
			@ApiResponse(description = "No content", responseCode = "204", content = @Content),
			@ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
			@ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
			@ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
	})
	ResponseEntity<?> delete(@PathVariable("id") Long id);
}
