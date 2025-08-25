package com.dsa360.api.aspect;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.dsa360.api.dao.AuditLogDao;
import com.dsa360.api.entity.AuditLog;

@Aspect
@Component
public class AuditAspect {

	@Autowired
	private AuditLogDao auditDao;

	@AfterReturning(pointcut = "@annotation(auditableAction)", returning = "result")
	public void logAudit(JoinPoint joinPoint, AuditableAction auditableAction, Object result) {
		String user = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
				.map(auth -> auth.getName()).orElse("ANONYMOUS");

		String action = auditableAction.action();
		String entity = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
		String description = Arrays.toString(joinPoint.getArgs());

		AuditLog log = new AuditLog();
		log.setUsername(user);
		log.setAction(action);
		log.setTimestamp(LocalDateTime.now());
		log.setEntity(entity);
		log.setDescription(description);
		log.setEntityId("N/A");

		auditDao.save(log);
	}
}