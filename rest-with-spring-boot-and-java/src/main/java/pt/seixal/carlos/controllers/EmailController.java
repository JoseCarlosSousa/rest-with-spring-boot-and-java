package pt.seixal.carlos.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;
import pt.seixal.carlos.controllers.docs.EmailControllerDocs;
import pt.seixal.carlos.data.dto.v1.MessageResponseDTO;
import pt.seixal.carlos.data.dto.v1.request.EmailRequestDTO;
import pt.seixal.carlos.services.EmailService;

@RestController
@RequestMapping("/api/email/v1")
@Tag(name = "e-Mail", description = "Endpoints for Managing Email")
public class EmailController implements EmailControllerDocs {

	@Autowired
	private EmailService service;

	@Override
	public ResponseEntity<MessageResponseDTO> sendEmail(@RequestBody EmailRequestDTO emailRequestDTO) {
		service.sendSimpleEmail(emailRequestDTO);
		return ResponseEntity.ok(new MessageResponseDTO("e-mail sent with success!"));
	}

	@Override
	public ResponseEntity<MessageResponseDTO> sendEmailWithAttachments(
			@RequestParam("emailRequest") String emailRequest,
			@RequestParam("attachment") List<MultipartFile> attachments) {
		service.sendEmailWithAttachments(emailRequest, attachments);
		return ResponseEntity.ok(new MessageResponseDTO("e-mail with attachments sent with success!"));
	}

}
