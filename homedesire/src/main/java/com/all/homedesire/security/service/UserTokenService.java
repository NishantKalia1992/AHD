package com.all.homedesire.security.service;

import com.all.homedesire.common.DesireStatus;

public interface UserTokenService {
	public DesireStatus getUserInfo(String token);
}
