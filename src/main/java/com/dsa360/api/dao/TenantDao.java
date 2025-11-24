package com.dsa360.api.dao;

import java.util.List;

import com.dsa360.api.entity.RegionsEntity;
import com.dsa360.api.entity.RoleEntity;
import com.dsa360.api.entity.SystemUserEntity;
import com.dsa360.api.entity.master.TenantEntity;

public interface TenantDao {
	TenantEntity findById(String tenantId);

	List<TenantEntity> findAll();

	void save(TenantEntity tenant);

	void delete(String tenantId);
	
	void update(TenantEntity tenant);
	
	void saveTenantEntities(String tenantId, RoleEntity adminRole, RegionsEntity defaultRegion, SystemUserEntity adminUser);
}