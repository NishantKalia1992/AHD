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

import com.all.homedesire.common.Constants;
import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.LogConstants;
import com.all.homedesire.common.PropertyInfo;
import com.all.homedesire.common.PropertyMapper;
import com.all.homedesire.common.Resources;
import com.all.homedesire.entities.Area;
import com.all.homedesire.entities.City;
import com.all.homedesire.entities.Comment;
import com.all.homedesire.entities.Favorite;
import com.all.homedesire.entities.Lead;
import com.all.homedesire.entities.Property;
import com.all.homedesire.entities.PropertyAttachment;
import com.all.homedesire.entities.PropertyFeature;
import com.all.homedesire.entities.PropertyImage;
import com.all.homedesire.entities.PropertyPurpose;
import com.all.homedesire.entities.PropertyStatus;
import com.all.homedesire.entities.PropertyStructure;
import com.all.homedesire.entities.PropertySubType;
import com.all.homedesire.entities.PropertyType;
import com.all.homedesire.entities.PropertyVideo;
import com.all.homedesire.entities.State;
import com.all.homedesire.entities.User;
import com.all.homedesire.entities.UserContact;
import com.all.homedesire.enums.EType;
import com.all.homedesire.repository.AreaRepository;
import com.all.homedesire.repository.CommentRepository;
import com.all.homedesire.repository.FavoriteRepository;
import com.all.homedesire.repository.LeadRepository;
import com.all.homedesire.repository.PropertyAttachmentsRepository;
import com.all.homedesire.repository.PropertyFeaturesRepository;
import com.all.homedesire.repository.PropertyImagesRepository;
import com.all.homedesire.repository.PropertyPurposeRepository;
import com.all.homedesire.repository.PropertyRepository;
import com.all.homedesire.repository.PropertyStatusRepository;
import com.all.homedesire.repository.PropertyStructureRepository;
import com.all.homedesire.repository.PropertySubTypeRepository;
import com.all.homedesire.repository.PropertyTypeRepository;
import com.all.homedesire.repository.PropertyVideoRepository;
import com.all.homedesire.repository.UserContactRepository;
import com.all.homedesire.repository.UserRepository;
import com.all.homedesire.resources.dto.ChangeStateRequest;
import com.all.homedesire.resources.dto.DesireSearchRequest;
import com.all.homedesire.resources.dto.LeadRequest;
import com.all.homedesire.resources.dto.property.AllRequest;
import com.all.homedesire.resources.dto.property.CommonLeadRequest;
import com.all.homedesire.resources.dto.property.MySpaceRequest;
import com.all.homedesire.resources.dto.property.PreferredRequest;
import com.all.homedesire.resources.dto.property.PropertyDetail;
import com.all.homedesire.resources.dto.property.Purpose;
import com.all.homedesire.resources.dto.property.SubType;
import com.all.homedesire.resources.dto.property.Type;
import com.all.homedesire.security.service.UserTokenService;
import com.all.homedesire.service.AdminService;
import com.all.homedesire.service.CommonService;
import com.all.homedesire.service.LogService;
import com.all.homedesire.service.PropertyService;

@Service
public class PropertyServiceImpl implements PropertyService {
	Logger LOGGER = LoggerFactory.getLogger(PropertyServiceImpl.class);

	@Value("${site.url}")
	private String assetUrl;
	@Value("${image.base-dir}")
	private String imageBaseDir;
	@Value("${upload.work-dir}")
	private String uploadWorkdir;

	@Autowired
	UserTokenService userTokenService;
	@Autowired
	LogService logService;
	@Autowired
	CommonService commonService;

	@Autowired
	PropertyRepository propertyRepository;
	@Autowired
	PropertyTypeRepository propertyTypeRepository;
	@Autowired
	PropertySubTypeRepository propertySubTypeRepository;
	@Autowired
	PropertyPurposeRepository propertyPurposeRepository;
	@Autowired
	PropertyStatusRepository propertyStatusRepository;
	@Autowired
	PropertyStructureRepository propertyStructureRepository;
	@Autowired
	PropertyFeaturesRepository propertyFeaturesRepository;
	@Autowired
	PropertyImagesRepository propertyImagesRepository;
	@Autowired
	PropertyAttachmentsRepository propertyAttachmentsRepository;
	@Autowired
	PropertyVideoRepository propertyVideoRepository;
	@Autowired
	LeadRepository leadRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	AreaRepository areaRepository;
	@Autowired
	CommentRepository commentRepository;
	@Autowired
	FavoriteRepository favoriteRepository;
	@Autowired
	UserContactRepository userContactRepository;

	@Autowired
	AdminService adminService;
	

