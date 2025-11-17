package com.dsa360.api.config;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.stereotype.Component;

/**
 * Tenant-aware KeyGenerator for Spring Cache. It prefixes generated keys with
 * the
 * current tenant id from TenantContext so cached entries are tenant-scoped.
 *
 * Registered with bean name "keyGenerator" so it becomes the default key
 * generator for Spring Cache if present.
 */
@Component("keyGenerator")
public class TenantKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String tenant = TenantContext.getCurrentTenant();
        if (tenant == null) {
            tenant = "master";
        }

        Object base = SimpleKeyGenerator.generateKey(params);
        return tenant + ":" + method.getName() + ":" + String.valueOf(base);
    }
}
