package pt.seixal.carlos.controllers.docs;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import pt.seixal.carlos.data.dto.v1.UploadFileResponseDTO;

@Tag(name = "File EndPoint", description = "Endpoints for file upload and download")
public interface FileControllerDocs {

	@PostMapping("/uploadFile")
	UploadFileResponseDTO uploadFile(@RequestParam("file") MultipartFile file);

	@PostMapping("/uploadMultipleFiles")
	List<UploadFileResponseDTO> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files);

	@GetMapping("/downloadFile/{fileName:.+}")
	ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request);
}
