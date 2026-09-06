package pt.seixal.carlos.file.importer.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import pt.seixal.carlos.exceptions.BadRequestException;
import pt.seixal.carlos.file.importer.contract.FileImporter;
import pt.seixal.carlos.file.importer.impl.CsvImporter;
import pt.seixal.carlos.file.importer.impl.XlsxImporter;

@Component
public class FileImporterFactory {

	private final Logger logger = LoggerFactory.getLogger(FileImporterFactory.class.getName());
	
	@Autowired
	private ApplicationContext context;
	
	public FileImporter getImporter(String fileName) {
		if (fileName.endsWith(".csv")) {
			return context.getBean(CsvImporter.class);
		} else if (fileName.endsWith(".xlsx")) {
			return context.getBean(XlsxImporter.class);
		} else {
			logger.error("Unsupported file type: {}", fileName);
			throw new BadRequestException("Invalid File Format: " + fileName.substring(fileName.lastIndexOf(".") + 1) + ". Supported formats are: .csv, .xlsx");
		}
	}
	
}
