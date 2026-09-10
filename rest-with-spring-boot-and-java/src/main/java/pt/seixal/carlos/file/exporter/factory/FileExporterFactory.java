package pt.seixal.carlos.file.exporter.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import pt.seixal.carlos.exceptions.BadRequestException;
import pt.seixal.carlos.file.exporter.MediaTypes;
import pt.seixal.carlos.file.exporter.contract.FileExporter;
import pt.seixal.carlos.file.exporter.impl.CsvExporter;
import pt.seixal.carlos.file.exporter.impl.PdfExporter;
import pt.seixal.carlos.file.exporter.impl.XlsxExporter;

@Component
public class FileExporterFactory {

	private final Logger logger = LoggerFactory.getLogger(FileExporterFactory.class);
	
	@Autowired
	private ApplicationContext context;
	
	public FileExporter getExporter(String acceptHeader) {
		switch (acceptHeader) {
		case MediaTypes.CSV:
			return context.getBean(CsvExporter.class);
		case MediaTypes.XLSX:
			return context.getBean(XlsxExporter.class);
		case MediaTypes.PDF:
			return context.getBean(PdfExporter.class);
		default:
			logger.error("Unsupported accept header: {}", acceptHeader);
			throw new BadRequestException("Invalid Accept Header: " + acceptHeader + ". Supported formats are: "+ MediaTypes.CSV + ", " + MediaTypes.XLSX);
		}
	}
}
