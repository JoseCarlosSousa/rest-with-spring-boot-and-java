package pt.seixal.carlos.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pt.seixal.carlos.controllers.docs.EmailControllerDocs;
import pt.seixal.carlos.data.dto.v1.request.EmailRequestDTO;
import pt.seixal.carlos.services.EmailService;

@RestController
@RequestMapping("/api/email/v1")
public class EmailController implements EmailControllerDocs{

	@Autowired
	private EmailService service;
	
	@PostMapping
	@Override
	public ResponseEntity<String> sendEmail(@RequestBody EmailRequestDTO emailRequestDTO) {
		service.sendSimpleEmail(emailRequestDTO);
		return new ResponseEntity<>("e-mail sent with success!", HttpStatus.OK);
	}

	@PostMapping(value = "/withAttachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Override
	public ResponseEntity<String> sendEmailWithAttachments(@RequestParam("emailRequest") String emailRequest, @RequestParam("attachment") List<MultipartFile> attachments) {
		service.sendEmailWithAttachments(emailRequest, attachments);
		return new ResponseEntity<>("e-mail with attachments sent with success!", HttpStatus.OK);
	}

}
