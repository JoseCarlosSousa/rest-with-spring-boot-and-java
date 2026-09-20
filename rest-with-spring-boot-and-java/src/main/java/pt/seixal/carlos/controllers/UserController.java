package pt.seixal.carlos.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import pt.seixal.carlos.controllers.docs.UserControllerDocs;
import pt.seixal.carlos.data.dto.v1.UserDTO;
import pt.seixal.carlos.file.exporter.MediaTypes;
import pt.seixal.carlos.services.UserService;

@RestController
@RequestMapping("/api/user/v1")
@Tag(name = "User", description = "Endpoints for user")
public class UserController implements UserControllerDocs {

	@Autowired
	UserService service;

	@Override
	@PreAuthorize("hasAuthority('ADMIN')")
	public UserDTO create(@RequestBody UserDTO credentials) {
		return service.create(credentials);
	}

	@Override
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
	public ResponseEntity<PagedModel<EntityModel<UserDTO>>> findAll(Map<String, String> allParams) {
		return ResponseEntity.ok(service.findAll(allParams));
	}

	@Override
	public ResponseEntity<Resource> exportPage(Map<String, String> allParams,
			HttpServletRequest request) {

		String acceptHeader = getAcceptHeader(request);

		Resource file = service.exportPage(allParams, acceptHeader);

		return sendResponse(acceptHeader, file);
	}

	@Override
	public ResponseEntity<Resource> export(Long id, HttpServletRequest request) {

		String acceptHeader = getAcceptHeader(request);

		Resource file = service.exportUser(id, acceptHeader);
		return sendResponse(acceptHeader, file);
	}

	@Override
	@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
	public UserDTO findById(Long id) {
		return service.findById(id);
	}

	@Override
	@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('MANAGER')")
	public UserDTO update(UserDTO user) {
		return service.update(user);
	}

	@Override
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<?> delete(Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	private ResponseEntity<Resource> sendResponse(String acceptHeader, Resource file) {
		var contentType = acceptHeader != null ? acceptHeader : "application/octet-stream";

		Map<String, String> extensionMap = Map.of(
				MediaTypes.CSV,
				".csv",
				MediaTypes.XLSX,
				".xlsx",
				MediaTypes.PDF,
				".pdf");
		var fileExtension = extensionMap.getOrDefault(contentType, "");
		String dateSuffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		var fileName = "user_exported_" + dateSuffix + fileExtension;

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.body(file);
	}

	private String getAcceptHeader(HttpServletRequest request) {
		return request.getHeader(HttpHeaders.ACCEPT);
	}

}
