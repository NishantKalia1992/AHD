package com.all.homedesire.service;

import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.entities.AppNotification;
import com.all.homedesire.entities.User;
import com.all.homedesire.resources.dto.DesireSearchRequest;

public interface NotificationService {
	public DesireStatus notifications(String authToken, DesireSearchRequest request);
	
	public DesireStatus notifications(String authToken, long userId, DesireSearchRequest request);

	public DesireStatus viewNotification(String authToken, long notificationId);

	public DesireStatus addNotification(String authToken, AppNotification notification);
	
	public DesireStatus addNotification(AppNotification notification, long createdBy);

	public DesireStatus editNotification(String authToken, AppNotification notification);

	public DesireStatus deleteNotification(String authToken, long notificationId);
	
	public DesireStatus setReadStatus(String authToken, long notificationId, boolean isRead);
	
	public DesireStatus getCount(String authToken);
	
	public boolean setNotification(User user, String notificationFor, long createdBy);
}
