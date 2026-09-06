package pt.seixal.carlos.file.importer.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import pt.seixal.carlos.exceptions.BadRequestException;
import pt.seixal.carlos.file.importer.FileExtension;
import pt.seixal.carlos.file.importer.contract.FileImporter;
import pt.seixal.carlos.file.importer.impl.CsvImporter;
import pt.seixal.carlos.file.importer.impl.XlsxImporter;

@Component
public class FileImporterFactory {

	private final Logger logger = LoggerFactory.getLogger(FileImporterFactory.class.getName());
	
	@Autowired
	private ApplicationContext context;
	
	public FileImporter getImporter(String fileName) {
		
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

		switch (extension) {
		    case FileExtension.CSV:
		        return context.getBean(CsvImporter.class);
		    case FileExtension.XLSX:
		        return context.getBean(XlsxImporter.class);
		    default:
		        logger.error("Unsupported file type: {}", fileName);
		        throw new BadRequestException("Invalid File Format: " + extension + ". Supported formats are: " + FileExtension.CSV + ", " + FileExtension.XLSX);
		}
	}
	
}
