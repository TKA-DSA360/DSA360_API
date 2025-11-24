package com.dsa360.api.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class CurrentTenantResolver implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenantId = TenantContext.getCurrentTenant(); // Assuming TenantContext is your ThreadLocal holder
        return (tenantId != null) ? tenantId : "master"; // Fallback to master
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false; // Typically false for dynamic tenants
    }
}