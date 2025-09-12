package com.dsa360.api.config;

import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class RoutingConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private final TenantRoutingDataSource routingDataSource;

    public RoutingConnectionProvider(@Qualifier("routingDataSource") DataSource routingDataSource) {
        this.routingDataSource = (TenantRoutingDataSource) routingDataSource;
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return routingDataSource.getResolvedDefaultDataSource();
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        Map<Object, DataSource> resolvedDataSources = routingDataSource.getResolvedDataSources();
        return (DataSource) resolvedDataSources.getOrDefault(tenantIdentifier, selectAnyDataSource());
    }
}