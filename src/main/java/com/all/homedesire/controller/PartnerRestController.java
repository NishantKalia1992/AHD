package com.all.homedesire.controller;

import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.Resources;
import com.all.homedesire.entities.Partner;
import com.all.homedesire.resources.dto.DesireSearchRequest;
import com.all.homedesire.service.PartnerService;

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
@RequestMapping("/partner")
public class PartnerRestController {
	Logger LOGGER = LoggerFactory.getLogger(PartnerRestController.class);
	@Autowired
	PartnerService partnerService;
	
	@GetMapping("partnerDetail/{partnerId}")
	public MappingJacksonValue partnerList(@RequestHeader(value="Authorization") String authToken, @PathVariable String partnerId) {
		LOGGER.info("PartnerRestController >> partnerList with partnerId >> " + partnerId + "called.");
		DesireStatus desireStatus = partnerService.viewPartner(authToken, Long.parseLong(partnerId));
		String[] properties = { "statusType", "text", "partner" };
		return Resources.formatedResponse(desireStatus, properties);
	}
	
	@PostMapping("list")
	public MappingJacksonValue partnerList(@RequestHeader(value="Authorization") String authToken, @RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("PartnerRestController >> propertyList >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = partnerService.partners(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "partners" };
		return Resources.formatedResponse(desireStatus, properties);
	}
	
	@PostMapping("/addPartner")
	public MappingJacksonValue addPartner(@RequestHeader(value="Authorization") String authToken, @RequestBody Partner partner) {
		LOGGER.info("PartnerRestController >> addPartner >> called.");
		DesireStatus desireStatus = partnerService.addPartner(authToken, partner);
		String[] properties = { "statusType", "text", "partner" };
		return Resources.formatedResponse(desireStatus, properties);
	}
	
	@PutMapping("/editPartner")
	public MappingJacksonValue editPartner(@RequestHeader(value="Authorization") String authToken, @RequestBody Partner partner) {
		LOGGER.info("PartnerRestController >> editPartner >> called.");
		DesireStatus desireStatus = partnerService.editPartner(authToken, partner);
		String[] properties = { "statusType", "text", "partner" };
		return Resources.formatedResponse(desireStatus, properties);
	}
	
	@DeleteMapping("deletePartner/{partnerId}")
	public MappingJacksonValue deletePartner(@RequestHeader(value="Authorization") String authToken, @PathVariable String partnerId) {
		LOGGER.info("PartnerRestController >> deletePartner with partnerId >> " + partnerId + "called.");
		DesireStatus desireStatus = partnerService.deletePartner(authToken, Long.parseLong(partnerId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

}
