package pt.seixal.carlos.controllers;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import pt.seixal.carlos.controllers.docs.FileControllerDocs;
import pt.seixal.carlos.data.dto.v1.UploadFileResponseDTO;
import pt.seixal.carlos.services.FileStoreService;

@RestController
@RequestMapping("/api/file/v1")
public class FileController implements FileControllerDocs {

	private static final Logger logger = LoggerFactory.getLogger(FileController.class);
	
	@Autowired
	private FileStoreService service;
	
	@PostMapping("/uploadFile")
	@Override
	public UploadFileResponseDTO uploadFile(@RequestParam("file") MultipartFile file) {
		var fileName = service.storeFile(file);
		var fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/api/file/v1/downloadFile/")
				.path(fileName)
				.toUriString();
		
		return new UploadFileResponseDTO(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}

	@PostMapping("/uploadMultipleFiles")
	@Override
	public List<UploadFileResponseDTO> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
		
		return Arrays.asList(files).stream().map(file -> uploadFile(file)).toList();
	}

	@GetMapping("/downloadFile/{fileName:.+}")
	@Override
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		Resource resource = service.loadFileAsResource(fileName);
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (Exception e) {
			logger.info("Could not determine file type.");
		}
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}



}
