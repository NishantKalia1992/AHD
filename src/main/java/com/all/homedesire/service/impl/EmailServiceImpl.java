package com.all.homedesire.service.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.all.homedesire.common.Constants;
import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.LogConstants;
import com.all.homedesire.common.Resources;
import com.all.homedesire.entities.DesireEmail;
import com.all.homedesire.entities.User;
import com.all.homedesire.repository.DesireEmailRepository;
import com.all.homedesire.security.service.UserTokenService;
import com.all.homedesire.service.EmailService;
import com.all.homedesire.service.EncryptionService;
import com.all.homedesire.service.LogService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {
	Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Autowired
	UserTokenService userTokenService;
	@Autowired
	LogService logService;

	@Autowired
	DesireEmailRepository desireEmailRepository;

	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private EncryptionService encryptionService;

	@Value("${spring.mail.username}")
	private String sender;
	
	@Value("${email.bcc.address}")
	private String bccEmail;


	@Override
	public DesireStatus ecomEmails(String authToken) {
		LOGGER.info("EmailService >> ecomEmails called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				List<DesireEmail> ecomEmails = desireEmailRepository.findAllByActiveAndDeleted(true, false);
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Email");
				status.setDesireEmails(ecomEmails);
				logService.createLog(authStatus.getUser(), LogConstants.DESIRED_EMAIL, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),"List email");
		}
		return status;
	}

	@Override
	public DesireStatus viewDesireEmail(String authToken, long metaId) {
		LOGGER.info("EmailService >> viewDesireEmail called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (metaId > 0) {
					Optional<DesireEmail> foundDesireEmail = desireEmailRepository.findByObjectId(metaId);
					if (foundDesireEmail != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Email");
						status.setDesireEmail(foundDesireEmail.get());
						logService.createLog(authStatus.getUser(), LogConstants.DESIRED_EMAIL, LogConstants.DETAIL, null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Email");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,"Email id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),"Detail email");
		}
		return status;
	}

	@Override
	public DesireStatus addDesireEmail(String authToken, DesireEmail email) {
		LOGGER.info("EmailService >> addDesireEmail called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("EmailService >> addDesireEmail object recieved >> " + email);
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				email.setCreatedOn(dtNow);
				email.setUpdatedOn(dtNow);
				email.setActive(true);
				email.setDeleted(false);
				DesireEmail savedDesireEmail = desireEmailRepository.save(email);
				if (savedDesireEmail != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Email");
					status.setDesireEmail(savedDesireEmail);
					logService.createLog(authStatus.getUser(), LogConstants.DESIRED_EMAIL, LogConstants.ADD, savedDesireEmail.getCreatedOn(), savedDesireEmail.getUpdatedOn());
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Email");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),"Add Email");
		}

		return status;
	}

	@Override
	public DesireStatus editDesireEmail(String authToken, DesireEmail email) {
		LOGGER.info("EmailService >> editDesireEmail called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (email.getId() != null && email.getId() > 0) {
					Optional<DesireEmail> optDesireEmail = desireEmailRepository.findByObjectId(email.getId());
					if (optDesireEmail.isPresent()) {
						DesireEmail foundDesireEmail = optDesireEmail.get();
						foundDesireEmail.setRecipient(email.getRecipient());
						foundDesireEmail.setMessageBody(email.getMessageBody());
						foundDesireEmail.setSubject(email.getSubject());
						foundDesireEmail.setAttachment(email.getAttachment());
						foundDesireEmail.setUpdatedOn(dtNow);
						DesireEmail savedDesireEmail = desireEmailRepository.save(foundDesireEmail);
						if (savedDesireEmail != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "Email");
							status.setDesireEmail(savedDesireEmail);
							logService.createLog(authStatus.getUser(), LogConstants.DESIRED_EMAIL, LogConstants.EDIT, null, savedDesireEmail.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "Email");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,"Email");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,"Email id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),"Edit Email");
		}
		return status;
	}

	@Override
	public DesireStatus deleteDesireEmail(String authToken, long emailId) {
		LOGGER.info("EmailService >> deleteDesireEmail called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (emailId > 0) {
					Optional<DesireEmail> optDesireEmail = desireEmailRepository.findByObjectId(emailId);
					if (optDesireEmail.isPresent()) {
						DesireEmail foundDesireEmail = optDesireEmail.get();
						desireEmailRepository.delete(foundDesireEmail);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "Email");
						logService.createLog(authStatus.getUser(), LogConstants.DESIRED_EMAIL, LogConstants.DELETE, null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "Email");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,"Email id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),"Delete Email");
		}
		return status;
	}
	
//	=======================================================================================
//	@Override
//	public DesireStatus sendSimpleMail(String authToken, DesireEmail email) {
//	    DesireStatus status = new DesireStatus();
//	    try {
//	        DesireStatus authStatus = userTokenService.getUserInfo(authToken);
//	        if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
//
//	            User user = authStatus.getUser();
//	            String encryptedUserId = encryptionService.encrypt(String.valueOf(user.getId()));
//	            String signUpLink = "http://localhost:8081/all/sign-up?userId=" + encryptedUserId;
//
//	            String modifiedMessageBody = email.getMessageBody() + "\n\nSign up using the following link:\n" + signUpLink;
//	            email.setMessageBody(modifiedMessageBody);
//
//	            // Creating a simple mail message
//	            SimpleMailMessage mailMessage = new SimpleMailMessage();
//	            mailMessage.setFrom(sender);
//	            mailMessage.setTo(email.getRecipient());
//	            mailMessage.setText(email.getMessageBody());
//	            mailMessage.setSubject(email.getSubject());
//	            mailMessage.setBcc(bccEmail);
//
//	            // Sending the mail
//	            mailSender.send(mailMessage);
//	            LOGGER.info("Email sent to: {}, BCC: {}, Subject: {}", email.getRecipient(), bccEmail, email.getSubject());
//	            status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.EMAIL_SUCCESS, "Email");
//	        } else {
//	            status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
//	        }
//	    } catch (Exception e) {
//	    	LOGGER.error("Error sending email: {}", e.getMessage());
//	        status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(), "Sent Email");
//	    }
//	    return status;
//	}

//	=======================================================================================

	@Override
	public DesireStatus sendSimpleMail(String authToken, DesireEmail email) {
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {

				// Creating a simple mail message
				SimpleMailMessage mailMessage = new SimpleMailMessage();

				// Setting up necessary details
				mailMessage.setFrom(sender);
				mailMessage.setTo(email.getRecipient());
				mailMessage.setText(email.getMessageBody());
				mailMessage.setSubject(email.getSubject());
				mailMessage.setBcc(bccEmail);

				// Sending the mail
				mailSender.send(mailMessage);
				LOGGER.info("Email sent to: {}, BCC: {}, Subject: {}", email.getRecipient(), bccEmail, email.getSubject());
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.EMAIL_SUCCESS, "Email");
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			LOGGER.error("Error sending email: {}", e.getMessage());
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),"Sent Email");
		}
		return status;
	}

	@Override
	public DesireStatus sendMailWithAttachment(String authToken, DesireEmail email) {
		DesireStatus status = new DesireStatus();

		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				boolean result = sendMailWithAttachment(email);
				if (result) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.EMAIL_SUCCESS, "Email");
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.EMAIL_FAILURE, "Email");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (MessagingException e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),"Sent Email");
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),"Sent Email");
		}
		return status;
	}

	@Override
	public boolean saveEmail(String recipient, String messageBody, String subject, String attachment) {
		boolean result = false;
		Date dtNow = new Date();
		try {
			DesireEmail email = new DesireEmail();
			email.setRecipient(recipient);
			email.setMessageBody(messageBody);
			email.setSubject(subject);
			email.setAttachment(attachment);
			email.setCreatedOn(dtNow);
			email.setUpdatedOn(dtNow);
			email.setActive(true);
			email.setDeleted(false);
			DesireEmail savedDesireEmail = desireEmailRepository.save(email);
			if (savedDesireEmail != null) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean sendMailWithAttachment(DesireEmail email) throws MessagingException, Exception {
		boolean result = false;
		// Creating a mime message
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper;
		// Creating a mime message
		mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
		mimeMessageHelper.setFrom(sender);
		mimeMessageHelper.setTo(email.getRecipient());
		mimeMessageHelper.setText(email.getMessageBody(), true);
		mimeMessageHelper.setSubject(email.getSubject());
		mimeMessageHelper.setBcc(bccEmail);
		if (email.getAttachment() != null && email.getAttachment().trim().length()>0) {
			// Adding the attachment
			FileSystemResource file = new FileSystemResource(new File(email.getAttachment()));

			mimeMessageHelper.addAttachment(file.getFilename(), file);
		}
		// Sending the mail
		mailSender.send(mimeMessage);
		LOGGER.info("Email with attachment sent to: {}, BCC: {}, Subject: {}", email.getRecipient(), bccEmail, email.getSubject());

		result = true;
		return result;
	}

	@Override
	public void sendStatusUpdateEmail(User user, String status, String password) {
	        String subject = generateEmailSubject(status);
	        String messageBody = generateStatusChangeEmailContent(user,status, password);

	        // Send email
	        boolean emailSent = saveEmail(user.getEmailId(), messageBody, subject, "");

	        if (!emailSent) {
	            LOGGER.error("Failed to send status update email to " + user.getEmailId());
	        }
	    }

	@Override
	public String generateStatusChangeEmailContent(User user, String status, String password) {
	    String roleName = user.getUserType().getName();
	    switch (status.toUpperCase()) {
	        case "PARTNER":
	        	 return String.format("<html><body>" +
	                     "<p>Hi %s,</p>" +
	                     "<p>Welcome to the All Home Desire partner family!</p>" +
	                     "<p>We’re excited to have you on board as we revolutionize the real estate industry " +
	                     "together. Your expertise combined with our platform will create endless opportunities for " +
	                     "both of us.</p>" +
	                     "<p>Let's work together to connect more buyers, sellers, and renters!</p>" +
	                     "<p>Best regards,<br>" +
	                     "The All Home Desire Team<br>" +
	                     "www.allhomedesire.com<br>" +
	                     "+91 9818961783</p>" +
	                     "</body></html>", user.getFirstName().toUpperCase());
	        case "CUSTOMER":
	        	return String.format("<html><body>" +
	                    "<p>Hi %s,</p>" +
	                    "<p>Congratulations on joining the All Home Desire family!</p>" +
	                    "<p>You're now part of a community that's all about finding the perfect property. Whether you're " +
	                    "looking to buy, sell, or rent, we're here to make your journey smooth and hassle-free.</p>" +
	                    "<p>Start exploring thousands of properties now!</p>" +
	                    "<p>You’re login credentials are :" +
	                    "<p><b>Username:</b> %s<br>" +
	                    "<b>Password:</b> %s</p>" +
	                    "<p>Best regards,<br>" +
	                    "The All Home Desire Team<br>" +
	                    "www.allhomedesire.com<br>" +
	                    "+91 9818961783</p>" +
	                    "</body></html>", user.getFirstName().toUpperCase(),user.getEmailId(),password);
	        case "APPROVE":
	        	 return String.format("<html><body>" +
	                     "<p>Dear %s,</p>" +
	                     "<p>Congratulations! Your partnership with All Home Desire has been approved.</p>" +
	                     "<p>We’re excited to start this journey together. You can now access our partner portal with the " +
	                     "following credentials:</p>" +
	                     "<p><b>Username:</b> %s<br>" +
	                     "<b>Password:</b> %s</p>" +
	                     "<p>Please keep these credentials confidential.</p>" +
	                     "<p>You can log in to the partner portal at [Link to Partner Portal] to manage your listings, track " +
	                     "performance, and access other partner tools.</p>" +
	                     "<p>If you have any questions or require assistance, please don't hesitate to contact our partner " +
	                     "support team at allhomedesire@gmail.com or +91 9818961783.</p>" +
	                     "<p>We look forward to a successful partnership!</p>" +
	                     "<p>Best regards,<br>" +
	                     "The All Home Desire Team<br>" +
	                     "www.allhomedesire.com<br>" +
	                     "+91 9818961783</p>" +
	                     "</body></html>", user.getFirstName().toUpperCase(),user.getEmailId(),password, status);

	        case "REJECT":
	        	 return String.format("<html><body>" +
	                     "<p>Dear %s,</p>" +
	                     "<p>Thank you for your interest in partnering with All Home Desire.</p>" +
	                     "<p>We’ve carefully reviewed your application, and unfortunately, we’re unable to approve your partnership at " +
	                     "this time.</p>" +
	                     "<p>[Optional: Briefly explain the reason for rejection, if applicable]</p>" +
	                     "<p>We appreciate you considering All Home Desire as a partner. We encourage you to reapply in the future " +
	                     "as our partnership criteria may change.</p>" +
	                     "<p>Best regards,<br>" +
	                     "The All Home Desire Team<br>" +
	                     "www.allhomedesire.com<br>" +
	                     "+91 9818961783</p>" +
	                     "</body></html>", user.getFirstName().toUpperCase(), status);
	        case "HOLD":
	        	return String.format("<html><body>" +
	                    "<p>Dear %s,</p>" +
	                    "<p>Thank you for your interest in partnering with All Home Desire.</p>" +
	                    "<p>We’ve received your partnership application and are currently reviewing it. We " +
	                    "appreciate your patience as we assess your application against our partnership criteria.</p>" +
	                    "<p>You will receive a notification once we have made a decision.</p>" +
	                    "<p>Thank you for your interest in All Home Desire.</p>" +
	                    "<p>Best regards,<br>" +
	                    "The All Home Desire Team<br>" +
	                    "www.allhomedesire.com<br>" +
	                    "+91 9818961783</p>" +
	                    "</body></html>", user.getFirstName().toUpperCase(), status);

	        case "DEACTIVATE":
	        	return String.format("<html><body>" +
	                    "<p>Dear %s,</p>" +
	                    "<p>We regret to inform you that your account with All Home Desire has been deactivated.</p>" +
	                    "<p>[Briefly explain the reason for deactivation, if applicable. For example, \"Due to inactivity...\" or " +
	                    "\"Due to a violation of our terms of service...\"]</p>" +
	                    "<p>If you believe this is an error, please contact our customer support at allhomedesire@gmail.com<br>" +
	                    "or +91 9818961783.</p>" +
	                    "<p>Best regards,<br>" +
	                    "The All Home Desire Team<br>" +
	                    "www.allhomedesire.com<br>" +
	                    "+91 9818961783</p>" +
	                    "</body></html>", user.getFirstName().toUpperCase(), status);
	        default:
	        	return String.format("<html><body>" +
	                    "<p>Dear %s,</p>" +
	                    "<p>Your account status has been updated to: %s.</p>" +
	                    "<p>Thank you.</p>" +
	                    "</body></html>", user.getFirstName().toUpperCase(), status);
	    }
	}
	@Override
	public String generateEmailSubject(String roleName) {
	    switch (roleName.toUpperCase()) {
	    	case "CUSTOMER":
	    		return "Welcome to All Home Desire! Your Property Journey Starts Here.";
	        case "PARTNER":
	            return "Partner with Us to Expand Your Real Estate Reach";
	        case "APPROVE":
	            return "Your Partnership with All Home Desire is Approved!";
	        case "REJECT":
	            return "Partnership Application - Regret to Inform";
	        case "HOLD":
	            return "Your Partnership with All Home Desire is on Hold!";
	        case "DEACTIVATE":
	            return "Your Account Has Been Deactivated";
	        default:
	            return "Welcome to All Home Desire!";
	    }
	}
	
}
