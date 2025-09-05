package com.dsa360.api.dao;

import java.util.List;
import com.dsa360.api.entity.master.TenantEntity;

public interface TenantDao {
	TenantEntity findById(String tenantId);

	List<TenantEntity> findAll();

	void save(TenantEntity tenant);

	void delete(String tenantId);
}