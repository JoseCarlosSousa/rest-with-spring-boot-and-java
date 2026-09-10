package pt.seixal.carlos.mail;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import pt.seixal.carlos.config.EmailConfig;

@Component
public class EmailSender implements Serializable{

	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(EmailSender.class);
	
	private final JavaMailSender mailSender;
	private String to;
	private String subject;
	private String body;
	private ArrayList<InternetAddress> recipients = new ArrayList<>();
	private List<File> attachments = new ArrayList<>();
	
	public EmailSender(JavaMailSender mailSender) {
		super();
		this.mailSender = mailSender;
	}

	public EmailSender to(String to) {
		this.to = to;
		this.recipients = getRecipients(to);
		return this;
	}
	
	public EmailSender withSubject(String subject) {
		this.subject = subject;
		return this;
	}
	
	public EmailSender withMessage(String body) {
		this.body = body;
		return this;
	}

	public EmailSender attach(String fileDir) {
		if (fileDir != null && !fileDir.trim().isEmpty()) {
			this.attachments.add(new File(fileDir));
		}
		return this;
	}
	
	public void send(EmailConfig config) {
		MimeMessage message = mailSender.createMimeMessage();
		
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(config.getUsername());
			helper.setTo(recipients.toArray(new InternetAddress[0]));
			helper.setSubject(subject);
			helper.setText(body, true);
			
			if (attachments != null && !attachments.isEmpty()) {
				for (File file : attachments) {
					helper.addAttachment(file.getName(), file);
				}
			}
			mailSender.send(message);
			logger.info("Email sent to {} with the subject '{}'", to, subject);
			
			reset();
		} catch (MessagingException e) {
			logger.error("Error sending message: {}", e.getMessage());
			throw new RuntimeException("Error send Message", e);
		}
	}

	private void reset() {
		to = null;
		subject = null;
		body = null;
		recipients = new ArrayList<>();
		attachments = new ArrayList<>();
	}

	private ArrayList<InternetAddress> getRecipients(String to2) {
		String toWithoutSpaces = to.replace("\\s", "");
		StringTokenizer tok = new StringTokenizer(toWithoutSpaces, ";");
		ArrayList<InternetAddress> recipientsList = new ArrayList<>();
		while(tok.hasMoreElements()) {
			try {
				recipientsList.add(new InternetAddress(tok.nextElement().toString()));
			} catch (AddressException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		return recipientsList;
	}
	
	
}
