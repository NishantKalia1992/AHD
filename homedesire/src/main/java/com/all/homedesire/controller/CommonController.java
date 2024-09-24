package com.all.homedesire.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.all.homedesire.common.Constants;
import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.Resources;
import com.all.homedesire.entities.ContactUs;
import com.all.homedesire.entities.DesiredQuery;
import com.all.homedesire.entities.Lead;
import com.all.homedesire.entities.PropertyAttachment;
import com.all.homedesire.entities.PropertyImage;
import com.all.homedesire.entities.User;
import com.all.homedesire.entities.UserContact;
import com.all.homedesire.entities.UserNetwork;
import com.all.homedesire.repository.UserContactRepository;
import com.all.homedesire.repository.UserNetworkRepository;
import com.all.homedesire.repository.UserRepository;
import com.all.homedesire.resources.dto.AreaSearchRequest;
import com.all.homedesire.resources.dto.LoginRequest;
import com.all.homedesire.resources.dto.LoginRequestDTO;
import com.all.homedesire.resources.dto.property.CommonLeadRequest;
import com.all.homedesire.service.AdminService;
import com.all.homedesire.service.EncryptionService;
import com.all.homedesire.service.PropertyService;
import com.all.homedesire.service.UserService;

@RestController
@RequestMapping("/all")
public class CommonController {

	Logger LOGGER = LoggerFactory.getLogger(PropertyRestController.class);

	@Value("${image.base-dir}")
	private String imageBasedir;
	@Value("${site.url}")
	private String siteUrl;

	@Autowired
	AdminService adminService;

	@Autowired
	PropertyService propertyService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	EncryptionService encryptionService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserContactRepository userContactRepository;
	
	@Autowired
	UserNetworkRepository userNetworkRepository;


	@PostMapping("/signup")
	public MappingJacksonValue signUp(@RequestBody User user, 
	                                  @RequestParam(value = "ref", required = false) String encryptedRefId) {
	    DesireStatus status;
	    
	    try {
	        status = adminService.signUp(user);

	        // If signup is successful and ref ID is provided
	        if (status.getStatusType().equals(Constants.STATUS_SUCCESS) && encryptedRefId != null && !encryptedRefId.isEmpty()) {
	            
	        	try {
	        	    // Decrypt the referral ID
	        	    String decryptedRefId = encryptionService.decrypt(encryptedRefId);
	        	    LOGGER.info("Decrypted Referral ID: " + decryptedRefId);

	        	    // Convert decrypted ID to Long
	        	    Long id = Long.valueOf(decryptedRefId);
	        	    LOGGER.info("Converted Referral ID to Long: " + id);

	        	    // Retrieve and update UserContact or UserNetwork
	        	    Optional<UserContact> optContact = userContactRepository.findById(id);
	        	    Optional<UserNetwork> optNetwork = userNetworkRepository.findById(id);
	        	    LOGGER.info("UserContact present: " + optContact.isPresent());
	        	    LOGGER.info("UserNetwork present: " + optNetwork.isPresent());

	        	    if (optContact.isPresent()) {
	        	        UserContact userContact = optContact.get();
	        	        userContact.setCustomerId(status.getUser());
	        	        userContact.setUpdatedOn(new Date());
	        	        userContactRepository.save(userContact);
	        	        LOGGER.info("Updated UserContact with customerId: " + status.getUser().getId());
	        	    }

	        	    if (optNetwork.isPresent()) {
	        	        UserNetwork userNetwork = optNetwork.get();
	        	        userNetwork.setPartnerId(status.getUser());
	        	        userNetwork.setUpdatedOn(new Date());
	        	        userNetworkRepository.save(userNetwork);
	        	        LOGGER.info("Updated UserNetwork with partnerId: " + status.getUser().getId());
	        	    }

	        	    if (!optContact.isPresent() && !optNetwork.isPresent()) {
	        	        LOGGER.warn("No contact or network found for referral ID: " + decryptedRefId);
	        	    }
	        	    
	        	} catch (IllegalArgumentException e) {
	        	    LOGGER.error("Error while decrypting referral ID: " + encryptedRefId, e);
	        	    status = Resources.setStatus(Constants.STATUS_ERROR, "Invalid referral ID format", "Signup");
	        	}

	        }
	        
	    } catch (Exception e) {
	        status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(), "Signup");
	    }

