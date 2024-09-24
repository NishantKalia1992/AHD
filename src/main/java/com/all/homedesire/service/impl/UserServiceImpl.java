package com.all.homedesire.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.all.homedesire.common.Constants;
import com.all.homedesire.common.CsvUtils;
import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.LogConstants;
import com.all.homedesire.common.Resources;
import com.all.homedesire.entities.Area;
import com.all.homedesire.entities.City;
import com.all.homedesire.entities.Country;
import com.all.homedesire.entities.State;
import com.all.homedesire.entities.User;
import com.all.homedesire.entities.UserContact;
import com.all.homedesire.entities.UserDocument;
import com.all.homedesire.entities.UserNetwork;
import com.all.homedesire.entities.UserPayment;
import com.all.homedesire.enums.EType;
import com.all.homedesire.repository.CityRepository;
import com.all.homedesire.repository.CountryRepository;
import com.all.homedesire.repository.StateRepository;
import com.all.homedesire.repository.UserContactRepository;
import com.all.homedesire.repository.UserDocumentRepository;
import com.all.homedesire.repository.UserNetworkRepository;
import com.all.homedesire.repository.UserPaymentRepository;
import com.all.homedesire.repository.UserRepository;
import com.all.homedesire.resources.dto.ContactDTO;
import com.all.homedesire.resources.dto.DesireSearchRequest;
import com.all.homedesire.resources.dto.NetworkDTO;
import com.all.homedesire.security.service.UserTokenService;
import com.all.homedesire.service.AdminService;
import com.all.homedesire.service.EmailService;
import com.all.homedesire.service.EncryptionService;
import com.all.homedesire.service.LogService;
import com.all.homedesire.service.StorageService;
import com.all.homedesire.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	@Value("${site.url}")
	private String siteUrl;

	@Value("${image.base-dir}")
	private String imageBasedir;

	@Autowired
	UserTokenService userTokenService;
	@Autowired
	LogService logService;
	@Autowired
	AdminService adminService;
	@Autowired
	StorageService storageService;

	@Autowired
	CityRepository cityRepository;
	@Autowired
	CountryRepository countryRepository;
	@Autowired
	StateRepository stateRepository;
	@Autowired
	UserContactRepository userContactRepository;
	@Autowired
	UserDocumentRepository userDocumentRepository;
	@Autowired
	UserPaymentRepository userPaymentRepository;
	@Autowired
	UserNetworkRepository userNetworkRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	EncryptionService encryptionService;
	@Autowired
	EmailService emailService;

	@Override
	public DesireStatus contacts(String authToken, DesireSearchRequest request) {
		LOGGER.info("UserService >> contacts called!");
		DesireStatus status = new DesireStatus();
		List<UserContact> contacts = null;
		Page<UserContact> propPage = null;
		int pageNumber = 0, pageSize = 1;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = (pageNumber > 0) ? pageNumber - 1 : 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					propPage = userContactRepository.findAllASC(authStatus.getUser(), pageable);
				} else {
					propPage = userContactRepository.findAllDESC(authStatus.getUser(), pageable);
				}

				contacts = propPage.getContent();
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "User contact");
				status.setContacts(contacts);
				status.setTotalPage(propPage.getTotalPages());
				status.setTotalRecord(propPage.getTotalElements());
				logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List user contact");
		}
		return status;
	}

	@Override
	public DesireStatus viewContact(String authToken, long contactId) {
		LOGGER.info("UserService >> viewUserContact called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (contactId > 0) {
					Optional<UserContact> foundUserContact = userContactRepository.findByObjectId(contactId);
					if (foundUserContact.isPresent()) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS,
								"User contact");
						status.setContact(foundUserContact.get());
						logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.DETAIL, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE,
								"User contact");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"View user contact");
		}
		return status;
	}
	
	@Override
	public DesireStatus addContact(String authToken, UserContact contact) {
	    LOGGER.info("UserService >> addUserContact called!");
	    DesireStatus status = new DesireStatus();
	    Date dtNow = new Date();
	    LOGGER.info("UserService >> addUserContact object received >> " + contact);

	    try {
	        DesireStatus authStatus = userTokenService.getUserInfo(authToken);
	        if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
	        	 User currentUser = authStatus.getUser();
	             if (!currentUser.getUserType().getName().equalsIgnoreCase(EType.PARTNER.toString())) {
	                 // User is not a Partner, return unauthorized status
	                 status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, "Only 'Partner' type can add a contact", "");
	                 return status;
	             }
	            // Check if the email already exists in the User table
	            Optional<User> optUser = userRepository.findByEmailId(contact.getEmailId());
	            if (optUser.isPresent()) {
	                // Email exists, send a notification email
	                User existingUser = optUser.get();
	                String subject = "Notification: You are already registered";
	                String messageBody = "Dear " + existingUser.getFirstName() + ",\n\n" +
	                                     "You were added as a contact by " + authStatus.getUser().getFirstName() +
	                                     ", but it seems you are already registered in our system.\n\n" +
	                                     "Best regards,\nAll Home Desire";
	                emailService.saveEmail(existingUser.getEmailId(), messageBody, subject, "");
	                status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_EXIST,
	                        "Email address " + contact.getEmailId());
	            } else {
	                // Email does not exist, proceed with adding the contact and sending the signup link
	                Area area = adminService.getArea(authToken, contact.getCountryName(), contact.getStateName(),
	                        contact.getCityName(), contact.getAreaName());
	                contact.setArea(area);
	                contact.setUser(authStatus.getUser());
	                contact.setCreatedOn(dtNow);
	                contact.setUpdatedOn(dtNow);
	                contact.setActive(true);
	                contact.setDeleted(false);
	                UserContact savedUserContact = userContactRepository.save(contact);
	                if (savedUserContact != null) {
	                    // Generate the signup link with the encrypted user ID
	                    String encryptedUserId = encryptionService.encrypt(String.valueOf(contact.getId()));
	                    String signupLink = "https://services.allhomedesire.in/all/signup?ref=" + encryptedUserId;

	                    // Prepare the email content
	                    String subject = "You're Invited to Sign Up!";
	                    String messageBody = "Dear " + contact.getFirstName() + ",\n\n" +
	                                         "You have been added as a contact by " + authStatus.getUser().getFirstName() +
	                                         ". Please sign up using the following link:\n\n" + signupLink + "\n\n" +
	                                         "Best regards,\nAll Home Desire";

	                    // Send the signup email
	                    emailService.saveEmail(contact.getEmailId(), messageBody, subject, "");

	                    // Update the status
	                    status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "User contact");
	                    status.setContact(savedUserContact);
	                    logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.ADD, dtNow, dtNow);
	                } else {
	                    status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "User contact");
	                }
	            }
	        } else {
	            status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
	        }
	    } catch (Exception e) {
	        status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
	                "Add user contact");
	    }

	    return status;
	}
