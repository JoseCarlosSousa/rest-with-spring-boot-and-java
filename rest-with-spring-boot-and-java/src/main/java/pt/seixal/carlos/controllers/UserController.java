package pt.seixal.carlos.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import pt.seixal.carlos.controllers.docs.UserControllerDocs;
import pt.seixal.carlos.data.dto.v1.UserDTO;
import pt.seixal.carlos.data.dto.v1.security.AccountCredentialsDTO;
import pt.seixal.carlos.services.UserService;

@RestController
@RequestMapping("/api/user/v1")
@Tag(name = "User", description = "Endpoints for user")
public class UserController implements UserControllerDocs {

	@Autowired
	UserService service;

	@Override
	public UserDTO createUser(@RequestBody AccountCredentialsDTO credentials) {
		return service.create(credentials);
	}

	@Override
	public ResponseEntity<PagedModel<EntityModel<UserDTO>>> findAll(
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "12") int size,
			@RequestParam(value = "direction", defaultValue = "asc") String direction) {
		var sort = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
		Pageable pageable = PageRequest.of(page, size, Sort.by(sort, "userName"));
		return ResponseEntity.ok(service.findAll(pageable));
	}

	@Override
	public UserDTO findById(Long id) {
		return null;
	}

	@Override
	public UserDTO update(UserDTO user) {
		return null;
	}

	@Override
	public ResponseEntity<?> deleteUser(Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

}
