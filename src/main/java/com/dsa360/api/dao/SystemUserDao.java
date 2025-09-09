package com.dsa360.api.dao;

import java.util.List;

import com.dsa360.api.entity.RoleEntity;
import com.dsa360.api.entity.SystemUserEntity;
import com.dsa360.api.security.CustomUserDetail;

public interface SystemUserDao {
	public CustomUserDetail loadUserByUserId(String userId);
    public SystemUserEntity getSystemUserByUsername(String username);
    public List<SystemUserEntity> getAllSystemUser();
    public void updateSystemUser(SystemUserEntity userEntity);
    public void saveRole(RoleEntity role);
    public void save(SystemUserEntity user);

}
