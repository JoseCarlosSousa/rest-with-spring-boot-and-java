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
import pt.seixal.carlos.data.dto.v1.PersonDTO;
import pt.seixal.carlos.file.exporter.contract.FileExporter;
import pt.seixal.carlos.services.QRCodeService;

@Component
public class PdfExporter implements FileExporter {

	@Autowired
	private QRCodeService service;
	
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
		
		try (InputStream mainStream = mainTemplate.getInputStream(); InputStream subStream = subTemplate.getInputStream()) {
			JasperReport mainReport = JasperCompileManager.compileReport(mainStream);
			JasperReport subReport = JasperCompileManager.compileReport(subStream);

			JRBeanCollectionDataSource mainSource = new JRBeanCollectionDataSource(Collections.singletonList(person));
			JRBeanCollectionDataSource subSource = new JRBeanCollectionDataSource(person.getBooks());
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("SUB_REPORT_DATA_SOURCE", subSource);
			String path = getClass().getResource("/templates/books.jasper").getPath();
			parameters.put("SUB_REPORT_DIR", path);
			parameters.put("BOOK_SUB_REPORT", subReport);
			
			InputStream qrCodeStream = service.generateQRCode(person.getProfileUrl(), 200, 200);
			parameters.put("QR_CODE_IMAGE", qrCodeStream);
			parameters.put("PERSON_ID", person.getId());
			
			JasperPrint jasperPrint = JasperFillManager.fillReport(mainReport, parameters, mainSource);
			
			try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
				JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
				return new ByteArrayResource(outputStream.toByteArray());
			}
		}
	}
}
