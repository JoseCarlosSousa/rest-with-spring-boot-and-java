package pt.seixal.carlos.controllers.docs;

import java.util.List;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import pt.seixal.carlos.data.dto.v1.PersonDTO;


public interface PersonControllerDocs {

    @GetMapping(
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    @Operation(summary = "Find all people",
            description = "Find all people in the database",
            tags = {"People"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {@Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PersonDTO.class))
                            )}),
                    @ApiResponse(description = "No content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findAll(
        	@RequestParam(value = "page", defaultValue = "0") int page,
        	@RequestParam(value = "size", defaultValue = "12") int size,
        	@RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    @GetMapping(value = "/findPeopleByName/{firstName}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    @Operation(summary = "Find people",
            description = "Find people in the database",
            tags = {"People"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = {@Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = PersonDTO.class))
                            )}),
                    @ApiResponse(description = "No content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findByName(
    		@PathVariable(value = "firstName") String firstName,
        	@RequestParam(value = "page", defaultValue = "0") int page,
        	@RequestParam(value = "size", defaultValue = "12") int size,
        	@RequestParam(value = "direction", defaultValue = "asc") String direction
    );

    @GetMapping(value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    @Operation(summary = "Find a person",
            description = "Find a person by ID",
            tags = {"People"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = PersonDTO.class))),
                    @ApiResponse(description = "No content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    PersonDTO findById(@PathVariable("id") Long id);

    @PostMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE},
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    @Operation(summary = "Create a person",
            description = "Create a person in the database",
            tags = {"People"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = PersonDTO.class))),
                    @ApiResponse(description = "No content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    PersonDTO create(@RequestBody PersonDTO person);


    @PostMapping(value = "/massCreation",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    @Operation(summary = "Massice people creation",
            description = "Massice people creation with upload of XLSX or CSV",
            tags = {"People"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = PersonDTO.class))
                            ),
                    @ApiResponse(description = "No content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    List<PersonDTO> massCreation(@RequestParam("file") MultipartFile file);
    
    @PutMapping(
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE},
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    @Operation(summary = "Update Person",
            description = "Update a person in the database",
            tags = {"People"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = PersonDTO.class))),
                    @ApiResponse(description = "No content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    PersonDTO update(@RequestBody PersonDTO person);

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Delete a person",
            description = "Delete a person by ID",
            tags = {"People"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = PersonDTO.class))),
                    @ApiResponse(description = "No content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    ResponseEntity<?> delete(@PathVariable("id") Long id);
    
    @PatchMapping(value = "/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE})
    @Operation(summary = "Disable a person",
            description = "Disable a person by ID",
            tags = {"People"},
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = PersonDTO.class))),
                    @ApiResponse(description = "No content", responseCode = "204", content = @Content),
                    @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
                    @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
            })
    PersonDTO disablePerson(@PathVariable("id") Long id);
}
