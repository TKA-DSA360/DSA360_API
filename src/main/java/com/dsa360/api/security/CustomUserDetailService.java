package com.dsa360.api.security;

import com.dsa360.api.config.TenantContext;
import com.dsa360.api.constants.UserStatus;
import com.dsa360.api.exceptions.ResourceNotFoundException;
import com.dsa360.api.exceptions.UserDeactivatedException;
import com.dsa360.api.exceptions.UserSuspendedException;
import com.dsa360.api.service.MasterUserService;
import com.dsa360.api.service.SystemUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author RAM
 *
 */
@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private SystemUserService systemUserService; // For tenant users

    @Autowired
    private MasterUserService masterUserService; // For master users

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("in load user method");
        UserDetails user = null;
        try {
            String tenantId = TenantContext.getCurrentTenant();
            if (tenantId == null || "master".equals(tenantId)) {
                // Load master user
                user = masterUserService.loadUserByUserId(username);
            } else {
                // Load tenant user
                user = systemUserService.loadUserByUserId(username);
            }
        } catch (UserDeactivatedException e) {
            throw new UserDeactivatedException(UserStatus.DEACTIVATED);
        } catch (UserSuspendedException e) {
            throw new UserSuspendedException(UserStatus.SUSPENDED);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResourceNotFoundException("Invalid User Name !!");
        }

        return user;
    }
}