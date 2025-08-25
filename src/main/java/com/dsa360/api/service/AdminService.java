package com.dsa360.api.service;

import java.util.List;
import java.util.Map;

import com.dsa360.api.dto.RegionsDto;
import com.dsa360.api.dto.RoleDto;
import com.dsa360.api.dto.SystemUserDto;

public interface AdminService {

	// system user

	public abstract SystemUserDto createSystemUserProfile(SystemUserDto userDto);

	public abstract void deleteSystemUser(String username);

	// role

	public abstract void addRole(RoleDto roleDto);

	public abstract RoleDto getRollById(String rollId);

	public abstract List<RoleDto> getAllRole();

	public abstract List<RoleDto> getAllRoleByIds(List<String> ids);

	public abstract RoleDto getRoleByName(String roleName);

	public abstract boolean deleteRole(String rollId);

	public abstract void updateRole(RoleDto roleDto);

	// Region

	public abstract void addRegion(RegionsDto regionsDto);

	public abstract RegionsDto getRegionById(String regionId);

	public abstract List<RegionsDto> getAllRegionsByIds(List<String> ids);

	public abstract RegionsDto getRegionByName(String regionName);

	public abstract List<RegionsDto> getAllRegions();

	public abstract void deleteRegion(String regionId);

	public abstract void updateRegion(RegionsDto regionsDto);

	public abstract Map<String, Integer> getCountOfSystemUser();

}
