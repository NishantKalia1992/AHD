package com.all.homedesire.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.all.homedesire.common.Constants;
import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.Resources;
import com.all.homedesire.entities.DesireEmail;
import com.all.homedesire.entities.User;
import com.all.homedesire.service.AdminService;
import com.all.homedesire.service.EmailService;
import com.all.homedesire.service.EncryptionService;

@RestController
@RequestMapping("/email")

public class EmailRestController {
	Logger LOGGER = LoggerFactory.getLogger(EmailRestController.class);
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	EncryptionService encryptionService;
	
	@Autowired
	AdminService adminService;
	
	@PostMapping("/send-email")
	public MappingJacksonValue sendSimpleMail(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody DesireEmail email) {
		LOGGER.info("Send Mail Controller >> Send simple mail >> " + email + "called.");
		DesireStatus desireStatus = emailService.sendSimpleMail(authToken, email);
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}
	
	 @PostMapping("/send")
	    public MappingJacksonValue sendMailWithAttachment(
	            @RequestHeader(value = "Authorization") String authToken,
	            @RequestBody DesireEmail email) {

	        LOGGER.info("Send Mail Controller >> Send mail with attachment >> " + email + " called.");

	        DesireStatus desireStatus = emailService.sendMailWithAttachment(authToken, email);

	        String[] properties = { "statusType", "text", "" };

	        return Resources.formatedResponse(desireStatus, properties);
	 }	
	
}
