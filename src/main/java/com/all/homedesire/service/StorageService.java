package com.all.homedesire.service;

import org.springframework.web.multipart.MultipartFile;

import com.all.homedesire.common.DesireStatus;

public interface StorageService {
	public DesireStatus storeProfilePicture(MultipartFile file, String fileName);
	public DesireStatus storeContactFile(MultipartFile file, String fileName);
	public DesireStatus storeUserDocument(MultipartFile file, String fileName, String subPath);
	public DesireStatus storeUserPayment(MultipartFile file, String fileName, String subPath);
	public DesireStatus loadAsResource(String fileName);
	public DesireStatus deleteFile(String fileName, String sysFileTyp);
}
