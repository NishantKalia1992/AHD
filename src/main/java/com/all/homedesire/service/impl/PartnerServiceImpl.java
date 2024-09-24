package com.all.homedesire.service.impl;

import com.all.homedesire.common.Constants;
import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.LogConstants;
import com.all.homedesire.common.Resources;
import com.all.homedesire.entities.Partner;
import com.all.homedesire.entities.User;
import com.all.homedesire.enums.ERole;
import com.all.homedesire.repository.PartnerRepository;
import com.all.homedesire.resources.dto.DesireSearchRequest;
import com.all.homedesire.security.service.UserTokenService;
import com.all.homedesire.service.LogService;
import com.all.homedesire.service.PartnerService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PartnerServiceImpl implements PartnerService {
	Logger LOGGER = LoggerFactory.getLogger(PartnerServiceImpl.class);

	@Autowired
	UserTokenService userTokenService;
	@Autowired
	LogService logService;

	@Autowired
	PartnerRepository partnerRepository;

	@Override
	public DesireStatus partners(String authToken, DesireSearchRequest request) {
		LOGGER.info("partners called!");
		DesireStatus status = new DesireStatus();
		List<Partner> partners = null;
		Page<Partner> page = null;
		int pageNumber = 0, pageSize = 1;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);
				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = partnerRepository.findAllASC(pageable);
				} else {
					page = partnerRepository.findAllDESC(pageable);
				}
				partners = page.getContent();
				
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Partner");
				status.setPartners(partners);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.PARTNER, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),"List partner");
		}
		return status;
	}

	@Override
	public DesireStatus viewPartner(String authToken, long partnerId) {
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (partnerId > 0) {
					Optional<Partner> optPartner = partnerRepository.findByObjectId(partnerId);
					if (optPartner.isPresent()) {
						Partner foundPartner = optPartner.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Partner");
						status.setPartner(foundPartner);
						logService.createLog(authStatus.getUser(), LogConstants.PARTNER, LogConstants.DETAIL, null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Partner");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Partner id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Detail partner");
		}
		return status;
	}

	@Override
	public DesireStatus addPartner(String authToken, Partner partner) {
		LOGGER.info("addPartner called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				
				if (partner.getStatus() == null || partner.getStatus().isEmpty()) {
	                partner.setStatus("NEW");
	                partner.setActive(true);
	            }
				partner.setCreatedOn(dtNow);
				partner.setUpdatedOn(dtNow);
//				partner.setActive(false);
				partner.setDeleted(false);
				Partner savedPartner = partnerRepository.save(partner);
				if (savedPartner != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Partner");
					status.setPartner(savedPartner);
					logService.createLog(authStatus.getUser(), LogConstants.PARTNER, LogConstants.ADD, savedPartner.getCreatedOn(), savedPartner.getUpdatedOn());
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Partner");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add partner");
		}
		return status;
	}

	@Override
	public DesireStatus editPartner(String authToken, Partner partner) {
	    LOGGER.info("editPartner called!");
	    DesireStatus status = new DesireStatus();
	    Date dtNow = new Date();
	    try {
	        DesireStatus authStatus = userTokenService.getUserInfo(authToken);
	        if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
	            User user = authStatus.getUser();

	            if (user.getRoles().stream().anyMatch(role -> role.getName().equals(ERole.ROLE_ADMIN))) {
	            	if (partner != null && partner.getId() != null && partner.getId() > 0) {
	                    Optional<Partner> optPartner = partnerRepository.findByObjectId(partner.getId());
	                    if (optPartner.isPresent()) {
	                        Partner foundPartner = optPartner.get();
	                        String currentStatus = foundPartner.getStatus();

	                        if ("NEW".equalsIgnoreCase(currentStatus) || 
	                            "HOLD".equalsIgnoreCase(currentStatus) || 
	                            "REJECT".equalsIgnoreCase(currentStatus) || 
	                            "DEACTIVATE".equalsIgnoreCase(currentStatus)) {
	                            
	                            if ("APPROVE".equalsIgnoreCase(partner.getStatus())) {
	                                foundPartner.setStatus("ACTIVE"); 
	                                foundPartner.setActive(true);     
	                            } else if ("HOLD".equalsIgnoreCase(partner.getStatus())) {
	                                foundPartner.setStatus("HOLD");
	                                foundPartner.setActive(true);   
	                            } else if ("REJECT".equalsIgnoreCase(partner.getStatus())) {
	                                foundPartner.setStatus("REJECT");
	                                foundPartner.setActive(false);  
	                            } else if ("DEACTIVATE".equalsIgnoreCase(partner.getStatus())) {
	                                foundPartner.setStatus("DEACTIVATE");
	                                foundPartner.setActive(false);    
	                            } else {
	                                foundPartner.setStatus(partner.getStatus());
	                                foundPartner.setActive(false);     
	                            }

	                            foundPartner.setFirstName(partner.getFirstName());
	                            foundPartner.setLastName(partner.getLastName());
	                            foundPartner.setEmailId(partner.getEmailId());
	                            foundPartner.setMobileNo(partner.getMobileNo());
	                            foundPartner.setUpdatedOn(dtNow);
	                            Partner savedPartner = partnerRepository.save(foundPartner);

	                            if (savedPartner != null) {
	                                status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "Partner");
	                                status.setPartner(savedPartner);
	                                logService.createLog(user, LogConstants.PARTNER, LogConstants.EDIT, null, savedPartner.getUpdatedOn());
	                            } else {
	                                status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "Partner");
	                            }
	                        } else {
	                            status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "Cannot edit partner in current status.");
	                        }
	                    } else {
	                        status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Partner id");
	                    }
	                } else {
	                    status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Partner id");
	                }
	            } else {
	                status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, "Only admins can change", "");
	            }
	        } else {
	            status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
	        }
	    } catch (Exception e) {
	        status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(), "Edit partner");
	    }
	    return status;
	}


	@Override
	public DesireStatus deletePartner(String authToken, long partnerId) {
		LOGGER.info("deletePartner called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (partnerId > 0) {
					Optional<Partner> optPartner = partnerRepository.findByObjectId(partnerId);
					if (optPartner.isPresent()) {
						Partner foundPartner = optPartner.get();
						partnerRepository.delete(foundPartner);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "Partner");
						logService.createLog(authStatus.getUser(), LogConstants.PARTNER, LogConstants.DELETE, null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "Partner");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Partner id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete partner");
		}
		return status;
	}

}