	    String[] properties = { "statusType", "text", "user" };
	    return Resources.formatedResponse(status, properties);
	}



	@PostMapping("/signin")
	public MappingJacksonValue signIn(@RequestBody LoginRequest loginRequest) {
		DesireStatus status = adminService.signIn(loginRequest, true, null);
		String[] properties = { "statusType", "text", "token", "type", "userDetails" };
		return Resources.formatedResponse(status, properties);
	}

	@PostMapping("/social/login")
	public MappingJacksonValue signInWithSocialMedia(@RequestBody LoginRequestDTO loginRequest) {
		DesireStatus status = adminService.signInWithSocialMedia(loginRequest);
		String[] properties = { "statusType", "text", "token", "type", "userDetails" };
		return Resources.formatedResponse(status, properties);
	}

	@GetMapping("countries")
	public MappingJacksonValue countries() {
		LOGGER.info("CommonController >> countries called for country list.");
		DesireStatus status = adminService.countries();
		String[] properties = { "statusType", "text", "countries" };
		return Resources.formatedResponse(status, properties);
	}

	@GetMapping("states")
	public MappingJacksonValue states() {
		LOGGER.info("CommonController >> states called for state list.");
		DesireStatus status = adminService.states();
		String[] properties = { "statusType", "text", "states" };
		return Resources.formatedResponse(status, properties);
	}

	@GetMapping("states/{countryId}")
	public MappingJacksonValue states(@PathVariable String countryId) {
		LOGGER.info("CommonController >> states called for state list with country id " + countryId + ".");
		DesireStatus status = adminService.states(Long.parseLong(countryId));
		String[] properties = { "statusType", "text", "states" };
		return Resources.formatedResponse(status, properties);
	}

	@GetMapping("cities")
	public MappingJacksonValue cities() {
		LOGGER.info("CommonController >> cities called for city list.");
		DesireStatus status = adminService.cities();
		String[] properties = { "statusType", "text", "cities" };
		return Resources.formatedResponse(status, properties);
	}

	@GetMapping("cities/{stateId}")
	public MappingJacksonValue cities(@PathVariable String stateId) {
		LOGGER.info("CommonController >> cities called for city list with state id " + stateId + ".");
		DesireStatus status = adminService.cities(Long.parseLong(stateId));
		String[] properties = { "statusType", "text", "cities" };
		return Resources.formatedResponse(status, properties);
	}

	@PostMapping("areas/by/name")
	public MappingJacksonValue areasByName(@RequestBody(required = false) AreaSearchRequest request) {
		LOGGER.info("AdminRestController >> areas called for area list with areaName " + request.getAreaName() + ".");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = adminService.areasByName(request);
		String[] properties = { "statusType", "text", "totalPage", "totalRecord", "areas" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("services")
	public MappingJacksonValue services() {
		LOGGER.info("CommonController >> services called for service list.");
		DesireStatus status = adminService.services();
		String[] properties = { "statusType", "text", "services" };
		return Resources.formatedResponse(status, properties);
	}

	@PostMapping("/addContactUs")
	public MappingJacksonValue addContactUs(@RequestBody ContactUs contactUs) {
		LOGGER.info("CommonController >> addContactUs called.");
		DesireStatus status = adminService.addContactUs(contactUs);
		String[] properties = { "statusType", "text", "contactUs" };
		return Resources.formatedResponse(status, properties);
	}

	@PostMapping("/addDesiredQuery")
	public MappingJacksonValue addDesiredQuery(@RequestBody DesiredQuery desiredQuery) {
		LOGGER.info("CommonController >> addDesiredQuery called.");
		DesireStatus status = adminService.addDesiredQuery(desiredQuery);
		String[] properties = { "statusType", "text", "desiredQuery" };
		return Resources.formatedResponse(status, properties);
	}

	@PostMapping("/lead/add")
	public MappingJacksonValue addLead(@RequestBody Lead lead) {
		LOGGER.info("CommonController >> addLead called.");
		DesireStatus desireStatus = propertyService.addLead(lead);
		String[] properties = { "statusType", "text", "lead" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("/property/types")
	public MappingJacksonValue propertyTypeList() {
		LOGGER.info("CommonController >> propertyTypeList >> called.");
		DesireStatus status = propertyService.propertyTypes();
		String[] properties = { "statusType", "text", "propertyTypes" };
		return Resources.formatedResponse(status, properties);
	}

	@GetMapping("/property/sub/types/{typeId}")
	public MappingJacksonValue propertySubTypeList(@PathVariable String typeId) {
		LOGGER.info("CommonController >> propertySubTypeList with typeId >> " + typeId + "called.");
		DesireStatus desireStatus = propertyService.propertySubTypes(Long.parseLong(typeId));
		String[] properties = { "statusType", "text", "propertySubTypes" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@GetMapping("/property/purposes")
	public MappingJacksonValue propertyPurposeList() {
		LOGGER.info("CommonController >> propertyPurposeList >> called.");
		DesireStatus status = propertyService.propertyPurposes();
		String[] properties = { "statusType", "text", "propertyPurposes" };
		return Resources.formatedResponse(status, properties);
	}

	@GetMapping("/property/propertyDetail/{propertyId}")
	public MappingJacksonValue propertyList(@PathVariable String propertyId) {
		LOGGER.info("PropertyRestController >> propertyList with propertyId >> " + propertyId + "called.");
		DesireStatus status = propertyService.viewProperty(Long.parseLong(propertyId));
		String[] properties = { "statusType", "text", "assetUrl", "propertyInfo" };
		return Resources.formatedResponse(status, properties);
	}

	@GetMapping("/property/latestList")
	public MappingJacksonValue latestProperties() {
		LOGGER.info("PropertyRestController >> latestProperties >> called.");
		DesireStatus status = propertyService.latestProperties();
		String[] properties = { "statusType", "text", "assetUrl", "properties" };
		return Resources.formatedResponse(status, properties);
	}

	@PostMapping(value = "/upload/files/{uploadType}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public MappingJacksonValue uploadFilesExample5(MultipartFile[] files, @PathVariable String uploadType) {
		LOGGER.info("Request contains, Files count: " + files.length);
		LOGGER.info("Request contains, uploadType: " + uploadType);
		DesireStatus status = new DesireStatus();
		List<PropertyImage> propertyImages = new ArrayList<PropertyImage>();
		List<PropertyAttachment> propertyAttachments = new ArrayList<PropertyAttachment>();
		boolean isError = true;
		List<String> fileExt = (uploadType.equals("attachments")) ? Constants.ATTACHMENT_FILE_EXT
				: Constants.IMAGE_FILE_EXT;
		int fileCount = 1;
		for (MultipartFile multipartFile : files) {
			String fileName = Resources.getFileName(uploadType) + fileCount
					+ multipartFile.getOriginalFilename().substring(
							multipartFile.getOriginalFilename().lastIndexOf("."),
							multipartFile.getOriginalFilename().length());
			LOGGER.info(
					"File name >> " + fileName + " containt type >> " + multipartFile.getContentType().toUpperCase());
			if (multipartFile.getSize() <= 0) {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPLOAD_FAILURE, "Empty file");
				isError = true;
				break;
			} else if (!fileExt.contains(multipartFile.getContentType().toUpperCase().trim())) {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPLOAD_FAILURE,
						"Invalid formate file");
				isError = true;
				break;
			} else {
				adminService.save(multipartFile, fileName, uploadType);
				if (uploadType.equals(Constants.FILE_ATTACHMENT)) {
					PropertyAttachment attachment = new PropertyAttachment();
					attachment.setAttachmentPath(fileName);
					attachment.setAttachmentUrl(siteUrl + imageBasedir + "/" + uploadType + "/" + fileName);
					propertyAttachments.add(attachment);
				} else {
					PropertyImage image = new PropertyImage();
					image.setImagePath(fileName);
					image.setImageUrl(siteUrl + imageBasedir + "/" + uploadType + "/" + fileName);
					propertyImages.add(image);

				}
				isError = false;
			}
			fileCount++;
		}
		if (!isError) {
			status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPLOAD_SUCCESS,
					"Files for " + uploadType.toLowerCase());

			if (uploadType.equals("attachments")) {
				status.setPropertyAttachments(propertyAttachments);
			} else {
				status.setPropertyImages(propertyImages);
			}
		}
		// status = Resources.setStatus(Constants.STATUS_SUCCESS, "User signed up
		// successfully!");
		// status = Resources.setStatus(Constants.STATUS_FAILURE, "User signed up
		// failed!");
		// Add your processing logic here
		String[] properties = { "statusType", "text",
				(uploadType.equals("attachments")) ? "propertyAttachments" : "propertyImages" };
		return Resources.formatedResponse(status, properties);
	}
	
	@PostMapping("lead/list")
	public MappingJacksonValue leadList(@RequestBody CommonLeadRequest request) {
		LOGGER.info("PropertyRestController >> listPreferred >> called.");
		request = Resources.getDefaultRequest(request);
		DesireStatus desireStatus = propertyService.leadList(request);
		String[] properties = { "statusType", "text", "assetUrl", "totalPage", "totalRecord", "properties" };
		return Resources.formatedResponse(desireStatus, properties);
	}
	
	@PostMapping("/user/document/add")
	public MappingJacksonValue addDocument(@RequestParam(required = true, name = "file") MultipartFile file,
			@RequestParam("name") String name, @RequestParam("user_id") String userId) {
		LOGGER.info("UserRestController >> addDocument called.");
		DesireStatus desireStatus = userService.addDocument(Long.parseLong(userId), name, file);
		String[] properties = { "statusType", "text", "assetUrl", "document" };
		return Resources.formatedResponse(desireStatus, properties);
	}

	@PostMapping("/payment/add")
	public MappingJacksonValue addPayment(
			@RequestParam(required = false, name = "file") MultipartFile file, @RequestParam("mode") String mode,
			@RequestParam("gateway") String gateway, @RequestParam("provider") String provider,
			@RequestParam("amount") String amount, @RequestParam("status") String paymentStatus,
			@RequestParam("user_id") String userId) {
		LOGGER.info("UserRestController >> addPayment called.");
		DesireStatus desireStatus = (file != null)
				? userService.addPayment(Long.parseLong(userId), mode, gateway, provider,
						Double.parseDouble(amount), paymentStatus, file)
				: userService.addPayment(Long.parseLong(userId), mode, gateway, provider,
						Double.parseDouble(amount), paymentStatus);
		String[] properties = { "statusType", "text", "assetUrl", "payment" };
		return Resources.formatedResponse(desireStatus, properties);
	}
}
