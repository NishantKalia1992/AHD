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
import com.all.homedesire.common.NotificationConstants;
import com.all.homedesire.common.Resources;
import com.all.homedesire.entities.AppNotification;
import com.all.homedesire.entities.User;
import com.all.homedesire.repository.AppNotificationRepository;
import com.all.homedesire.repository.UserRepository;
import com.all.homedesire.resources.dto.DesireSearchRequest;
import com.all.homedesire.security.service.UserTokenService;
import com.all.homedesire.service.LogService;
import com.all.homedesire.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {
	Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

	@Autowired
	AppNotificationRepository appNotificationRepository;
	@Autowired
	UserTokenService tokenService;
	@Autowired
	LogService logService;
	
	@Autowired
	UserRepository userRepository;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public DesireStatus notifications(String authToken, DesireSearchRequest request) {
		LOGGER.info("NotificationService >> notifications called!");
		DesireStatus status = new DesireStatus();
		List<AppNotification> appNotifications = null;
		Page propPage = null;
		int pageNumber = 0, pageSize = 1;
		try {
			DesireStatus authStatus = tokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
				pageNumber= pageNumber-1;
				pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
				Pageable pageable = PageRequest.of(pageNumber, pageSize);
				if (request.getOrderBy().equalsIgnoreCase("ASC")) {
					propPage = appNotificationRepository.findAllASC(pageable);
				} else {
					propPage = appNotificationRepository.findAllDESC(pageable);
				}
				appNotifications = propPage.getContent();
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Notification");
				status.setNotifications(appNotifications);
				status.setTotalRecord(propPage.getTotalElements());
				status.setTotalPage(propPage.getTotalPages());
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(), "Emails");
		}
		return status;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public DesireStatus notifications(String authToken, long userId, DesireSearchRequest request) {
		LOGGER.info("NotificationService >> notifications called!");
		DesireStatus status = new DesireStatus();
		List<AppNotification> appNotifications = null;
		Page propPage = null;
		int pageNumber = 0, pageSize = 1;
		try {
			DesireStatus authStatus = tokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				User user = userRepository.getReferenceById(userId);
				if (user != null) {
					pageNumber = (request.getPageNumber() >= 0) ? request.getPageNumber() : pageNumber;
					pageNumber = pageNumber -1;
					pageSize = (request.getPageSize() >= 0) ? request.getPageSize() : pageSize;
					Pageable pageable = PageRequest.of(pageNumber, pageSize);
					if (request.getOrderBy().equalsIgnoreCase("ASC")) {
						propPage = appNotificationRepository.findAllByUserASC(userId, pageable);
					} else {
						propPage = appNotificationRepository.findAllByUserDESC(userId, pageable);
					}
					appNotifications = propPage.getContent();
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.LIST_SUCCESS, "Notification");
					status.setNotifications(appNotifications);
					status.setTotalRecord(propPage.getTotalElements());
					status.setTotalPage(propPage.getTotalPages());
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(), "Notification");
		}
		return status;
	}

	@Override
	public DesireStatus viewNotification(String authToken, long notificationId) {
		LOGGER.info("NotificationService >> viewNotification called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = tokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (notificationId > 0) {
					Optional<AppNotification> optAppNotification = appNotificationRepository.findById(notificationId);
					if (optAppNotification != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS,
								"Notification");
						status.setNotification(optAppNotification.get());
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DETAIL_FAILURE,
								"Notification");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Notification id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"View notification");
		}
		return status;
	}

	@Override
	public DesireStatus addNotification(String authToken, AppNotification notification) {
		LOGGER.info("NotificationService >> addNotification called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("NotificationService >> addNotification object recieved >> " + notification.getNotificationFor());
		LOGGER.info("NotificationService >> addNotification object recieved >> " + notification.getNotificationTitle());

		try {
			DesireStatus authStatus = tokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				User reqUser = authStatus.getUser();
				User user = (notification.getUserId() > 0) ? userRepository.getReferenceById(notification.getUserId())
						: notification.getUser();
				if (user != null) {
					notification.setUser(user);
					notification.setIsRead(false);
					notification
							.setCreatedOn((notification.getCreatedOn() != null) ? notification.getCreatedOn() : dtNow);
					notification.setCreatedBy(reqUser.getId());
					notification
							.setUpdatedOn((notification.getUpdatedOn() != null) ? notification.getUpdatedOn() : dtNow);
					notification.setUpdatedBy(reqUser.getId());
					notification.setActive(true);
					notification.setDeleted(false);
					AppNotification savedAppNotification = appNotificationRepository.save(notification);
					if (savedAppNotification != null) {
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Notification");
						status.setNotification(savedAppNotification);
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Notification");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add notification");
		}

		return status;
	}

	@Override
	public DesireStatus addNotification(AppNotification notification, long createdBy) {
		LOGGER.info("NotificationService >> addNotification called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		LOGGER.info("NotificationService >> addNotification object recieved >> " + notification.getNotificationFor());
		LOGGER.info("NotificationService >> addNotification object recieved >> " + notification.getNotificationTitle());

		try {
			if (notification.getUser() != null) {
				notification.setUser(notification.getUser());
				notification.setIsRead(false);
				notification.setCreatedOn((notification.getCreatedOn() != null) ? notification.getCreatedOn() : dtNow);
				notification.setCreatedBy(createdBy);
				notification.setUpdatedOn((notification.getUpdatedOn() != null) ? notification.getUpdatedOn() : dtNow);
				notification.setUpdatedBy(createdBy);
				notification.setActive(true);
				notification.setDeleted(false);
				AppNotification savedAppNotification = appNotificationRepository.save(notification);
				if (savedAppNotification != null) {
					status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.SAVE_SUCCESS, "Notification");
					status.setNotification(savedAppNotification);
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.SAVE_FAILURE, "Notification");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING, "User id");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Add notification");
		}

		return status;
	}

	@Override
	public DesireStatus editNotification(String authToken, AppNotification notification) {
		LOGGER.info("NotificationService >> editNotification called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = tokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				User reqUser = authStatus.getUser();
				if (notification.getId() != null && notification.getId() > 0) {
					AppNotification foundAppNotification = appNotificationRepository
							.getReferenceById(notification.getId());
					if (foundAppNotification != null) {
						foundAppNotification.setNotificationTitle(notification.getNotificationTitle());
						foundAppNotification.setNotificationText(notification.getNotificationText());
						foundAppNotification.setNotificationFor(notification.getNotificationFor());
						foundAppNotification.setUpdatedBy(reqUser.getId());
						foundAppNotification.setUpdatedOn(
								(notification.getUpdatedOn() != null) ? notification.getUpdatedOn() : dtNow);
						AppNotification savedAppNotification = appNotificationRepository.save(foundAppNotification);
						if (savedAppNotification != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Notification");
							status.setNotification(savedAppNotification);
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Notification");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"Notification id");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Notification id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Edit notification");
		}
		return status;
	}

	@Override
	public DesireStatus deleteNotification(String authToken, long notificationId) {
		LOGGER.info("NotificationService >> deleteNotification called!");
		DesireStatus status = new DesireStatus();
		try {
			DesireStatus authStatus = tokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				if (notificationId > 0) {
					AppNotification foundAppNotification = appNotificationRepository.getReferenceById(notificationId);
					if (foundAppNotification != null) {
						appNotificationRepository.delete(foundAppNotification);
						status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DELETE_SUCCESS,
								"Notification");
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.DELETE_FAILURE,
								"Notification");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Notification id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"Delete notification");
		}
		return status;
	}

	@Override
	public DesireStatus setReadStatus(String authToken, long notificationId, boolean isRead) {
		LOGGER.info("NotificationService >> setReadStatus called!");
		DesireStatus status = new DesireStatus();
		Date dtNow = new Date();
		try {
			DesireStatus authStatus = tokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				User reqUser = authStatus.getUser();
				if (notificationId > 0) {
					AppNotification foundAppNotification = appNotificationRepository.getReferenceById(notificationId);
					if (foundAppNotification != null) {
						foundAppNotification.setIsRead(isRead);
						foundAppNotification.setUpdatedBy(reqUser.getId());
						foundAppNotification.setUpdatedOn(dtNow);
						AppNotification savedAppNotification = appNotificationRepository.save(foundAppNotification);
						if (savedAppNotification != null) {
							status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.UPDATE_SUCCESS,
									"Notification");
							status.setNotification(savedAppNotification);
						} else {
							status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.UPDATE_FAILURE,
									"Notification");
						}
					} else {
						status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
								"Notification id");
					}
				} else {
					status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.PARAMETER_MISSING,
							"Notification id");
				}
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}

		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"View notification");
		}
		return status;
	}
	
	@Override
	public DesireStatus getCount(String authToken) {
		LOGGER.info("NotificationService >> viewNotification called!");
		DesireStatus status = new DesireStatus();
		int numCount=0;
		try {
			DesireStatus authStatus = tokenService.getUserInfo(authToken);
			if (authStatus.getStatusType().equals(Constants.STATUS_SUCCESS) && authStatus.getUser() != null) {
				List<AppNotification> notifications = appNotificationRepository.findAllUnreadByUser(authStatus.getUser().getId());
				numCount = notifications.size();
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.DETAIL_SUCCESS,
						"Notification");
			} else {
				status = Resources.setStatus(Constants.STATUS_UNAUTHORIZE, Constants.INVALID_TOKEN, "");
			}
			status.setTotalRecord(numCount);
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),
					"View notification");
		}
		return status;
	}

	@Override
	public boolean setNotification(User user, String notificationFor, long createdBy) {
		boolean result = false;
		try {
			String title = "";
			String message = "";
			LOGGER.info("NotificationService >> setNotification >> notificationFor >> "+notificationFor);
			if (notificationFor.equals(NotificationConstants.REGISTER)) {
				title = NotificationConstants.TITLE_USER_REGISTER;
				message = NotificationConstants.MESSAGE_USER_REGISTER;
			}
			LOGGER.info("NotificationService >> setNotification >> title >> "+title);
			LOGGER.info("NotificationService >> setNotification >> message >> "+message);
			AppNotification appNotification = new AppNotification();
			appNotification.setUser(user);
			appNotification.setNotificationTitle(title);
			appNotification.setNotificationText(message);
			appNotification.setNotificationFor(notificationFor);
			DesireStatus status = addNotification(appNotification, createdBy);
			LOGGER.info("NotificationService >> setNotification >> status.getStatusType() >> "+status.getStatusType());
			LOGGER.info("NotificationService >> setNotification >> status.getText() >> "+status.getText());
			if (status.getStatusType().equals(Constants.STATUS_SUCCESS)) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
