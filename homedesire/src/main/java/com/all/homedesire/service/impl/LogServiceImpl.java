package com.all.homedesire.service.impl;

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

import com.all.homedesire.common.Constants;
import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.Resources;
import com.all.homedesire.entities.User;
import com.all.homedesire.entities.UserLog;
import com.all.homedesire.repository.UserLogRepository;
import com.all.homedesire.resources.dto.DesireSearchRequest;
import com.all.homedesire.security.service.UserTokenService;
import com.all.homedesire.service.LogService;

@Service
public class LogServiceImpl implements LogService {
	Logger LOGGER = LoggerFactory.getLogger(LogService.class);

	@Autowired
	UserTokenService userTokenService;
	
	@Autowired
	UserLogRepository userLogRepository;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public DesireStatus logs(String authToken, DesireSearchRequest request) {
		LOGGER.info("UserLogService >> userLogs called!");
		DesireStatus status = new DesireStatus();
		List<UserLog> userLogs = null;
		Page page = null;
		int pageNumber = 0, pageSize = 1;
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);
				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					page = userLogRepository.findAllASC(pageable);
				} else {
					page = userLogRepository.findAllDESC(pageable);
				}

				userLogs = page.getContent();
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "User log");
				status.setUserLogs(userLogs);
				status.setTotalRecord(page.getTotalElements());
				status.setTotalPage(page.getTotalPages());
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(), "Emails");
		}
		return status;
	}

	@Override
	public DesireStatus viewLog(String authToken, long logId) {
		LOGGER.info("UserLogService >> viewUserLog called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (logId > 0) {
					Optional<UserLog> foundUserLog = userLogRepository.findByObjectId(logId);
					if (foundUserLog != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS, "User log");
						status.setUserLog(foundUserLog.get());
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE, "User log");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Email id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"View email");
		}
		return status;
	}

	@Override
	public DesireStatus addLog(String authToken, UserLog userLog) {
		LOGGER.info("UserLogService >> addUserLog called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("UserLogService >> addUserLog object recieved >> " + userLog);
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				userLog.setCreatedOn((userLog.getCreatedOn() != null) ? userLog.getCreatedOn() : dtNow);
				userLog.setUpdatedOn((userLog.getUpdatedOn() != null) ? userLog.getUpdatedOn() : dtNow);
				userLog.setActive(true);
				userLog.setDeleted(false);
				UserLog savedUserLog = userLogRepository.save(userLog);
				if (savedUserLog != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "User log");
					status.setUserLog(savedUserLog);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "User log");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add email");
		}

		return status;
	}

	@Override
	public DesireStatus editLog(String authToken, UserLog userLog) {
		LOGGER.info("UserLogService >> editUserLog called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (userLog.getId() != null && userLog.getId() > 0) {
					Optional<UserLog> optUserLog = userLogRepository.findByObjectId(userLog.getId());
					if (optUserLog.isPresent()) {
						UserLog foundUserLog = optUserLog.get();
						foundUserLog.setAction(userLog.getAction());
						foundUserLog.setSubAction(userLog.getSubAction());
						foundUserLog.setUpdatedOn((userLog.getUpdatedOn() != null) ? userLog.getUpdatedOn() : dtNow);
						UserLog savedUserLog = userLogRepository.save(foundUserLog);
						if (savedUserLog != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"User log");
							status.setUserLog(savedUserLog);
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"User log");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Email id");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Email id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit email");
		}
		return status;
	}

	@Override
	public DesireStatus deleteLog(String authToken, long logId) {
		LOGGER.info("UserLogService >> deleteUserLog called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = userTokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (logId > 0) {
					Optional<UserLog> optUserLog = userLogRepository.findByObjectId(logId);
					if (optUserLog.isPresent()) {
						UserLog foundUserLog = optUserLog.get();
						userLogRepository.delete(foundUserLog);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS, "User log");
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE, "User log");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "Email id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete email");
		}
		return status;
	}

	@Override
	public boolean createLog(User user, String action, String subAction, Date createdOn, Date updatedOn) {
		LOGGER.info("UserLogService >> createLog called!");
		boolean status = false;
		Date dtNow = new Date();
		try {
			UserLog userLog = new UserLog();
			userLog.setUser(user);
			userLog.setAction(action);
			userLog.setSubAction(subAction);
			userLog.setCreatedOn((userLog.getCreatedOn() != null) ? createdOn : dtNow);
			userLog.setUpdatedOn((userLog.getUpdatedOn() != null) ? updatedOn : dtNow);
			userLog.setActive(true);
			userLog.setDeleted(false);
			UserLog savedUserLog = userLogRepository.save(userLog);
			if (savedUserLog != null) {
				status = true;
			}

		} catch (Exception e) {
			status = false;
		}
		return status;
	}

}
