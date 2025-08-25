package com.dsa360.api.service;

import java.util.List;

import com.dsa360.api.dto.SystemUserDto;
import com.dsa360.api.entity.SystemUserEntity;
import com.dsa360.api.security.CustomUserDetail;

public interface SystemUserService {
	public abstract CustomUserDetail loadUserByUserId(String userId);

	public abstract SystemUserEntity getSystemUserByUsername(String username);

	public abstract List<SystemUserEntity> getAllSystemUser();

	public abstract SystemUserEntity updateSystemUser(SystemUserDto userDto);

}
