package com.dsa360.api.dao;

import java.util.List;

import com.dsa360.api.entity.SystemUserEntity;
import com.dsa360.api.security.CustomUserDetail;

public interface SystemUserDao {
	public CustomUserDetail loadUserByUserId(String userId);

	public abstract SystemUserEntity getSystemUserByUsername(String username);

	public abstract List<SystemUserEntity> getAllSystemUser();

	public abstract void updateSystemUser(SystemUserEntity userEntity);

}
