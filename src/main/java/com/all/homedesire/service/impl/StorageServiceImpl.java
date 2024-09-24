package com.all.homedesire.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.all.homedesire.common.Constants;
import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.Resources;
import com.all.homedesire.service.StorageService;

import jakarta.annotation.PostConstruct;

@Service
public class StorageServiceImpl implements StorageService {
	Logger LOGGER = LoggerFactory.getLogger(StorageService.class);

	@Value("${upload.work-dir}")
	private String uploadWorkdir;

	@Value("${image.base-dir}")
	private String imageBasedir;

	private Path uploadPath;
	private Path uploadProfilePicPath;
	private Path uploadContactFilePath;
	private Path uploadDocumentFilePath;
	private Path uploadPaymentFilePath;

	@PostConstruct
	private void init() {
		this.uploadPath = Paths.get(uploadWorkdir).resolve(Constants.WORK_IMAGE);
		this.uploadProfilePicPath = Paths.get(uploadWorkdir).resolve(Constants.PROFILE_PIC_IMAGE);
		this.uploadContactFilePath = Paths.get(uploadWorkdir).resolve(Constants.CONTACT_FILE);
		this.uploadDocumentFilePath = Paths.get(uploadWorkdir).resolve(Constants.DOCUMENT_FILE);
		this.uploadPaymentFilePath = Paths.get(uploadWorkdir).resolve(Constants.PAYMENT_FILE);

	}

