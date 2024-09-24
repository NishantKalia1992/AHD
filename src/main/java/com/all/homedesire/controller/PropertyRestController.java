package com.all.homedesire.controller;

import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.PropertyInfo;
import com.all.homedesire.common.Resources;
import com.all.homedesire.entities.Comment;
import com.all.homedesire.entities.Favorite;
import com.all.homedesire.entities.Lead;
import com.all.homedesire.entities.PropertyAttachment;
import com.all.homedesire.entities.PropertyFeature;
import com.all.homedesire.entities.PropertyImage;
import com.all.homedesire.entities.PropertyPurpose;
import com.all.homedesire.entities.PropertyStatus;
import com.all.homedesire.entities.PropertyStructure;
import com.all.homedesire.entities.PropertySubType;
import com.all.homedesire.entities.PropertyType;
import com.all.homedesire.entities.PropertyVideo;
import com.all.homedesire.resources.dto.ChangeStateRequest;
import com.all.homedesire.resources.dto.DesireSearchRequest;
import com.all.homedesire.resources.dto.LeadRequest;
import com.all.homedesire.resources.dto.property.AllRequest;
import com.all.homedesire.resources.dto.property.MySpaceRequest;
import com.all.homedesire.resources.dto.property.PreferredRequest;
import com.all.homedesire.service.PropertyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
@RequestMapping("/property")
public class PropertyRestController {
	Logger LOGGER = LoggerFactory.getLogger(PropertyRestController.class);

	@Autowired
	PropertyService propertyService;

