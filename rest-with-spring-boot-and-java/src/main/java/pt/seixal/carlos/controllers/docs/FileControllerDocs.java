package pt.seixal.carlos.controllers.docs;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import pt.seixal.carlos.data.dto.v1.UploadFileResponseDTO;

@Tag(name = "File EndPoint", description = "Endpoints for file upload and download")
public interface FileControllerDocs {
	
	UploadFileResponseDTO uploadFile(MultipartFile file);
	List<UploadFileResponseDTO> uploadMultipleFiles(MultipartFile[] files);
	ResponseEntity<Resource> downloadFile(String fileName, HttpServletRequest request);
}
