package com.dsa360.api.service;

import com.dsa360.api.entity.master.MasterUserEntity;
import com.dsa360.api.security.CustomUserDetail;

public interface MasterUserService {
    CustomUserDetail loadUserByUserId(String userId);
    MasterUserEntity getMasterUserByUsername(String username);
    // Add other methods if needed, e.g., MasterUserEntity updateMasterUser(MasterUserDto dto);
}