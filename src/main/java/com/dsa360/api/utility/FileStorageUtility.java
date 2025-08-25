package com.dsa360.api.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dsa360.api.constants.ErrorMessage;
import com.dsa360.api.exceptions.ResourceAlreadyExistsException;
import com.dsa360.api.exceptions.SomethingWentWrongException;

@Service
public class FileStorageUtility {
	Logger logger = LoggerFactory.getLogger(FileStorageUtility.class);
	private final Path kycRootLocation;
	private final Path customerRootLocation;
	
	public FileStorageUtility(@Value("${file.upload.kyc}") String kycUploadDir,@Value("${file.upload.customer}") String customerUploadDir) {
		this.kycRootLocation = Paths.get(kycUploadDir).toAbsolutePath().normalize();
		this.customerRootLocation = Paths.get(customerUploadDir).toAbsolutePath().normalize();
	}
	
	//added getter to get the path
	public Path getKycRootLocation() {
	    return this.kycRootLocation;
	}

	public List<Path> storeKYCFiles(String dsaApplicationId, MultipartFile... files) {
		Path targetDir = this.kycRootLocation.resolve(dsaApplicationId);
		List<Path> storedFilePaths = new ArrayList<>();

		try {
			// Delete the directory if it exists; re submit
			if (Files.exists(targetDir)) {
				try (Stream<Path> paths = Files.walk(targetDir).sorted(Comparator.reverseOrder())) {
					paths.map(Path::toFile).forEach(File::delete);
				} catch (IOException e) {
					logger.error(ErrorMessage.DELETE_FILE_ERROR.getValue());
					throw new SomethingWentWrongException(ErrorMessage.DELETE_FILE_ERROR.getValue());
				}
			}

			// Create a new directory
			Files.createDirectories(targetDir);

			// Save each file
			for (MultipartFile file : files) {
				String fileName = file.getOriginalFilename();
				if (fileName != null && fileName.contains("..")) {
					throw new SomethingWentWrongException("Invalid path sequence in file name: " + fileName);
				}

				var targetPath = targetDir.resolve(fileName).normalize();
				Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
				storedFilePaths.add(targetPath);
			}
		} catch (IOException e) {
			// Rollback if any error occurs
			revokeAllFiles(storedFilePaths);
			logger.error("Failed to store files. Transaction rolled back:  {}", e);
			throw new SomethingWentWrongException("Failed to store files. Transaction rolled back.", e);
		}
		return storedFilePaths;
	}
	
	public boolean storeCustomerFile(String customerId, MultipartFile file, String newFileName) {
	    Path targetDir = this.customerRootLocation.resolve(customerId);

	    try {
	        Files.createDirectories(targetDir);

	        if (newFileName != null && newFileName.contains("..")) {
	            throw new SomethingWentWrongException("Invalid path sequence in file name: " + newFileName);
	        }

	        // Delete any existing file containing the document type
	        int underscoreIndex = newFileName.lastIndexOf('_');
	        int dotIndex = newFileName.lastIndexOf('.');
	        if (underscoreIndex != -1 && dotIndex != -1 && underscoreIndex < dotIndex) {
	            String documentType = newFileName.substring(underscoreIndex + 1, dotIndex);
	            File folder = targetDir.toFile();
	            File[] matchingFiles = folder.listFiles((dir, name) -> name.contains(documentType));
	            if (matchingFiles != null) {
	                for (File f : matchingFiles) {
	                    f.delete();
	                }
	            }
	        }

	        Path targetPath = targetDir.resolve(newFileName).normalize();
	        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
	        return true;

	    } catch (IOException e) {
	        logger.error("Failed to store customer file for {}: {}", customerId, e.getMessage());
	        throw new SomethingWentWrongException("Failed to store customer file.", e);
	    }
	}



	

	public static void revokeAllFiles(List<Path> files) {
		
		System.out.println("in revoke ");
		for (Path path : files) {
			try {
				Files.deleteIfExists(path);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
