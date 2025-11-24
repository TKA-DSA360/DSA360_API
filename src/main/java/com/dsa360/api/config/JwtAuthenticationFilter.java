package com.dsa360.api.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dsa360.api.constants.JwtConstant;
import com.dsa360.api.exceptions.SomethingWentWrongException;
import com.dsa360.api.exceptions.TokenExpirationException;
import com.dsa360.api.security.CustomUserDetail;
import com.dsa360.api.utility.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        String header = req.getHeader(JwtConstant.HEADER_STRING.getValue());
        String username = null;
        String authToken = null;
        String tenantId = null;
        String userType = null;

        // Extract token details
        if (header != null && header.startsWith(JwtConstant.TOKEN_PREFIX.getValue())) {
            authToken = header.replace(JwtConstant.TOKEN_PREFIX.getValue(), "");
            try {
                username = jwtTokenUtil.getUsernameFromToken(authToken);
                tenantId = jwtTokenUtil.getTenantIdFromToken(authToken);
                userType = jwtTokenUtil.getUserTypeFromToken(authToken);
                log.debug("Extracted from token: username={}, tenantId={}, userType={}", username, tenantId, userType);
            } catch (IllegalArgumentException e) {
                log.error("Failed to extract username from token: {}", e.getMessage());
                throw new SomethingWentWrongException("Facing issue while getting username from token.");
            } catch (ExpiredJwtException e) {
                log.warn("Token expired for request: {}", e.getMessage());
                throw new TokenExpirationException("Your session has been expired. Please log in again.");
            } catch (SignatureException e) {
                log.error("Invalid token signature: {}", e.getMessage());
                throw new SomethingWentWrongException("Invalid signature. Please log in again.");
            }
        } else {
            log.warn("No bearer token found in header; proceeding without authentication.");
        }

        // Process authentication and tenant context
        if (username != null && authToken != null) {
            // Set TenantContext
            if (tenantId != null && !"master".equals(userType)) {
                TenantContext.setCurrentTenant(tenantId);
                log.info("Set TenantContext to tenantId: {}", tenantId);
            } else if ("master".equals(userType)) {
                TenantContext.setCurrentTenant("master");
                log.info("Set TenantContext to master for userType: {}", userType);
            } else {
                log.warn("No tenantId or master userType found in token for username: {}", username);
                TenantContext.clear();
                throw new SomethingWentWrongException("Invalid token: missing tenantId or userType.");
            }

            // Check existing authentication in SecurityContextHolder
            Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
            if (existingAuth != null && existingAuth.isAuthenticated() && 
                username.equals(existingAuth.getName()) && 
                isTenantContextValid(existingAuth, tenantId, userType)) {
                // Reuse existing authentication if token is still valid
                if (jwtTokenUtil.validateToken(authToken, (UserDetails) existingAuth.getPrincipal())) {
                    log.debug("Reusing existing authentication for username: {} in tenant: {}", 
                              username, tenantId != null ? tenantId : "master");
                    // Update details if Authentication is UsernamePasswordAuthenticationToken
                    if (existingAuth instanceof UsernamePasswordAuthenticationToken) {
                        ((UsernamePasswordAuthenticationToken) existingAuth)
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    }
                    chain.doFilter(req, res);
                    return;
                } else {
                    log.warn("Existing authentication found but token validation failed for username: {}", username);
                    SecurityContextHolder.clearContext();
                }
            }

            // Load user and set new authentication
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = jwtTokenUtil.getAuthentication(
                        authToken, null, userDetails);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                log.info("Authenticated user {}, setting security context", username);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.warn("Token validation failed for username: {}", username);
            }
        }

        try {
            chain.doFilter(req, res);
        } finally {
            TenantContext.clear();
            log.debug("Cleared TenantContext for request");
            // Do not clear SecurityContextHolder to allow reuse across requests
            // It will be cleared on logout or token invalidation
        }
    }

    /**
     * Validates that the existing Authentication's tenant context matches the token's tenantId or userType.
     */
    private boolean isTenantContextValid(Authentication auth, String tenantId, String userType) {
        String expectedTenant = tenantId != null && !"master".equals(userType) ? tenantId : "master";
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        if (!(userDetails instanceof CustomUserDetail)) {
            log.warn("UserDetails is not an instance of CustomUserDetail for username: {}", auth.getName());
            return false;
        }
        String actualUserType = ((CustomUserDetail) userDetails).getUserType();
        String actualTenant = "master".equals(actualUserType) ? "master" : TenantContext.getCurrentTenant();
        return expectedTenant.equals(actualTenant);
    }
}