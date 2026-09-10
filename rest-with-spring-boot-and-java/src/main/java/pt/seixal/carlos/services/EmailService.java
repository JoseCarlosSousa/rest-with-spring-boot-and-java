package pt.seixal.carlos.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import pt.seixal.carlos.config.EmailConfig;
import pt.seixal.carlos.data.dto.v1.request.EmailRequestDTO;
import pt.seixal.carlos.mail.EmailSender;

@Service
public class EmailService {

	@Autowired
	private EmailSender emailSender;

	@Autowired
	private EmailConfig emailConfigs;
	
	public void sendSimpleEmail(EmailRequestDTO request) {
		send(request, null);
	}
	
	public void sendEmailWithAttachments(String emailRequestJson, List<MultipartFile> attachments) {
	
	    List<File> tmpFiles = new ArrayList<>();
	    
	    try {
	    
	        EmailRequestDTO request = new ObjectMapper().readValue(emailRequestJson, EmailRequestDTO.class);
	        
	        if (attachments != null && !attachments.isEmpty()) {
	            for (MultipartFile attachment : attachments) {
	                if (attachment != null && !attachment.isEmpty()) {
	                    File tmpFile = File.createTempFile("attachment-", attachment.getOriginalFilename());
	                    attachment.transferTo(tmpFile);
	                    tmpFiles.add(tmpFile);
	                }
	            }
	        }
	        send(request, tmpFiles);
	        
	    } catch (JsonProcessingException e) {
	        throw new RuntimeException("Error parsing email request", e);
	    } catch (IOException e) {
	        throw new RuntimeException("Error parsing attachments", e);
	    } finally {
	        for (File tmpFile : tmpFiles) {
	            if (tmpFile != null && tmpFile.exists()) {
	                tmpFile.delete();
	            }
	        }
	    }
	}

	private void send(EmailRequestDTO request, List<File> attachments) {
	    var mailBuilder = emailSender
	        .to(request.getTo())
	        .withSubject(request.getSubject())
	        .withMessage(request.getBody());

	    if (attachments != null && !attachments.isEmpty()) {
	        for (File file : attachments) {
	            mailBuilder.attach(file.getAbsolutePath());
	        }
	    }

	    mailBuilder.send(emailConfigs);
	}


}
