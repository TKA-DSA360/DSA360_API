package com.dsa360.api.controller;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dsa360.api.aspect.TrackExecutionTime;
import com.dsa360.api.config.TenantContext;
import com.dsa360.api.daoimpl.TenantDaoImpl;
import com.dsa360.api.dto.LogedInUserDetailModelDto;
import com.dsa360.api.entity.master.TenantEntity;
import com.dsa360.api.exceptions.SomethingWentWrongException;
import com.dsa360.api.security.CustomUserDetail;
import com.dsa360.api.security.CustomUserDetailService;
import com.dsa360.api.service.SystemUserService;
import com.dsa360.api.utility.JwtUtil;

/**
 * @author RAM
 *
 */
@RestController
@RequestMapping("/auth")

public class AuthController {
    private static Logger log = LogManager.getLogger(AuthController.class);

    @Autowired
    SystemUserService userService;

    @Autowired
    CustomUserDetailService customUserDetailService;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private TenantDaoImpl tenantDao;

    // completed
    @PostMapping("/login-user")
    @TrackExecutionTime
    public ResponseEntity<LogedInUserDetailModelDto> login(@RequestParam String username, @RequestParam String password,
                                                           @RequestParam(required = false) String tenantId, // Optional for master
                                                           HttpServletResponse response) throws AuthenticationException {

        log.info("Trying to login = {} for tenant = {}", username, tenantId);
        
        String userType;
        if (tenantId == null || "master".equals(tenantId)) {
            // Master login
            userType = "master";
            TenantContext.setCurrentTenant("master");
        } else {
            // Tenant login
            userType = "tenant";
            TenantEntity tenant = tenantDao.findById(tenantId);
            if (tenant == null) {
                throw new SomethingWentWrongException("Invalid tenant ID");
            }
            if (!"ACTIVE".equals(tenant.getSubscriptionStatus())) {
                throw new SomethingWentWrongException("Tenant subscription is not active");
            }
            TenantContext.setCurrentTenant(tenantId);
        }

        try {
            final var logedInUser = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));

            SecurityContextHolder.getContext().setAuthentication(logedInUser);

            CustomUserDetail userDetail = (CustomUserDetail) logedInUser.getPrincipal();
            userDetail.setUserType(userType); // Set userType
            Collection<? extends GrantedAuthority> authorities = userDetail.getAuthorities();

            List<String> roles = authorities.stream().map(authority -> authority.getAuthority().substring(5))
                    .collect(Collectors.toList());

            log.info("Logged In = {} as {}", username, userType);

            final String token = jwtUtil.generateToken(logedInUser, tenantId, userType); // Pass userType

            response.setHeader("token", token);

            var model = new LogedInUserDetailModelDto(userDetail.getId(), userDetail.getUsername(), roles,
                    userDetail.getStatus(), token);

            return new ResponseEntity<>(model, HttpStatus.OK);
        } finally {
            TenantContext.clear(); 
        }
    }
}