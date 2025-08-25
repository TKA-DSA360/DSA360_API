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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dsa360.api.constants.JwtConstant;
import com.dsa360.api.exceptions.SomethingWentWrongException;
import com.dsa360.api.exceptions.TokenExpirationException;
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
		if (header != null && header.startsWith(JwtConstant.TOKEN_PREFIX.getValue())) {

			// header= Bearer
			// eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYW0iLCJzY29wZXMiOiJST0xFX0FETUlOIiwiaWF0IjoxNjgxMTEyOTUyLCJleHAiOjE2ODExMzA5NTJ9.SHNB6bgI_HgWFBZyhNnG0nQO7SWvJLfaxmSDAeRkZiw
			// token =
			// eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyYW0iLCJzY29wZXMiOiJST0xFX0FETUlOIiwiaWF0IjoxNjgxMTEyOTUyLCJleHAiOjE2ODExMzA5NTJ9.SHNB6bgI_HgWFBZyhNnG0nQO7SWvJLfaxmSDAeRkZiw

			authToken = header.replace(JwtConstant.TOKEN_PREFIX.getValue(), "");

			try {
				username = jwtTokenUtil.getUsernameFromToken(authToken);

			} catch (IllegalArgumentException e) {
				throw new SomethingWentWrongException("Facing Issue while getting username from token.");
			} catch (ExpiredJwtException e) {
				throw new TokenExpirationException("Your session has been expired. Please log in again.");
			} catch (SignatureException e) {
				throw new SomethingWentWrongException("Invalid Signature . Please log in agains.");
			}

			

		} else {
			logger.warn("Couldn't find bearer string, will ignore the header! Logged in with credentials.");
		}
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			var userDetails = userDetailsService.loadUserByUsername(username);

			Boolean isValidated = jwtTokenUtil.validateToken(authToken, userDetails); 
			if (Boolean.TRUE.equals(isValidated)) {
				UsernamePasswordAuthenticationToken authentication = jwtTokenUtil.getAuthentication(authToken,
						SecurityContextHolder.getContext().getAuthentication(), userDetails);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
				log.info("Authenticated user {}, setting security context", username);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}

		chain.doFilter(req, res);
	}
}