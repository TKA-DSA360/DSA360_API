package com.dsa360.api.security;

import com.dsa360.api.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom UserDetails implementation for multi-tenant authentication.
 *
 * @author RAM
 */
@SuppressWarnings("serial")
public class CustomUserDetail implements UserDetails {

    private String id;
    private String username;
    private String password;
    private List<? extends Role> roles; // Use Role interface
    private String status;
    private String userType; // "master" or "tenant"
    private String tenantId; // Tenant ID for tenant users, null for master users

    public CustomUserDetail() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<? extends Role> getRoles() {
        return roles;
    }

    public void setRoles(List<? extends Role> roles) {
        this.roles = roles;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return "ACTIVE".equals(status);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}