package com.all.homedesire.service;

import java.util.Date;

import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.entities.User;
import com.all.homedesire.entities.UserLog;
import com.all.homedesire.resources.dto.DesireSearchRequest;

public interface LogService {
	public DesireStatus logs(String authToken, DesireSearchRequest request);

	public DesireStatus viewLog(String authToken, long logId);

	public DesireStatus addLog(String authToken, UserLog userLog);

	public DesireStatus editLog(String authToken, UserLog userLog);

	public DesireStatus deleteLog(String authToken, long logId);

	public boolean createLog(User user, String action, String subAction, Date createdOn, Date updatedOn);

}
