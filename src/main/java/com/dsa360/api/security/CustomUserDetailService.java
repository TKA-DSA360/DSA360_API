package com.dsa360.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dsa360.api.config.TenantContext;
import com.dsa360.api.service.MasterUserService;
import com.dsa360.api.service.SystemUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom UserDetailsService implementation for a multi-tenant application.
 * Loads user details from either the master database or a tenant-specific database
 * based on the current TenantContext.
 *
 * @author RAM
 */
@Service
public class CustomUserDetailService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailService.class);

    @Autowired
    private SystemUserService systemUserService; // For tenant users

    @Autowired
    private MasterUserService masterUserService; // For master users

    /**
     * Loads a user by username, routing to the appropriate service based on TenantContext.
     * If tenantId is null or "master", queries the master database; otherwise, queries the tenant database.
     *
     * @param username the username to load
     * @return UserDetails for the authenticated user
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        String tenantId = TenantContext.getCurrentTenant();
        log.debug("Current Tenant ID: {}", tenantId);

        UserDetails user;
        try {
            if (tenantId == null || "master".equals(tenantId)) {
                log.debug("Loading master user for username: {}", username);
                user = masterUserService.loadUserByUserId(username);
            } else {
                log.debug("Loading tenant user for username: {} in tenant: {}", username, tenantId);
                user = systemUserService.loadUserByUserId(username);
            }

            if (user == null) {
                log.error("User not found for username: {} in tenant: {}", username, tenantId);
                throw new UsernameNotFoundException("User not found: " + username + " in tenant: " + (tenantId != null ? tenantId : "master"));
            }

            // Validate tenant consistency if user is CustomUserDetail
            if (user instanceof CustomUserDetail) {
                CustomUserDetail customUser = (CustomUserDetail) user;
                String expectedTenant = "master".equals(customUser.getUserType()) ? "master" : tenantId;
                if (tenantId != null && !tenantId.equals(expectedTenant)) {
                    log.error("Tenant mismatch for username: {}. Expected tenant: {}, actual tenant: {}", 
                              username, expectedTenant, tenantId);
                    throw new UsernameNotFoundException("Tenant mismatch for user: " + username);
                }
            }

            return user;
        } catch (Exception e) {
            log.error("Failed to load user {} in tenant: {}. Error: {}", username, tenantId, e.getMessage(), e);
            throw new UsernameNotFoundException("Failed to load user: " + username + " in tenant: " + (tenantId != null ? tenantId : "master"), e);
        }
    }
}