//	=========================================================================================================================

	@Override
	public DesireStatus editContact(String authToken, UserContact contact) {
		LOGGER.info("UserService >> editUserContact called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (contact.getId() != null && contact.getId() > 0) {
					Optional<UserContact> optUserContact = userContactRepository.findByObjectId(contact.getId());
					if (optUserContact.isPresent()) {
						UserContact foundUserContact = optUserContact.get();
						// City foundCity = getCity(authToken, contact.getCountryName(),
						// contact.getStateName(),
						// contact.getCityName());
						// foundUserContact.setCity(foundCity);

						Area area = adminService.getArea(authToken, contact.getCountryName(), contact.getStateName(),
								contact.getCityName(), contact.getAreaName());
						contact.setArea(area);
						foundUserContact.setFirstName(contact.getFirstName());
						foundUserContact.setLastName(contact.getLastName());
						foundUserContact.setMobileNo(contact.getMobileNo());
						foundUserContact.setEmailId(contact.getEmailId());
						foundUserContact.setAddress(contact.getAddress());
						foundUserContact.setAddressOne(contact.getAddressOne());
						foundUserContact.setStreet(contact.getStreet());
						foundUserContact.setApartment(contact.getApartment());
						foundUserContact.setUpdatedOn(dtNow);
						UserContact savedUserContact = userContactRepository.save(foundUserContact);
						if (savedUserContact != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"User contact");
							status.setContact(savedUserContact);
							logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.EDIT,
									null, dtNow);
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"User contact");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"User contact id");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"User contact id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit user contact");
		}
		return status;
	}

	@Override
	public DesireStatus deleteContact(String authToken, long contactId) {
		LOGGER.info("UserService >> deleteUserContact called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (contactId > 0) {
					Optional<UserContact> foundUserContact = userContactRepository.findByObjectId(contactId);
					if (foundUserContact.isPresent()) {
						userContactRepository.delete(foundUserContact.get());
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS,
								"User contact");
						logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.DELETE, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE,
								"User contact");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"User contact id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete user contact");
		}
		return status;
	}

	@Override
	public DesireStatus uploadContact(String authToken, MultipartFile file) {
		LOGGER.info("UserService >> uploadPackage called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("UserService >> file object recieved >> " + file.getContentType());
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {

				if (file.isEmpty() || file.getOriginalFilename().contains("..")) {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPLOAD_FAILURE, "Contact file");
				} else if (!Constants.EXCEL_FILE_EXT.contains(file.getContentType())) {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.INVALID_CSV, "");
				} else {
					List<UserContact> finalContacts = new ArrayList<>();
					List<ContactDTO> contactDTOs = CsvUtils.read(ContactDTO.class, file.getInputStream());

					for (ContactDTO contactDTO : contactDTOs) {
						UserContact contact = new UserContact();
						LOGGER.info("UserService >> contactDTO.getMobileNo() >> " + contactDTO.getMobileNo());
						LOGGER.info("UserService >> contactDTO.getEmailId() >> " + contactDTO.getEmailId());
						List<UserContact> lstUserContact = userContactRepository.findByPhoneOrEmail(
								authStatus.getUser(), contactDTO.getMobileNo(), contactDTO.getEmailId());
						if (lstUserContact.isEmpty()) {

							// City foundCity = getCity(authToken, contactDTO.getCountryName(),
							// contactDTO.getStateName(),
							// contactDTO.getCityName());
							// contact.setCity(foundCity);

							Area area = adminService.getArea(authToken, contact.getCountryName(),
									contact.getStateName(), contact.getCityName(), contact.getAreaName());
							contact.setArea(area);
							contact.setFirstName(contactDTO.getFirstName());
							contact.setLastName(contactDTO.getLastName());
							contact.setMobileNo(contactDTO.getMobileNo());
							contact.setEmailId(contactDTO.getEmailId());
							contact.setAddress(contactDTO.getAddress());
							contact.setAddressOne(contactDTO.getAddressOne());
							contact.setStreet(contactDTO.getStreet());
							contact.setApartment(contactDTO.getApartment());
							contact.setCountryName(contactDTO.getCountryName());
							contact.setStateName(contactDTO.getStateName());
							contact.setCityName(contactDTO.getCityName());
							contact.setZipCode(contactDTO.getZipCode());
							contact.setUser(authStatus.getUser());
							contact.setCreatedOn(dtNow);
							contact.setUpdatedOn(dtNow);
							contact.setActive(true);
							contact.setDeleted(false);
							finalContacts.add(contact);
						}
					}
					List<UserContact> savedUserContacts = userContactRepository.saveAll(finalContacts);
					if (!savedUserContacts.isEmpty()) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.CONTACT_UPLOAD__SUCCESS,
								"User contact");
						status.setContacts(savedUserContacts);
						logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.ADD, dtNow,
								dtNow);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.CONTACT_UPLOAD_FAILURE, "");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add user contact");
		}

		return status;
	}

	@Override
	public DesireStatus networks(String authToken, DesireSearchRequest request) {
		LOGGER.info("UserService >> networks called!");
		DesireStatus status = new DesireStatus();
		List<UserNetwork> networks = null;
		Page<UserNetwork> propPage = null;
		int pageNumber = 0, pageSize = 1;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = (pageNumber > 0) ? pageNumber - 1 : 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					propPage = userNetworkRepository.findAllASC(authStatus.getUser(), pageable);
				} else {
					propPage = userNetworkRepository.findAllDESC(authStatus.getUser(), pageable);
				}

				networks = propPage.getContent();
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "User network");
				status.setNetworks(networks);
				status.setTotalPage(propPage.getTotalPages());
				status.setTotalRecord(propPage.getTotalElements());
				logService.createLog(authStatus.getUser(), LogConstants.USER_NETWORK, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List user network");
		}
		return status;
	}

	@Override
	public DesireStatus viewNetwork(String authToken, long networkId) {
		LOGGER.info("UserService >> viewUserNetwork called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (networkId > 0) {
					Optional<UserNetwork> foundUserNetwork = userNetworkRepository.findByObjectId(networkId);
					if (foundUserNetwork.isPresent()) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS,
								"User network");
						status.setNetwork(foundUserNetwork.get());
						logService.createLog(authStatus.getUser(), LogConstants.USER_NETWORK, LogConstants.DETAIL, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE,
								"User network");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"View user network");
		}
		return status;
	}

	@Override
	public DesireStatus addNetwork(String authToken, UserNetwork network) {
	    LOGGER.info("UserService >> addUserNetwork called!");
	    DesireStatus status = new DesireStatus();
	    Date dtNow = new Date();
	    LOGGER.info("UserService >> addUserNetwork object received >> " + network);

	    try {
	        DesireStatus authStatus = userTokenService.getUserInfo(authToken);
	        if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
	        	 User currentUser = authStatus.getUser();
	             if (!currentUser.getUserType().getName().equalsIgnoreCase(EType.PARTNER.toString())) {
	                 // User is not a Partner, return unauthorized status
	                 status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, "Only 'Partner' can add a network", "");
	                 return status;
	             }
	            // Check if the email already exists in the User table
	            Optional<User> optUser = userRepository.findByEmailId(network.getEmailId());
	            if (optUser.isPresent()) {
	                // Email exists, send a notification email
	                User existingUser = optUser.get();
	                String subject = "Notification: You are already registered";
	                String messageBody = "Dear " + existingUser.getFirstName() + ",\n\n" +
	                                     "You were added to the network by " + authStatus.getUser().getFirstName() +
	                                     ", but it seems you are already registered in our system.\n\n" +
	                                     "Best regards,\nAll Home Desire";
	                emailService.saveEmail(existingUser.getEmailId(), messageBody, subject, "");
	                status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_EXIST,
	                        "Email address " + network.getEmailId());
	            } else {
	                // Email does not exist, proceed with adding the network and sending the signup link
	                Area area = adminService.getArea(authToken, network.getCountryName(), network.getStateName(),
	                        network.getCityName(), network.getAreaName());
	                network.setArea(area);
	                network.setUser(authStatus.getUser());
//	                network.setPartnerId(null); 
	                network.setCreatedOn(dtNow);
	                network.setUpdatedOn(dtNow);
	                network.setActive(true);
	                network.setDeleted(false);
	                UserNetwork savedUserNetwork = userNetworkRepository.save(network);
	                if (savedUserNetwork != null) {
	                    // Generate the signup link with the encrypted user ID
	                    String encryptedUserId = encryptionService.encrypt(String.valueOf(network.getId()));
	                    String signupLink = "https://services.allhomedesire.in/all/signup?ref=" + encryptedUserId;

	                    // Prepare the email content
	                    String subject = "You're Invited to Sign Up!";
	                    String messageBody = "Dear " + network.getFirstName() + ",\n\n" +
	                                         "You have been added to the network by " + authStatus.getUser().getFirstName() +
	                                         ". Please sign up using the following link:\n\n" + signupLink + "\n\n" +
	                                         "Best regards,\nAll Home Desire";

	                    // Send the signup email
	                    emailService.saveEmail(network.getEmailId(), messageBody, subject, "");

	                    // Update the status
	                    status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "User network");
	                    status.setNetwork(savedUserNetwork);
	                    logService.createLog(authStatus.getUser(), LogConstants.USER_NETWORK, LogConstants.ADD, dtNow, dtNow);
	                } else {
	                    status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "User network");
	                }
	            }
	        } else {
	            status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
	        }
	    } catch (Exception e) {
	        status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
	                "Add user network");
	    }

	    return status;
	}
