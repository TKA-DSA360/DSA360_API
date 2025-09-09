package com.dsa360.api.serviceimpl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsa360.api.config.TenantContext;
import com.dsa360.api.dao.TenantDao;
import com.dsa360.api.entity.master.TenantEntity;
import com.dsa360.api.exceptions.SomethingWentWrongException;
import com.dsa360.api.service.TenantService;
import com.dsa360.api.utility.DynamicID;

@Service
public class TenantServiceImpl implements TenantService {

	private static final Logger log = LogManager.getLogger(TenantServiceImpl.class);

	@Autowired
	private TenantDao tenantDao;

	
	@Value("${tenant.dbUsername}")
	private String dbUsername;
	@Value("${tenant.dbPassword}")
	private String dbPassword;

	

	@Override
	@Transactional("masterTransactionManager")
	public String createTenant(String tenantName) {
		String tenantId = DynamicID.getGeneratedTenantId(tenantName);
		

		try {
			// Save tenant metadata in master database
			TenantEntity tenant = new TenantEntity();
			tenant.setTenantId(tenantId);
			tenant.setTenantName(tenantName);
			tenant.setDbUrl("jdbc:mysql://localhost:3306/dsa360_" + tenantId + "?createDatabaseIfNotExist=true");
			tenant.setDbUsername(dbUsername);
			tenant.setDbPassword(dbPassword);
			tenant.setSubscriptionStatus("INACTIVE");
			log.info("Attempting to save TenantEntity: tenantId={}, tenantName={}", tenantId, tenantName);

			tenantDao.save(tenant);
			log.info("TenantEntity saved successfully in master_dsa360.tenants: tenantId={}", tenantId);

			log.info("Tenant registered successfully: {}", tenantId);
			return tenantId;
		} catch (Exception e) {
			log.error("Failed to register tenant: {}", tenantId, e);
			throw new SomethingWentWrongException("Failed to register tenant: " + tenantId, e);
		} finally {
			TenantContext.clear();
		}
	}

}