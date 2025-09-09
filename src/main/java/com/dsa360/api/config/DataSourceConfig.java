package com.dsa360.api.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
            log.warn("Failed to load tenants from master database, proceeding with only master data source: {}", e.getMessage());
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
     LocalSessionFactoryBean tenantSessionFactory(@Qualifier("routingDataSource") DataSource routingDataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(routingDataSource);
        sessionFactory.setPackagesToScan("com.dsa360.api.entity", "com.dsa360.api.entity.loan");
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "none");
        hibernateProperties.setProperty("hibernate.show_sql", "true");
        hibernateProperties.setProperty("hibernate.format_sql", "true");
        sessionFactory.setHibernateProperties(hibernateProperties);
        return sessionFactory;
    }

    @Bean
    @Primary
     PlatformTransactionManager masterTransactionManager(@Qualifier("masterSessionFactory") SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    @Bean
     PlatformTransactionManager tenantTransactionManager(@Qualifier("tenantSessionFactory") SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }
}