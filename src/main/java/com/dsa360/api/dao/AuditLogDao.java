package com.dsa360.api.dao;

import java.util.List;

import com.dsa360.api.entity.AuditLog;

public interface AuditLogDao {

	public void save(AuditLog auditLog);

	public List<AuditLog> findAll();
	

}
