package com.dsa360.api.dao;

import java.util.List;
import com.dsa360.api.entity.RegionsEntity;
import com.dsa360.api.entity.RoleEntity;
import com.dsa360.api.entity.SystemUserEntity;

public interface AdminDao {
	// system user

	public abstract void createSystemUserProfile(SystemUserEntity userEntity);

	public abstract void deleteSystemUser(String username);

	// role

	public abstract void addRole(RoleEntity roleEntity);

	public abstract RoleEntity getRollById(String rollId);

	public abstract List<RoleEntity> getAllRole();

	public abstract List<RoleEntity> getAllRoleByIds(List<String> ids);

	public abstract RoleEntity getRoleByName(String roleName);

	public abstract boolean deleteRole(String rollId);

	public abstract RoleEntity updateRole(RoleEntity roleEntity);

	// Region

	public abstract void addRegion(RegionsEntity regionsEntity);

	public abstract RegionsEntity getRegionById(String regionId);

	public abstract List<RegionsEntity> getAllRegionsByIds(List<String> ids);

	public abstract RegionsEntity getRegionByName(String regionName);

	public abstract List<RegionsEntity> getAllRegions();

	public abstract void deleteRegion(String regionId);

	public abstract void updateRegion(RegionsEntity regionsEntity);
	
	public List<Object[]> getCountOfSystemUser();

}
