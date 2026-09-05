package pt.seixal.carlos.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import pt.seixal.carlos.config.FileStorageConfig;
import pt.seixal.carlos.exceptions.FileStorageException;

@Service
public class FileStoreService {


	private static final Logger logger = LoggerFactory.getLogger(FileStoreService.class);
	
	private final Path fileStoreLocation;
	
	@Autowired
	public FileStoreService(FileStorageConfig fileStoreConfig) {
		Path path = Paths.get(fileStoreConfig.getUploadDir()).toAbsolutePath().normalize();
		
		fileStoreLocation = path;
		
		try {
			logger.info("Creating directory for file storage at: " + fileStoreLocation.toString());
			Files.createDirectories(fileStoreLocation);
		} catch (IOException e) {
			logger.error("Could not create the directory where the uploaded files will be stored.", e);
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", e);
		}
	}
	
	public String storeFile(MultipartFile file) {
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		try {
			if (fileName.contains("..")) {
				logger.error("Sorry! Filename contains invalid path sequence " + fileName);
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}
			logger.info("Storing file " + fileName + " at location: " + fileStoreLocation.toString());
			
			Path targetLocation = this.fileStoreLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			return fileName;
		} catch (IOException e) {
			logger.error("Could not store file " + fileName + ". Please try again!", e);
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", e);
		}
	}
	
	public Resource loadFileAsResource(String fileName) {
		try {
			Path filePath = this.fileStoreLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				logger.error("File not found " + fileName);
				throw new FileStorageException("File not found " + fileName);
			}
		} catch (MalformedURLException e) {
			logger.error("File not found " + fileName, e);
			throw new FileStorageException("File not found " + fileName, e);
		}
	}
	
}
