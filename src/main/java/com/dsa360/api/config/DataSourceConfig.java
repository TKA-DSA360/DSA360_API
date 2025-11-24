package com.dsa360.api.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.hibernate.SessionFactory;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.dsa360.api.entity.master.TenantEntity;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LogManager.getLogger(DataSourceConfig.class);

    @Bean
    @ConfigurationProperties("spring.datasource")
    DataSourceProperties masterDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Qualifier("masterDataSource")
    DataSource masterDataSource() {
        return masterDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    @DependsOn("masterSessionFactory")
    DataSource routingDataSource(@Qualifier("masterDataSource") DataSource masterDataSource) {
        TenantRoutingDataSource routingDataSource = new TenantRoutingDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("master", masterDataSource);

        try {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(masterDataSource);
            List<TenantEntity> tenants = jdbcTemplate.query(
                    "SELECT tenant_id, tenant_name, db_url, db_username, db_password, subscription_status FROM tenants",
                    new BeanPropertyRowMapper<>(TenantEntity.class));
            log.info("Found {} tenants in master database", tenants.size());

            for (TenantEntity tenant : tenants) {
                DataSource tenantDataSource = createTenantDataSource(tenant);
                targetDataSources.put(tenant.getTenantId(), tenantDataSource);
                log.info("Added tenant data source for tenantId: {}", tenant.getTenantId());
            }
        } catch (Exception e) {
            log.warn("Failed to load tenants from master database, proceeding with only master data source: {}",
                    e.getMessage());
        }

        routingDataSource.setInitialTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }

    private DataSource createTenantDataSource(TenantEntity tenant) {
        DataSourceProperties properties = new DataSourceProperties();
        properties.setUrl(tenant.getDbUrl());
        properties.setUsername(tenant.getDbUsername());
        properties.setPassword(tenant.getDbPassword());
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    JdbcTemplate masterJdbcTemplate(@Qualifier("masterDataSource") DataSource masterDataSource) {
        return new JdbcTemplate(masterDataSource);
    }

    @Bean(name = "masterSessionFactory")
    LocalSessionFactoryBean masterSessionFactory(@Qualifier("masterDataSource") DataSource masterDataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(masterDataSource);
        sessionFactory.setPackagesToScan("com.dsa360.api.entity.master");
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");
        hibernateProperties.setProperty("hibernate.show_sql", "true");
        hibernateProperties.setProperty("hibernate.format_sql", "true");
        sessionFactory.setHibernateProperties(hibernateProperties);
        return sessionFactory;
    }

    @Bean(name = "tenantSessionFactory")
    @DependsOn("routingDataSource")
    LocalSessionFactoryBean tenantSessionFactory(
            @Qualifier("routingDataSource") DataSource routingDataSource,
            @Qualifier("multiTenantConnectionProvider") MultiTenantConnectionProvider multiTenantConnectionProvider,
            @Qualifier("currentTenantIdentifierResolver") CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(routingDataSource);
        sessionFactory.setPackagesToScan("com.dsa360.api.entity", "com.dsa360.api.entity.loan");
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "none");
        hibernateProperties.setProperty("hibernate.show_sql", "true");
        hibernateProperties.setProperty("hibernate.format_sql", "true");
        // Multi-tenancy
        hibernateProperties.setProperty("hibernate.multiTenancy", "DATABASE");
        // Second-level cache properties
        hibernateProperties.setProperty("hibernate.cache.use_second_level_cache", "true");
        hibernateProperties.setProperty("hibernate.cache.use_query_cache", "true");
        hibernateProperties.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.jcache.JCacheRegionFactory");
        hibernateProperties.setProperty("hibernate.javax.cache.provider", "org.ehcache.jsr107.EhcacheCachingProvider");
        hibernateProperties.setProperty("hibernate.javax.cache.missing_cache_strategy", "create");
        hibernateProperties.setProperty("hibernate.javax.cache.cache_manager_uri", "classpath:ehcache.xml");
        sessionFactory.setHibernateProperties(hibernateProperties);
        // Set multi-tenancy beans directly
        sessionFactory.setMultiTenantConnectionProvider(multiTenantConnectionProvider);
        sessionFactory.setCurrentTenantIdentifierResolver(currentTenantIdentifierResolver);
        return sessionFactory;
    }

    @Bean
    @Primary
    PlatformTransactionManager masterTransactionManager(
            @Qualifier("masterSessionFactory") SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    @Bean
    PlatformTransactionManager tenantTransactionManager(
            @Qualifier("tenantSessionFactory") SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    @Bean
    CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
        return new CurrentTenantResolver();
    }

    @Bean
    MultiTenantConnectionProvider multiTenantConnectionProvider(
            @Qualifier("routingDataSource") DataSource routingDataSource) {
        return new RoutingConnectionProvider(routingDataSource);
    }
    
}