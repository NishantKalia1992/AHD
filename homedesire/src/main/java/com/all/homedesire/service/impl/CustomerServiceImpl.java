package com.all.homedesire.service.impl;

import com.all.homedesire.common.Constants;
import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.LogConstants;
import com.all.homedesire.common.Resources;
import com.all.homedesire.entities.Customer;
import com.all.homedesire.repository.CustomerRepository;
import com.all.homedesire.resources.dto.DesireSearchRequest;
import com.all.homedesire.security.service.UserTokenService;
import com.all.homedesire.service.CustomerService;
import com.all.homedesire.service.LogService;

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
public class CustomerServiceImpl implements CustomerService {
	Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);

	@Autowired
	UserTokenService userTokenService;
	@Autowired
	LogService logService;
	
	@Autowired
	CustomerRepository customerRepository;

	@Override
	public DesireStatus customers(String authToken, DesireSearchRequest request) {
		LOGGER.info("customers called!");
		DesireStatus status = new DesireStatus();
		List<Customer> customers = null;
		Page<Customer> page = null;
		int pageNumber = 0, pageSize = 1;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber = pageNumber - 1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);
				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = customerRepository.findAllASC(pageable);
				} else {
					page = customerRepository.findAllDESC(pageable);
				}
				customers = page.getContent();
				
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Customer");
				status.setCustomers(customers);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
				logService.createLog(authStatus.getUser(), LogConstants.CUSTOMER, LogConstants.LIST, null, null);
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),"List Customer");
		}
		return status;
	}

	@Override
	public DesireStatus viewCustomer(String authToken, long customerId) {
		LOGGER.info("viewCustomer called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (customerId > 0) {
					Optional<Customer> optCustomer = customerRepository.findByObjectId(customerId);
					if (optCustomer.isPresent()) {
						Customer foundCustomer = optCustomer.get();
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "Customer");
						status.setCustomer(foundCustomer);
						logService.createLog(authStatus.getUser(), LogConstants.CUSTOMER, LogConstants.DETAIL, null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "Customer");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,"Customer id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status.setStatusType(Constants.STATUS_ERROR);
			status.setText("View customer got error : " + e.getMessage());
		}
		return status;
	}

	@Override
	public DesireStatus addCustomer(String authToken, Customer customer) {
		LOGGER.info("addCustomer called!");
		DesireStatus status = new DesireStatus();
		LOGGER.info("Customer object recieved >> " + customer);
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				customer.setCreatedOn(dtNow);
				customer.setUpdatedOn(dtNow);
				customer.setActive(true);
				customer.setDeleted(false);
				Customer saveCustomer = customerRepository.save(customer);
				if (saveCustomer != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Customer");
					status.setCustomer(saveCustomer);
					logService.createLog(authStatus.getUser(), LogConstants.CUSTOMER, LogConstants.ADD, saveCustomer.getCreatedOn(), saveCustomer.getUpdatedOn());
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Customer");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),"Add Customer");
		}
		return status;
	}

	@Override
	public DesireStatus editCustomer(String authToken, Customer customer) {
		LOGGER.info("editCustomer called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (customer.getId() != null && customer.getId() > 0) {
					Optional<Customer> optCustomer = customerRepository.findByObjectId(customer.getId());
					if (optCustomer.isPresent()) {
						Customer foundCustomer = optCustomer.get();
						foundCustomer.setFirstName(customer.getFirstName());
						foundCustomer.setLastName(customer.getLastName());
						foundCustomer.setEmailId(customer.getEmailId());
						foundCustomer.setMobileNo(customer.getMobileNo());
						foundCustomer.setUpdatedOn(dtNow);
						Customer savedCustomer = customerRepository.save(foundCustomer);
						if (savedCustomer != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS, "Customer");
							status.setCustomer(savedCustomer);
							logService.createLog(authStatus.getUser(), LogConstants.CUSTOMER, LogConstants.EDIT, null, savedCustomer.getUpdatedOn());
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE, "Customer");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,"Customer id");
					}
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),"Edit Customer");
		}
		return status;
	}

	@Override
	public DesireStatus deleteCustomer(String authToken, long customerId) {
		LOGGER.info("deleteCustomer called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (customerId > 0) {
					Optional<Customer> optCustomer = customerRepository.findByObjectId(customerId);
					if (optCustomer.isPresent()) {
						Customer foundCustomer = optCustomer.get();
						customerRepository.delete(foundCustomer);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "Customer");
						logService.createLog(authStatus.getUser(), LogConstants.CUSTOMER, LogConstants.DELETE, null, null);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "Customer");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,"Customer id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),"Delete Customer");
		}
		return status;
	}

}
