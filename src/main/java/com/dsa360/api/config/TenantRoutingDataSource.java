package com.dsa360.api.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class TenantRoutingDataSource extends AbstractRoutingDataSource {

    private static final Logger log = LogManager.getLogger(TenantRoutingDataSource.class);

    private Map<Object, Object> targetDataSources = new HashMap<>();

    @Override
    protected Object determineCurrentLookupKey() {
        String tenantId = TenantContext.getCurrentTenant();
        log.info("Current tenant lookup key: {}", tenantId);
        return tenantId;
    }

    public void addTenantDataSource(String tenantId, DataSource dataSource) {
        targetDataSources.put(tenantId, dataSource);
        setTargetDataSources(targetDataSources);
        afterPropertiesSet();
        log.info("Added tenant data source to routing map: {}", tenantId);
    }

    public void setInitialTargetDataSources(Map<Object, Object> initialDataSources) {
        this.targetDataSources = new HashMap<>(initialDataSources);
        setTargetDataSources(this.targetDataSources);
        afterPropertiesSet();
        log.info("Initialized target data sources: {}", this.targetDataSources.keySet());
    }
}