	@Override
	public DesireStatus leads(String authToken, DesireSearchRequest request) {
		LOGGER.info("leads called!");
		DesireStatus status = new DesireStatus();
		List<Lead> leads = null;
		Page<Lead> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = leadRepository.findAllASC(pageable);
				} else {
					page = leadRepository.findAllDESC(pageable);
				}
				leads = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Lead");
				status.setLeads(leads);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.DESIRED_REQUEST, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Lead");
		}
		return status;
	}

	@Override
	public DesireStatus leadList(CommonLeadRequest request) {
		LOGGER.info("PropertyService >> leadList called!");
		DesireStatus status = new DesireStatus();
		List<PropertyDetail> properties = null;
		Page<Lead> page = null;
		int pageNumber = 0, pageSize = 0;
		try {

			pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
			pageNumber = pageNumber - 1;
			pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
			Pageable pageable = PageRequest.of(pageNumber, pageSize);
			String searchText = (request.getSearchText() != null && !request.getSearchText().equals(""))
					? request.getSearchText()
					: "";
			String address = (request.getAddress() != null && !request.getAddress().equals("")) ? request.getAddress()
					: "";
			String leadStatus = (request.getStatus() != null && !request.getStatus().equals("")) ? request.getStatus()
					: "";
			String type = (request.getType() != null && !request.getType().equals("")) ? request.getType() : "";
			double price = (request.getPrice() > 0) ? request.getPrice() : 0;

			page = commonService.findAllPostByTextAddressStatusTypePrice(searchText, address, leadStatus, type, price,
					request.getOrderBy(), pageable);

			properties = PropertyMapper.getMappedProperties(page.getContent(), null);

			status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Leads");
			status.setAssetUrl(assetUrl);
			status.setProperties(properties);
			status.setTotalRecord(page.getTotalElements());
			status.setTotalPage(page.getTotalPages());

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property");
		}
		return status;
	}

	@Override
	public DesireStatus leadById(String authToken, long leadId) {
		LOGGER.info("leadById called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (leadId > 0) {
					Optional<Lead> foundLead = leadRepository.findByObjectId(leadId);
					if (foundLead.isPresent()) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Lead");
						status.setLead(foundLead.get());
						logService.createLog(authStatus.getUser(), LogConstants.DESIRED_REQUEST, LogConstants.DETAIL,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Lead");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Lead id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Lead");
		}
		return status;
	}

	@Override
	public DesireStatus addLead(String authToken, Lead lead) {
		LOGGER.info("addLead called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addLead object recieved >> " + lead);
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				User user = authStatus.getUser();
				Optional<PropertyPurpose> optPurpose = propertyPurposeRepository.findByObjectId(lead.getPurposeId());
				if (optPurpose.isPresent()) {
					PropertyPurpose propertyPurpose = optPurpose.get();
					Optional<PropertySubType> optType = propertySubTypeRepository.findByObjectId(lead.getSubTypeId());
					if (optType.isPresent()) {
						PropertySubType propertySubType = optType.get();
						if (user.getUserType().geteType().equals(EType.PARTNER)) {
							lead.setPartner(user);
						} else {
							// Search for User with preferred location
						}
						Area area = adminService.getArea(authToken, lead.getCountryName(), lead.getStateName(),
								lead.getCityName(), lead.getAreaName());
						lead.setCustomer(user);
						lead.setPropertyPurpose(propertyPurpose);
						lead.setPropertySubType(propertySubType);
						lead.setArea(area);
						lead.setCreatedOn(dtNow);
						lead.setUpdatedOn(dtNow);
						lead.setActive(true);
						lead.setDeleted(false);
						Lead savedLead = leadRepository.save(lead);
						if (savedLead != null) {
							String customerName = savedLead.getCustomerName();
							String title = (savedLead.getTitle() != null) ? savedLead.getTitle() : "";
							List<PropertyImage> images = (lead.getImages() != null) ? lead.getImages()
									: new ArrayList<>();
							String newPropAssetPath = Resources.getPropertyFilePath(
									Resources.getRandomNumber() + Resources.removeSpace(customerName),
									Resources.getRandomNumber() + Resources.removeSpace(title), "images");

							for (PropertyImage propertyImage : images) {
								LOGGER.info(
										"addProperty propertyImage.getImagePath() >> " + propertyImage.getImagePath());
								Boolean moveStatus = Resources.movePropertyFile(
										uploadWorkdir + "/" + Constants.FILE_IMAGE + "/",
										uploadWorkdir + "/" + Constants.FILE_IMAGE + "/" + newPropAssetPath,
										propertyImage.getImagePath());
								if (moveStatus) {
									propertyImage.setLead(savedLead);
									propertyImage.setName(propertyImage.getImagePath());
									propertyImage.setDescription("");
									propertyImage.setImageUrl(imageBaseDir + "/" + Constants.FILE_IMAGE + "/"
											+ newPropAssetPath + "/" + propertyImage.getImagePath());
									propertyImage.setImagePath(Constants.FILE_IMAGE + "/" + newPropAssetPath);
								}
								propertyImage.setActive(true);
								propertyImage.setDeleted(false);
								propertyImage.setCreatedOn(dtNow);
								propertyImage.setUpdatedOn(dtNow);
								DesireStatus dsImage = addPropertyImage(authToken, propertyImage);
								LOGGER.info("addProperty dsImage.getStatusType() >> " + dsImage.getStatusType());
								LOGGER.info("addProperty dsImage.getText() >> " + dsImage.getText());
							}
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Lead");
							status.setLead(savedLead);
							logService.createLog(authStatus.getUser(), LogConstants.DESIRED_REQUEST, LogConstants.ADD,
									savedLead.getCreatedOn(), savedLead.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Lead");
						}

					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Lead type");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Lead purpose");
				}

			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Lead");
		}

		return status;
	}

	@Override
	public DesireStatus addLead(Lead lead) {
		LOGGER.info("addLead called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addLead object recieved >> " + lead);
		try {

			Optional<PropertyPurpose> optPurpose = propertyPurposeRepository.findByObjectId(lead.getPurposeId());
			if (optPurpose.isPresent()) {
				PropertyPurpose propertyPurpose = optPurpose.get();
				Optional<PropertySubType> optType = propertySubTypeRepository.findByObjectId(lead.getSubTypeId());
				if (optType.isPresent()) {
					PropertySubType propertySubType = optType.get();

					// Search for User with preferred location
					/**
					 * Define here
					 */
					Area area = adminService.getArea(lead.getCountryName(), lead.getStateName(), lead.getCityName(),
							lead.getAreaName());

					lead.setPropertyPurpose(propertyPurpose);
					lead.setPropertySubType(propertySubType);
					lead.setArea(area);
					lead.setCreatedOn(dtNow);
					lead.setUpdatedOn(dtNow);
					lead.setActive(true);
					lead.setDeleted(false);
					Lead savedLead = leadRepository.save(lead);
					if (savedLead != null) {
						String customerName = savedLead.getCustomerName();
						String title = (savedLead.getTitle() != null) ? savedLead.getTitle() : "";
						List<PropertyImage> images = (lead.getImages() != null) ? lead.getImages() : new ArrayList<>();
						String newPropAssetPath = Resources.getPropertyFilePath(
								Resources.getRandomNumber() + Resources.removeSpace(customerName),
								Resources.getRandomNumber() + Resources.removeSpace(title), "images");

						for (PropertyImage propertyImage : images) {
							LOGGER.info("addProperty propertyImage.getImagePath() >> " + propertyImage.getImagePath());
							Boolean moveStatus = Resources.movePropertyFile(
									uploadWorkdir + "/" + Constants.FILE_IMAGE + "/",
									uploadWorkdir + "/" + Constants.FILE_IMAGE + "/" + newPropAssetPath,
									propertyImage.getImagePath());
							if (moveStatus) {
								propertyImage.setLead(savedLead);
								propertyImage.setName(propertyImage.getImagePath());
								propertyImage.setDescription("");
								propertyImage.setImageUrl(imageBaseDir + "/" + Constants.FILE_IMAGE + "/"
										+ newPropAssetPath + "/" + propertyImage.getImagePath());
								propertyImage.setImagePath(Constants.FILE_IMAGE + "/" + newPropAssetPath);
							}
							propertyImage.setActive(true);
							propertyImage.setDeleted(false);
							propertyImage.setCreatedOn(dtNow);
							propertyImage.setUpdatedOn(dtNow);
							DesireStatus dsImage = addPropertyImage(propertyImage);
							LOGGER.info("addProperty dsImage.getStatusType() >> " + dsImage.getStatusType());
							LOGGER.info("addProperty dsImage.getText() >> " + dsImage.getText());
						}
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Lead");
						status.setLead(savedLead);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Lead");
					}

				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Lead type");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Lead purpose");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Lead");
		}

		return status;
	}

	@Override
	public DesireStatus editLead(String authToken, Lead lead) {
		LOGGER.info("editLead called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				User user = authStatus.getUser();
				if (lead.getId() != null && lead.getId() > 0) {
					Optional<Lead> optLead = leadRepository.findByObjectId(lead.getId());
					if (optLead.isPresent()) {
						Lead foundLead = optLead.get();
						Optional<PropertyPurpose> optPurpose = propertyPurposeRepository
								.findByObjectId(lead.getPurposeId());
						if (optPurpose.isPresent()) {
							PropertyPurpose propertyPurpose = optPurpose.get();
							Optional<PropertySubType> optType = propertySubTypeRepository
									.findByObjectId(lead.getSubTypeId());
							if (optType.isPresent()) {
								PropertySubType propertySubType = optType.get();
								if (user.getUserType().geteType().equals(EType.PARTNER)) {
									lead.setPartner(user);
								} else {
									// Search for User with preferred location
								}
								Area area = adminService.getArea(authToken, lead.getCountryName(), lead.getStateName(),
										lead.getCityName(), lead.getAreaName());
								foundLead.setCustomer(user);
								foundLead.setPropertyPurpose(propertyPurpose);
								foundLead.setPropertySubType(propertySubType);
								foundLead.setArea(area);
								foundLead.setTitle(lead.getTitle());
								foundLead.setDescription(lead.getDescription());
								foundLead.setFeaturedProperty(lead.isFeaturedProperty());
								foundLead.setPrice(lead.getPrice());
								foundLead.setUpdatedOn(dtNow);
								Lead savedLead = leadRepository.save(foundLead);
								if (savedLead != null) {
									status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
											"Lead");
									status.setLead(savedLead);
									logService.createLog(authStatus.getUser(), LogConstants.DESIRED_REQUEST,
											LogConstants.EDIT, null, savedLead.getUpdatedOn());
								} else {
									status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
											"Lead");
								}
							} else {
								status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
										"Lead type");
							}
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
									"Lead purpose");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Lead id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit Lead");
		}
		return status;
	}

	@Override
	public DesireStatus deleteLead(String authToken, long leadId) {
		LOGGER.info("deleteLead called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (leadId > 0) {
					Optional<Lead> optLead = leadRepository.findByObjectId(leadId);
					if (optLead.isPresent()) {
						Lead foundLead = optLead.get();
						List<PropertyImage> propertyImages = propertyImagesRepository.listByLead(foundLead.getId());
						for (PropertyImage propertyImage : propertyImages) {
							propertyImagesRepository.delete(propertyImage);
						}
						leadRepository.delete(foundLead);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "Lead");
						logService.createLog(authStatus.getUser(), LogConstants.DESIRED_REQUEST, LogConstants.DELETE,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "Lead");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Lead id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete Lead");
		}
		return status;
	}

	@Override
	public DesireStatus changeLeadState(String authToken, ChangeStateRequest request) {
		LOGGER.info("changeLeadState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (request.getId() > 0) {
					Optional<Lead> optObj = leadRepository.findByObjectIdToActivate(request.getId());
					if (optObj.isPresent()) {
						Lead lead = optObj.get();
						lead.setActive(request.isActive());
						Lead savedRequest = leadRepository.save(lead);
						if (savedRequest != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "Lead");
							status.setLead(savedRequest);
							logService.createLog(authStatus.getUser(), LogConstants.DESIRED_REQUEST,
									LogConstants.ACTIVE, null, savedRequest.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "Lead");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Lead");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Lead id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Change desired request active state");
		}
		return status;
	}

	@Override
	public DesireStatus properties(String authToken, DesireSearchRequest request) {
		LOGGER.info("properties called!");
		DesireStatus status = new DesireStatus();
		List<PropertyDetail> properties = null;
		Page<Lead> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = leadRepository.findAllPropertyASC(pageable);
				} else {
					page = leadRepository.findAllPropertyDESC(pageable);
				}
				properties = PropertyMapper.getMappedProperties(page.getContent(), authStatus.getUser());
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property");
				status.setAssetUrl(assetUrl);
				status.setProperties(properties);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property");
		}
		return status;
	}

	@Override
	public DesireStatus propertiesByUser(String authToken, long userId, DesireSearchRequest request) {
		LOGGER.info("properties called!");
		DesireStatus status = new DesireStatus();
		List<PropertyDetail> properties = null;
		Page<Lead> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = leadRepository.findAllPropertyByCustomerASC(userId, pageable);
				} else {
					page = leadRepository.findAllPropertyByCustomerDESC(userId, pageable);
				}
				properties = PropertyMapper.getMappedProperties(page.getContent(), authStatus.getUser());

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property");
				status.setAssetUrl(assetUrl);
				status.setProperties(properties);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property");
		}
		return status;
	}

	@Override
	public DesireStatus listMySpace(String authToken, MySpaceRequest request) {
		LOGGER.info("PropertyService >> listMySpace called!");
		DesireStatus status = new DesireStatus();
		List<PropertyDetail> properties = null;
		Page<Lead> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);
				List<Property> list = null;
				if (request.getSearchType().equalsIgnoreCase("Property")) {
					if (request.getOrderBy().equalsIgnoreCase("ASC")) {
						page = leadRepository.findAllPropertyByCustomerASC(authStatus.getUser().getId(), pageable);
					} else {
						page = leadRepository.findAllPropertyByCustomerDESC(authStatus.getUser().getId(), pageable);
					}
				} else if (request.getSearchType().equalsIgnoreCase("Post")) {
					list = propertyRepository.listByObjectId(authStatus.getUser().getId());
					List<Long> listLeadId = new ArrayList<>();
					LOGGER.info("PropertyService >> list >> size >> " + list.size());
					for (Property property : list) {
						listLeadId.add(property.getLeadId());
					}
					if (listLeadId.isEmpty()) {
						listLeadId.add(Long.parseLong("0"));
					}
					if (request.getOrderBy().equalsIgnoreCase("ASC")) {
						page = leadRepository.findAllLeadASC(listLeadId, pageable);
					} else {
						page = leadRepository.findAllLeadDESC(listLeadId, pageable);
					}
				} else if (request.getSearchType().equalsIgnoreCase("Follow")) {
					List<Favorite> favorites = favoriteRepository.findAllByUser(authStatus.getUser());
					LOGGER.info("PropertyService >> favorites >> size >> " + favorites.size());
					// list = new ArrayList<>();
					List<Long> listLeadId = new ArrayList<>();
					for (Favorite favorite : favorites) {
						listLeadId.add(favorite.getLead().getId());
					}
					// LOGGER.info("PropertyService >> list >> size >> " + list.size());
					// if (list.isEmpty()) {
					// Property prop = new Property();
					// prop.setId(Long.parseLong("0"));
					// prop.setLeadId(0);
					// list.add(prop);
					// }
					// if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					// page = leadRepository.findAllByPropertiesASC(list, pageable);
					// } else {
					// page = leadRepository.findAllByPropertiesDESC(list, pageable);
					// }
					if (listLeadId.isEmpty()) {
						listLeadId.add(Long.parseLong("0"));
					}
					if (request.getOrderBy().equalsIgnoreCase("ASC")) {
						page = leadRepository.findAllFollowLeadASC(listLeadId, pageable);
					} else {
						page = leadRepository.findAllFollowLeadDESC(listLeadId, pageable);
					}

				} else {
					if (request.getOrderBy().equalsIgnoreCase("ASC")) {
						page = leadRepository.findAllByCustomerASC(authStatus.getUser().getId(), pageable);
					} else {
						page = leadRepository.findAllByCustomerDESC(authStatus.getUser().getId(), pageable);
					}
				}

				properties = PropertyMapper.getMappedProperties(page.getContent(), authStatus.getUser());

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property");
				status.setAssetUrl(assetUrl);
				status.setProperties(properties);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property");
		}
		return status;
	}


	@Override
	public DesireStatus listAll(String authToken, AllRequest request) {
		LOGGER.info("PropertyService >> listAll called!");
		DesireStatus status = new DesireStatus();
		List<PropertyDetail> properties = null;
		Page<Lead> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);
				String state = (request.getState() != null && !request.getState().equals("")) ? request.getState() : "";
				String city = (request.getCity() != null && !request.getCity().equals("")) ? request.getCity() : "";
				String area = (request.getArea() != null && !request.getArea().equals("")) ? request.getArea() : "";

				page = commonService.findAllPostByStateCityArea(state, city, area, request.getOrderBy(), pageable);

				properties = PropertyMapper.getMappedProperties(page.getContent(), authStatus.getUser());

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property");
				status.setAssetUrl(assetUrl);
				status.setProperties(properties);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property");
		}
		return status;
	}

	@Override
	public DesireStatus listPreferred(String authToken, PreferredRequest request) {
		LOGGER.info("PropertyService >> listPreferred called!");
		DesireStatus status = new DesireStatus();
		List<PropertyDetail> properties = null;
		Page<Lead> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);
				String type = (request.getType() != null && !request.getType().equals("")) ? request.getType() : "";
				String purpose = (request.getPurpose() != null && !request.getPurpose().equals(""))
						? request.getPurpose()
						: "";
				String state = (request.getState() != null && !request.getState().equals("")) ? request.getState() : "";
				String city = (request.getCity() != null && !request.getCity().equals("")) ? request.getCity() : "";
				String area = (request.getArea() != null && !request.getArea().equals("")) ? request.getArea() : "";
				double price = (request.getPrice() > 0) ? request.getPrice() : 0;
				String keyboardSearch = (request.getKeyboardSearch() != null && !request.getKeyboardSearch().isEmpty()) ? request.getKeyboardSearch() : "";

				page = commonService.findAllPost(type, purpose, state, city, area, price, keyboardSearch, request.getOrderBy(),
						pageable);
				

				properties = PropertyMapper.getMappedProperties(page.getContent(), authStatus.getUser());

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property");
				status.setAssetUrl(assetUrl);
				status.setProperties(properties);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property");
		}
		return status;
	}

	@Override
	public DesireStatus latestProperties(String authToken, DesireSearchRequest request) {
		LOGGER.info("properties called!");
		DesireStatus status = new DesireStatus();
		List<PropertyDetail> properties = null;
		Page<Lead> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				page = leadRepository.findAllPropertyDESC(pageable);

				properties = PropertyMapper.getMappedProperties(page.getContent(), authStatus.getUser());

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property");
				status.setAssetUrl(assetUrl);
				status.setProperties(properties);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property");
		}
		return status;
	}

	@Override
	public DesireStatus latestProperties() {
		LOGGER.info("properties called!");
		DesireStatus status = new DesireStatus();
		try {
			status.setAssetUrl(assetUrl);
			List<Lead> leads = leadRepository.listLatestProperties();
			status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property");
			status.setProperties(PropertyMapper.getMappedProperties(leads, null));
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property");
		}
		return status;
	}

	@Override
	public DesireStatus viewProperty(String authToken, long propertyId) {
		LOGGER.info("viewProperty called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (propertyId > 0) {
					 LOGGER.info("Looking for property with ID: " + propertyId);
					Optional<Property> optProperty = propertyRepository.findByObjectId(propertyId);
					if (optProperty.isPresent()) {
						 LOGGER.info("Property found: " + optProperty.get());
						Property foundProperty = optProperty.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Property");

						PropertyInfo propertyInfo = (PropertyInfo) Resources.getObjectMapped(foundProperty,
								PropertyInfo.class);
						propertyInfo.setUserId(foundProperty.getLead().getCustomer().getId());
						propertyInfo.setCityName(foundProperty.getLead().getArea().getCity().getName());
						propertyInfo.setStateName(foundProperty.getLead().getArea().getCity().getState().getName());
						propertyInfo.setCountryName(
								foundProperty.getLead().getArea().getCity().getState().getCountry().getName());
						propertyInfo.setAreaName(foundProperty.getLead().getArea().getName());
						propertyInfo.setPropertyTypeId(
								foundProperty.getLead().getPropertySubType().getPropertyType().getId());
						propertyInfo.setPropertySubTypeId(foundProperty.getLead().getPropertySubType().getId());
						propertyInfo.setPropertyPurposeId(foundProperty.getLead().getPropertyPurpose().getId());
						propertyInfo.setPropertyStatusId(foundProperty.getPropertyStatus().getId());
						City city = (City) PropertyMapper.getObjectMapped(foundProperty.getLead().getArea().getCity(),
								City.class);
						propertyInfo.setCity(city);
						State state = (State) PropertyMapper
								.getObjectMapped(foundProperty.getLead().getArea().getCity().getState(), State.class);
						propertyInfo.setState(state);
						List<PropertyImage> propertyImages = propertyImagesRepository
								.listByLead(foundProperty.getLead().getId());
						List<PropertyAttachment> propertyAttachments = propertyAttachmentsRepository
								.listByProperty(propertyId);
						List<PropertyVideo> propertyVideos = propertyVideoRepository.listByProperty(propertyId);
						propertyInfo.setImages(propertyImages);
						propertyInfo.setAttachments(propertyAttachments);
						propertyInfo.setVideos(propertyVideos);
						propertyInfo.setName(foundProperty.getLead().getTitle());
						propertyInfo.setDescription(foundProperty.getLead().getDescription());
						propertyInfo.setPrice(foundProperty.getLead().getPrice());
						propertyInfo.setUser(foundProperty.getLead().getCustomer());
						// Purpose
						Purpose purpose = (Purpose) PropertyMapper
								.getObjectMapped(foundProperty.getLead().getPropertyPurpose(), Purpose.class);
						propertyInfo.setPurpose(purpose);
						// Type
						Type type = (Type) PropertyMapper.getObjectMapped(
								foundProperty.getLead().getPropertySubType().getPropertyType(), Type.class);
						propertyInfo.setType(type);
						// SubType
						SubType subType = (SubType) PropertyMapper
								.getObjectMapped(foundProperty.getLead().getPropertySubType(), SubType.class);
						propertyInfo.setSubType(subType);

						boolean isFavorite = false;
						long favoriteId = 0;
						if (authStatus.getUser() != null) {
							List<Favorite> favorites = authStatus.getUser().getFavorites();
							for (Favorite favorite : favorites) {
								if (favorite.getLead().getId() == foundProperty.getLead().getId()) {
									isFavorite = true;
									favoriteId = favorite.getId();
								}
							}
						}
						propertyInfo.setFavorite(isFavorite);
						propertyInfo.setFavoriteId(favoriteId);
						if (foundProperty.getLead() != null) {
							propertyInfo.setLeadId(foundProperty.getLead().getId());
						}
						status.setPropertyInfo(propertyInfo);
						status.setAssetUrl(assetUrl);
						// status.setProperty(foundProperty);
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY, LogConstants.DETAIL, null,
								null);
					} else {
						LOGGER.error("No property found with ID: " + propertyId);
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Property");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Property id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Property");
		}
		return status;
	}

	@Override
	public DesireStatus viewProperty(long propertyId) {
		LOGGER.info("viewProperty called!");
		DesireStatus status = new DesireStatus();
		try {
			if (propertyId > 0) {
				Optional<Property> optProperty = propertyRepository.findByObjectId(propertyId);
				if (optProperty.isPresent()) {
					Property foundProperty = optProperty.get();
					if (foundProperty.isPublished()) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Property");
						PropertyInfo propertyInfo = (PropertyInfo) Resources.getObjectMapped(foundProperty,
								PropertyInfo.class);
						List<PropertyImage> propertyImages = propertyImagesRepository
								.listByLead(foundProperty.getLead().getId());
						List<PropertyAttachment> propertyAttachments = propertyAttachmentsRepository
								.listByProperty(propertyId);
						List<PropertyVideo> propertyVideos = propertyVideoRepository.listByProperty(propertyId);
						propertyInfo.setImages(propertyImages);
						propertyInfo.setAttachments(propertyAttachments);
						propertyInfo.setVideos(propertyVideos);
						propertyInfo.setUserId(foundProperty.getLead().getCustomer().getId());
						propertyInfo.setCityName(foundProperty.getLead().getArea().getCity().getName());
						propertyInfo.setStateName(foundProperty.getLead().getArea().getCity().getState().getName());
						propertyInfo.setCountryName(
								foundProperty.getLead().getArea().getCity().getState().getCountry().getName());
						propertyInfo.setAreaName(foundProperty.getLead().getArea().getName());
						propertyInfo.setPropertyTypeId(
								foundProperty.getLead().getPropertySubType().getPropertyType().getId());
						propertyInfo.setPropertySubTypeId(foundProperty.getLead().getPropertySubType().getId());
						propertyInfo.setPropertyPurposeId(foundProperty.getLead().getPropertyPurpose().getId());
						propertyInfo.setPropertyStatusId(foundProperty.getPropertyStatus().getId());
						boolean isFavorite = false;
						long favoriteId = 0;
						propertyInfo.setFavorite(isFavorite);
						propertyInfo.setFavoriteId(favoriteId);
						if (foundProperty.getLead() != null) {
							propertyInfo.setLeadId(foundProperty.getLead().getId());
						}
						status.setPropertyInfo(propertyInfo);
						status.setAssetUrl(assetUrl);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Property");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Property");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Property id");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Property");
		}
		return status;
	}
	
	@SuppressWarnings("unused")
	@Override
	public DesireStatus addProperty(String authToken, PropertyInfo propertyInfo) {
		LOGGER.info("addProperty called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				
				User user = authStatus.getUser(); // Get the current user
				LOGGER.info("User type: " + user);
	            // Check if the user is a "customer" type
	            if (user.getUserType().getName().equals(EType.CUSTOMER.toString())) {
	            	LOGGER.info("User type called" +user.getUserType());
	                // Count the number of active leads/properties associated with this customer
	                long propertyCount = propertyRepository.countByCustomerId(user.getId());
	                LOGGER.info("Active property count for customer ID " + user.getId() + ": " + propertyCount);
	                
	                // Check if the customer has already added 5 properties
	                if (propertyCount >= 5) {
	                    LOGGER.info("Customer " + user.getId() + " has reached the property limit.");
	                    return Resources.setStatus(Constants.STATUS_FAILURE, Constants.MAX_PROPERTY_LIMIT_REACHED, "Customer can only add 5 properties.");
	                }
	            }
				
				Lead lead = null;

				Optional<Property> optProperty = propertyRepository.findByLeadId(propertyInfo.getLeadId());
				if (!optProperty.isPresent()) {
					Property property = (Property) Resources.getObjectMapped(propertyInfo, Property.class);
					property.setPublished(propertyInfo.isPublished());

					if (property.getLeadId() <= 0) {
						Lead newLead = new Lead();
						newLead.setTitle(propertyInfo.getName());
						newLead.setDescription(propertyInfo.getDescription());
						newLead.setPrice(propertyInfo.getPrice());
						newLead.setPurposeId(propertyInfo.getPropertyPurposeId());
						newLead.setTypeId(propertyInfo.getPropertyTypeId());
						newLead.setSubTypeId(propertyInfo.getPropertySubTypeId());
						newLead.setFeaturedProperty(propertyInfo.isFeaturedProperty());
						newLead.setCityName(propertyInfo.getCityName());
						newLead.setStateName(propertyInfo.getStateName());
						newLead.setCountryName(propertyInfo.getCountryName());
						newLead.setAreaName(propertyInfo.getAreaName());
						DesireStatus dsLead = addLead(authToken, newLead);
						LOGGER.info("addProperty >> dsLead.getStatusType() >> " + dsLead.getStatusType());
						if (dsLead.getStatusType().equals(Constants.STATUS_SUCCESS)) {
							propertyInfo.setLeadId(dsLead.getLead().getId());
							lead = dsLead.getLead();
							String customerName = lead.getCustomerName();
							String title = (lead.getTitle() != null) ? lead.getTitle() : "";
							List<PropertyImage> images = (propertyInfo.getImages() != null) ? propertyInfo.getImages()
									: new ArrayList<>();
							String newPropAssetPath = Resources.getPropertyFilePath(
									Resources.getRandomNumber() + Resources.removeSpace(customerName),
									Resources.getRandomNumber() + Resources.removeSpace(title), "images");

							for (PropertyImage propertyImage : images) {
								LOGGER.info(
										"addProperty propertyImage.getImagePath() >> " + propertyImage.getImagePath());
								Boolean moveStatus = Resources.movePropertyFile(
										uploadWorkdir + "/" + Constants.FILE_IMAGE + "/",
										uploadWorkdir + "/" + Constants.FILE_IMAGE + "/" + newPropAssetPath,
										propertyImage.getImagePath());
								if (moveStatus) {
									propertyImage.setLead(lead);
									propertyImage.setName(propertyImage.getImagePath());
									propertyImage.setDescription("");
									propertyImage.setImageUrl(imageBaseDir + "/" + Constants.FILE_IMAGE + "/"
											+ newPropAssetPath + "/" + propertyImage.getImagePath());
									propertyImage.setImagePath(Constants.FILE_IMAGE + "/" + newPropAssetPath);
								}
								propertyImage.setActive(true);
								propertyImage.setDeleted(false);
								propertyImage.setCreatedOn(dtNow);
								propertyImage.setUpdatedOn(dtNow);
								DesireStatus dsImage = addPropertyImage(authToken, propertyImage);
								LOGGER.info("addProperty dsImage.getStatusType() >> " + dsImage.getStatusType());
								LOGGER.info("addProperty dsImage.getText() >> " + dsImage.getText());
							}
						}
					} else {
						Optional<Lead> optLead = leadRepository.findByObjectId(property.getLeadId());
						if (optLead.isPresent()) {
							lead = optLead.get();
						}
					}
					Area area = adminService.getArea(authToken, propertyInfo.getCountryName(),
							propertyInfo.getStateName(), propertyInfo.getCityName(), propertyInfo.getAreaName());

					Optional<PropertyStatus> propertyStatus = propertyStatusRepository
							.findByObjectId(propertyInfo.getPropertyStatusId());
					Optional<PropertyStructure> optStructure = propertyStructureRepository
							.findByObjectId((propertyInfo.getPropertyStructure().getId() != null)
									? propertyInfo.getPropertyStructure().getId()
									: 0);
					PropertyStructure propertyStructure = null;
					if (optStructure.isEmpty()) {

						DesireStatus dsStructure = addPropertyStructure(authToken, propertyInfo.getPropertyStructure());
						propertyStructure = (dsStructure.getStatusType().equals(Constants.STATUS_SUCCESS))
								? dsStructure.getPropertyStructure()
								: null;
					} else {
						propertyStructure = optStructure.get();
					}
					PropertyFeature propertyFeature = null;
					if (propertyInfo.getPropertyFeature().getId() != null) {
						Optional<PropertyFeature> optFeature = propertyFeaturesRepository
								.findByObjectId(propertyInfo.getPropertyFeature().getId());
						if (optFeature.isPresent()) {
							propertyFeature = optFeature.get();
						}
					}

					if (propertyFeature == null) {
						DesireStatus dsFeature = addPropertyFeature(authToken, propertyInfo.getPropertyFeature());
						propertyFeature = (dsFeature.getStatusType().equals(Constants.STATUS_SUCCESS))
								? dsFeature.getPropertyFeature()
								: null;
					}
					if (area == null) {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Area");
					} else if (!propertyStatus.isPresent()) {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
								"Property status");
					} else if (propertyStructure == null) {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
								"Property structure");
					} else if (propertyFeature == null) {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
								"Property feature");
					} else {

						if (lead != null) {
							String customerName = lead.getCustomerName();
							String title = (lead.getTitle() != null) ? lead.getTitle() : "";
							property.setLead(lead);
							property.setPropertyStatus(propertyStatus.get());
							property.setPropertyStructure(propertyStructure);
							property.setPropertyFeature(propertyFeature);
							property.setFeaturedProperty(lead.isFeaturedProperty());
							property.setCreatedOn(dtNow);
							property.setUpdatedOn(dtNow);
							property.setActive(true);
							property.setDeleted(false);
							
							// Set the published status, defaulting to false if null
	                        Boolean isPublished = propertyInfo.isPublished();
	                        if (isPublished == null) {
	                            isPublished = Boolean.FALSE;
	                        }
	                        property.setPublished(isPublished);
	                        
	                        Boolean isShared = propertyInfo.isShared();
	                        if(isShared == null) {
	                        	isShared = Boolean.FALSE;
	                        }
	                        property.setShared(isShared);

							Property saveProperty = propertyRepository.save(property);
							if (saveProperty != null) {

								List<PropertyVideo> videos = (propertyInfo.getVideos() != null)
										? propertyInfo.getVideos()
										: new ArrayList<>();
								for (PropertyVideo propertyVideo : videos) {
									propertyVideo.setProperty(saveProperty);
									propertyVideo
											.setName((propertyVideo.getName() != null) ? propertyVideo.getName() : "");
									propertyVideo.setDescription(
											(propertyVideo.getDescription() != null) ? propertyVideo.getDescription()
													: "");
									DesireStatus dsVideo = addPropertyVideo(authToken, propertyVideo);
									LOGGER.info("addProperty dsImage.getStatusType() >> " + dsVideo.getStatusType());
									LOGGER.info("addProperty dsImage.getText() >> " + dsVideo.getText());
								}
								List<PropertyAttachment> attachments = (propertyInfo.getAttachments() != null)
										? propertyInfo.getAttachments()
										: new ArrayList<>();
								String newPropAttachemntPath = Resources.getPropertyFilePath(
										Resources.getRandomNumber() + Resources.removeSpace(customerName),
										Resources.getRandomNumber() + Resources.removeSpace(title), "attachments");
								for (PropertyAttachment propertyAttachment : attachments) {
									LOGGER.info("addProperty propertyAttachment.getAttachmentPath() >> "
											+ propertyAttachment.getAttachmentPath());
									Boolean moveStatus = Resources.movePropertyFile(
											uploadWorkdir + "/" + Constants.FILE_ATTACHMENT, uploadWorkdir + "/"
													+ Constants.FILE_ATTACHMENT + "/" + newPropAttachemntPath,
											propertyAttachment.getAttachmentPath());
									if (moveStatus) {
										propertyAttachment.setProperty(saveProperty);
										propertyAttachment.setName(propertyAttachment.getAttachmentPath());
										propertyAttachment.setDescription("");
										propertyAttachment.setAttachmentUrl(imageBaseDir + "/"
												+ Constants.FILE_ATTACHMENT + "/" + newPropAttachemntPath + "/"
												+ propertyAttachment.getAttachmentPath());
										propertyAttachment.setAttachmentPath(
												Constants.FILE_ATTACHMENT + "/" + newPropAttachemntPath);

									}
									propertyAttachment.setActive(true);
									propertyAttachment.setDeleted(false);
									propertyAttachment.setCreatedOn(dtNow);
									propertyAttachment.setUpdatedOn(dtNow);
									DesireStatus dsAttachment = addPropertyAttachment(authToken, propertyAttachment);
									LOGGER.info(
											"addProperty dsImage.getStatusType() >> " + dsAttachment.getStatusType());
									LOGGER.info("addProperty dsImage.getText() >> " + dsAttachment.getText());
								}
//								// New addition: Toggle publish status
//								if (propertyInfo.getIsPublished()) {
//								    togglePropertyPublishStatus(authToken, property.getId(), true);
//								} else {
//								    togglePropertyPublishStatus(authToken, property.getId(), false);
//								}

								
								status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS,
										"Property");
								status.setProperty(PropertyMapper.getMappedProperty(saveProperty.getLead(),
										saveProperty, authStatus.getUser()));
								status.setAssetUrl(assetUrl);
								logService.createLog(authStatus.getUser(), LogConstants.PROPERTY, LogConstants.ADD,
										saveProperty.getCreatedOn(), saveProperty.getUpdatedOn());

							} else {
								status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE,
										"Property");
							}
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Lead");
						}
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_EXIST, "Property for lead");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Property");
		}
		return status;
	}

	
	@Override
	public DesireStatus editProperty(String authToken, PropertyInfo propertyInfo) {
		LOGGER.info("editProperty called! AuthToken: {}, PropertyInfo: {}", authToken, propertyInfo);
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				 
				Property property = (Property) Resources.getObjectMapped(propertyInfo, Property.class);
				if (property.getId() != null && property.getId() > 0) {
					 

					Optional<Property> optProperty = propertyRepository.findByObjectId(property.getId());
					if (optProperty.isEmpty()) {
						      
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Property");
					} else {
						Property foundProperty = optProperty.get();

						Area area = adminService.getArea(authToken, propertyInfo.getCountryName(),
								propertyInfo.getStateName(), propertyInfo.getCityName(), propertyInfo.getAreaName());
						/*
						 * Optional<PropertyType> propertyType = propertyTypeRepository
						 * .findByObjectId(propertyInfo.getPropertyTypeId());
						 */
						Optional<PropertySubType> propertySubType = propertySubTypeRepository
								.findByObjectId(propertyInfo.getPropertySubTypeId());
						Optional<PropertyPurpose> propertyPurpose = propertyPurposeRepository
								.findByObjectId(propertyInfo.getPropertyPurposeId());
						Optional<PropertyStatus> propertyStatus = propertyStatusRepository
								.findByObjectId(propertyInfo.getPropertyStatusId());
						Optional<PropertyStructure> propertyStructure = propertyStructureRepository
								.findByObjectId(propertyInfo.getPropertyStructure().getId());
						Optional<PropertyFeature> propertyFeature = propertyFeaturesRepository
								.findByObjectId(propertyInfo.getPropertyFeature().getId());
						if (area == null) {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Area");
						} else if (!propertySubType.isPresent()) {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
									"Property sub type");
						} else if (!propertyPurpose.isPresent()) {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
									"Property purpose");
						} else if (!propertyStatus.isPresent()) {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
									"Property status");
						} else if (!propertyStructure.isPresent()) {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
									"Property structure");
						} else if (!propertyFeature.isPresent()) {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
									"Property feature");
						} else {
							if (foundProperty != null) {
								Lead lead = foundProperty.getLead();
								lead.setTitle(propertyInfo.getName());
								lead.setDescription(propertyInfo.getDescription());
								lead.setPrice(propertyInfo.getPrice());
								// lead.setUser(user);
								lead.setArea(area);
								lead.setPropertySubType(propertySubType.get());
								lead.setPropertyPurpose(propertyPurpose.get());

								leadRepository.save(lead);

								foundProperty.setPropertyStatus(propertyStatus.get());
								foundProperty.setPropertyStructure(propertyStructure.get());
								foundProperty.setPropertyFeature(propertyFeature.get());
								foundProperty.setAddress(property.getAddress());
								foundProperty.setLandMark(property.getLandMark());
								foundProperty.setPinCode(property.getPinCode());
								
								foundProperty.setPublished(propertyInfo.isPublished());
//								foundProperty.setShared(propertyInfo.isShared());

								foundProperty.setFeaturedProperty(property.isFeaturedProperty());
								foundProperty.setUpdatedOn(dtNow);
								Property savedProperty = propertyRepository.save(foundProperty);
								if (savedProperty != null) {

									status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
											"Property");
									status.setProperty(PropertyMapper.getMappedProperty(savedProperty.getLead(),
											savedProperty, authStatus.getUser()));
									status.setAssetUrl(assetUrl);
									logService.createLog(authStatus.getUser(), LogConstants.PROPERTY, LogConstants.EDIT,
											null, savedProperty.getUpdatedOn());
								} else {
									status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
											"Property");
								}
							} else {
								status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
										"Property id");
							}
						}
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit Property");
		}
		return status;
	}

	@Override
	public DesireStatus deleteProperty(String authToken, long propertyId) {
		LOGGER.info("deleteProperty called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (propertyId > 0) {

					List<PropertyAttachment> propertyAttachments = propertyAttachmentsRepository
							.listByProperty(propertyId);
					for (PropertyAttachment propertyAttachment : propertyAttachments) {
						propertyAttachmentsRepository.delete(propertyAttachment);
					}
					List<PropertyVideo> propertyVideos = propertyVideoRepository.listByProperty(propertyId);
					for (PropertyVideo propertyVideo : propertyVideos) {
						propertyVideoRepository.delete(propertyVideo);
					}
					Optional<Property> optProperty = propertyRepository.findByObjectId(propertyId);
					if (optProperty.isPresent()) {
						Property foundProperty = optProperty.get();
						propertyRepository.delete(foundProperty);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "Property");
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY, LogConstants.DELETE, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "Property");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Property id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete Property");
		}
		return status;
	}

	@Override
	public DesireStatus editLeadPublish(String authToken, LeadRequest request) {
		LOGGER.info("editLeadPublish called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Lead lead = null;
				Optional<Lead> optRequest = leadRepository.findByObjectId(request.getLeadId());
				if (optRequest.isPresent()) {
					lead = optRequest.get();
					lead.setPublished(request.isPublish());
					lead.setUpdatedOn(dtNow);
					Lead savedRequest = leadRepository.save(lead);
					if (savedRequest != null) {
						Optional<Property> optProperty = propertyRepository.findByLeadId(savedRequest.getId());
						if (optProperty.isPresent()) {
							Property property = optProperty.get();
							property.setPublished(request.isPublish());
							property.setUpdatedOn(dtNow);
							propertyRepository.save(property);
						}
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
								"Lead/Property publish status");
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY, LogConstants.PUBLISHED, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
								"Lead/Property publish status");
					}

				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Lead/Request");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit lead/property publish status");
		}
		return status;
	}

	@Override
	public DesireStatus propertyTypes(String authToken, DesireSearchRequest request) {
		LOGGER.info("propertyTypes called!");
		DesireStatus status = new DesireStatus();
		List<PropertyType> propertyTypes = null;
		Page<PropertyType> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = propertyTypeRepository.findAllASC(pageable);
				} else {
					page = propertyTypeRepository.findAllDESC(pageable);
				}
				propertyTypes = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property type");
				status.setPropertyTypes(propertyTypes);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_TYPE, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property type");
		}
		return status;
	}

	@Override
	public DesireStatus propertyTypes() {
		LOGGER.info("propertyTypes called!");
		DesireStatus status = new DesireStatus();
		try {
			List<PropertyType> propertyTypes = propertyTypeRepository.findAll();
			status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property type");
			status.setPropertyTypes(propertyTypes);
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property type");
		}
		return status;
	}

	@Override
	public DesireStatus viewPropertyType(String authToken, long typeId) {
		LOGGER.info("viewPropertyType called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (typeId > 0) {
					Optional<PropertyType> optPropertyType = propertyTypeRepository.findByObjectId(typeId);
					if (optPropertyType.isPresent()) {
						PropertyType foundPropertyType = optPropertyType.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS,
								"Property type");
						status.setPropertyType(foundPropertyType);
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_TYPE, LogConstants.DETAIL,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE,
								"Property type");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property type id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Property type");
		}
		return status;
	}

	@Override
	public DesireStatus addPropertyType(String authToken, PropertyType propertyType) {
		LOGGER.info("addPropertyType called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<PropertyType> foundPropertyType = propertyTypeRepository.findByName(propertyType.getName());
				if (foundPropertyType.isPresent()) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Property type");
					status.setPropertyType(foundPropertyType.get());
				} else {
					propertyType.setCreatedOn(dtNow);
					propertyType.setUpdatedOn(dtNow);
					propertyType.setActive(true);
					propertyType.setDeleted(false);
					PropertyType savePropertyType = propertyTypeRepository.save(propertyType);
					if (savePropertyType != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Property type");
						status.setPropertyType(savePropertyType);
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_TYPE, LogConstants.ADD,
								savePropertyType.getCreatedOn(), savePropertyType.getUpdatedOn());
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Property type");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Property type");
		}
		return status;
	}

	@Override
	public DesireStatus addPropertyType(PropertyType propertyType) {
		LOGGER.info("addPropertyType called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			Optional<PropertyType> foundPropertyType = propertyTypeRepository.findByName(propertyType.getName());
			if (foundPropertyType.isPresent()) {
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Property type");
				status.setPropertyType(foundPropertyType.get());
			} else {
				propertyType.setCreatedOn(dtNow);
				propertyType.setUpdatedOn(dtNow);
				propertyType.setActive(true);
				propertyType.setDeleted(false);
				PropertyType savePropertyType = propertyTypeRepository.save(propertyType);
				if (savePropertyType != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Property type");
					status.setPropertyType(savePropertyType);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Property type");
				}
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Property type");
		}
		return status;
	}

	@Override
	public DesireStatus editPropertyType(String authToken, PropertyType propertyType) {
		LOGGER.info("editPropertyType called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (propertyType.getId() != null && propertyType.getId() > 0) {
					Optional<PropertyType> optPropertyType = propertyTypeRepository
							.findByObjectId(propertyType.getId());
					if (optPropertyType.isPresent()) {
						PropertyType foundPropertyType = optPropertyType.get();
						foundPropertyType.setName(propertyType.getName());
						foundPropertyType.setDescription(propertyType.getDescription());
						foundPropertyType.setUpdatedOn(dtNow);
						PropertyType savedPropertyType = propertyTypeRepository.save(foundPropertyType);
						if (savedPropertyType != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Property type");
							status.setPropertyType(savedPropertyType);
							logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_TYPE, LogConstants.EDIT,
									null, savedPropertyType.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Property type");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"Property type id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit Property type");
		}
		return status;
	}

	@Override
	public DesireStatus deletePropertyType(String authToken, long typeId) {
		LOGGER.info("deletePropertyType called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (typeId > 0) {
					Optional<PropertyType> optPropertyType = propertyTypeRepository.findByObjectId(typeId);
					if (optPropertyType.isPresent()) {
						PropertyType foundPropertyType = optPropertyType.get();
						propertyTypeRepository.delete(foundPropertyType);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS,
								"Property type");
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_TYPE, LogConstants.DELETE,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE,
								"Property type");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property type id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete Property type");
		}
		return status;
	}

	@Override
	public DesireStatus propertySubTypes(String authToken, DesireSearchRequest request, long typeId) {
		LOGGER.info("propertySubTypes called!");
		DesireStatus status = new DesireStatus();
		List<PropertySubType> propertySubTypes = null;
		Page<PropertySubType> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = propertySubTypeRepository.findAllASC(typeId, pageable);
				} else {
					page = propertySubTypeRepository.findAllDESC(typeId, pageable);
				}
				propertySubTypes = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property sub type");
				status.setPropertySubTypes(propertySubTypes);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_SUB_TYPE, LogConstants.LIST, null,
						null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property sub type");
		}
		return status;
	}

	@Override
	public DesireStatus propertySubTypes(long typeId) {
		LOGGER.info("propertySubTypes called!");
		DesireStatus status = new DesireStatus();
		try {
			List<PropertySubType> propertySubTypes = propertySubTypeRepository.findAllByType(typeId);
			status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property sub type");
			status.setPropertySubTypes(propertySubTypes);
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property sub type");
		}
		return status;
	}

	@Override
	public DesireStatus propertySubTypes(String authToken, long typeId) {
		LOGGER.info("propertySubTypes called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				List<PropertySubType> propertySubTypes = propertySubTypeRepository.findAllByType(typeId);
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property sub type");
				status.setPropertySubTypes(propertySubTypes);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property sub type");
		}
		return status;
	}

	@Override
	public DesireStatus viewPropertySubType(String authToken, long typeId) {
		LOGGER.info("viewPropertySubType called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (typeId > 0) {
					Optional<PropertySubType> optPropertySubType = propertySubTypeRepository.findByObjectId(typeId);
					if (optPropertySubType.isPresent()) {
						PropertySubType foundPropertySubType = optPropertySubType.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS,
								"Property sub type");
						status.setPropertySubType(foundPropertySubType);
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_SUB_TYPE, LogConstants.DETAIL,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE,
								"Property sub type");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property sub type id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Property sub type");
		}
		return status;
	}

	@Override
	public DesireStatus addPropertySubType(String authToken, PropertySubType propertySubType) {
		LOGGER.info("addPropertySubType called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<PropertySubType> foundPropertySubType = propertySubTypeRepository
						.findByNameAndType(propertySubType.getName(), propertySubType.getPropertyTypeId());
				if (foundPropertySubType.isPresent()) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Property sub type");
					status.setPropertySubType(foundPropertySubType.get());
				} else {
					Optional<PropertyType> optType = propertyTypeRepository
							.findByObjectId(propertySubType.getPropertyTypeId());
					if (optType.isPresent()) {
						propertySubType.setPropertyType(optType.get());
						propertySubType.setCreatedOn(dtNow);
						propertySubType.setUpdatedOn(dtNow);
						propertySubType.setActive(true);
						propertySubType.setDeleted(false);
						PropertySubType savePropertySubType = propertySubTypeRepository.save(propertySubType);
						if (savePropertySubType != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS,
									"Property sub type");
							status.setPropertySubType(savePropertySubType);
							logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_SUB_TYPE, LogConstants.ADD,
									savePropertySubType.getCreatedOn(), savePropertySubType.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE,
									"Property sub type");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
								"Property type");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Property sub type");
		}
		return status;
	}

	@Override
	public DesireStatus addPropertySubType(PropertySubType propertySubType) {
		LOGGER.info("addPropertySubType called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			Optional<PropertySubType> foundPropertySubType = propertySubTypeRepository
					.findByName(propertySubType.getName());
			if (foundPropertySubType.isPresent()) {
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Property sub type");
				status.setPropertySubType(foundPropertySubType.get());
			} else {
				LOGGER.info("addPropertySubType >> propertySubType.getPropertyTypeId() >> "
						+ propertySubType.getPropertyTypeId());
				Optional<PropertyType> optType = propertyTypeRepository
						.findByObjectId(propertySubType.getPropertyTypeId());
				if (optType.isPresent()) {
					propertySubType.setPropertyType(optType.get());
					propertySubType.setCreatedOn(dtNow);
					propertySubType.setUpdatedOn(dtNow);
					propertySubType.setActive(true);
					propertySubType.setDeleted(false);
					PropertySubType savePropertySubType = propertySubTypeRepository.save(propertySubType);
					if (savePropertySubType != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS,
								"Property sub type");
						status.setPropertySubType(savePropertySubType);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE,
								"Property sub type");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Property type");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Property sub type");
		}
		return status;
	}

	@Override
	public DesireStatus editPropertySubType(String authToken, PropertySubType propertySubType) {
		LOGGER.info("editPropertySubType called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (propertySubType.getId() != null && propertySubType.getId() > 0) {
					Optional<PropertySubType> optPropertySubType = propertySubTypeRepository
							.findByObjectId(propertySubType.getId());
					if (optPropertySubType.isPresent()) {
						PropertySubType foundPropertySubType = optPropertySubType.get();
						foundPropertySubType.setName(propertySubType.getName());
						foundPropertySubType.setDescription(propertySubType.getDescription());
						foundPropertySubType.setUpdatedOn(dtNow);
						PropertySubType savedPropertySubType = propertySubTypeRepository.save(foundPropertySubType);
						if (savedPropertySubType != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Property sub type");
							status.setPropertySubType(savedPropertySubType);
							logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_SUB_TYPE,
									LogConstants.EDIT, null, savedPropertySubType.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Property sub type");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"Property sub type id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit Property sub type");
		}
		return status;
	}

	@Override
	public DesireStatus deletePropertySubType(String authToken, long typeId) {
		LOGGER.info("deletePropertySubType called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (typeId > 0) {
					Optional<PropertySubType> optPropertySubType = propertySubTypeRepository.findByObjectId(typeId);
					if (optPropertySubType.isPresent()) {
						PropertySubType foundPropertySubType = optPropertySubType.get();
						propertySubTypeRepository.delete(foundPropertySubType);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS,
								"Property sub type");
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_SUB_TYPE, LogConstants.DELETE,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE,
								"Property sub type");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property sub type id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete Property sub type");
		}
		return status;
	}

	@Override
	public DesireStatus changePropertySubTypeState(String authToken, ChangeStateRequest request) {
		LOGGER.info("changePropertySubTypeState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (request.getId() > 0) {
					Optional<PropertySubType> optObj = propertySubTypeRepository.findByObjectId(request.getId());
					if (optObj.isPresent()) {
						PropertySubType type = optObj.get();
						type.setActive(request.isActive());
						PropertySubType savedType = propertySubTypeRepository.save(type);
						if (savedType != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Property sub type");
							status.setPropertySubType(savedType);
							logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_SUB_TYPE,
									LogConstants.ACTIVE, null, savedType.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Property sub type");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
								"Property sub type");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property sub type id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Change property sub type active state");
		}
		return status;
	}

	@Override
	public DesireStatus propertyPurposes(String authToken, DesireSearchRequest request) {
		LOGGER.info("propertyPurposes called!");
		DesireStatus status = new DesireStatus();
		List<PropertyPurpose> propertyPurposes = null;
		Page<PropertyPurpose> page = null;
		int pageNumber = 0, pageSize = 0;

		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = propertyPurposeRepository.findAllASC(pageable);
				} else {
					page = propertyPurposeRepository.findAllDESC(pageable);
				}
				propertyPurposes = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property purpose");
				status.setPropertyPurposes(propertyPurposes);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_PURPOSE, LogConstants.LIST, null,
						null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status.setStatusType(Constants.STATUS_ERROR);
			status.setText("Property purposes got error : " + e.getMessage());
		}
		return status;
	}

	@Override
	public DesireStatus propertyPurposes() {
		LOGGER.info("propertyPurposes called!");
		DesireStatus status = new DesireStatus();
		try {
			List<PropertyPurpose> propertyPurposes = propertyPurposeRepository.findAll();
			status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property purpose");
			status.setPropertyPurposes(propertyPurposes);
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property purpose");
		}
		return status;
	}

	@Override
	public DesireStatus viewPropertyPurpose(String authToken, long purposeId) {
		LOGGER.info("viewPropertyPurpose called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (purposeId > 0) {
					Optional<PropertyPurpose> optPropertyPurpose = propertyPurposeRepository.findByObjectId(purposeId);
					if (optPropertyPurpose.isPresent()) {
						PropertyPurpose foundPropertyPurpose = optPropertyPurpose.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS,
								"Property purpose");
						status.setPropertyPurpose(foundPropertyPurpose);
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_PURPOSE, LogConstants.DETAIL,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE,
								"Property purpose");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property purpose id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Property purpose");
		}
		return status;
	}

	@Override
	public DesireStatus addPropertyPurpose(String authToken, PropertyPurpose propertyPurpose) {
		LOGGER.info("addPropertyPurpose called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<PropertyPurpose> foundPurpose = propertyPurposeRepository
						.findByName(propertyPurpose.getName());
				if (foundPurpose.isPresent()) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Property purpose");
					status.setPropertyPurpose(foundPurpose.get());
				} else {
					propertyPurpose.setCreatedOn(dtNow);
					propertyPurpose.setUpdatedOn(dtNow);
					propertyPurpose.setActive(true);
					propertyPurpose.setDeleted(false);
					PropertyPurpose savePropertyPurpose = propertyPurposeRepository.save(propertyPurpose);
					if (savePropertyPurpose != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS,
								"Property purpose");
						status.setPropertyPurpose(savePropertyPurpose);
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_PURPOSE, LogConstants.ADD,
								savePropertyPurpose.getCreatedOn(), savePropertyPurpose.getUpdatedOn());
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE,
								"Property purpose");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Property purpose");
		}
		return status;
	}

	@Override
	public DesireStatus addPropertyPurpose(PropertyPurpose propertyPurpose) {
		LOGGER.info("addPropertyPurpose called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			Optional<PropertyPurpose> foundPurpose = propertyPurposeRepository.findByName(propertyPurpose.getName());
			if (foundPurpose.isPresent()) {
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Property purpose");
				status.setPropertyPurpose(foundPurpose.get());
			} else {
				propertyPurpose.setCreatedOn(dtNow);
				propertyPurpose.setUpdatedOn(dtNow);
				propertyPurpose.setActive(true);
				propertyPurpose.setDeleted(false);
				PropertyPurpose savePropertyPurpose = propertyPurposeRepository.save(propertyPurpose);
				if (savePropertyPurpose != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Property purpose");
					status.setPropertyPurpose(savePropertyPurpose);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Property purpose");
				}
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Property purpose");
		}
		return status;
	}

	@Override
	public DesireStatus editPropertyPurpose(String authToken, PropertyPurpose propertyPurpose) {
		LOGGER.info("editPropertyPurpose called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (propertyPurpose.getId() != null && propertyPurpose.getId() > 0) {
					Optional<PropertyPurpose> optPropertyPurpose = propertyPurposeRepository
							.findByObjectId(propertyPurpose.getId());
					if (optPropertyPurpose.isPresent()) {
						PropertyPurpose foundPropertyPurpose = optPropertyPurpose.get();
						foundPropertyPurpose.setName(propertyPurpose.getName());
						foundPropertyPurpose.setDescription(propertyPurpose.getDescription());
						foundPropertyPurpose.setUpdatedOn(dtNow);
						PropertyPurpose savedPropertyPurpose = propertyPurposeRepository.save(foundPropertyPurpose);
						if (savedPropertyPurpose != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Property purpose");
							status.setPropertyPurpose(savedPropertyPurpose);
							logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_PURPOSE, LogConstants.EDIT,
									null, savedPropertyPurpose.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Property purpose");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"Property purpose id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit Property purpose");
		}
		return status;
	}

	@Override
	public DesireStatus deletePropertyPurpose(String authToken, long purposeId) {
		LOGGER.info("deletePropertyPurpose called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (purposeId > 0) {
					Optional<PropertyPurpose> optPropertyPurpose = propertyPurposeRepository.findByObjectId(purposeId);
					if (optPropertyPurpose.isPresent()) {
						PropertyPurpose foundPropertyPurpose = optPropertyPurpose.get();
						propertyPurposeRepository.delete(foundPropertyPurpose);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS,
								"Property purpose");
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_PURPOSE, LogConstants.DELETE,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE,
								"Property purpose");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property purpose id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete Property purpose");
		}
		return status;
	}

	@Override
	public DesireStatus propertyStatusList(String authToken, DesireSearchRequest request) {
		LOGGER.info("propertyStatus called!");
		DesireStatus status = new DesireStatus();
		List<PropertyStatus> propertyStatus = null;
		Page<PropertyStatus> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = propertyStatusRepository.findAllASC(pageable);
				} else {
					page = propertyStatusRepository.findAllDESC(pageable);
				}
				propertyStatus = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property status");
				status.setPropertyStatuss(propertyStatus);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_STATUS, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property status");
		}
		return status;
	}

	@Override
	public DesireStatus viewPropertyStatus(String authToken, long statusId) {
		LOGGER.info("viewPropertyStatus called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (statusId > 0) {
					Optional<PropertyStatus> optPropertyStatus = propertyStatusRepository.findByObjectId(statusId);
					if (optPropertyStatus.isPresent()) {
						PropertyStatus foundPropertyStatus = optPropertyStatus.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS,
								"Property status");
						status.setPropertyStatus(foundPropertyStatus);
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_STATUS, LogConstants.DETAIL,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE,
								"Property status");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property status id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Property status");
		}
		return status;
	}

	@Override
	public DesireStatus addPropertyStatus(String authToken, PropertyStatus propertyStatus) {
		LOGGER.info("addPropertyStatus called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				Optional<PropertyStatus> foundPropertyStatus = propertyStatusRepository
						.findByName(propertyStatus.getName());
				if (foundPropertyStatus.isPresent()) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Property status");
					status.setPropertyStatus(foundPropertyStatus.get());
				} else {
					propertyStatus.setCreatedOn(dtNow);
					propertyStatus.setUpdatedOn(dtNow);
					propertyStatus.setActive(true);
					propertyStatus.setDeleted(false);
					PropertyStatus savePropertyStatus = propertyStatusRepository.save(propertyStatus);
					if (savePropertyStatus != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS,
								"Property status");
						status.setPropertyStatus(savePropertyStatus);
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_STATUS, LogConstants.ADD,
								savePropertyStatus.getCreatedOn(), savePropertyStatus.getUpdatedOn());
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE,
								"Property status");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Property status");
		}
		return status;
	}

	@Override
	public DesireStatus addPropertyStatus(PropertyStatus propertyStatus) {
		LOGGER.info("addPropertyStatus called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			Optional<PropertyStatus> foundPropertyStatus = propertyStatusRepository
					.findByName(propertyStatus.getName());
			if (foundPropertyStatus.isPresent()) {
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Property status");
				status.setPropertyStatus(foundPropertyStatus.get());
			} else {
				propertyStatus.setCreatedOn(dtNow);
				propertyStatus.setUpdatedOn(dtNow);
				propertyStatus.setActive(true);
				propertyStatus.setDeleted(false);
				PropertyStatus savePropertyStatus = propertyStatusRepository.save(propertyStatus);
				if (savePropertyStatus != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Property status");
					status.setPropertyStatus(savePropertyStatus);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Property status");
				}
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Property status");
		}
		return status;
	}

	@Override
	public DesireStatus editPropertyStatus(String authToken, PropertyStatus propertyStatus) {
		LOGGER.info("editPropertyStatus called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (propertyStatus.getId() != null && propertyStatus.getId() > 0) {
					Optional<PropertyStatus> optPropertyStatus = propertyStatusRepository
							.findByObjectId(propertyStatus.getId());
					if (optPropertyStatus.isPresent()) {
						PropertyStatus foundPropertyStatus = optPropertyStatus.get();
						foundPropertyStatus.setName(propertyStatus.getName());
						foundPropertyStatus.setDescription(propertyStatus.getDescription());
						foundPropertyStatus.setUpdatedOn(dtNow);
						PropertyStatus savedPropertyStatus = propertyStatusRepository.save(foundPropertyStatus);
						if (savedPropertyStatus != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Property status");
							status.setPropertyStatus(savedPropertyStatus);
							logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_STATUS, LogConstants.EDIT,
									null, savedPropertyStatus.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Property status");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
								"Property status");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit Property status");
		}
		return status;
	}

	@Override
	public DesireStatus deletePropertyStatus(String authToken, long statusId) {
		LOGGER.info("deletePropertyStatus called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (statusId > 0) {
					Optional<PropertyStatus> optPropertyStatus = propertyStatusRepository.findByObjectId(statusId);
					if (optPropertyStatus.isPresent()) {
						PropertyStatus foundPropertyStatus = optPropertyStatus.get();
						propertyStatusRepository.delete(foundPropertyStatus);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS,
								"Property status");
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_STATUS, LogConstants.DELETE,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE,
								"Property status");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property status id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete Property status");
		}
		return status;
	}

	@Override
	public DesireStatus PropertyStructureList(String authToken, DesireSearchRequest request) {
		LOGGER.info("propertyStructure called!");
		DesireStatus status = new DesireStatus();
		List<PropertyStructure> propertyStructures = null;
		Page<PropertyStructure> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = propertyStructureRepository.findAllASC(pageable);
				} else {
					page = propertyStructureRepository.findAllDESC(pageable);
				}
				propertyStructures = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property structure");
				status.setPropertyStructures(propertyStructures);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_STRUCTURE, LogConstants.LIST, null,
						null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property structure");
		}
		return status;
	}

	@Override
	public DesireStatus viewPropertyStructure(String authToken, long structureId) {
		LOGGER.info("viewPropertyStructure called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (structureId > 0) {
					Optional<PropertyStructure> optPropertyStructure = propertyStructureRepository
							.findByObjectId(structureId);
					if (optPropertyStructure.isPresent()) {
						PropertyStructure foundPropertyStructure = optPropertyStructure.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS,
								"Property structure");
						status.setPropertyStructure(foundPropertyStructure);
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_STRUCTURE, LogConstants.DETAIL,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE,
								"Property structure");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property structure id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Property structure");
		}
		return status;
	}

	@Override
	public DesireStatus addPropertyStructure(String authToken, PropertyStructure propertyStructure) {
		LOGGER.info("addPropertyStructure called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				propertyStructure.setCreatedOn(dtNow);
				propertyStructure.setUpdatedOn(dtNow);
				propertyStructure.setActive(true);
				propertyStructure.setDeleted(false);
				PropertyStructure savePropertyStructure = propertyStructureRepository.save(propertyStructure);
				if (savePropertyStructure != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS,
							"Property structure");
					status.setPropertyStructure(savePropertyStructure);
					logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_STRUCTURE, LogConstants.ADD,
							savePropertyStructure.getCreatedOn(), savePropertyStructure.getUpdatedOn());
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE,
							"Property structure");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Property structure");
		}
		return status;
	}

	@Override
	public DesireStatus editPropertyStructure(String authToken, PropertyStructure propertyStructure) {
		LOGGER.info("editPropertyStructure called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (propertyStructure.getId() != null && propertyStructure.getId() > 0) {
					Optional<PropertyStructure> optPropertyStructure = propertyStructureRepository
							.findByObjectId(propertyStructure.getId());
					if (optPropertyStructure.isPresent()) {
						PropertyStructure foundPropertyStructure = optPropertyStructure.get();
						foundPropertyStructure.setBedRooms(propertyStructure.getBathRooms());
						;
						foundPropertyStructure.setBathRooms(propertyStructure.getBathRooms());
						;
						foundPropertyStructure.setRooms(propertyStructure.getRooms());
						;
						foundPropertyStructure.setUpdatedOn(dtNow);
						PropertyStructure savedPropertyStructure = propertyStructureRepository
								.save(foundPropertyStructure);
						if (savedPropertyStructure != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Property structure");
							status.setPropertyStructure(savedPropertyStructure);
							logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_STRUCTURE,
									LogConstants.EDIT, null, savedPropertyStructure.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Property structure");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"Property structure id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit Property structure");
		}
		return status;
	}

	@Override
	public DesireStatus deletePropertyStructure(String authToken, long structureId) {
		LOGGER.info("deletePropertyStructure called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (structureId > 0) {
					Optional<PropertyStructure> optPropertyStructure = propertyStructureRepository
							.findByObjectId(structureId);
					if (optPropertyStructure.isPresent()) {
						PropertyStructure foundPropertyStructure = optPropertyStructure.get();
						propertyStructureRepository.delete(foundPropertyStructure);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS,
								"Property structure");
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_STRUCTURE, LogConstants.DELETE,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE,
								"Property structure");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property structure id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete Property structure");
		}
		return status;
	}

	@Override
	public DesireStatus propertyFeatures(String authToken, DesireSearchRequest request) {
		LOGGER.info("propertyFeatures called!");
		DesireStatus status = new DesireStatus();
		List<PropertyFeature> propertyFeatures = null;
		Page<PropertyFeature> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = propertyFeaturesRepository.findAllASC(pageable);
				} else {
					page = propertyFeaturesRepository.findAllDESC(pageable);
				}
				propertyFeatures = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property feature");
				status.setPropertyFeatures(propertyFeatures);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_FEATURE, LogConstants.LIST, null,
						null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property feature");
		}
		return status;
	}

	@Override
	public DesireStatus viewPropertyFeature(String authToken, long featureId) {
		LOGGER.info("viewPropertyFeature called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (featureId > 0) {
					Optional<PropertyFeature> optPropertyFeature = propertyFeaturesRepository.findByObjectId(featureId);
					if (optPropertyFeature.isPresent()) {
						PropertyFeature foundPropertyFeature = optPropertyFeature.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS,
								"Property feature");
						status.setPropertyFeature(foundPropertyFeature);
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_FEATURE, LogConstants.DETAIL,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE,
								"Property feature");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property feature id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Property feature");
		}
		return status;
	}

	@Override
	public DesireStatus addPropertyFeature(String authToken, PropertyFeature propertyFeature) {
		LOGGER.info("addPropertyFeature called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				propertyFeature.setCreatedOn(dtNow);
				propertyFeature.setUpdatedOn(dtNow);
				propertyFeature.setActive(true);
				propertyFeature.setDeleted(false);
				PropertyFeature savePropertyFeature = propertyFeaturesRepository.save(propertyFeature);
				if (savePropertyFeature != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Property feature");
					status.setPropertyFeature(savePropertyFeature);
					logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_FEATURE, LogConstants.ADD,
							savePropertyFeature.getCreatedOn(), savePropertyFeature.getUpdatedOn());
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Property feature");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Property feature");
		}
		return status;
	}

	@Override
	public DesireStatus editPropertyFeature(String authToken, PropertyFeature propertyFeature) {
		LOGGER.info("editPropertyFeature called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (propertyFeature.getId() != null && propertyFeature.getId() > 0) {
					Optional<PropertyFeature> optPropertyFeature = propertyFeaturesRepository
							.findByObjectId(propertyFeature.getId());
					if (optPropertyFeature.isPresent()) {
						PropertyFeature foundPropertyFeature = optPropertyFeature.get();
						foundPropertyFeature.setSwimingPool(propertyFeature.isSwimingPool());
						foundPropertyFeature.setGasConection(propertyFeature.isGasConection());
						foundPropertyFeature.setRo(propertyFeature.isRo());
						foundPropertyFeature.setClubHouse(propertyFeature.isClubHouse());
						foundPropertyFeature.setBaketballCourt(propertyFeature.isBaketballCourt());
						foundPropertyFeature.setTennisCourt(propertyFeature.isTennisCourt());
						foundPropertyFeature.setGym(propertyFeature.isGym());
						foundPropertyFeature.setIndoorGames(propertyFeature.isIndoorGames());
						foundPropertyFeature.setChildPlayArea(propertyFeature.isChildPlayArea());
						foundPropertyFeature.setHospitalNearBy(propertyFeature.isHospitalNearBy());
						foundPropertyFeature.setMallNearBy(propertyFeature.isMallNearBy());
						foundPropertyFeature.setMarketNearBy(propertyFeature.isMarketNearBy());
						foundPropertyFeature.setSchoolNearBy(propertyFeature.isSchoolNearBy());
						foundPropertyFeature.setUpdatedOn(dtNow);
						PropertyFeature savedPropertyFeature = propertyFeaturesRepository.save(foundPropertyFeature);
						if (savedPropertyFeature != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Property feature");
							status.setPropertyFeature(savedPropertyFeature);
							logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_FEATURE, LogConstants.EDIT,
									null, savedPropertyFeature.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Property feature");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"Property feature id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit Property feature");
		}
		return status;
	}

	@Override
	public DesireStatus deletePropertyFeature(String authToken, long featureId) {
		LOGGER.info("deletePropertyFeature called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (featureId > 0) {
					Optional<PropertyFeature> optPropertyFeature = propertyFeaturesRepository.findByObjectId(featureId);
					if (optPropertyFeature.isPresent()) {
						PropertyFeature foundPropertyFeature = optPropertyFeature.get();
						propertyFeaturesRepository.delete(foundPropertyFeature);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS,
								"Property feature");
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_FEATURE, LogConstants.DELETE,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE,
								"Property feature");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property feature id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete Property feature");
		}
		return status;
	}

	@Override
	public DesireStatus propertyImages(String authToken, DesireSearchRequest request) {
		LOGGER.info("propertyImages called!");
		DesireStatus status = new DesireStatus();
		List<PropertyImage> propertyImages = null;
		Page<PropertyImage> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = propertyImagesRepository.findAllASC(pageable);
				} else {
					page = propertyImagesRepository.findAllDESC(pageable);
				}
				propertyImages = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property image");
				status.setPropertyImages(propertyImages);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_IMAGE, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property image");
		}
		return status;
	}

	@Override
	public DesireStatus propertyImages(String authToken, long leadId, DesireSearchRequest request) {
		LOGGER.info("propertyImages called!");
		DesireStatus status = new DesireStatus();
		List<PropertyImage> propertyImages = null;
		Page<PropertyImage> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = propertyImagesRepository.findAllByLeadASC(leadId, pageable);
				} else {
					page = propertyImagesRepository.findAllByLeadDESC(leadId, pageable);
				}
				propertyImages = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property image");
				status.setPropertyImages(propertyImages);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_IMAGE, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property image");
		}
		return status;
	}

	@Override
	public DesireStatus viewPropertyImage(String authToken, long imageId) {
		LOGGER.info("viewPropertyImage called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (imageId > 0) {
					Optional<PropertyImage> optPropertyImage = propertyImagesRepository.findByObjectId(imageId);
					if (optPropertyImage.isPresent()) {
						PropertyImage foundPropertyImage = optPropertyImage.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS,
								"Property image");
						status.setPropertyImage(foundPropertyImage);
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_IMAGE, LogConstants.DETAIL,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE,
								"Property image");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property image id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Property image");
		}
		return status;
	}

	@Override
	public DesireStatus addPropertyImage(String authToken, PropertyImage propertyImage) {
		LOGGER.info("addPropertyImage called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				propertyImage.setCreatedOn(dtNow);
				propertyImage.setUpdatedOn(dtNow);
				propertyImage.setActive(true);
				propertyImage.setDeleted(false);
				PropertyImage savePropertyImage = propertyImagesRepository.save(propertyImage);
				if (savePropertyImage != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Property image");
					status.setPropertyImage(savePropertyImage);
					logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_IMAGE, LogConstants.ADD,
							savePropertyImage.getCreatedOn(), savePropertyImage.getUpdatedOn());
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Property image");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Property image");
		}
		return status;
	}

	@Override
	public DesireStatus addPropertyImage(PropertyImage propertyImage) {
		LOGGER.info("addPropertyImage called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {

			propertyImage.setCreatedOn(dtNow);
			propertyImage.setUpdatedOn(dtNow);
			propertyImage.setActive(true);
			propertyImage.setDeleted(false);
			PropertyImage savePropertyImage = propertyImagesRepository.save(propertyImage);
			if (savePropertyImage != null) {
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Property image");
				status.setPropertyImage(savePropertyImage);

			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Property image");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Property image");
		}
		return status;
	}

	@Override
	public DesireStatus editPropertyImage(String authToken, PropertyImage propertyImage) {
		LOGGER.info("editPropertyImage called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (propertyImage.getId() != null && propertyImage.getId() > 0) {
					Optional<PropertyImage> optPropertyImage = propertyImagesRepository
							.findByObjectId(propertyImage.getId());
					if (optPropertyImage.isPresent()) {
						PropertyImage foundPropertyImage = optPropertyImage.get();
						foundPropertyImage.setName(propertyImage.getName());
						foundPropertyImage.setDescription(propertyImage.getDescription());
						foundPropertyImage.setImagePath(propertyImage.getImagePath());
						foundPropertyImage.setImageUrl(propertyImage.getImageUrl());
						foundPropertyImage.setUpdatedOn(dtNow);
						PropertyImage savedPropertyImage = propertyImagesRepository.save(foundPropertyImage);
						if (savedPropertyImage != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Property image");
							status.setPropertyImage(savedPropertyImage);
							logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_IMAGE, LogConstants.EDIT,
									null, savedPropertyImage.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Property image");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"Property image id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit Property image");
		}
		return status;
	}

	@Override
	public DesireStatus deletePropertyImage(String authToken, long imageId) {
		LOGGER.info("deletePropertyImage called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (imageId > 0) {
					Optional<PropertyImage> optPropertyImage = propertyImagesRepository.findByObjectId(imageId);
					if (optPropertyImage.isPresent()) {
						PropertyImage foundPropertyImage = optPropertyImage.get();
						propertyImagesRepository.delete(foundPropertyImage);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS,
								"Property image");
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_IMAGE, LogConstants.DELETE,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE,
								"Property image");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property image id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete Property image");
		}
		return status;
	}

	@Override
	public DesireStatus propertyAttachments(String authToken, DesireSearchRequest request) {
		LOGGER.info("propertyAttachments called!");
		DesireStatus status = new DesireStatus();
		List<PropertyAttachment> propertyAttachments = null;
		Page<PropertyAttachment> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = propertyAttachmentsRepository.findAllASC(pageable);
				} else {
					page = propertyAttachmentsRepository.findAllDESC(pageable);
				}
				propertyAttachments = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property attachment");
				status.setPropertyAttachments(propertyAttachments);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_ATTACHEMENT, LogConstants.LIST, null,
						null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property attachment");
		}
		return status;
	}

	@Override
	public DesireStatus propertyAttachments(String authToken, long propertyId, DesireSearchRequest request) {
		LOGGER.info("propertyAttachments called!");
		DesireStatus status = new DesireStatus();
		List<PropertyAttachment> propertyAttachments = null;
		Page<PropertyAttachment> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = propertyAttachmentsRepository.findAllByPropertyASC(propertyId, pageable);
				} else {
					page = propertyAttachmentsRepository.findAllByPropertyDESC(propertyId, pageable);
				}
				propertyAttachments = page.getContent();
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property attachment");
				status.setPropertyAttachments(propertyAttachments);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_ATTACHEMENT, LogConstants.LIST, null,
						null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property attachment");
		}
		return status;
	}

	@Override
	public DesireStatus viewPropertyAttachment(String authToken, long attachmentId) {
		LOGGER.info("viewPropertyAttachment called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (attachmentId > 0) {
					Optional<PropertyAttachment> optPropertyAttachment = propertyAttachmentsRepository
							.findByObjectId(attachmentId);
					if (optPropertyAttachment.isPresent()) {
						PropertyAttachment foundPropertyAttachment = optPropertyAttachment.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS,
								"Property attachment");
						status.setPropertyAttachment(foundPropertyAttachment);
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_ATTACHEMENT,
								LogConstants.DETAIL, null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE,
								"Property attachment");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property attachment id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Property attachment");
		}
		return status;
	}

	@Override
	public DesireStatus addPropertyAttachment(String authToken, PropertyAttachment propertyAttachment) {
		LOGGER.info("addPropertyAttachment called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {

				propertyAttachment.setCreatedOn(dtNow);
				propertyAttachment.setUpdatedOn(dtNow);
				propertyAttachment.setActive(true);
				propertyAttachment.setDeleted(false);
				PropertyAttachment savePropertyAttachment = propertyAttachmentsRepository.save(propertyAttachment);
				if (savePropertyAttachment != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS,
							"Property attachment");
					status.setPropertyAttachment(savePropertyAttachment);
					logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_ATTACHEMENT, LogConstants.ADD,
							savePropertyAttachment.getCreatedOn(), savePropertyAttachment.getUpdatedOn());
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE,
							"Property attachment");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Property attachment");
		}
		return status;
	}

	@Override
	public DesireStatus editPropertyAttachment(String authToken, PropertyAttachment propertyAttachment) {
		LOGGER.info("editPropertyAttachment called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (propertyAttachment.getId() != null && propertyAttachment.getId() > 0) {
					Optional<PropertyAttachment> optPropertyAttachment = propertyAttachmentsRepository
							.findByObjectId(propertyAttachment.getId());
					if (optPropertyAttachment.isPresent()) {
						PropertyAttachment foundPropertyAttachment = optPropertyAttachment.get();
						foundPropertyAttachment.setName(propertyAttachment.getName());
						foundPropertyAttachment.setDescription(propertyAttachment.getDescription());
						foundPropertyAttachment.setAttachmentPath(propertyAttachment.getAttachmentPath());
						foundPropertyAttachment.setAttachmentUrl(propertyAttachment.getAttachmentUrl());
						foundPropertyAttachment.setUpdatedOn(dtNow);
						PropertyAttachment savedPropertyAttachment = propertyAttachmentsRepository
								.save(foundPropertyAttachment);
						if (savedPropertyAttachment != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Property attachment");
							status.setPropertyAttachment(savedPropertyAttachment);
							logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_ATTACHEMENT,
									LogConstants.EDIT, null, savedPropertyAttachment.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Property attachment");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"Property attachment id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit Property attachment");
		}
		return status;
	}

	@Override
	public DesireStatus deletePropertyAttachment(String authToken, long attachmentId) {
		LOGGER.info("deletePropertyAttachment called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (attachmentId > 0) {
					Optional<PropertyAttachment> optPropertyAttachment = propertyAttachmentsRepository
							.findByObjectId(attachmentId);
					if (optPropertyAttachment.isPresent()) {
						PropertyAttachment foundPropertyAttachment = optPropertyAttachment.get();
						propertyAttachmentsRepository.delete(foundPropertyAttachment);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS,
								"Property attachment");
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_ATTACHEMENT,
								LogConstants.DELETE, null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE,
								"Property attachment");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property attachment id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete Property attachment");
		}
		return status;
	}

	@Override
	public DesireStatus propertyVideos(String authToken, DesireSearchRequest request) {
		LOGGER.info("propertyVideos called!");
		DesireStatus status = new DesireStatus();
		List<PropertyVideo> propertyVideos = null;
		Page<PropertyVideo> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = propertyVideoRepository.findAllASC(pageable);
				} else {
					page = propertyVideoRepository.findAllDESC(pageable);
				}
				propertyVideos = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property video");
				status.setPropertyVideos(propertyVideos);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_VIDEO, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property video");
		}
		return status;
	}

	@Override
	public DesireStatus propertyVideos(String authToken, long propertyId, DesireSearchRequest request) {
		LOGGER.info("propertyVideos called!");
		DesireStatus status = new DesireStatus();
		List<PropertyVideo> propertyVideos = null;
		Page<PropertyVideo> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = propertyVideoRepository.findAllByPropertyASC(propertyId, pageable);
				} else {
					page = propertyVideoRepository.findAllByPropertyDESC(propertyId, pageable);
				}
				propertyVideos = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Property video");
				status.setPropertyVideos(propertyVideos);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_VIDEO, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Property video");
		}
		return status;
	}

	@Override
	public DesireStatus viewPropertyVideo(String authToken, long videoId) {
		LOGGER.info("viewPropertyVideo called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (videoId > 0) {
					Optional<PropertyVideo> optPropertyVideo = propertyVideoRepository.findByObjectId(videoId);
					if (optPropertyVideo.isPresent()) {
						PropertyVideo foundPropertyVideo = optPropertyVideo.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS,
								"Property video");
						status.setPropertyVideo(foundPropertyVideo);
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_VIDEO, LogConstants.DETAIL,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE,
								"Property video");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property video id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Property video");
		}
		return status;
	}

	@Override
	public DesireStatus addPropertyVideo(String authToken, PropertyVideo propertyVideo) {
		LOGGER.info("addPropertyVideo called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				propertyVideo.setCreatedOn(dtNow);
				propertyVideo.setUpdatedOn(dtNow);
				propertyVideo.setActive(true);
				propertyVideo.setDeleted(false);
				PropertyVideo savePropertyVideo = propertyVideoRepository.save(propertyVideo);
				if (savePropertyVideo != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Property video");
					status.setPropertyVideo(savePropertyVideo);
					logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_VIDEO, LogConstants.ADD,
							savePropertyVideo.getCreatedOn(), savePropertyVideo.getUpdatedOn());
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Property video");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Property video");
		}
		return status;
	}

	@Override
	public DesireStatus editPropertyVideo(String authToken, PropertyVideo propertyVideo) {
		LOGGER.info("editPropertyVideo called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (propertyVideo.getId() != null && propertyVideo.getId() > 0) {
					Optional<PropertyVideo> optPropertyVideo = propertyVideoRepository
							.findByObjectId(propertyVideo.getId());
					if (optPropertyVideo.isPresent()) {
						PropertyVideo foundPropertyVideo = optPropertyVideo.get();
						foundPropertyVideo.setName(propertyVideo.getName());
						foundPropertyVideo.setDescription(propertyVideo.getDescription());
						foundPropertyVideo.setUrlPath(propertyVideo.getUrlPath());
						foundPropertyVideo.setUpdatedOn(dtNow);
						PropertyVideo savedPropertyVideo = propertyVideoRepository.save(foundPropertyVideo);
						if (savedPropertyVideo != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Property video");
							status.setPropertyVideo(savedPropertyVideo);
							logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_VIDEO, LogConstants.EDIT,
									null, savedPropertyVideo.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Property video");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"Property video id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit Property video");
		}
		return status;
	}

	@Override
	public DesireStatus deletePropertyVideo(String authToken, long videoId) {
		LOGGER.info("deletePropertyVideo called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (videoId > 0) {
					Optional<PropertyVideo> optPropertyVideo = propertyVideoRepository.findByObjectId(videoId);
					if (optPropertyVideo.isPresent()) {
						PropertyVideo foundPropertyVideo = optPropertyVideo.get();
						propertyVideoRepository.delete(foundPropertyVideo);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS,
								"Property video");
						logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_VIDEO, LogConstants.DELETE,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE,
								"Property video");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property video id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete Property video");
		}
		return status;
	}

	@Override
	public DesireStatus changePropertyTypeState(String authToken, ChangeStateRequest request) {
		LOGGER.info("changePropertyTypeState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (request.getId() > 0) {
					Optional<PropertyType> optObj = propertyTypeRepository.findByObjectId(request.getId());
					if (optObj.isPresent()) {
						PropertyType type = optObj.get();
						type.setActive(request.isActive());
						PropertyType savedType = propertyTypeRepository.save(type);
						if (savedType != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Property type");
							status.setPropertyType(savedType);
							logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_TYPE, LogConstants.ACTIVE,
									null, savedType.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Property type");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
								"Property type");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property type id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Change property type active state");
		}
		return status;
	}

	@Override
	public DesireStatus changePropertyPurposeState(String authToken, ChangeStateRequest request) {
		LOGGER.info("changePropertyPurposeState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (request.getId() > 0) {
					Optional<PropertyPurpose> optObj = propertyPurposeRepository.findByObjectId(request.getId());
					if (optObj.isPresent()) {
						PropertyPurpose purpose = optObj.get();
						purpose.setActive(request.isActive());
						PropertyPurpose savedPurpose = propertyPurposeRepository.save(purpose);
						if (savedPurpose != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Property purpose");
							status.setPropertyPurpose(savedPurpose);
							logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_PURPOSE,
									LogConstants.ACTIVE, null, savedPurpose.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Property purpose");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
								"Property purpose");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property purpose id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Change property purpose active state");
		}
		return status;
	}

	@Override
	public DesireStatus changePropertyStatusState(String authToken, ChangeStateRequest request) {
		LOGGER.info("changePropertyStatusState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			LOGGER.info("Authentication Status: {}", authStatus.getStatusType());
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				 LOGGER.info("User authenticated successfully.");
				if (request.getId() > 0) {
					Optional<PropertyStatus> optObj = propertyStatusRepository.findByObjectId(request.getId());
					if (optObj.isPresent()) {
						PropertyStatus propertyStatus = optObj.get();
						propertyStatus.setActive(request.isActive());
						PropertyStatus savedStatus = propertyStatusRepository.save(propertyStatus);
						if (savedStatus != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Property status");
							status.setPropertyStatus(savedStatus);
							LOGGER.info("Property status updated successfully for ID: {}", savedStatus.getId());
							logService.createLog(authStatus.getUser(), LogConstants.PROPERTY_STATUS,
									LogConstants.ACTIVE, null, savedStatus.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Property status");
							LOGGER.warn("Failed to save updated property status for ID: {}", request.getId());
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
								"Property status");
						LOGGER.warn("Property status with ID {} not found.", request.getId());
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Property status id");
					LOGGER.warn("Request ID is missing or invalid.");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
				LOGGER.warn("Unauthorized request. Invalid token.");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Change property status active state");
			LOGGER.error("Exception in changePropertyStatusState: ", e);
		}
		return status;
	}
	


	@Override
	public DesireStatus requestCommentsByUserAndRequest(String authToken, long userId, long requestId,
			DesireSearchRequest request) {
		LOGGER.info("requestCommentsByUserAndRequest called!");
		DesireStatus status = new DesireStatus();
		List<Comment> comments = null;
		Page<Comment> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				LOGGER.info("requestCommentsByUserAndRequest >> userId >> " + userId);
				LOGGER.info("requestCommentsByUserAndRequest >> requestId >> " + requestId);
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);

				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = commentRepository.findAllUserAndRequestASC(userId, requestId, pageable);
				} else {
					page = commentRepository.findAllUserAndRequestDESC(userId, requestId, pageable);
				}

				comments = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Comment");
				status.setRequestComments(comments);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.REQUEST_COMMENT, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"List Comment");
		}
		return status;
	}

	@Override
	public DesireStatus requestCommentById(String authToken, long commentId) {
		LOGGER.info("requestCommentById called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (commentId > 0) {
					Optional<Comment> foundRequestComment = commentRepository.findByObjectId(commentId);
					if (foundRequestComment.isPresent()) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Comment");
						status.setRequestComment(foundRequestComment.get());
						logService.createLog(authStatus.getUser(), LogConstants.REQUEST_COMMENT, LogConstants.DETAIL,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Comment");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Comment id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail Comment");
		}
		return status;
	}

	@Override
	public DesireStatus addRequestComment(String authToken, Comment comment) {
		LOGGER.info("addRequestComment called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("addRequestComment object recieved >> " + comment);
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {

				if (comment.getUser().getId() != null && comment.getUser().getId() > 0) {
					if (comment.getLead().getId() != null && comment.getLead().getId() > 0) {
						Optional<User> optUser = userRepository.findByObjectId(comment.getUser().getId());
						Optional<Lead> optLead = leadRepository.findByObjectId(comment.getLead().getId());
						if (optUser.isPresent() && optLead.isPresent()) {
							User user = optUser.get();
							if (comment.getParentId() > 0) {
								Optional<Comment> optParent = commentRepository.findByObjectId(comment.getParentId());
								if (optParent.isPresent()) {
									comment.setParent(optParent.get());
								}
							}
							comment.setUser(user);
							comment.setCreatedOn(dtNow);
							comment.setUpdatedOn(dtNow);
							comment.setActive(true);
							comment.setDeleted(false);
							Comment savedRequestComment = commentRepository.save(comment);
							if (savedRequestComment != null) {
								status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS,
										"Comment");
								status.setRequestComment(savedRequestComment);
								logService.createLog(authStatus.getUser(), LogConstants.REQUEST_COMMENT,
										LogConstants.ADD, savedRequestComment.getCreatedOn(),
										savedRequestComment.getUpdatedOn());
							} else {
								status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE,
										"Comment");
							}
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
									"User/Comment");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"Comment id");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add Comment");
		}

		return status;
	}

	@Override
	public DesireStatus editRequestComment(String authToken, Comment comment) {
		LOGGER.info("editRequestComment called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (comment.getId() != null && comment.getId() > 0) {
					Optional<Comment> optRequestComment = commentRepository.findByObjectId(comment.getId());
					if (optRequestComment.isPresent()) {
						Comment foundRequestComment = optRequestComment.get();
						foundRequestComment.setComment(comment.getComment());
						foundRequestComment.setUpdatedOn(dtNow);
						Comment savedRequestComment = commentRepository.save(foundRequestComment);
						if (savedRequestComment != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "Comment");
							status.setRequestComment(savedRequestComment);
							logService.createLog(authStatus.getUser(), LogConstants.REQUEST_COMMENT, LogConstants.EDIT,
									null, savedRequestComment.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "Comment");
						}

					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"Comment id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit Comment");
		}
		return status;
	}

	@Override
	public DesireStatus deleteRequestComment(String authToken, long commentId) {
		LOGGER.info("deleteRequestComment called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (commentId > 0) {
					Optional<Comment> optRequestComment = commentRepository.findByObjectId(commentId);
					if (optRequestComment.isPresent()) {
						Comment foundRequestComment = optRequestComment.get();
						commentRepository.delete(foundRequestComment);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "Comment");
						logService.createLog(authStatus.getUser(), LogConstants.REQUEST_COMMENT, LogConstants.DELETE,
								null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "Comment");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Comment id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status.setStatusType(Constants.STATUS_ERROR);
			status.setText("Delete request comment got error : " + e.getMessage());
		}
		return status;
	}

	@Override
	public DesireStatus changeRequestCommentState(String authToken, ChangeStateRequest request) {
		LOGGER.info("changeRequestCommentState called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (request.getId() > 0) {
					Optional<Comment> optObj = commentRepository.findByObjectIdToActivate(request.getId());
					if (optObj.isPresent()) {
						Comment comment = optObj.get();
						comment.setActive(request.isActive());
						Comment savedComment = commentRepository.save(comment);
						if (savedComment != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Request comment");
							status.setRequestComment(savedComment);
							logService.createLog(authStatus.getUser(), LogConstants.REQUEST_COMMENT,
									LogConstants.ACTIVE, null, savedComment.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Request comment");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST,
								"Request comment");
					}

				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Request comment id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Change request comment active state");
		}
		return status;
	}

	@Override
	public DesireStatus favorites(String authToken, DesireSearchRequest request) {
		LOGGER.info("CouponService >> favoriteList called!");
		DesireStatus status = new DesireStatus();
		List<Favorite> favorites = null;
		Page<Favorite> page = null;
		int pageNumber = 0, pageSize = 0;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);
				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = favoriteRepository.findAllASC(authStatus.getUser(), pageable);
				} else {
					page = favoriteRepository.findAllDESC(authStatus.getUser(), pageable);
				}
				favorites = page.getContent();

				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Favorite");
				status.setFavorites(favorites);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.FAVORITE, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Favoritees");
		}
		return status;
	}

	@Override
	public DesireStatus viewFavorite(String authToken, long favoriteId) {
		LOGGER.info("CouponService >> viewFavorite called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (favoriteId > 0) {
					Optional<Favorite> optFavorite = favoriteRepository.findObjectByUserAndId(authStatus.getUser(),
							favoriteId);
					if (optFavorite.isPresent()) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Favorite");
						status.setFavorite(optFavorite.get());
						logService.createLog(authStatus.getUser(), LogConstants.FAVORITE, LogConstants.DETAIL, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Favorite");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Favorite id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"View coupon");
		}
		return status;
	}

	@Override
	public DesireStatus addFavorite(String authToken, Favorite favorite) {
		LOGGER.info("CouponService >> addFavorite called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("CouponService >> addFavorite object recieved >> " + favorite);
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {

				Optional<Lead> optLead = leadRepository.findByObjectId(favorite.getLeadId());
				if (optLead.isPresent()) {
					Optional<Favorite> optFavorite = favoriteRepository.findObjectByUserAndLead(authStatus.getUser(),
							favorite.getLeadId());
					if (optFavorite.isEmpty()) {
						Lead foundLead = optLead.get();
						favorite.setUser(authStatus.getUser());
						favorite.setLead(foundLead);
						favorite.setCreatedOn(dtNow);
						favorite.setUpdatedOn(dtNow);
						favorite.setActive(true);
						favorite.setDeleted(false);
						Favorite savedFavorite = favoriteRepository.save(favorite);
						if (savedFavorite != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Favorite");
							status.setFavorite(savedFavorite);
							logService.createLog(authStatus.getUser(), LogConstants.FAVORITE, LogConstants.ADD,
									savedFavorite.getCreatedOn(), savedFavorite.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Favorite");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_EXIST, "Property");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Property");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add favorite");
		}

		return status;
	}

	@Override
	public DesireStatus editFavorite(String authToken, Favorite favorite) {
		LOGGER.info("CouponService >> editFavorite called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (favorite.getId() != null && favorite.getId() > 0) {
					Optional<Favorite> optFavorite = favoriteRepository.findObjectByUserAndId(authStatus.getUser(),
							favorite.getId());
					if (optFavorite.isPresent()) {
						Optional<Lead> optLead = leadRepository.findByObjectId(favorite.getLeadId());
						if (optLead.isPresent()) {
							Favorite foundFavorite = optFavorite.get();
							Lead foundLead = optLead.get();
							foundFavorite.setUser(authStatus.getUser());
							foundFavorite.setLead(foundLead);
							foundFavorite.setUpdatedOn(dtNow);
							Favorite savedFavorite = favoriteRepository.save(foundFavorite);
							if (savedFavorite != null) {
								status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
										"Favorite");
								status.setFavorite(savedFavorite);
								logService.createLog(authStatus.getUser(), LogConstants.FAVORITE, LogConstants.EDIT,
										null, savedFavorite.getUpdatedOn());
							} else {
								status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
										"Favorite");
							}
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Property");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Favorite");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Favorite id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (

		Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit favorite");
		}
		return status;
	}

	@Override
	public DesireStatus deleteFavorite(String authToken, long favoriteId) {
		LOGGER.info("CouponService >> deleteFavorite called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (favoriteId > 0) {
					Optional<Favorite> optFavorite = favoriteRepository.findObjectById(favoriteId);
					if (optFavorite.isPresent()) {
						Favorite foundFavorite = optFavorite.get();
						favoriteRepository.delete(foundFavorite);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "Favorite");
						logService.createLog(authStatus.getUser(), LogConstants.FAVORITE, LogConstants.DELETE, null,
								null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "Favorite");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Favorite id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete favorite");
		}
		return status;
	}

	@Override
	public DesireStatus togglePropertyPublishStatus(String authToken, Long propertyId, Boolean isPublished) {
	    DesireStatus status = new DesireStatus();
	    try {
	        DesireStatus authStatus = userTokenService.getUserInfo(authToken);
	        if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
	            Optional<Property> optProperty = propertyRepository.findById(propertyId);
	            if (optProperty.isPresent()) {
	                Property property = optProperty.get();
	                property.setPublished(isPublished);
	                propertyRepository.save(property);
	                status = Resources.setStatus(Constants.STATUS_SUCCESS, "Publish status updated", "Property");
	            } else {
	                status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "Property");
	            }
	        } else {
	            status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(), "Toggle Publish Status");
	    }
	    return status;
	}
	
}
