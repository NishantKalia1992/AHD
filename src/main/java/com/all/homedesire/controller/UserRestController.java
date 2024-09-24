package com.all.homedesire.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.Resources;
import com.all.homedesire.entities.UserContact;
import com.all.homedesire.entities.UserNetwork;
import com.all.homedesire.resources.dto.DesireSearchRequest;
import com.all.homedesire.service.UserService;

@RestController
@RequestMapping("/user")
public class UserRestController {
	Logger LOGGER = LoggerFactory.getLogger(UserRestController.class);

	@Autowired
	UserService userService;

	@PostMapping("/contact/all")
	public MappingJacksonValue contacts(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("UserRestController >> contacts called for contact list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = userService.contacts(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "contacts" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("/contact/id/{contactId}")
	public MappingJacksonValue viewContact(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String contactId) {
		LOGGER.info("UserRestController >> viewContact called for contact details.");
		DesireStatus desireStatus = userService.viewContact(authToken, Long.parseLong(contactId));
		String[] properties = { "statusType", "text", "contact" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/contact/add")
	public MappingJacksonValue addContact(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody UserContact contact) {
		LOGGER.info("UserRestController >> addContact called.");
		DesireStatus desireStatus = userService.addContact(authToken, contact);
		String[] properties = { "statusType", "text", "contact" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/contact/edit")
	public MappingJacksonValue editContact(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody UserContact contact) {
		LOGGER.info("UserRestController >> editContact called.");
		DesireStatus desireStatus = userService.editContact(authToken, contact);
		String[] properties = { "statusType", "text", "contact" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("/contact/delete/{contactId}")
	public MappingJacksonValue deleteContact(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String contactId) {
		LOGGER.info("UserRestController >> deleteContact with contactId >> " + contactId + " called.");
		DesireStatus desireStatus = userService.deleteContact(authToken, Long.parseLong(contactId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/contact/upload")
	public MappingJacksonValue uploadContact(@RequestHeader(value = "Authorization") String authToken,
			@RequestParam("file") MultipartFile file) {
		LOGGER.info("ProductController >> uploadContact called.");
		DesireStatus desireStatus = userService.uploadContact(authToken, file);
		String[] properties = { "statusType", "text", "contacts" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/network/all")
	public MappingJacksonValue networks(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("UserRestController >> networks called for network list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = userService.networks(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "networks" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("/network/id/{networkId}")
	public MappingJacksonValue viewNetwork(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String networkId) {
		LOGGER.info("UserRestController >> viewNetwork called for network details.");
		DesireStatus desireStatus = userService.viewNetwork(authToken, Long.parseLong(networkId));
		String[] properties = { "statusType", "text", "network" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/network/add")
	public MappingJacksonValue addNetwork(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody UserNetwork network) {
		LOGGER.info("UserRestController >> addNetwork called.");
		DesireStatus desireStatus = userService.addNetwork(authToken, network);
		String[] properties = { "statusType", "text", "network" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/network/edit")
	public MappingJacksonValue editNetwork(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody UserNetwork network) {
		LOGGER.info("UserRestController >> editNetwork called.");
		DesireStatus desireStatus = userService.editNetwork(authToken, network);
		String[] properties = { "statusType", "text", "network" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("/network/delete/{networkId}")
	public MappingJacksonValue deleteNetwork(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String networkId) {
		LOGGER.info("UserRestController >> deleteNetwork with networkId >> " + networkId + " called.");
		DesireStatus desireStatus = userService.deleteNetwork(authToken, Long.parseLong(networkId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/network/upload")
	public MappingJacksonValue uploadNetwork(@RequestHeader(value = "Authorization") String authToken,
			@RequestParam("file") MultipartFile file) {
		LOGGER.info("ProductController >> uploadNetwork called.");
		DesireStatus desireStatus = userService.uploadNetwork(authToken, file);
		String[] properties = { "statusType", "text", "networks" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/follow")
	public MappingJacksonValue follow(@RequestHeader(value = "Authorization") String authToken,
			@RequestParam String followTo) {
		LOGGER.info("UserRestController >> addContact called.");
		DesireStatus desireStatus = userService.follow(authToken, Integer.parseInt(followTo));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/followings")
	public MappingJacksonValue followings(@RequestHeader(value = "Authorization") String authToken) {
		LOGGER.info("UserRestController >> addContact called.");
		DesireStatus desireStatus = userService.followings(authToken);
		String[] properties = { "statusType", "text", "users" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/followers")
	public MappingJacksonValue followers(@RequestHeader(value = "Authorization") String authToken) {
		LOGGER.info("UserRestController >> addContact called.");
		DesireStatus desireStatus = userService.followers(authToken);
		String[] properties = { "statusType", "text", "users" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/document/all")
	public MappingJacksonValue documents(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("UserRestController >> documents called for document list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = userService.documents(authToken, request);
		String[] properties = { "statusType", "text", "assetUrl", "totalPage", "totalRecord", "documents" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("/document/id/{documentId}")
	public MappingJacksonValue viewDocument(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String documentId) {
		LOGGER.info("UserRestController >> viewDocument called for document details.");
		DesireStatus desireStatus = userService.viewDocument(authToken, Long.parseLong(documentId));
		String[] properties = { "statusType", "text", "assetUrl", "document" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/document/add")
	public MappingJacksonValue addDocument(@RequestHeader(value = "Authorization") String authToken,
			@RequestParam(required = false, name = "file") MultipartFile file, @RequestParam("name") String name,
			@RequestParam("user_id") String userId) {
		LOGGER.info("UserRestController >> addDocument called.");
		DesireStatus desireStatus = (file != null)
				? userService.addDocument(authToken, Long.parseLong(userId), name, file)
				: userService.addDocument(authToken, Long.parseLong(userId), name);
		String[] properties = { "statusType", "text", "assetUrl", "document" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/document/edit")
	public MappingJacksonValue editDocument(@RequestHeader(value = "Authorization") String authToken,
			@RequestParam(required = false, name = "file") MultipartFile file, @RequestParam("name") String name,
			@RequestParam("document_id") String documentId) {
		LOGGER.info("UserRestController >> editDocument called.");
		DesireStatus desireStatus = (file != null)
				? userService.editDocument(authToken, Long.parseLong(documentId), name, file)
				: userService.editDocument(authToken, Long.parseLong(documentId), name);
		String[] properties = { "statusType", "text", "assetUrl", "document" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("/document/delete/{documentId}")
	public MappingJacksonValue deleteDocument(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String documentId) {
		LOGGER.info("UserRestController >> deleteDocument with documentId >> " + documentId + " called.");
		DesireStatus desireStatus = userService.deleteDocument(authToken, Long.parseLong(documentId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/payment/all")
	public MappingJacksonValue payments(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("UserRestController >> payments called for payment list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = userService.payments(authToken, request);
		String[] properties = { "statusType", "text", "assetUrl", "totalPage", "totalRecord", "payments" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("/payment/id/{paymentId}")
	public MappingJacksonValue viewPayment(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String paymentId) {
		LOGGER.info("UserRestController >> viewPayment called for payment details.");
		DesireStatus desireStatus = userService.viewPayment(authToken, Long.parseLong(paymentId));
		String[] properties = { "statusType", "text", "assetUrl", "payment" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/payment/add")
	public MappingJacksonValue addPayment(@RequestHeader(value = "Authorization") String authToken,
			@RequestParam(required = false, name = "file") MultipartFile file, @RequestParam("mode") String mode,
			@RequestParam("gateway") String gateway, @RequestParam("provider") String provider,
			@RequestParam("amount") String amount, @RequestParam("status") String paymentStatus,
			@RequestParam("user_id") String userId) {
		LOGGER.info("UserRestController >> addPayment called.");
		DesireStatus desireStatus = (file != null)
				? userService.addPayment(authToken, Long.parseLong(userId), mode, gateway, provider,
						Double.parseDouble(amount), paymentStatus, file)
				: userService.addPayment(authToken, Long.parseLong(userId), mode, gateway, provider,
						Double.parseDouble(amount), paymentStatus);
		String[] properties = { "statusType", "text", "assetUrl", "payment" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/payment/edit")
	public MappingJacksonValue editPayment(@RequestHeader(value = "Authorization") String authToken,
			@RequestParam(required = false, name = "file") MultipartFile file, @RequestParam("mode") String mode,
			@RequestParam("gateway") String gateway, @RequestParam("provider") String provider,
			@RequestParam("amount") String amount, @RequestParam("status") String paymentStatus,
			@RequestParam("payment_id") String paymentId) {
		LOGGER.info("UserRestController >> editPayment called.");
		DesireStatus desireStatus = (file != null)
				? userService.editPayment(authToken, Long.parseLong(paymentId), mode, gateway, provider,
						Double.parseDouble(amount), paymentStatus, file)
				: userService.editPayment(authToken, Long.parseLong(paymentId), mode, gateway, provider,
						Double.parseDouble(amount), paymentStatus);
		String[] properties = { "statusType", "text", "assetUrl", "payment" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("/payment/delete/{paymentId}")
	public MappingJacksonValue deletePayment(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String paymentId) {
		LOGGER.info("UserRestController >> deletePayment with paymentId >> " + paymentId + " called.");
		DesireStatus desireStatus = userService.deletePayment(authToken, Long.parseLong(paymentId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

}
