package com.dsa360.api.serviceimpl;

import com.dsa360.api.config.TenantContext;
import com.dsa360.api.config.TenantRoutingDataSource;
import com.dsa360.api.constants.ApprovalStatus;
import com.dsa360.api.dao.SubscriptionDAO;
import com.dsa360.api.dao.TenantDao;
import com.dsa360.api.dto.DSAApplicationDTO;
import com.dsa360.api.dto.SubscriptionDTO;
import com.dsa360.api.entity.AuditLog;
import com.dsa360.api.entity.ContactUsEntity;
import com.dsa360.api.entity.CustomerEntity;
import com.dsa360.api.entity.DocumentEntity;
import com.dsa360.api.entity.DsaApplicationEntity;
import com.dsa360.api.entity.DsaKycEntity;
import com.dsa360.api.entity.RegionsEntity;
import com.dsa360.api.entity.RoleEntity;
import com.dsa360.api.entity.SystemUserEntity;
import com.dsa360.api.entity.loan.DisbursementEntity;
import com.dsa360.api.entity.loan.LoanApplicationEntity;
import com.dsa360.api.entity.loan.LoanConditioEntity;
import com.dsa360.api.entity.loan.LoanDisbursementEntity;
import com.dsa360.api.entity.loan.LoanTrancheEntity;
import com.dsa360.api.entity.loan.ReconciliationEntity;
import com.dsa360.api.entity.loan.RepaymentEntity;
import com.dsa360.api.entity.loan.TrancheAuditEntity;
import com.dsa360.api.entity.loan.TrancheEntity;
import com.dsa360.api.entity.master.SubscriptionEntity;
import com.dsa360.api.entity.master.SubscriptionPlanEntity;
import com.dsa360.api.entity.master.TenantEntity;
import com.dsa360.api.exceptions.SomethingWentWrongException;
import com.dsa360.api.service.SubscriptionService;
import com.dsa360.api.utility.DynamicID;
import com.zaxxer.hikari.HikariDataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.sql.DataSource;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

	private static final Logger log = LogManager.getLogger(SubscriptionServiceImpl.class);

	@Autowired
	private SubscriptionDAO subscriptionDAO;

	@Autowired
	private TenantDao tenantDao;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private DSAServiceImpl dsaServiceImpl;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	@Qualifier("routingDataSource")
	private TenantRoutingDataSource routingDataSource;

	@Override
	@Transactional("masterTransactionManager")
	public SubscriptionDTO createSubscription(SubscriptionDTO subscriptionDTO) {
		String subscriptionId = DynamicID.getGeneratedSubscriptionId();
		subscriptionDTO.setSubscriptionId(subscriptionId);

		// start date 2007-12-03T10:15:30
		subscriptionDTO.setStartDate(java.time.LocalDateTime.now());

		// end date after one month from start date
		subscriptionDTO.setEndDate(subscriptionDTO.getStartDate().plusMonths(1));

		// default billing status as PENDING
		subscriptionDTO.setBillingStatus("DONE");

		// paymentGatewayId
		subscriptionDTO.setPaymentGatewayId(UUID.randomUUID().toString());

		SubscriptionEntity entity = modelMapper.map(subscriptionDTO, SubscriptionEntity.class);

		SubscriptionPlanEntity plan = new SubscriptionPlanEntity();
		plan.setPlanId(subscriptionDTO.getPlanId());

		TenantEntity tenant = tenantDao.findById(subscriptionDTO.getTenantId());

		entity.setPlan(plan);
		entity.setTenant(tenant);

		subscriptionDAO.save(entity);

		// Active Tenant
		tenant.setSubscriptionStatus("ACTIVE");
		tenantDao.update(tenant);

		// Create tenant DataSource and add to routingDataSource
		DataSource tenantDataSource = createTenantDataSource(tenant);
		routingDataSource.addTenantDataSource(subscriptionDTO.getTenantId(), tenantDataSource);
		log.info("Tenant DataSource added for tenantId={}", subscriptionDTO.getTenantId());

		// Test database connection
		testDatabaseConnection(subscriptionDTO.getTenantId(), tenantDataSource);

		// Initialize tenant schema with retry
		initializeTenantSchema(subscriptionDTO.getTenantId(), tenantDataSource, 3, 1000);

		// Verify schema creation
		verifyTenantSchema(subscriptionDTO.getTenantId(), tenantDataSource);

		// Save default role, region, DSA application, and user in tenant database
		saveTenantEntities(subscriptionDTO.getTenantId(), tenant.getTenantName());

		return modelMapper.map(entity, SubscriptionDTO.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SubscriptionDTO> getSubscriptionsByTenant(String tenantId) {
		List<SubscriptionEntity> entities = subscriptionDAO.findByTenantId(tenantId);
		return entities.stream().map(entity -> modelMapper.map(entity, SubscriptionDTO.class))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public SubscriptionDTO getSubscription(String subscriptionId) {
		SubscriptionEntity entity = subscriptionDAO.findById(subscriptionId)
				.orElseThrow(() -> new SomethingWentWrongException("Subscription not found: " + subscriptionId));
		return modelMapper.map(entity, SubscriptionDTO.class);
	}

	@Override
	@Transactional("masterTransactionManager")
	public SubscriptionDTO changeSubscriptionPlan(String subscriptionId, String newPlanId) {
		SubscriptionEntity entity = subscriptionDAO.findById(subscriptionId)
				.orElseThrow(() -> new SomethingWentWrongException("Subscription not found: " + subscriptionId));

		SubscriptionPlanEntity newPlan = new SubscriptionPlanEntity();
		newPlan.setPlanId(newPlanId);
		entity.setPlan(newPlan);

		subscriptionDAO.update(entity);
		SubscriptionDTO dto = modelMapper.map(entity, SubscriptionDTO.class);

		return dto;
	}

	@Override
	@Transactional
	public void deleteSubscription(String subscriptionId, String tenantId) {
		subscriptionDAO.delete(subscriptionId, tenantId);
	}

	@Transactional("tenantTransactionManager")
	private void saveTenantEntities(String tenantId, String tenantName) {
		TenantContext.setCurrentTenant(tenantId);
		// Create default role
		RoleEntity adminRole = new RoleEntity();
		adminRole.setId(DynamicID.getGeneratedRoleId());
		adminRole.setName("ROLE_ADMIN");
		adminRole.setCreatedAt(LocalDateTime.now());

		// Create default region
		RegionsEntity defaultRegion = new RegionsEntity();
		defaultRegion.setId(DynamicID.getGeneratedRegionId());
		defaultRegion.setRegionName("Default Region");
		defaultRegion.setRegionCode("DR001");

		// Create default DSA Application
		DSAApplicationDTO applicationDTO = new DSAApplicationDTO();
		applicationDTO.setFirstName("TenantAdmin");
		applicationDTO.setMiddleName("TenantAdmin");
		applicationDTO.setLastName("TenantAdmin");
		applicationDTO.setGender("Female");
		applicationDTO.setDateOfBirth("2025-09-25");
		applicationDTO.setNationality("Indian");
		applicationDTO.setContactNumber("98257245");
		applicationDTO.setEmailAddress("thekiranacademyojtdev@gmail.com");
		applicationDTO.setStreetAddress("14 Powai Road");
		applicationDTO.setCity("Mumbai");
		applicationDTO.setState("Maharashtra");
		applicationDTO.setPostalCode("400076");
		applicationDTO.setCountry("India");
		applicationDTO.setPreferredLanguage("Hindi");
		applicationDTO.setEducationalQualifications("Master of Business Administration");
		applicationDTO.setExperience("7 years in finance and accounting");
		applicationDTO.setIsAssociatedWithOtherDSA("NO");
		applicationDTO.setAssociatedInstitutionName("NA");
		applicationDTO.setReferralSource("Job portal");
		applicationDTO.setEmailVerified(true);
		applicationDTO.setApprovalStatus(ApprovalStatus.APPROVED.getValue());

		DSAApplicationDTO dsaApplication = dsaServiceImpl.dsaApplication(applicationDTO);

		// Create default admin user
		SystemUserEntity adminUser = new SystemUserEntity();
		adminUser.setUsername("tenant_admin_" + tenantName);
		adminUser.setPassword(passwordEncoder.encode("Temp@123"));
		adminUser.setQuestion("Default question");
		adminUser.setAnswer("Default answer");
		adminUser.setStatus("ACTIVE");
		adminUser.setCreatedAt(LocalDateTime.now());
		adminUser.setRoles(Collections.singletonList(adminRole));
		adminUser.setRegions(Collections.singletonList(defaultRegion));

		if (dsaApplication != null) {
			DsaApplicationEntity applicationEntity = mapper.map(dsaApplication, DsaApplicationEntity.class);
			adminUser.setDsaApplicationId(applicationEntity);
		}

		// Delegate to DAO for persistence
		tenantDao.saveTenantEntities(tenantId, adminRole, defaultRegion, adminUser);

		log.info("Default role, region, DSA application, and admin user prepared for tenantId={}", tenantId);
	}

	private DataSource createTenantDataSource(TenantEntity tenant) {
		DataSourceProperties properties = new DataSourceProperties();
		properties.setUrl(tenant.getDbUrl());
		properties.setUsername(tenant.getDbUsername());
		properties.setPassword(tenant.getDbPassword());
		return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
	}

	private void testDatabaseConnection(String tenantId, DataSource tenantDataSource) {
		TenantContext.setCurrentTenant(tenantId);
		try {
			log.info("Testing database connection for tenantId={}", tenantId);
			JdbcTemplate jdbcTemplate = new JdbcTemplate(tenantDataSource);
			jdbcTemplate.execute("SELECT 1");
			log.info("Database connection successful for tenantId={}", tenantId);
		} catch (Exception e) {
			log.error("Failed to connect to tenant database for tenantId: {}", tenantId, e);
			throw new SomethingWentWrongException("Failed to connect to tenant database: " + tenantId, e);
		} finally {
			TenantContext.clear();
		}
	}

	private void initializeTenantSchema(String tenantId, DataSource tenantDataSource, int maxRetries, long delayMs) {
		TenantContext.setCurrentTenant(tenantId);
		int attempt = 0;
		Exception lastException = null;

		while (attempt < maxRetries) {
			try {
				log.info("Initializing tenant schema for tenantId={}, attempt={}", tenantId, attempt + 1);
				StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
						.applySetting("hibernate.connection.datasource", tenantDataSource)
						.applySetting("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect")
						.applySetting("hibernate.hbm2ddl.auto", "update").applySetting("hibernate.show_sql", "true")
						.applySetting("hibernate.format_sql", "true").build();

				MetadataSources sources = new MetadataSources(registry);
				sources.addAnnotatedClass(AuditLog.class);
				sources.addAnnotatedClass(ContactUsEntity.class);
				sources.addAnnotatedClass(CustomerEntity.class);
				sources.addAnnotatedClass(DocumentEntity.class);
				sources.addAnnotatedClass(DsaKycEntity.class);
				sources.addAnnotatedClass(DisbursementEntity.class);
				sources.addAnnotatedClass(LoanApplicationEntity.class);
				sources.addAnnotatedClass(LoanConditioEntity.class);
				sources.addAnnotatedClass(LoanDisbursementEntity.class);
				sources.addAnnotatedClass(LoanTrancheEntity.class);
				sources.addAnnotatedClass(ReconciliationEntity.class);
				sources.addAnnotatedClass(RepaymentEntity.class);
				sources.addAnnotatedClass(TrancheAuditEntity.class);
				sources.addAnnotatedClass(TrancheEntity.class);
				sources.addAnnotatedClass(RoleEntity.class);
				sources.addAnnotatedClass(SystemUserEntity.class);
				sources.addAnnotatedClass(DsaApplicationEntity.class);
				sources.addAnnotatedClass(RegionsEntity.class);
				sources.addPackage("com.dsa360.api.entity");
				sources.addPackage("com.dsa360.api.entity.loan");

				SessionFactory sessionFactory = sources.buildMetadata().buildSessionFactory();
				try (var session = sessionFactory.openSession()) {
					session.beginTransaction().commit();
				}
				sessionFactory.close();
				log.info("Tenant schema initialized successfully for tenantId={}", tenantId);
				return;
			} catch (Exception e) {
				lastException = e;
				log.warn("Failed to initialize tenant schema for tenantId={}, attempt={}: {}", tenantId, attempt + 1,
						e.getMessage());
				attempt++;
				try {
					Thread.sleep(delayMs);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					log.error("Interrupted during retry delay for tenantId={}", tenantId);
				}
			} finally {
				TenantContext.clear();
			}
		}
		log.error("Failed to initialize tenant schema for tenantId={} after {} attempts", tenantId, maxRetries,
				lastException);
		throw new SomethingWentWrongException("Failed to initialize tenant schema for tenant: " + tenantId,
				lastException);
	}

	private void verifyTenantSchema(String tenantId, DataSource tenantDataSource) {
		TenantContext.setCurrentTenant(tenantId);
		try {
			log.info("Verifying tenant schema for tenantId={}", tenantId);
			JdbcTemplate jdbcTemplate = new JdbcTemplate(tenantDataSource);
			List<String> tables = jdbcTemplate.queryForList("SHOW TABLES", String.class);
			log.info("Tables in tenant database {}: {}", tenantId, tables);
			if (!tables.contains("role")) {
				throw new SomethingWentWrongException("Role table not found in tenant database: " + tenantId);
			}
			if (!tables.contains("system_user")) {
				throw new SomethingWentWrongException("System_user table not found in tenant database: " + tenantId);
			}
			if (!tables.contains("dsa_application")) {
				throw new SomethingWentWrongException(
						"Dsa_application table not found in tenant database: " + tenantId);
			}
		} catch (Exception e) {
			log.error("Failed to verify tenant schema for tenantId: {}", tenantId, e);
			throw new SomethingWentWrongException("Failed to verify tenant schema for tenant: " + tenantId, e);
		} finally {
			TenantContext.clear();
		}
	}
}