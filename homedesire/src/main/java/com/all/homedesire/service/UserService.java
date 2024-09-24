package com.all.homedesire.service;

import org.springframework.web.multipart.MultipartFile;

import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.entities.UserContact;
import com.all.homedesire.entities.UserNetwork;
import com.all.homedesire.resources.dto.DesireSearchRequest;

public interface UserService {

	public DesireStatus contacts(String authToken, DesireSearchRequest request);
	public DesireStatus viewContact(String authToken, long contactId);
	public DesireStatus addContact(String authToken, UserContact contact);
	public DesireStatus editContact(String authToken, UserContact contact);
	public DesireStatus deleteContact(String authToken, long contactId);
	public DesireStatus uploadContact(String authToken, MultipartFile file);
	
	public DesireStatus networks(String authToken, DesireSearchRequest request);
	public DesireStatus viewNetwork(String authToken, long networkId);
	public DesireStatus addNetwork(String authToken, UserNetwork network);
	public DesireStatus editNetwork(String authToken, UserNetwork network);
	public DesireStatus deleteNetwork(String authToken, long networkId);
	public DesireStatus uploadNetwork(String authToken, MultipartFile file);
	
	public DesireStatus follow(String authToken, long userId);
	public DesireStatus followings(String authToken);
	public DesireStatus followers(String authToken);
	
	public DesireStatus documents(String authToken, DesireSearchRequest request);
	public DesireStatus viewDocument(String authToken, long documentId);
	public DesireStatus addDocument(String authToken, long userId, String name, MultipartFile file);
	public DesireStatus addDocument(String authToken, long userId, String name);
	public DesireStatus addDocument(long userId, String name, MultipartFile file);
	public DesireStatus addDocument(long userId, String name);
	public DesireStatus editDocument(String authToken, long documentId, String name, MultipartFile file);
	public DesireStatus editDocument(String authToken, long documentId, String name);
	public DesireStatus deleteDocument(String authToken, long documentId);
	
	public DesireStatus payments(String authToken, DesireSearchRequest request);
	public DesireStatus viewPayment(String authToken, long paymentId);
	public DesireStatus addPayment(String authToken, long userId, String mode, String gateway, String provider, double amount, String paymentStatus, MultipartFile file);
	public DesireStatus addPayment(String authToken, long userId, String mode, String gateway, String provider, double amount, String paymentStatus);
	public DesireStatus addPayment(long userId, String mode, String gateway, String provider, double amount, String paymentStatus, MultipartFile file);
	public DesireStatus addPayment(long userId, String mode, String gateway, String provider, double amount, String paymentStatus);
	public DesireStatus editPayment(String authToken, long paymentId, String mode, String gateway, String provider, double amount, String paymentStatus, MultipartFile file);
	public DesireStatus editPayment(String authToken, long paymentId, String mode, String gateway, String provider, double amount, String paymentStatus);
	public DesireStatus deletePayment(String authToken, long paymentId);
}