//===================================================================================================================================

	@Override
	public DesireStatus editNetwork(String authToken, UserNetwork network) {
		LOGGER.info("UserService >> editUserNetwork called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (network.getId() != null && network.getId() > 0) {
					Optional<UserNetwork> optUserNetwork = userNetworkRepository.findByObjectId(network.getId());
					if (optUserNetwork.isPresent()) {
						UserNetwork foundUserNetwork = optUserNetwork.get();
						// City foundCity = getCity(authToken, network.getCountryName(),
						// network.getStateName(),
						// network.getCityName());
						// foundUserNetwork.setCity(foundCity);
						Area area = adminService.getArea(authToken, network.getCountryName(), network.getStateName(),
								network.getCityName(), network.getAreaName());
						network.setArea(area);
						foundUserNetwork.setFirstName(network.getFirstName());
						foundUserNetwork.setLastName(network.getLastName());
						foundUserNetwork.setMobileNo(network.getMobileNo());
						foundUserNetwork.setEmailId(network.getEmailId());
						foundUserNetwork.setAddress(network.getAddress());
						foundUserNetwork.setAddressOne(network.getAddressOne());
						foundUserNetwork.setStreet(network.getStreet());
						foundUserNetwork.setApartment(network.getApartment());
						foundUserNetwork.setUpdatedOn(dtNow);
						UserNetwork savedUserNetwork = userNetworkRepository.save(foundUserNetwork);
						if (savedUserNetwork != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"User network");
							status.setNetwork(savedUserNetwork);
							logService.createLog(authStatus.getUser(), LogConstants.USER_NETWORK, LogConstants.EDIT,
									null, dtNow);
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"User network");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"User network id");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"User network id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit user network");
		}
		return status;
	}

	@Override
	public DesireStatus deleteNetwork(String authToken, long networkId) {
		LOGGER.info("UserService >> deleteUserNetwork called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (networkId > 0) {
					Optional<UserNetwork> foundUserNetwork = userNetworkRepository.findByObjectId(networkId);
					if (foundUserNetwork.isPresent()) {
						userNetworkRepository.delete(foundUserNetwork.get());
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS,
								"User network");
						logService.createLog(authStatus.getUser(), LogConstants.USER_NETWORK, LogConstants.DELETE, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE,
								"User network");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"User network id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete user network");
		}
		return status;
	}

	@Override
	public DesireStatus uploadNetwork(String authToken, MultipartFile file) {
		LOGGER.info("UserService >> uploadNetwork called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("UserService >> file object recieved >> " + file.getContentType());
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {

				if (file.isEmpty() || file.getOriginalFilename().contains("..")) {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPLOAD_FAILURE, "Network file");
				} else if (!Constants.EXCEL_FILE_EXT.contains(file.getContentType())) {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.INVALID_CSV, "");
				} else {
					List<UserNetwork> finalNetworks = new ArrayList<>();
					List<NetworkDTO> networkDTOs = CsvUtils.read(NetworkDTO.class, file.getInputStream());

					for (NetworkDTO networkDTO : networkDTOs) {
						UserNetwork network = new UserNetwork();
						LOGGER.info("UserService >> networkDTO.getMobileNo() >> " + networkDTO.getMobileNo());
						LOGGER.info("UserService >> networkDTO.getEmailId() >> " + networkDTO.getEmailId());
						List<UserNetwork> lstUserNetwork = userNetworkRepository.findByPhoneOrEmail(
								authStatus.getUser(), networkDTO.getMobileNo(), networkDTO.getEmailId());
						if (lstUserNetwork.isEmpty()) {

							// City foundCity = getCity(authToken, networkDTO.getCountryName(),
							// networkDTO.getStateName(),
							// networkDTO.getCityName());
							// network.setCity(foundCity);
							Area area = adminService.getArea(authToken, networkDTO.getCountryName(),
									networkDTO.getStateName(), networkDTO.getCityName(), networkDTO.getAreaName());
							network.setArea(area);
							network.setFirstName(networkDTO.getFirstName());
							network.setLastName(networkDTO.getLastName());
							network.setMobileNo(networkDTO.getMobileNo());
							network.setEmailId(networkDTO.getEmailId());
							network.setAddress(networkDTO.getAddress());
							network.setAddressOne(networkDTO.getAddressOne());
							network.setStreet(networkDTO.getStreet());
							network.setApartment(networkDTO.getApartment());
							network.setCountryName(networkDTO.getCountryName());
							network.setStateName(networkDTO.getStateName());
							network.setCityName(networkDTO.getCityName());
							network.setZipCode(networkDTO.getZipCode());
							network.setUser(authStatus.getUser());
							network.setCreatedOn(dtNow);
							network.setUpdatedOn(dtNow);
							network.setActive(true);
							network.setDeleted(false);
							finalNetworks.add(network);
						}
					}
					List<UserNetwork> savedUserNetwork = userNetworkRepository.saveAll(finalNetworks);
					if (!savedUserNetwork.isEmpty()) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.NETWORK_UPLOAD__SUCCESS,
								"User network");
						status.setNetworks(savedUserNetwork);
						logService.createLog(authStatus.getUser(), LogConstants.USER_NETWORK, LogConstants.ADD, dtNow,
								dtNow);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.NETWORK_UPLOAD_FAILURE, "");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Upload user network");
		}

		return status;
	}

	@SuppressWarnings("unused")
	private City getCity(String authToken, String countryName, String stateName, String cityName) {
		Country country = null;
		State state = null;
		City city = null;

		countryName = (countryName != null) ? countryName : "NONE";
		stateName = (stateName != null) ? stateName : "NONE";
		cityName = (cityName != null) ? cityName : "NONE";

		Optional<Country> optCountry = countryRepository.findByName(countryName);
		if (optCountry.isPresent()) {
			country = optCountry.get();
		} else {
			Country newCountry = new Country();
			newCountry.setName(countryName);
			newCountry.setShortCode("");
			newCountry.setCountryCode("");
			DesireStatus status = adminService.addCountry(authToken, newCountry);
			country = (status.getStatusType().equals(Constants.STATUS_SUCCESS)) ? status.getCountry() : null;
		}
		if (country != null) {
			Optional<State> optState = stateRepository.findByNameAndCountry(stateName, country.getId());
			if (optState.isPresent()) {
				state = optState.get();
			} else {
				State newState = new State();
				newState.setName(stateName);
				newState.setCountry(country);
				DesireStatus status = adminService.addState(authToken, newState);
				state = (status.getStatusType().equals(Constants.STATUS_SUCCESS)) ? status.getState() : null;
			}

			if (state != null) {
				Optional<City> optCity = cityRepository.findByNameAndState(cityName, state.getId());
				if (optCity.isPresent()) {
					city = optCity.get();
				} else {
					City newCity = new City();
					newCity.setName(cityName);
					newCity.setState(state);
					DesireStatus status = adminService.addCity(authToken, newCity);
					city = (status.getStatusType().equals(Constants.STATUS_SUCCESS)) ? status.getCity() : null;
				}
			}
		}
		return city;
	}

	@Override
	public DesireStatus follow(String authToken, long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DesireStatus followings(String authToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DesireStatus followers(String authToken) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DesireStatus documents(String authToken, DesireSearchRequest request) {
		LOGGER.info("UserService >> documents called!");
		DesireStatus status = new DesireStatus();
		List<UserDocument> documents = null;
		Page<UserDocument> propPage = null;
		int pageNumber = 0, pageSize = 1;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = (pageNumber > 0) ? pageNumber - 1 : 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					if (request.getUserId() > 0) {
						propPage = userDocumentRepository.findAllASC(request.getUserId(), pageable);
					} else {
						propPage = userDocumentRepository.findAllASC(pageable);
					}
				} else {
					if (request.getUserId() > 0) {
						propPage = userDocumentRepository.findAllDESC(request.getUserId(), pageable);
					} else {
						propPage = userDocumentRepository.findAllDESC(pageable);
					}
				}

				documents = propPage.getContent();
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "User document");
				status.setAssetUrl(siteUrl);
				status.setDocuments(documents);
				status.setTotalPage(propPage.getTotalPages());
				status.setTotalRecord(propPage.getTotalElements());
				logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List user document");
		}
		return status;
	}

	@Override
	public DesireStatus viewDocument(String authToken, long documentId) {
		LOGGER.info("UserService >> viewUserDocument called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (documentId > 0) {
					Optional<UserDocument> foundUserDocument = userDocumentRepository.findByObjectId(documentId);
					if (foundUserDocument.isPresent()) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS,
								"User document");
						status.setAssetUrl(siteUrl);
						status.setDocument(foundUserDocument.get());
						logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.DETAIL, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE,
								"User document");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"View user document");
		}
		return status;
	}

	@Override
	public DesireStatus addDocument(String authToken, long userId, String name, MultipartFile file) {
		LOGGER.info("UserService >> addUserDocument called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("UserService >> addUserDocument >> userId >> " + userId);
		LOGGER.info("UserService >> addUserDocument >> name >> " + name);
		LOGGER.info("UserService >> addUserDocument >> file >> " + file);
		LOGGER.info("UserService >> addUserDocument >> file.getContentType() >> "
				+ file.getContentType().toUpperCase().trim());
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<User> optUser = userRepository.findByObjectId(userId);
				if (optUser.isPresent()) {
					User user = optUser.get();
					List<String> fileExt = Constants.ATTACHMENT_FILE_EXT;
					if (fileExt.contains(file.getContentType().toUpperCase().trim())) {
						String fileName = Resources.getFileName(file.getOriginalFilename(), Resources.removeSpace(
								user.getId() + "-" + user.getFirstName() + "-" + Resources.formatDateForName(dtNow)));
						String subPath = Resources
								.removeSpace(user.getUserType().getName() + "-" + user.getUserType().getId());
						subPath += "/" + Resources.removeSpace(user.getId() + "-" + user.getFirstName());
						subPath += "/" + Resources.removeSpace(name);
						LOGGER.info("ProductService >> addProductImage >> fileName >> " + fileName);
						DesireStatus uploadStatus = storageService.storeUserDocument(file, fileName, subPath);
						if (uploadStatus.getStatusType().equals(Constants.STATUS_SUCCESS)) {
							UserDocument document = new UserDocument();
							document.setUser(user);
							document.setDocumentUrl(uploadStatus.getFileName());
							document.setName(name);
							document.setCreatedOn(dtNow);
							document.setUpdatedOn(dtNow);
							document.setActive(true);
							document.setDeleted(false);
							UserDocument savedUserDocument = userDocumentRepository.save(document);
							if (savedUserDocument != null) {
								status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS,
										"User document");
								status.setAssetUrl(siteUrl);
								status.setDocument(savedUserDocument);
								logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.ADD,
										dtNow, dtNow);
							} else {
								status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE,
										"User document");
							}
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPLOAD_FAILURE,
									"User document");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.INVALID_DOCUMENT,
								"User document");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "User");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add user document");
		}

		return status;
	}

	@Override
	public DesireStatus addDocument(String authToken, long userId, String name) {
		LOGGER.info("UserService >> addUserDocument called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("UserService >> addUserDocument >> userId >> " + userId);
		LOGGER.info("UserService >> addUserDocument >> name >> " + name);

		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<User> optUser = userRepository.findByObjectId(userId);
				if (optUser.isPresent()) {
					User user = optUser.get();

					UserDocument document = new UserDocument();
					document.setUser(user);
					document.setName(name);
					document.setCreatedOn(dtNow);
					document.setUpdatedOn(dtNow);
					document.setActive(true);
					document.setDeleted(false);
					UserDocument savedUserDocument = userDocumentRepository.save(document);
					if (savedUserDocument != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "User document");
						status.setAssetUrl(siteUrl);
						status.setDocument(savedUserDocument);
						logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.ADD, dtNow,
								dtNow);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "User document");
					}

				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "User");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add user document");
		}

		return status;
	}

	@Override
	public DesireStatus addDocument(long userId, String name, MultipartFile file) {
		LOGGER.info("UserService >> addUserDocument called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("UserService >> addUserDocument >> userId >> " + userId);
		LOGGER.info("UserService >> addUserDocument >> name >> " + name);
		LOGGER.info("UserService >> addUserDocument >> file >> " + file);
		LOGGER.info("UserService >> addUserDocument >> file.getContentType() >> "
				+ file.getContentType().toUpperCase().trim());
		try {

			Optional<User> optUser = userRepository.findByObjectIdToActivate(userId);
			if (optUser.isPresent()) {
				User user = optUser.get();
				List<String> fileExt = Constants.ATTACHMENT_FILE_EXT;
				if (fileExt.contains(file.getContentType().toUpperCase().trim())) {
					String fileName = Resources.getFileName(file.getOriginalFilename(), Resources.removeSpace(
							user.getId() + "-" + user.getFirstName() + "-" + Resources.formatDateForName(dtNow)));
					String subPath = Resources
							.removeSpace(user.getUserType().getName() + "-" + user.getUserType().getId());
					subPath += "/" + Resources.removeSpace(user.getId() + "-" + user.getFirstName());
					subPath += "/" + Resources.removeSpace(name);
					LOGGER.info("ProductService >> addProductImage >> fileName >> " + fileName);
					DesireStatus uploadStatus = storageService.storeUserDocument(file, fileName, subPath);
					if (uploadStatus.getStatusType().equals(Constants.STATUS_SUCCESS)) {
						UserDocument document = new UserDocument();
						document.setUser(user);
						document.setDocumentUrl(uploadStatus.getFileName());
						document.setName(name);
						document.setCreatedOn(dtNow);
						document.setUpdatedOn(dtNow);
						document.setActive(true);
						document.setDeleted(false);
						UserDocument savedUserDocument = userDocumentRepository.save(document);
						if (savedUserDocument != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS,
									"User document");
							status.setAssetUrl(siteUrl);
							status.setDocument(savedUserDocument);
							logService.createLog(user, LogConstants.USER_CONTACT, LogConstants.ADD, dtNow, dtNow);
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE,
									"User document");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPLOAD_FAILURE,
								"User document");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.INVALID_DOCUMENT, "User document");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "User");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add user document");
		}

		return status;
	}

	@Override
	public DesireStatus addDocument(long userId, String name) {
		LOGGER.info("UserService >> addUserDocument called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("UserService >> addUserDocument >> userId >> " + userId);
		LOGGER.info("UserService >> addUserDocument >> name >> " + name);

		try {

			Optional<User> optUser = userRepository.findByObjectId(userId);
			if (optUser.isPresent()) {
				User user = optUser.get();

				UserDocument document = new UserDocument();
				document.setUser(user);
				document.setName(name);
				document.setCreatedOn(dtNow);
				document.setUpdatedOn(dtNow);
				document.setActive(true);
				document.setDeleted(false);
				UserDocument savedUserDocument = userDocumentRepository.save(document);
				if (savedUserDocument != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "User document");
					status.setAssetUrl(siteUrl);
					status.setDocument(savedUserDocument);
					logService.createLog(user, LogConstants.USER_CONTACT, LogConstants.ADD, dtNow, dtNow);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "User document");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPLOAD_FAILURE, "User document");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add user document");
		}

		return status;
	}

	@Override
	public DesireStatus editDocument(String authToken, long documentId, String name, MultipartFile file) {
		LOGGER.info("UserService >> editUserDocument called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (documentId > 0) {
					Optional<UserDocument> optUserDocument = userDocumentRepository.findByObjectId(documentId);
					if (optUserDocument.isPresent()) {
						User user = authStatus.getUser();
						UserDocument foundUserDocument = optUserDocument.get();
						List<String> fileExt = Constants.ATTACHMENT_FILE_EXT;
						if (fileExt.contains(file.getContentType().toUpperCase().trim())) {
							String existingFile = foundUserDocument.getDocumentUrl();
							String fileName = Resources.getFileName(file.getOriginalFilename(),
									Resources.removeSpace(user.getId() + "-" + user.getFirstName() + "-"
											+ Resources.formatDateForName(dtNow)));
							String subPath = Resources
									.removeSpace(user.getUserType().getName() + "-" + user.getUserType().getId());
							subPath += "/" + Resources.removeSpace(user.getId() + "-" + user.getFirstName());
							subPath += "/" + Resources.removeSpace(name);
							LOGGER.info("ProductService >> addProductImage >> fileName >> " + fileName);
							DesireStatus uploadStatus = storageService.storeUserDocument(file, fileName, subPath);
							if (uploadStatus.getStatusType().equals(Constants.STATUS_SUCCESS)) {
								foundUserDocument.setDocumentUrl(uploadStatus.getFileName());
								foundUserDocument.setName(name);
								foundUserDocument.setUpdatedOn(dtNow);

								UserDocument savedUserDocument = userDocumentRepository.save(foundUserDocument);
								if (savedUserDocument != null) {
									status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
											"User document");
									status.setAssetUrl(siteUrl);
									status.setDocument(savedUserDocument);
									logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT,
											LogConstants.ADD, dtNow, dtNow);
									storageService.deleteFile(existingFile, Constants.DOCUMENT_FILE);
								} else {
									status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
											"User document");
								}
							} else {
								status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPLOAD_FAILURE,
										"User document");
							}
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.INVALID_DOCUMENT,
									"User document");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"User document id");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"User document id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit user document");
		}
		return status;
	}

	@Override
	public DesireStatus editDocument(String authToken, long documentId, String name) {
		LOGGER.info("UserService >> editUserDocument called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (documentId > 0) {
					Optional<UserDocument> optUserDocument = userDocumentRepository.findByObjectId(documentId);
					if (optUserDocument.isPresent()) {

						UserDocument foundUserDocument = optUserDocument.get();

						foundUserDocument.setName(name);
						foundUserDocument.setUpdatedOn(dtNow);

						UserDocument savedUserDocument = userDocumentRepository.save(foundUserDocument);
						if (savedUserDocument != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"User document");
							status.setAssetUrl(siteUrl);
							status.setDocument(savedUserDocument);
							logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.ADD,
									dtNow, dtNow);
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"User document");
						}

					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"User document id");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"User document id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit user document");
		}
		return status;
	}

	@Override
	public DesireStatus deleteDocument(String authToken, long documentId) {
		LOGGER.info("UserService >> deleteUserDocument called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (documentId > 0) {
					Optional<UserDocument> optDocument = userDocumentRepository.findByObjectId(documentId);
					if (optDocument.isPresent()) {
						UserDocument foundUserDocument = optDocument.get();
						String existingFile = foundUserDocument.getDocumentUrl();
						userDocumentRepository.delete(foundUserDocument);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS,
								"User document");
						logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.DELETE, null,
								null);
						storageService.deleteFile(existingFile, Constants.DOCUMENT_FILE);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE,
								"User document");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"User document id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete user document");
		}
		return status;
	}

	@Override
	public DesireStatus payments(String authToken, DesireSearchRequest request) {
		LOGGER.info("UserService >> payments called!");
		DesireStatus status = new DesireStatus();
		List<UserPayment> payments = null;
		Page<UserPayment> propPage = null;
		int pageNumber = 0, pageSize = 1;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = (pageNumber > 0) ? pageNumber - 1 : 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					if (request.getUserId() > 0) {
						propPage = userPaymentRepository.findAllASC(request.getUserId(), pageable);
					} else {
						propPage = userPaymentRepository.findAllASC(pageable);
					}
				} else {
					if (request.getUserId() > 0) {
						propPage = userPaymentRepository.findAllDESC(request.getUserId(), pageable);
					} else {
						propPage = userPaymentRepository.findAllDESC(pageable);
					}
				}

				payments = propPage.getContent();
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "User payment");
				status.setAssetUrl(siteUrl);
				status.setPayments(payments);
				status.setTotalPage(propPage.getTotalPages());
				status.setTotalRecord(propPage.getTotalElements());
				logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List user payment");
		}
		return status;
	}

	@Override
	public DesireStatus viewPayment(String authToken, long paymentId) {
		LOGGER.info("UserService >> viewUserPayment called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (paymentId > 0) {
					Optional<UserPayment> foundUserPayment = userPaymentRepository.findByObjectId(paymentId);
					if (foundUserPayment.isPresent()) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS,
								"User payment");
						status.setAssetUrl(siteUrl);
						status.setPayment(foundUserPayment.get());
						logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.DETAIL, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE,
								"User payment");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"View user payment");
		}
		return status;
	}

	@Override
	public DesireStatus addPayment(String authToken, long userId, String mode, String gateway, String provider,
			double amount, String paymentStatus, MultipartFile file) {
		LOGGER.info("UserService >> addUserPayment called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("UserService >> addUserPayment >> userId >> " + userId);
		LOGGER.info("UserService >> addUserPayment >> mode >> " + mode);
		LOGGER.info("UserService >> addUserPayment >> file >> " + file);
		LOGGER.info("UserService >> addUserPayment >> file.getContentType() >> "
				+ file.getContentType().toUpperCase().trim());
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<User> optUser = userRepository.findByObjectIdToActivate(userId);
				if (optUser.isPresent()) {
					User user = optUser.get();
					List<String> fileExt = Constants.ATTACHMENT_FILE_EXT;
					if (fileExt.contains(file.getContentType().toUpperCase().trim())) {
						String fileName = Resources.getFileName(file.getOriginalFilename(), Resources.removeSpace(
								user.getId() + "-" + user.getFirstName() + "-" + Resources.formatDateForName(dtNow)));
						String subPath = Resources
								.removeSpace(user.getUserType().getName() + "-" + user.getUserType().getId());
						subPath += "/" + Resources.removeSpace(user.getId() + "-" + user.getFirstName());
						subPath += "/" + Resources.removeSpace((mode != null) ? mode : "UNKNOWN");
						LOGGER.info("ProductService >> addProductImage >> fileName >> " + fileName);
						DesireStatus uploadStatus = storageService.storeUserPayment(file, fileName, subPath);
						if (uploadStatus.getStatusType().equals(Constants.STATUS_SUCCESS)) {
							UserPayment payment = new UserPayment();
							payment.setUser(user);
							payment.setDocumentUrl(uploadStatus.getFileName());
							payment.setMode(mode);
							payment.setGateway(gateway);
							payment.setProvider(provider);
							payment.setAmount(amount);
							payment.setStatus(paymentStatus);
							payment.setCreatedOn(dtNow);
							payment.setUpdatedOn(dtNow);
							payment.setActive(true);
							payment.setDeleted(false);
							UserPayment savedUserPayment = userPaymentRepository.save(payment);
							if (savedUserPayment != null) {
								status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS,
										"User payment");
								status.setAssetUrl(siteUrl);
								status.setPayment(savedUserPayment);
								logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.ADD,
										dtNow, dtNow);
							} else {
								status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE,
										"User payment");
							}
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPLOAD_FAILURE,
									"User payment");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.INVALID_DOCUMENT,
								"User payment");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "User");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add user payment");
		}

		return status;
	}

	@Override
	public DesireStatus addPayment(String authToken, long userId, String mode, String gateway, String provider,
			double amount, String paymentStatus) {
		LOGGER.info("UserService >> addUserPayment called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("UserService >> addUserPayment >> userId >> " + userId);
		LOGGER.info("UserService >> addUserPayment >> mode >> " + mode);

		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<User> optUser = userRepository.findByObjectId(userId);
				if (optUser.isPresent()) {
					User user = optUser.get();

					UserPayment payment = new UserPayment();
					payment.setUser(user);
					payment.setMode(mode);
					payment.setGateway(gateway);
					payment.setProvider(provider);
					payment.setAmount(amount);
					payment.setStatus(paymentStatus);
					payment.setCreatedOn(dtNow);
					payment.setUpdatedOn(dtNow);
					payment.setActive(true);
					payment.setDeleted(false);
					UserPayment savedUserPayment = userPaymentRepository.save(payment);
					if (savedUserPayment != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "User payment");
						status.setAssetUrl(siteUrl);
						status.setPayment(savedUserPayment);
						logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.ADD, dtNow,
								dtNow);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "User payment");
					}

				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "User");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add user payment");
		}

		return status;
	}

	@Override
	public DesireStatus addPayment(long userId, String mode, String gateway, String provider, double amount,
			String paymentStatus, MultipartFile file) {
		LOGGER.info("UserService >> addUserPayment called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("UserService >> addUserPayment >> userId >> " + userId);
		LOGGER.info("UserService >> addUserPayment >> mode >> " + mode);
		LOGGER.info("UserService >> addUserPayment >> file >> " + file);
		LOGGER.info("UserService >> addUserPayment >> file.getContentType() >> "
				+ file.getContentType().toUpperCase().trim());
		try {

			Optional<User> optUser = userRepository.findByObjectIdToActivate(userId);
			if (optUser.isPresent()) {
				User user = optUser.get();
				List<String> fileExt = Constants.ATTACHMENT_FILE_EXT;
				if (fileExt.contains(file.getContentType().toUpperCase().trim())) {
					String fileName = Resources.getFileName(file.getOriginalFilename(), Resources.removeSpace(
							user.getId() + "-" + user.getFirstName() + "-" + Resources.formatDateForName(dtNow)));
					String subPath = Resources
							.removeSpace(user.getUserType().getName() + "-" + user.getUserType().getId());
					subPath += "/" + Resources.removeSpace(user.getId() + "-" + user.getFirstName());
					subPath += "/" + Resources.removeSpace((mode != null) ? mode : "UNKNOWN");
					LOGGER.info("ProductService >> addProductImage >> fileName >> " + fileName);
					DesireStatus uploadStatus = storageService.storeUserPayment(file, fileName, subPath);
					if (uploadStatus.getStatusType().equals(Constants.STATUS_SUCCESS)) {
						UserPayment payment = new UserPayment();
						payment.setUser(user);
						payment.setDocumentUrl(uploadStatus.getFileName());
						payment.setMode(mode);
						payment.setGateway(gateway);
						payment.setProvider(provider);
						payment.setAmount(amount);
						payment.setStatus(paymentStatus);
						payment.setCreatedOn(dtNow);
						payment.setUpdatedOn(dtNow);
						payment.setActive(true);
						payment.setDeleted(false);
						UserPayment savedUserPayment = userPaymentRepository.save(payment);
						if (savedUserPayment != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS,
									"User payment");
							status.setAssetUrl(siteUrl);
							status.setPayment(savedUserPayment);
							logService.createLog(user, LogConstants.USER_CONTACT, LogConstants.ADD, dtNow, dtNow);
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE,
									"User payment");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPLOAD_FAILURE,
								"User payment");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.INVALID_DOCUMENT, "User payment");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "User");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add user payment");
		}

		return status;
	}

	@Override
	public DesireStatus addPayment(long userId, String mode, String gateway, String provider, double amount,
			String paymentStatus) {
		LOGGER.info("UserService >> addUserPayment called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("UserService >> addUserPayment >> userId >> " + userId);
		LOGGER.info("UserService >> addUserPayment >> mode >> " + mode);

		try {

			Optional<User> optUser = userRepository.findByObjectIdToActivate(userId);
			if (optUser.isPresent()) {
				User user = optUser.get();

				UserPayment payment = new UserPayment();
				payment.setUser(user);
				payment.setMode(mode);
				payment.setGateway(gateway);
				payment.setProvider(provider);
				payment.setAmount(amount);
				payment.setStatus(paymentStatus);
				payment.setCreatedOn(dtNow);
				payment.setUpdatedOn(dtNow);
				payment.setActive(true);
				payment.setDeleted(false);
				UserPayment savedUserPayment = userPaymentRepository.save(payment);
				if (savedUserPayment != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "User payment");
					status.setAssetUrl(siteUrl);
					status.setPayment(savedUserPayment);
					logService.createLog(user, LogConstants.USER_CONTACT, LogConstants.ADD, dtNow, dtNow);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "User payment");
				}

			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "User");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add user payment");
		}

		return status;
	}

	@Override
	public DesireStatus editPayment(String authToken, long paymentId, String mode, String gateway, String provider,
			double amount, String paymentStatus, MultipartFile file) {
		LOGGER.info("UserService >> editUserPayment called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (paymentId > 0) {
					Optional<UserPayment> optUserPayment = userPaymentRepository.findByObjectId(paymentId);
					if (optUserPayment.isPresent()) {
						User user = authStatus.getUser();
						UserPayment foundUserPayment = optUserPayment.get();
						List<String> fileExt = Constants.ATTACHMENT_FILE_EXT;
						if (fileExt.contains(file.getContentType().toUpperCase().trim())) {
							String existingFile = foundUserPayment.getDocumentUrl();
							String fileName = Resources.getFileName(file.getOriginalFilename(),
									Resources.removeSpace(user.getId() + "-" + user.getFirstName() + "-"
											+ Resources.formatDateForName(dtNow)));
							String subPath = Resources
									.removeSpace(user.getUserType().getName() + "-" + user.getUserType().getId());
							subPath += "/" + Resources.removeSpace(user.getId() + "-" + user.getFirstName());
							subPath += "/" + Resources.removeSpace((mode != null) ? mode : "UNKNOWN");
							LOGGER.info("ProductService >> addProductImage >> fileName >> " + fileName);
							DesireStatus uploadStatus = storageService.storeUserPayment(file, fileName, subPath);
							if (uploadStatus.getStatusType().equals(Constants.STATUS_SUCCESS)) {
								foundUserPayment.setDocumentUrl(uploadStatus.getFileName());
								foundUserPayment.setMode(mode);
								foundUserPayment.setGateway(gateway);
								foundUserPayment.setProvider(provider);
								foundUserPayment.setAmount(amount);
								foundUserPayment.setStatus(paymentStatus);
								foundUserPayment.setUpdatedOn(dtNow);

								UserPayment savedUserPayment = userPaymentRepository.save(foundUserPayment);
								if (savedUserPayment != null) {
									status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
											"User payment");
									status.setAssetUrl(siteUrl);
									status.setPayment(savedUserPayment);
									logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT,
											LogConstants.ADD, dtNow, dtNow);
									storageService.deleteFile(existingFile, Constants.PAYMENT_FILE);
								} else {
									status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
											"User payment");
								}
							} else {
								status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPLOAD_FAILURE,
										"User payment");
							}
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.INVALID_DOCUMENT,
									"User payment");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"User payment id");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"User payment id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit user payment");
		}
		return status;
	}

	@Override
	public DesireStatus editPayment(String authToken, long paymentId, String mode, String gateway, String provider,
			double amount, String paymentStatus) {
		LOGGER.info("UserService >> editUserPayment called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (paymentId > 0) {
					Optional<UserPayment> optUserPayment = userPaymentRepository.findByObjectId(paymentId);
					if (optUserPayment.isPresent()) {

						UserPayment foundUserPayment = optUserPayment.get();
						foundUserPayment.setMode(mode);
						foundUserPayment.setGateway(gateway);
						foundUserPayment.setProvider(provider);
						foundUserPayment.setAmount(amount);
						foundUserPayment.setStatus(paymentStatus);
						foundUserPayment.setUpdatedOn(dtNow);

						UserPayment savedUserPayment = userPaymentRepository.save(foundUserPayment);
						if (savedUserPayment != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"User payment");
							status.setAssetUrl(siteUrl);
							status.setPayment(savedUserPayment);
							logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.ADD,
									dtNow, dtNow);

						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"User payment");
						}

					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"User payment id");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"User payment id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit user payment");
		}
		return status;
	}

	@Override
	public DesireStatus deletePayment(String authToken, long paymentId) {
		LOGGER.info("UserService >> deleteUserPayment called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (paymentId > 0) {
					Optional<UserPayment> optPayment = userPaymentRepository.findByObjectId(paymentId);
					if (optPayment.isPresent()) {
						UserPayment foundUserPayment = optPayment.get();
						String existingFile = foundUserPayment.getDocumentUrl();
						userPaymentRepository.delete(foundUserPayment);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS,
								"User payment");
						logService.createLog(authStatus.getUser(), LogConstants.USER_CONTACT, LogConstants.DELETE, null,
								null);
						storageService.deleteFile(existingFile, Constants.PAYMENT_FILE);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE,
								"User payment");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"User payment id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete user payment");
		}
		return status;
	}

}
