package pt.seixal.carlos.file.exporter.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import pt.seixal.carlos.data.dto.v1.BookDTO;
import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.data.dto.v1.UserDTO;
import pt.seixal.carlos.file.exporter.contract.FileExporter;
import pt.seixal.carlos.services.QRCodeService;

@Component
public class PdfExporter implements FileExporter {

	@Autowired
	private QRCodeService qrCodeService;

	@Override
	public Resource exportPeople(List<PersonDTO> people) throws Exception {

		Resource templateResource = new ClassPathResource("templates/people.jrxml");

		if (!templateResource.exists()) {
			throw new RuntimeException("Template file not found in resources: templates/people.jrxml");
		}

		try (InputStream inputStream = templateResource.getInputStream()) {
			JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(people);
			Map<String, Object> parameters = new HashMap<>();

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
				return new ByteArrayResource(outputStream.toByteArray());
			}
		}
	}

	@Override
	public Resource exportPerson(PersonDTO person) throws Exception {

		Resource mainTemplate = new ClassPathResource("templates/person.jrxml");
		Resource subTemplate = new ClassPathResource("templates/books.jrxml");

		if (!mainTemplate.exists() || !subTemplate.exists()) {
			throw new RuntimeException("Template file/s not found in resources: templates/person.jrxml or books.jrxml");
		}

		try (InputStream mainStream = mainTemplate.getInputStream();
				InputStream subStream = subTemplate.getInputStream()) {
			JasperReport mainReport = JasperCompileManager.compileReport(mainStream);
			JasperReport subReport = JasperCompileManager.compileReport(subStream);

			JRBeanCollectionDataSource mainSource = new JRBeanCollectionDataSource(Collections.singletonList(person));
			JRBeanCollectionDataSource subSource = new JRBeanCollectionDataSource(person.getBooks());
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("SUB_REPORT_DATA_SOURCE", subSource);

			parameters.put("BOOK_SUB_REPORT", subReport);

			InputStream qrCodeStream = qrCodeService.generateQRCode(person.getProfileUrl(), 200, 200);
			parameters.put("QR_CODE_IMAGE", qrCodeStream);
			parameters.put("PERSON_ID", person.getId());

			JasperPrint jasperPrint = JasperFillManager.fillReport(mainReport, parameters, mainSource);

			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
				return new ByteArrayResource(outputStream.toByteArray());
			}
		}
	}

	@Override
	public Resource exportBooks(List<BookDTO> books) throws Exception {
		// Lemos o template tabular que criámos exclusivamente para a listagem de livros
		Resource templateResource = new ClassPathResource("templates/books.jrxml");

		if (!templateResource.exists()) {
			throw new RuntimeException("Template file not found in resources: templates/books.jrxml");
		}

		try (InputStream inputStream = templateResource.getInputStream()) {
			JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(books);
			Map<String, Object> parameters = new HashMap<>();

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
				return new ByteArrayResource(outputStream.toByteArray());
			}
		}
	}

	@Override
	public Resource exportBook(BookDTO book) throws Exception {
		if (book == null) {
			return new ByteArrayResource(new byte[0]);
		}

		Resource templateResource = new ClassPathResource("templates/book.jrxml");

		if (!templateResource.exists()) {
			throw new RuntimeException("Template file not found in resources: templates/book.jrxml");
		}

		try (InputStream inputStream = templateResource.getInputStream()) {
			JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

			// Ficha individual: passa o livro numa lista de um único elemento
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList(book));
			Map<String, Object> parameters = new HashMap<>();

			// Gera o QR Code dinâmico do Livro se tiver configurado (opcional, como em
			// Person)
			InputStream qrCodeStream = qrCodeService.generateQRCode(getProfileUrl(), 200, 200); // ajuste o método
																								// se necessário
			parameters.put("QR_CODE_IMAGE", qrCodeStream);
			parameters.put("BOOK_ID", book.getId());

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
				return new ByteArrayResource(outputStream.toByteArray());
			}
		}
	}

	private String getProfileUrl() {
		return "https://en.wikipedia.org/wiki/Ayrton_Senna";
	}

	@Override
	public Resource exportUsers(List<UserDTO> users) throws Exception {
		Resource templateResource = new ClassPathResource("templates/users.jrxml");

		if (!templateResource.exists()) {
			throw new RuntimeException("Template file not found in resources: templates/users.jrxml");
		}

		try (InputStream inputStream = templateResource.getInputStream()) {
			JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(users);
			Map<String, Object> parameters = new HashMap<>();

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
				return new ByteArrayResource(outputStream.toByteArray());
			}
		}
	}

	@Override
	public Resource exportUser(UserDTO user) throws Exception {
		if (user == null) {
			return new ByteArrayResource(new byte[0]);
		}

		Resource templateResource = new ClassPathResource("templates/user.jrxml");

		if (!templateResource.exists()) {
			throw new RuntimeException("Template file not found in resources: templates/user.jrxml");
		}

		try (InputStream inputStream = templateResource.getInputStream()) {
			JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
			JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(Collections.singletonList(user));
			Map<String, Object> parameters = new HashMap<>();

			// Injeta o Código QR dinâmico do Utilizador (igual ao que fez em Person)
			InputStream qrCodeStream = qrCodeService.generateQRCode(getProfileUrl(), 200, 200);
			parameters.put("QR_CODE_IMAGE", qrCodeStream);
			parameters.put("USER_ID", user.getId());

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
				return new ByteArrayResource(outputStream.toByteArray());
			}
		}
	}

}
