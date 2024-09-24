package com.all.homedesire.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.Resources;
import com.all.homedesire.entities.AppNotification;
import com.all.homedesire.resources.dto.DesireSearchRequest;
import com.all.homedesire.service.NotificationService;

/**
 * This controller contains APIs. Which are create for notification purpose.
 * 
 * @author Arun Kumar Tiwari
 * @version 1.0
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/notify")
public class NotificationController {
	Logger LOGGER = LoggerFactory.getLogger(NotificationController.class);
	
	@Autowired
	NotificationService notificationService;
	
	@PostMapping("/all")
	public MappingJacksonValue notifications(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("NotificationController >> notifications called for notification list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus status = notificationService.notifications(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "notifications" };
		return Resources.formatedResponse(status, properties);
	}
	
	@PostMapping("/all/user/{userId}")
	public MappingJacksonValue notifications(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String userId, @RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("NotificationController >> notifications called for notification list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus status = notificationService.notifications(authToken, Long.parseLong(userId), request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "notifications" };
		return Resources.formatedResponse(status, properties);
	}

	@GetMapping("/id/{notificationId}")
	public MappingJacksonValue notification(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String notificationId) {
		LOGGER.info("NotificationController >> notification called for notification details.");
		DesireStatus status = notificationService.viewNotification(authToken, Long.parseLong(notificationId));
		String[] properties = { "statusType", "text", "notification" };
		return Resources.formatedResponse(status, properties);
	}

	@PostMapping("/add")
	public MappingJacksonValue addNotification(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody AppNotification notification) {
		LOGGER.info("NotificationController >> addNotification called.");
		DesireStatus status = notificationService.addNotification(authToken, notification);
		String[] properties = { "statusType", "text", "notification" };
		return Resources.formatedResponse(status, properties);
	}

	@PutMapping("/edit")
	public MappingJacksonValue editNotification(@RequestHeader(value = "Authorization") String authToken, @RequestBody AppNotification notification) {
		LOGGER.info("NotificationController >> editNotification called.");
		DesireStatus status = notificationService.editNotification(authToken, notification);
		String[] properties = { "statusType", "text", "notification" };
		return Resources.formatedResponse(status, properties);
	}

	@DeleteMapping("/delete/{notificationId}")
	public MappingJacksonValue deleteNotification(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String notificationId) {
		LOGGER.info("NotificationController >> deleteNotification with notificationId >> " + notificationId + "called.");
		DesireStatus status = notificationService.deleteNotification(authToken, Long.parseLong(notificationId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(status, properties);
	}
	
	@PutMapping("/setRead")
	public MappingJacksonValue setRead(@RequestHeader(value = "Authorization") String authToken,
			String notificationId, String isRead) {
		LOGGER.info("NotificationController >> setRead called to update read status.");
		DesireStatus status = notificationService.setReadStatus(authToken, Long.parseLong(notificationId), Boolean.parseBoolean(isRead));
		String[] properties = { "statusType", "text", "notification" };
		return Resources.formatedResponse(status, properties);
	}
	
	@GetMapping("/getCount")
	public MappingJacksonValue getCount(@RequestHeader(value = "Authorization") String authToken) {
		LOGGER.info("NotificationController >> getCount called to get unread notification count.");
		DesireStatus status = notificationService.getCount(authToken);
		String[] properties = { "statusType", "text", "totalRecord" };
		return Resources.formatedResponse(status, properties);
	}
}
