package pt.seixal.carlos.file.exporter.impl;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@Component
public class PdfExporter implements FileExporter {

	@Override
	public Resource exportFile(List<PersonDTO> people) throws Exception {
	
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

}
