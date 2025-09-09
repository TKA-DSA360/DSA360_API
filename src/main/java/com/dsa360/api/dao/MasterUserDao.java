package com.dsa360.api.dao;

import com.dsa360.api.entity.master.MasterUserEntity;
import com.dsa360.api.security.CustomUserDetail;

import java.util.Optional;

public interface MasterUserDao {
    Optional<MasterUserEntity> findByUsername(String username);
    CustomUserDetail loadUserByUserId(String userId);
    // Add other methods if needed, e.g., void save(MasterUserEntity entity);
}