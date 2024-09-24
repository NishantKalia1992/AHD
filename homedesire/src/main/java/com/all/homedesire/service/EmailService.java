package com.all.homedesire.service;

import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.entities.DesireEmail;
import com.all.homedesire.entities.User;

import jakarta.mail.MessagingException;

public interface EmailService {
	
	public DesireStatus ecomEmails(String authToken);

	public DesireStatus viewDesireEmail(String authToken, long metaId);

	public DesireStatus addDesireEmail(String authToken, DesireEmail email);

	public DesireStatus editDesireEmail(String authToken, DesireEmail email);

	public DesireStatus deleteDesireEmail(String authToken, long metaId);

	public DesireStatus sendSimpleMail(String authToken, DesireEmail email);

	public DesireStatus sendMailWithAttachment(String authToken, DesireEmail email);
	
	public boolean sendMailWithAttachment(DesireEmail email) throws MessagingException, Exception;
	
	public boolean saveEmail(String recipient, String messageBody, String subject, String attachment);
	
	public void sendStatusUpdateEmail(User user, String status, String password);
	
	public String generateStatusChangeEmailContent(User user, String status, String password);
	
	public String generateEmailSubject(String roleName);
}
