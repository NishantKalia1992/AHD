package com.all.homedesire.security.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.all.homedesire.common.Constants;
import com.all.homedesire.common.DesireStatus;
import com.all.homedesire.common.Resources;
import com.all.homedesire.entities.User;
import com.all.homedesire.repository.UserRepository;
import com.all.homedesire.security.jwt.JwtUtils;


@Service
public class UserTokenServiceImpl implements UserTokenService {
	Logger LOGGER = LoggerFactory.getLogger(UserTokenService.class);
	
	@Autowired
	JwtUtils jwtUtils;
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	public DesireStatus getUserInfo(String token) {
		token = token.replace("Bearer ", "");
		LOGGER.info("UserTokenService >> getUserInfo >> token >> " + token);
		DesireStatus status = new DesireStatus();
		User user = null;
		try {
			String userName = jwtUtils.getClaims(token);
			LOGGER.info("UserTokenService >> getUserInfo >> userName >> " + userName);
			Optional<User> optUser = userRepository.findByEmailId(userName);
			if (optUser.isPresent()) {
				user = optUser.get();
				status = Resources.setStatus(Constants.STATUS_SUCCESS, Constants.OBJ_EXIST, "User");

				status.setUser(user);
			} else {
				status = Resources.setStatus(Constants.STATUS_FAILURE, Constants.OBJ_NOT_EXIST, "User");

			}
		} catch (Exception e) {
			status = Resources.setStatus(Constants.STATUS_ERROR, Constants.EXECUTION_ERROR + e.getMessage(),"User info");
		}
		return status;
	}

}
