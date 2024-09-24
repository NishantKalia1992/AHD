package com.all.homedesire.controller;

import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.Resources;
import com.all.homedesire.entities.Customer;
import com.all.homedesire.resources.dto.DesireSearchRequest;
import com.all.homedesire.service.CustomerService;

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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
public class CustomerRestController {
	Logger LOGGER = LoggerFactory.getLogger(CustomerRestController.class);
	@Autowired
	CustomerService customerService;
	
	@GetMapping("customerDetail/{customerId}")
	public MappingJacksonValue customerList(@RequestHeader(value="Authorization") String authToken, @PathVariable String customerId) {
		LOGGER.info("CustomerRestController >> customerList with customerId >> " + customerId + "called.");
		DesireStatus desireStatus = customerService.viewCustomer(authToken, Long.parseLong(customerId));
		String[] properties = { "statusType", "text", "customer" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("list")
	public MappingJacksonValue customerList(@RequestHeader(value="Authorization") String authToken, @RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("CustomerRestController >> customerList >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = customerService.customers(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "customers" };
		return Resources.formatedResponse(desireStatus, properties);
	}
	
	
	@PostMapping("/addCustomer")
	public MappingJacksonValue addCustomer(@RequestHeader(value="Authorization") String authToken, @RequestBody Customer customer) {
		LOGGER.info("CustomerRestController >> addCustomer >> called.");
		DesireStatus desireStatus = customerService.addCustomer(authToken, customer);
		String[] properties = { "statusType", "text", "customer" };
		return Resources.formatedResponse(desireStatus, properties);
	}
	
	@PutMapping("/editCustomer")
	public MappingJacksonValue editCustomer(@RequestHeader(value="Authorization") String authToken, @RequestBody Customer customer) {
		LOGGER.info("CustomerRestController >> editCustomer >> called.");
		DesireStatus desireStatus = customerService.editCustomer(authToken, customer);
		String[] properties = { "statusType", "text", "customer" };
		return Resources.formatedResponse(desireStatus, properties);
	}
	
	@DeleteMapping("deleteCustomer/{customerId}")
	public MappingJacksonValue deleteCustomer(@RequestHeader(value="Authorization") String authToken, @PathVariable String customerId) {
		LOGGER.info("CustomerRestController >> deleteCustomer with customerId >> " + customerId + "called.");
		DesireStatus desireStatus = customerService.deleteCustomer(authToken, Long.parseLong(customerId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}
}