	@Override
	public DesireStatus storeProfilePicture(MultipartFile file, String fileName) {
		LOGGER.info("StorageService >> store called!");
		DesireStatus status = new DesireStatus();
		try {
			if (file.isEmpty() || fileName.contains("..")) {
				throw new RuntimeException("Invalid file.");
			}
			try {
				Files.createDirectories(Paths.get(uploadProfilePicPath.toString()));
				Files.copy(file.getInputStream(), uploadProfilePicPath.resolve(fileName),
						StandardCopyOption.REPLACE_EXISTING);
				LOGGER.info("StorageService >> store >> file with name {} was stored", fileName);
			} catch (IOException e) {
				LOGGER.error("StorageService >> store >> file was not stored", e);
				status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
						"Upload profile picture file");
			}
			status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPLOAD_SUCCESS, "Profile picture file");
			status.setFileName(imageBasedir + "/" + Constants.PROFILE_PIC_IMAGE + "/" + fileName);

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Store profile picture file");
		}
		return status;
	}

	@Override
	public DesireStatus storeContactFile(MultipartFile file, String fileName) {
		LOGGER.info("StorageService >> store contact file called!");
		DesireStatus status = new DesireStatus();
		try {

			try {
				if (file.isEmpty() || fileName.contains("..")) {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPLOAD_FAILURE, "Contact file");
				} else if (!Constants.EXCEL_FILE_EXT.contains(file.getContentType())) {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.INVALID_CSV, "");
				} else {
					Files.createDirectories(Paths.get(uploadContactFilePath.toString()));
					Files.copy(file.getInputStream(), uploadContactFilePath.resolve(fileName),
							StandardCopyOption.REPLACE_EXISTING);
					LOGGER.info("StorageService >> store contact file >> file with name {} was stored", fileName);
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPLOAD_SUCCESS, "Contact file");
				}
			} catch (IOException e) {
				LOGGER.error("StorageService >> store contact file >> file was not stored", e);
				status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
						"Upload profile picture file");
			}
			status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPLOAD_SUCCESS, "Profile picture file");
			status.setFileName(imageBasedir + "/" + Constants.PROFILE_PIC_IMAGE + "/" + fileName);

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Store profile picture file");
		}
		return status;
	}

	@Override
	public DesireStatus storeUserDocument(MultipartFile file, String fileName, String subPath) {
		LOGGER.info("StorageService >> storeUserDocument called!");
		DesireStatus status = new DesireStatus();
		try {
			if (file.isEmpty() || fileName.contains("..")) {
				throw new RuntimeException("Invalid file.");
			}
			try {
				Path uploadProductImagePath1 = uploadDocumentFilePath.resolve(subPath);
				Files.createDirectories(Paths.get(uploadProductImagePath1.toString()));
				Files.copy(file.getInputStream(), uploadProductImagePath1.resolve(fileName),
						StandardCopyOption.REPLACE_EXISTING);
				LOGGER.info("StorageService >> storeProductImage >> file with name {} was stored", fileName);
			} catch (IOException e) {
				LOGGER.error("StorageService >> storeProductImage >> file was not stored", e);
				status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
						"Upload  document file");
			}
			status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPLOAD_SUCCESS, "Document file");
			status.setFileName(imageBasedir + "/" + Constants.DOCUMENT_FILE + "/" + subPath + "/" + fileName);

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Store document file");
		}
		return status;
	}
	
	@Override
	public DesireStatus storeUserPayment(MultipartFile file, String fileName, String subPath) {
		LOGGER.info("StorageService >> storeUserPayment called!");
		DesireStatus status = new DesireStatus();
		try {
			if (file.isEmpty() || fileName.contains("..")) {
				throw new RuntimeException("Invalid file.");
			}
			try {
				Path uploadProductImagePath1 = uploadPaymentFilePath.resolve(subPath);
				Files.createDirectories(Paths.get(uploadProductImagePath1.toString()));
				Files.copy(file.getInputStream(), uploadProductImagePath1.resolve(fileName),
						StandardCopyOption.REPLACE_EXISTING);
				LOGGER.info("StorageService >> storeUserPayment >> file with name {} was stored", fileName);
			} catch (IOException e) {
				LOGGER.error("StorageService >> storeUserPayment >> file was not stored", e);
				status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
						"Upload payment file");
			}
			status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPLOAD_SUCCESS, "Payment file");
			status.setFileName(imageBasedir + "/" + Constants.PAYMENT_FILE + "/" + subPath + "/" + fileName);

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Store payment file");
		}
		return status;
	}

	@Override
	public DesireStatus loadAsResource(String fileName) {
		LOGGER.info("StorageService >> loadAsResource called!");
		DesireStatus status = new DesireStatus();
		try {
			Path file = uploadPath.resolve(fileName);
			UrlResource resource = new UrlResource(file.toUri());
			status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPLOAD_SUCCESS, "Load as resource");
			status.setResource(resource);
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Load as resource");
		}
		return status;
	}

	@Override
	public DesireStatus deleteFile(String fileName, String sysFileTyp) {
		LOGGER.info("StorageService >> deleteFile called!");
		LOGGER.info("StorageService >> deleteFile >> fileName >> " + fileName);
		LOGGER.info("StorageService >> deleteFile >> sysFileTyp >> " + sysFileTyp);
		DesireStatus status = new DesireStatus();
		try {
			fileName = uploadWorkdir + fileName.replace(imageBasedir, "");
			LOGGER.info("StorageService >> deleteFile >> deleteFile >> " + fileName);
			Path file = null;
			if (sysFileTyp.equals(Constants.WORK_IMAGE)) {
				LOGGER.info("StorageService >> deleteFile >> IN WORK IMAGE ");
				file = uploadPath.resolve(fileName);
			} else if (sysFileTyp.equals(Constants.PROFILE_PIC_IMAGE)) {
				LOGGER.info("StorageService >> deleteFile >> IN PROFILE PIC IMAGE ");
				file = uploadProfilePicPath.resolve(fileName);
			} else if (sysFileTyp.equals(Constants.PAYMENT_FILE)) {
				LOGGER.info("StorageService >> deleteFile >> IN PAYMENT FILE ");
				file = uploadPaymentFilePath.resolve(fileName);
			} else if (sysFileTyp.equals(Constants.DOCUMENT_FILE)) {
				LOGGER.info("StorageService >> deleteFile >> IN DOCUMENT FILE ");
			} else {
				throw new Exception("Path is not metioned!");
			}
			LOGGER.info("StorageService >> deleteFile >> file >> " + file.getRoot());
			Files.delete(file);
			File parentDir = file.getParent().toFile();
			deleteFolder(parentDir);
			status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "File");
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete file");
		}
		return status;
	}
	
	private boolean deleteFolder(File parentDir) {
		boolean result = false;
		try {
			if (parentDir.isDirectory()) {
				LOGGER.info("StorageService >> deleteFile >> file >> folder >> parentDir.getPath() >> "
						+ parentDir.getPath());
				long fileCount = Files.list(Paths.get(parentDir.getPath())).filter(p -> p.toFile().isFile()).count();
				long folderCount = Files.list(Paths.get(parentDir.getPath())).filter(p -> p.toFile().isDirectory()).count();
				LOGGER.info("StorageService >> deleteFile >> file >> folder >> count >> " + fileCount);
				LOGGER.info("StorageService >> deleteFile >> file >> folder >> folderCount >> " + folderCount);
				if (fileCount <= 0 && folderCount <= 0) {
					Files.delete(parentDir.toPath());
					deleteFolder(new File(parentDir.getParent()));
				}
			}
		} catch (Exception e) {

		}
		return result;
	}

}
