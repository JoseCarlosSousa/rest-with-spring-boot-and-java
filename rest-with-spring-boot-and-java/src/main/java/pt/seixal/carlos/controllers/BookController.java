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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import pt.seixal.carlos.controllers.docs.BookControllerDocs;
import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.file.exporter.MediaTypes;
import pt.seixal.carlos.services.BookService;

@RestController
@RequestMapping("/api/book/v1")
@Tag(name = "Book", description = "Endpoints for Managing Book")
public class BookController implements BookControllerDocs {

	@Autowired
	private BookService service;

	@Override
	public ResponseEntity<PagedModel<EntityModel<BookDTO>>> findAll(Map<String, String> allParams) {
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

		Resource file = service.exportBook(id, acceptHeader);
		return sendResponse(acceptHeader, file);
	}

	@Override
	public BookDTO findById(Long id) {
		return service.findById(id);
	}

	@Override
	public BookDTO create(BookDTO book) {
		return service.create(book);
	}

	@Override
	public BookDTO update(BookDTO book) {
		return service.update(book);
	}

	@Override
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
		var fileName = "book_exported_" + dateSuffix + fileExtension;

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
				.body(file);
	}

	private String getAcceptHeader(HttpServletRequest request) {
		return request.getHeader(HttpHeaders.ACCEPT);
	}
}