	@GetMapping("/lead/{requestId}")
	public MappingJacksonValue leadById(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String requestId) {
		LOGGER.info("AdminRestController >> leadById with requestId >> " + requestId + "called.");
		DesireStatus desireStatus = propertyService.leadById(authToken, Long.parseLong(requestId));
		String[] properties = { "statusType", "text", "lead" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/leads")
	public MappingJacksonValue leads(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("AdminRestController >> leads called for request list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.leads(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "leads" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/lead/add")
	public MappingJacksonValue addLead(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody Lead lead) {
		LOGGER.info("AdminRestController >> addLead called.");
		DesireStatus desireStatus = propertyService.addLead(authToken, lead);
		String[] properties = { "statusType", "text", "lead" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/lead/edit")
	public MappingJacksonValue editLead(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody Lead lead) {
		LOGGER.info("AdminRestController >> editLead called.");
		DesireStatus desireStatus = propertyService.editLead(authToken, lead);
		String[] properties = { "statusType", "text", "lead" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("/lead/delete/{requestId}")
	public MappingJacksonValue deleteLead(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String requestId) {
		LOGGER.info("AdminRestController >> deleteLead with requestId >> " + requestId + " called.");
		DesireStatus desireStatus = propertyService.deleteLead(authToken, Long.parseLong(requestId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/lead/state")
	public MappingJacksonValue changeLeadState(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ChangeStateRequest request) {
		LOGGER.info("AdminRestController >> changeLeadState called.");
		DesireStatus desireStatus = propertyService.changeLeadState(authToken, request);
		String[] properties = { "statusType", "text", "lead" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("requestComment/{commentId}")
	public MappingJacksonValue requestCommentById(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String commentId) {
		LOGGER.info("AdminRestController >> requestCommentById with commentId >> " + commentId + "called.");
		DesireStatus desireStatus = propertyService.requestCommentById(authToken, Long.parseLong(commentId));
		String[] properties = { "statusType", "text", "requestComment" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("requestComments/{userId}/{requestId}")
	public MappingJacksonValue requestCommentsByUserAndRequest(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String userId, @PathVariable String requestId,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("AdminRestController >> requestComments called for comment list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.requestCommentsByUserAndRequest(authToken, Long.parseLong(userId),
				Long.parseLong(requestId), request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "requestComments" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addRequestComment")
	public MappingJacksonValue addRequestComment(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody Comment comment) {
		LOGGER.info("AdminRestController >> addRequestComment called.");
		DesireStatus desireStatus = propertyService.addRequestComment(authToken, comment);
		String[] properties = { "statusType", "text", "requestComment" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editRequestComment")
	public MappingJacksonValue editRequestComment(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody Comment comment) {
		LOGGER.info("AdminRestController >> editLead called.");
		DesireStatus desireStatus = propertyService.editRequestComment(authToken, comment);
		String[] properties = { "statusType", "text", "requestComment" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteRequestComment/{commentId}")
	public MappingJacksonValue deleteRequestComment(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String commentId) {
		LOGGER.info("AdminRestController >> deleteRequestComment with commentId >> " + commentId + " called.");
		DesireStatus desireStatus = propertyService.deleteRequestComment(authToken, Long.parseLong(commentId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("propertyDetail/{propertyId}")
	public MappingJacksonValue propertyList(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String propertyId) {
		LOGGER.info("PropertyRestController >> propertyList with propertyId >> " + propertyId + "called.");
		DesireStatus desireStatus = propertyService.viewProperty(authToken, Long.parseLong(propertyId));
		String[] properties = { "statusType", "text", "assetUrl", "propertyInfo" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("list")
	public MappingJacksonValue propertyList(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("PropertyRestController >> propertyList >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.properties(authToken, request);
		String[] properties = { "statusType", "text", "assetUrl", "totalPage", "totalRecord", "properties" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("listByUser/{userId}")
	public MappingJacksonValue propertyListByUser(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String userId, @RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("PropertyRestController >> propertyList >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.propertiesByUser(authToken, Long.parseLong(userId), request);
		String[] properties = { "statusType", "text", "assetUrl", "totalPage", "totalRecord", "properties" };
		return Resources.formatedResponse(desireStatus, properties);
	}
	
	@PostMapping("list/my/space")
	public MappingJacksonValue listMySpace(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody MySpaceRequest request) {
		LOGGER.info("PropertyRestController >> listMySpace >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.listMySpace(authToken, request);
		String[] properties = { "statusType", "text", "assetUrl", "totalPage", "totalRecord", "properties" };
		return Resources.formatedResponse(desireStatus, properties);
	}
	
	@PostMapping("list/all")
	public MappingJacksonValue listAll(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody AllRequest request) {
		LOGGER.info("PropertyRestController >> listAll >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.listAll(authToken, request);
		String[] properties = { "statusType", "text", "assetUrl", "totalPage", "totalRecord", "properties" };
		return Resources.formatedResponse(desireStatus, properties);
	}
	
	@PostMapping("list/preferred")
	public MappingJacksonValue listPreferred(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PreferredRequest request) {
		LOGGER.info("PropertyRestController >> listPreferred >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.listPreferred(authToken, request);
		String[] properties = { "statusType", "text", "assetUrl", "totalPage", "totalRecord", "properties" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addProperty")
	public MappingJacksonValue addProperty(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyInfo propertyInfo) {
		LOGGER.info("PropertyRestController >> addProperty >> called.");
		ObjectMapper Obj = new ObjectMapper();
		try {
			LOGGER.info(
					"PropertyRestController >> addProperty >> propertyInfo >> " + Obj.writeValueAsString(propertyInfo));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DesireStatus desireStatus = propertyService.addProperty(authToken, propertyInfo);
		String[] properties = { "statusType", "text", "assetUrl", "property" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editProperty")
	public MappingJacksonValue editProperty(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyInfo propertyInfo) {
		LOGGER.info("PropertyRestController >> editProperty >> called.");
		DesireStatus desireStatus = propertyService.editProperty(authToken, propertyInfo);
		String[] properties = { "statusType", "text", "assetUrl", "property" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteProperty/{propertyId}")
	public MappingJacksonValue deleteProperty(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String propertyId) {
		LOGGER.info("PropertyRestController >> deleteProperty with propertyId >> " + propertyId + "called.");
		DesireStatus desireStatus = propertyService.deleteProperty(authToken, Long.parseLong(propertyId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	// Publish/In-Publish property or lead(Desired Request)
	@PutMapping("publish/status")
	public MappingJacksonValue publishStatus(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody LeadRequest request) {
		LOGGER.info("PropertyRestController >> publishStatus >> called.");
		DesireStatus desireStatus = propertyService.editLeadPublish(authToken, request);
		String[] properties = { "statusType", "text" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	// PropertyType
	@GetMapping("type/{typeId}")
	public MappingJacksonValue propertyTypeList(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String typeId) {
		LOGGER.info("PropertyRestController >> propertyTypeList with typeId >> " + typeId + "called.");
		DesireStatus desireStatus = propertyService.viewPropertyType(authToken, Long.parseLong(typeId));
		String[] properties = { "statusType", "text", "propertyType" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("types")
	public MappingJacksonValue propertyTypeList(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("PropertyRestController >> propertyTypeList >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.propertyTypes(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "propertyTypes" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addType")
	public MappingJacksonValue addPropertyType(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyType propertyType) {
		LOGGER.info("PropertyRestController >> addPropertyType >> called.");
		DesireStatus desireStatus = propertyService.addPropertyType(authToken, propertyType);
		String[] properties = { "statusType", "text", "propertyType" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editType")
	public MappingJacksonValue editPropertyType(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyType propertyType) {
		LOGGER.info("PropertyRestController >> editPropertyType >> called.");
		DesireStatus desireStatus = propertyService.editPropertyType(authToken, propertyType);
		String[] properties = { "statusType", "text", "propertyType" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteType/{typeId}")
	public MappingJacksonValue deletePropertyType(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String typeId) {
		LOGGER.info("PropertyRestController >> deletePropertyType with typeId >> " + typeId + "called.");
		DesireStatus desireStatus = propertyService.deletePropertyType(authToken, Long.parseLong(typeId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("type/state")
	public MappingJacksonValue changePropertyTypeState(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ChangeStateRequest request) {
		LOGGER.info("PropertyRestController >> changePropertyTypeState called.");
		DesireStatus desireStatus = propertyService.changePropertyTypeState(authToken, request);
		String[] properties = { "statusType", "text", "propertyType" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	// PropertySubType
	@GetMapping("/sub/type/{typeId}")
	public MappingJacksonValue propertySubType(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String typeId) {
		LOGGER.info("PropertyRestController >> propertySubTypeList with typeId >> " + typeId + "called.");
		DesireStatus desireStatus = propertyService.viewPropertySubType(authToken, Long.parseLong(typeId));
		String[] properties = { "statusType", "text", "propertySubType" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("/sub/types/{typeId}")
	public MappingJacksonValue propertySubTypeList(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String typeId) {
		LOGGER.info("PropertyRestController >> propertySubTypeList with typeId >> " + typeId + "called.");
		DesireStatus desireStatus = propertyService.propertySubTypes(authToken, Long.parseLong(typeId));
		String[] properties = { "statusType", "text", "propertySubTypes" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/sub/types/{typeId}")
	public MappingJacksonValue propertySubTypeList(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String typeId, @RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("PropertyRestController >> propertySubTypeList >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.propertySubTypes(authToken, request, Long.parseLong(typeId));
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "propertySubTypes" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/sub/type/add")
	public MappingJacksonValue addPropertySubType(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertySubType propertySubType) {
		LOGGER.info("PropertyRestController >> addPropertySubType >> called.");
		DesireStatus desireStatus = propertyService.addPropertySubType(authToken, propertySubType);
		String[] properties = { "statusType", "text", "propertySubType" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/sub/type/edit")
	public MappingJacksonValue editPropertySubType(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertySubType propertySubType) {
		LOGGER.info("PropertyRestController >> editPropertySubType >> called.");
		DesireStatus desireStatus = propertyService.editPropertySubType(authToken, propertySubType);
		String[] properties = { "statusType", "text", "propertySubType" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("sub/type/delete/{typeId}")
	public MappingJacksonValue deletePropertySubType(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String typeId) {
		LOGGER.info("PropertyRestController >> deletePropertySubType with typeId >> " + typeId + "called.");
		DesireStatus desireStatus = propertyService.deletePropertySubType(authToken, Long.parseLong(typeId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/sub/type/state")
	public MappingJacksonValue changePropertySubTypeState(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ChangeStateRequest request) {
		LOGGER.info("PropertyRestController >> changePropertySubTypeState called.");
		DesireStatus desireStatus = propertyService.changePropertySubTypeState(authToken, request);
		String[] properties = { "statusType", "text", "propertySubType" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	// PropertyPurpose
	@GetMapping("purpose/{purposeId}")
	public MappingJacksonValue viewPropertyPurpose(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String purposeId) {
		LOGGER.info("PropertyRestController >> propertyList with purposeId >> " + purposeId + "called.");
		DesireStatus desireStatus = propertyService.viewPropertyPurpose(authToken, Long.parseLong(purposeId));
		String[] properties = { "statusType", "text", "propertyPurpose" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("purposes")
	public MappingJacksonValue propertyPurposeList(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("PropertyRestController >> propertyPurposeList >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.propertyPurposes(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "propertyPurposes" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addPurpose")
	public MappingJacksonValue addPropertyPurpose(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyPurpose propertyPurpose) {
		LOGGER.info("PropertyRestController >> addPropertyPurpose >> called.");
		DesireStatus desireStatus = propertyService.addPropertyPurpose(authToken, propertyPurpose);
		String[] properties = { "statusType", "text", "propertyPurpose" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editPurpose")
	public MappingJacksonValue editPropertyPurpose(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyPurpose propertyPurpose) {
		LOGGER.info("PropertyRestController >> editPropertyPurpose >> called.");
		DesireStatus desireStatus = propertyService.editPropertyPurpose(authToken, propertyPurpose);
		String[] properties = { "statusType", "text", "propertyPurpose" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deletePurpose/{purposeId}")
	public MappingJacksonValue deletePropertyPurpose(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String purposeId) {
		LOGGER.info("PropertyRestController >> deletePropertyPurpose with purposeId >> " + purposeId + "called.");
		DesireStatus desireStatus = propertyService.deletePropertyPurpose(authToken, Long.parseLong(purposeId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("purpose/state")
	public MappingJacksonValue changePropertyPurposeState(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ChangeStateRequest request) {
		LOGGER.info("PropertyRestController >> changePropertyPurposeState called.");
		DesireStatus desireStatus = propertyService.changePropertyPurposeState(authToken, request);
		String[] properties = { "statusType", "text", "propertyPurpose" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	// PropertyStatus
	@GetMapping("status/{statusId}")
	public MappingJacksonValue propertyStatusList(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String statusId) {
		LOGGER.info("PropertyRestController >> propertyList with statusId >> " + statusId + "called.");
		DesireStatus desireStatus = propertyService.viewPropertyStatus(authToken, Long.parseLong(statusId));
		String[] properties = { "statusType", "text", "propertyStatus" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("statusList")
	public MappingJacksonValue propertyStatusList(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("PropertyRestController >> propertyStatusList >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.propertyStatusList(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "propertyStatuss" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addStatus")
	public MappingJacksonValue addPropertyStatus(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyStatus propertyStatus) {
		LOGGER.info("PropertyRestController >> addStatus >> called.");
		DesireStatus desireStatus = propertyService.addPropertyStatus(authToken, propertyStatus);
		String[] properties = { "statusType", "text", "propertyStatus" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editStatus")
	public MappingJacksonValue editPropertyStatus(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyStatus propertyStatus) {
		LOGGER.info("PropertyRestController >> editStatus >> called.");
		DesireStatus desireStatus = propertyService.editPropertyStatus(authToken, propertyStatus);
		String[] properties = { "statusType", "text", "propertyStatus" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteStatus/{statusId}")
	public MappingJacksonValue deletePropertyStatus(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String statusId) {
		LOGGER.info("PropertyRestController >> deletePropertyStatus with statusId >> " + statusId + "called.");
		DesireStatus desireStatus = propertyService.deletePropertyStatus(authToken, Long.parseLong(statusId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("status/state")
	public MappingJacksonValue changePropertyStatusState(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ChangeStateRequest request) {
		LOGGER.info("PropertyRestController >> changePropertyStatusState called.");
		DesireStatus desireStatus = propertyService.changePropertyStatusState(authToken, request);
		String[] properties = { "statusType", "text", "propertyStatus" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	// PropertyStructure
	@GetMapping("structure/{structureId}")
	public MappingJacksonValue propertyStructureList(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String structureId) {
		LOGGER.info("PropertyRestController >> propertyStructureList with structureId >> " + structureId + "called.");
		DesireStatus desireStatus = propertyService.viewPropertyStructure(authToken, Long.parseLong(structureId));
		String[] properties = { "statusType", "text", "propertyStructure" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("structures")
	public MappingJacksonValue propertyStructureList(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("PropertyRestController >> propertyStructureList >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.PropertyStructureList(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "propertyStructures" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addStructure")
	public MappingJacksonValue addPropertyStructure(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyStructure propertyStructure) {
		LOGGER.info("PropertyRestController >> addPropertyStructure >> called.");
		DesireStatus desireStatus = propertyService.addPropertyStructure(authToken, propertyStructure);
		String[] properties = { "statusType", "text", "propertyStructure" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editStructure")
	public MappingJacksonValue editProperty(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyStructure propertyStructure) {
		LOGGER.info("PropertyRestController >> editPropertyStructure >> called.");
		DesireStatus desireStatus = propertyService.editPropertyStructure(authToken, propertyStructure);
		String[] properties = { "statusType", "text", "propertyStructure" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteStructure/{structureId}")
	public MappingJacksonValue deletePropertyStructure(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String structureId) {
		LOGGER.info("PropertyRestController >> deletePropertyStructure with structureId >> " + structureId + "called.");
		DesireStatus desireStatus = propertyService.deletePropertyStructure(authToken, Long.parseLong(structureId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	// PropertyFeatures
	@GetMapping("feature/{featureId}")
	public MappingJacksonValue propertyFeatureList(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String featureId) {
		LOGGER.info("PropertyRestController >> propertyList with propertyId >> " + featureId + "called.");
		DesireStatus desireStatus = propertyService.viewPropertyFeature(authToken, Long.parseLong(featureId));
		String[] properties = { "statusType", "text", "propertyFeature" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("features")
	public MappingJacksonValue featureList(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("PropertyRestController >> featureList >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.propertyFeatures(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "propertyFeatures" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addFeature")
	public MappingJacksonValue addPropertyFeature(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyFeature propertyFeature) {
		LOGGER.info("PropertyRestController >> addPropertyFeature >> called.");
		DesireStatus desireStatus = propertyService.addPropertyFeature(authToken, propertyFeature);
		String[] properties = { "statusType", "text", "propertyFeature" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editFeature")
	public MappingJacksonValue editPropertyFeature(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyFeature propertyFeature) {
		LOGGER.info("PropertyRestController >> editPropertyFeature >> called.");
		DesireStatus desireStatus = propertyService.editPropertyFeature(authToken, propertyFeature);
		String[] properties = { "statusType", "text", "propertyFeature" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteFeature/{featureId}")
	public MappingJacksonValue deletePropertyFeature(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String featureId) {
		LOGGER.info("PropertyRestController >> deleteFeature with featureId >> " + featureId + "called.");
		DesireStatus desireStatus = propertyService.deletePropertyFeature(authToken, Long.parseLong(featureId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	// PropertyImages
	@GetMapping("image/{imageId}")
	public MappingJacksonValue propertyImagesList(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String imageId) {
		LOGGER.info("PropertyRestController >> propertyList with imageId >> " + imageId + "called.");
		DesireStatus desireStatus = propertyService.viewProperty(authToken, Long.parseLong(imageId));
		String[] properties = { "statusType", "text", "propertyImage" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("images")
	public MappingJacksonValue propertyImageList(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("PropertyRestController >> propertyImageList >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.propertyImages(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "propertyImages" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("images/{propertyId}")
	public MappingJacksonValue propertyImages(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String propertyId, @RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("PropertyRestController >> propertyImageList >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.propertyImages(authToken, Long.parseLong(propertyId), request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "propertyImages" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addImage")
	public MappingJacksonValue addPropertyImage(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyImage propertyImage) {
		LOGGER.info("PropertyRestController >> addPropertyImage >> called.");
		DesireStatus desireStatus = propertyService.addPropertyImage(authToken, propertyImage);
		String[] properties = { "statusType", "text", "propertyImage" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editImage")
	public MappingJacksonValue editPropertyImage(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyImage propertyImage) {
		LOGGER.info("PropertyRestController >> editPropertyImage >> called.");
		DesireStatus desireStatus = propertyService.editPropertyImage(authToken, propertyImage);
		String[] properties = { "statusType", "text", "property" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteImage/{imageId}")
	public MappingJacksonValue deletePropertyImage(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String imageId) {
		LOGGER.info("PropertyRestController >> deleteProperty with imageId >> " + imageId + "called.");
		DesireStatus desireStatus = propertyService.deletePropertyImage(authToken, Long.parseLong(imageId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	// PropertyAttachment
	@GetMapping("attachment/{attachmentId}")
	public MappingJacksonValue propertyAttachmentDetails(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String attachmentId) {
		LOGGER.info("PropertyRestController >> propertyList with attachmentId >> " + attachmentId + "called.");
		DesireStatus desireStatus = propertyService.viewPropertyAttachment(authToken, Long.parseLong(attachmentId));
		String[] properties = { "statusType", "text", "propertyAttachment" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("attachments")
	public MappingJacksonValue propertyAttachmentList(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("PropertyRestController >> attachments >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.propertyAttachments(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "propertyAttachments" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("attachments/{propertyId}")
	public MappingJacksonValue propertyAttachmentList(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String propertyId, @RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("PropertyRestController >> attachments >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.propertyAttachments(authToken, Long.parseLong(propertyId), request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "propertyAttachments" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addAttachment")
	public MappingJacksonValue addPropertyAttachment(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyAttachment propertyAttachment) {
		LOGGER.info("PropertyRestController >> addPropertyAttachment >> called.");
		DesireStatus desireStatus = propertyService.addPropertyAttachment(authToken, propertyAttachment);
		String[] properties = { "statusType", "text", "propertyAttachment" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editAttachment")
	public MappingJacksonValue editPropertyAttachment(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyAttachment propertyAttachment) {
		LOGGER.info("PropertyRestController >> editProperty >> called.");
		DesireStatus desireStatus = propertyService.editPropertyAttachment(authToken, propertyAttachment);
		String[] properties = { "statusType", "text", "propertyAttachment" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteAttachment/{attachmentId}")
	public MappingJacksonValue deletePropertyAttachment(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String attachmentId) {
		LOGGER.info("PropertyRestController >> deleteProperty with attachmentId >> " + attachmentId + "called.");
		DesireStatus desireStatus = propertyService.deletePropertyAttachment(authToken, Long.parseLong(attachmentId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	// PropertyVideo
	@GetMapping("video/{videoId}")
	public MappingJacksonValue propertyVideoDetail(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String videoId) {
		LOGGER.info("PropertyRestController >> propertyList with propertyId >> " + videoId + "called.");
		DesireStatus desireStatus = propertyService.viewPropertyVideo(authToken, Long.parseLong(videoId));
		String[] properties = { "statusType", "text", "propertyVideo" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("videos")
	public MappingJacksonValue propertyVideos(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("PropertyRestController >> propertyVideos >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.propertyVideos(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "propertyVideos" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("videos/{propertyId}")
	public MappingJacksonValue propertyVideos(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String propertyId, @RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("PropertyRestController >> propertyVideos >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.propertyVideos(authToken, Long.parseLong(propertyId), request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "propertyVideos" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/addVideo")
	public MappingJacksonValue addPropertyVideo(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyVideo propertyVideo) {
		LOGGER.info("PropertyRestController >> addPropertyVideo >> called.");
		DesireStatus desireStatus = propertyService.addPropertyVideo(authToken, propertyVideo);
		String[] properties = { "statusType", "text", "propertyVideo" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/editVideo")
	public MappingJacksonValue editPropertyVideo(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody PropertyVideo propertyVideo) {
		LOGGER.info("PropertyRestController >> editPropertyVideo >> called.");
		DesireStatus desireStatus = propertyService.editPropertyVideo(authToken, propertyVideo);
		String[] properties = { "statusType", "text", "propertyVideo" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@DeleteMapping("deleteVideo/{videoId}")
	public MappingJacksonValue deletePropertyVideo(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String videoId) {
		LOGGER.info("PropertyRestController >> deletePropertyVideo with videoId >> " + videoId + "called.");
		DesireStatus desireStatus = propertyService.deletePropertyVideo(authToken, Long.parseLong(videoId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PutMapping("/request/comment/state")
	public MappingJacksonValue changeRequestCommentState(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody ChangeStateRequest request) {
		LOGGER.info("PropertyRestController >> changeRequestCommentState called.");
		DesireStatus desireStatus = propertyService.changeRequestCommentState(authToken, request);
		String[] properties = { "statusType", "text", "requestComment" };
		return Resources.formatedResponse(desireStatus, properties);
	}
	
	@PostMapping("/favorite/all")
	public MappingJacksonValue favorites(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody(required = false) DesireSearchRequest request) {
		LOGGER.info("PropertyRestController >> favorites called for favorite list.");
		request = Resources.getDefaultRequest(request);
		DesireStatus DesireStatus = propertyService.favorites(authToken, request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "favorites" };
		return Resources.formatedResponse(DesireStatus, properties);
	}

	@GetMapping("/favorite/id/{favoriteId}")
	public MappingJacksonValue viewFavorite(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String favoriteId) {
		LOGGER.info("PropertyRestController >> viewFavorite called for favorite details.");
		DesireStatus DesireStatus = propertyService.viewFavorite(authToken, Long.parseLong(favoriteId));
		String[] properties = { "statusType", "text", "favorite" };
		return Resources.formatedResponse(DesireStatus, properties);
	}

	@PostMapping("/favorite/add")
	public MappingJacksonValue addFavorite(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody Favorite favorite) {
		LOGGER.info("PropertyRestController >> addFavorite called.");
		DesireStatus DesireStatus = propertyService.addFavorite(authToken, favorite);
		String[] properties = { "statusType", "text", "favorite" };
		return Resources.formatedResponse(DesireStatus, properties);
	}

	@PutMapping("/favorite/edit")
	public MappingJacksonValue editFavorite(@RequestHeader(value = "Authorization") String authToken,
			@RequestBody Favorite favorite) {
		LOGGER.info("PropertyRestController >> editFavorite called.");
		DesireStatus DesireStatus = propertyService.editFavorite(authToken, favorite);
		String[] properties = { "statusType", "text", "favorite" };
		return Resources.formatedResponse(DesireStatus, properties);
	}

	@DeleteMapping("/favorite/delete/{favoriteId}")
	public MappingJacksonValue deleteFavorite(@RequestHeader(value = "Authorization") String authToken,
			@PathVariable String favoriteId) {
		LOGGER.info("PropertyRestController >> deleteFavorite with favoriteId >> " + favoriteId + "called.");
		DesireStatus DesireStatus = propertyService.deleteFavorite(authToken, Long.parseLong(favoriteId));
		String[] properties = { "statusType", "text", "" };
		return Resources.formatedResponse(DesireStatus, properties);
	}

}
