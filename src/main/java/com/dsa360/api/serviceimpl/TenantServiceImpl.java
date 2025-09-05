package com.dsa360.api.serviceimpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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

import com.dsa360.api.config.TenantContext;
import com.dsa360.api.config.TenantRoutingDataSource;
import com.dsa360.api.dao.TenantDao;
import com.dsa360.api.dto.DSAApplicationDTO;
import com.dsa360.api.entity.DsaApplicationEntity;
import com.dsa360.api.entity.RegionsEntity;
import com.dsa360.api.entity.RoleEntity;
import com.dsa360.api.entity.SystemUserEntity;
import com.dsa360.api.entity.master.TenantEntity;
import com.dsa360.api.exceptions.SomethingWentWrongException;
import com.dsa360.api.service.TenantService;
import com.dsa360.api.utility.DynamicID;
import com.zaxxer.hikari.HikariDataSource;

@Service
public class TenantServiceImpl implements TenantService {

	private static final Logger log = LogManager.getLogger(TenantServiceImpl.class);

	@Autowired
	private TenantDao tenantDao;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private DSAServiceImpl dsaServiceImpl;

	@Autowired
	@Qualifier("tenantSessionFactory")
	private SessionFactory tenantSessionFactory;

	@Autowired
	@Qualifier("routingDataSource")
	private TenantRoutingDataSource routingDataSource;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	@Transactional("masterTransactionManager")
	public String createTenant(String tenantName) {
		String tenantId = DynamicID.getGeneratedId();
		String dbUsername = "root";
		String dbPassword = "admin";

		try {
			// Save tenant metadata in master database
			TenantEntity tenant = new TenantEntity();
			tenant.setTenantId(tenantId);
			tenant.setTenantName(tenantName);
			tenant.setDbUrl("jdbc:mysql://localhost:3306/dsa360_" + tenantId + "?createDatabaseIfNotExist=true");
			tenant.setDbUsername(dbUsername);
			tenant.setDbPassword(dbPassword);
			tenant.setSubscriptionStatus("INACTIVE");
			tenant.setCreatedAt(LocalDateTime.now());
			log.info("Attempting to save TenantEntity: tenantId={}, tenantName={}", tenantId, tenantName);

			tenantDao.save(tenant);
			log.info("TenantEntity saved successfully in master_dsa360.tenants: tenantId={}", tenantId);

			// Create tenant DataSource and add to routingDataSource
			DataSource tenantDataSource = createTenantDataSource(tenant);
			routingDataSource.addTenantDataSource(tenantId, tenantDataSource);
			log.info("Tenant DataSource added for tenantId={}", tenantId);

			// Test database connection
			testDatabaseConnection(tenantId, tenantDataSource);

			// Initialize tenant schema with retry
			initializeTenantSchema(tenantId, tenantDataSource, 3, 1000);

			// Verify schema creation
			verifyTenantSchema(tenantId, tenantDataSource);

			// Save default role,region, dsa application and user in tenant database
			saveTenantEntities(tenantId, tenantName);

			log.info("Tenant registered successfully: {}", tenantId);
			return tenantId;
		} catch (Exception e) {
			log.error("Failed to register tenant: {}", tenantId, e);
			throw new SomethingWentWrongException("Failed to register tenant: " + tenantId, e);
		} finally {
			TenantContext.clear();
		}
	}

	@Transactional("tenantTransactionManager")
	private void saveTenantEntities(String tenantId, String tenantName) {
		TenantContext.setCurrentTenant(tenantId);

		try {
			// Create default role
			RoleEntity adminRole = null;
			RegionsEntity defaultRegion = null;
			try (var session = tenantSessionFactory.openSession()) {
				Transaction transaction = session.beginTransaction();
				adminRole = new RoleEntity();
				adminRole.setId(DynamicID.getGeneratedRoleId());
				adminRole.setName("ROLE_ADMIN");
				adminRole.setCreatedAt(LocalDateTime.now());
				session.save(adminRole);
                transaction.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Create default region
			try (var session = tenantSessionFactory.openSession()) {
				Transaction transaction = session.beginTransaction();

				defaultRegion = new RegionsEntity();
				defaultRegion.setId(DynamicID.getGeneratedRegionId());
				defaultRegion.setRegionName("Default Region");
				defaultRegion.setRegionCode("DR001");
				session.save(defaultRegion);
				                transaction.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Create default DSA Application
			try (var session = tenantSessionFactory.openSession()) {
				Transaction transaction = session.beginTransaction();

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

				DSAApplicationDTO dsaApplication = dsaServiceImpl.dsaApplication(applicationDTO);

				if (dsaApplication != null) {
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

					DsaApplicationEntity applicationEntity = mapper.map(dsaApplication, DsaApplicationEntity.class);
					adminUser.setDsaApplicationId(applicationEntity);

					session.save(adminUser);
                    transaction.commit();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			// Saved role and user in tenant database
			log.info("Saving default role,region,dsa application and admin user in tenant database: tenantId={}",
					tenantId);

		} finally {
			TenantContext.clear();
		}

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