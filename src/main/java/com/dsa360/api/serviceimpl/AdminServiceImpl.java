package com.dsa360.api.serviceimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dsa360.api.aspect.AuditableAction;
import com.dsa360.api.dao.AdminDao;
import com.dsa360.api.dto.DSAApplicationDTO;
import com.dsa360.api.dto.RegionsDto;
import com.dsa360.api.dto.RoleDto;
import com.dsa360.api.dto.SystemUserDto;
import com.dsa360.api.entity.DsaApplicationEntity;
import com.dsa360.api.entity.RegionsEntity;
import com.dsa360.api.entity.RoleEntity;
import com.dsa360.api.entity.SystemUserEntity;
import com.dsa360.api.exceptions.ResourceAlreadyExistsException;
import com.dsa360.api.exceptions.ResourceNotFoundException;
import com.dsa360.api.service.AdminService;
import com.dsa360.api.service.DSAService;
import com.dsa360.api.utility.DynamicID;
import com.dsa360.api.utility.MailAsyncServices;

/**
 * @author RAM
 *
 */
@Service
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

	private static final String NOTEXITS = " is Not Exists";

	@Autowired
	private AdminDao dao;

	@Autowired
	private DSAService dsaService;

	@Autowired
	private ModelMapper mapper;

	@Autowired
	private BCryptPasswordEncoder encoder;

	@Autowired
	MailAsyncServices mailAsyncServices;

	@Override
	@AuditableAction(action = "CREATE_SYSTEM_USER_PROFILE")
	public SystemUserDto createSystemUserProfile(SystemUserDto userDto) {
		DsaApplicationEntity dsaApplicationEntity = null;
		DSAApplicationDTO dsaById = dsaService.getDSAById(userDto.getDsaApplicationId());
		if (dsaById != null) {

			String password = userDto.getPassword();
			userDto.setPassword(encoder.encode(password));
			dsaApplicationEntity = mapper.map(dsaById, DsaApplicationEntity.class);

			SystemUserEntity entity = mapper.map(userDto, SystemUserEntity.class);
			entity.setDsaApplicationId(dsaApplicationEntity);

			List<RegionsEntity> allRegionsByIds = dao.getAllRegionsByIds(userDto.getRegions());
			List<RoleEntity> allRoleByIds = dao.getAllRoleByIds(userDto.getRoles());

			entity.setRegions(allRegionsByIds);
			entity.setRoles(allRoleByIds);

			// call dao
			dao.createSystemUserProfile(entity);
			userDto.setPassword(password);

			// mail confirmation

			String username = userDto.getUsername();

			mailAsyncServices.userProfileCreatedConfirmationMail(dsaById, username, password, allRoleByIds,
					allRegionsByIds);

			return userDto;

		} else {
			throw new ResourceNotFoundException(
					"DSA Application not found of this id =" + userDto.getDsaApplicationId());
		}

	}

	@Override
	@AuditableAction(action = "DELETE_SYSTEM_USER")
	public void deleteSystemUser(String username) {
		dao.deleteSystemUser(username);
	}

	@Override
	@AuditableAction(action = "ADD_ROLE")
	public void addRole(RoleDto roleDto) {
		roleDto.setId(DynamicID.getGeneratedRoleId());
		RoleEntity entity = mapper.map(roleDto, RoleEntity.class);
		dao.addRole(entity);
	}

	@Override
	@AuditableAction(action = "DELETE_ROLL")
	public boolean deleteRole(String rollId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	@AuditableAction(action = "UPDATE_ROLL")
	public void updateRole(RoleDto roleDto) {
		// TODO Auto-generated method stub
	}

	@Override
	public RoleDto getRollById(String rollId) {
		RoleEntity rollEntity = dao.getRollById(rollId);
		if (rollEntity != null) {
			return mapper.map(rollEntity, RoleDto.class);
		} else {
			throw new ResourceNotFoundException(rollId + NOTEXITS);
		}
	}

	@Override
	public List<RoleDto> getAllRole() {
		List<RoleEntity> allRole = dao.getAllRole();
		if (allRole.isEmpty()) {
			throw new ResourceNotFoundException("Roll Not Found");
		} else {
			return allRole.stream().map(roleEntity -> mapper.map(roleEntity, RoleDto.class))
					.collect(Collectors.toList());
		}

	}

	@Override
	public List<RoleDto> getAllRoleByIds(List<String> ids) {

		List<RoleEntity> allRole = dao.getAllRoleByIds(ids);
		if (allRole.isEmpty()) {
			throw new ResourceNotFoundException("Roll Not Found");
		} else {
			return allRole.stream().map(roleEntity -> mapper.map(roleEntity, RoleDto.class))
					.collect(Collectors.toList());
		}
	}

	@Override
	public RoleDto getRoleByName(String roleName) {
		RoleEntity rollEntity = dao.getRoleByName(roleName);
		if (rollEntity != null) {
			return mapper.map(rollEntity, RoleDto.class);
		} else {
			throw new ResourceAlreadyExistsException(roleName + NOTEXITS);
		}
	}

	@Override
	@AuditableAction(action = "CREATE_REGION")
	public void addRegion(RegionsDto regionsDto) {

		regionsDto.setId(DynamicID.getGeneratedRegionId());
		RegionsEntity regionEntity = mapper.map(regionsDto, RegionsEntity.class);
		dao.addRegion(regionEntity);

	}

	@Override
	public RegionsDto getRegionById(String regionId) {
		var regionsEntity = dao.getRegionById(regionId);
		if (regionsEntity != null) {
			return mapper.map(regionsEntity, RegionsDto.class);
		} else {
			throw new ResourceAlreadyExistsException(regionId + NOTEXITS);
		}
	}

	@Override
	public List<RegionsDto> getAllRegionsByIds(List<String> ids) {
		List<RegionsEntity> allRegions = dao.getAllRegionsByIds(ids);
		if (allRegions.isEmpty()) {
			throw new ResourceNotFoundException("Regions Not Found");
		} else {
			return allRegions.stream().map(regionEntity -> mapper.map(regionEntity, RegionsDto.class))
					.collect(Collectors.toList());
		}
	}

	@Override
	public List<RegionsDto> getAllRegions() {
		List<RegionsEntity> allRegions = dao.getAllRegions();
		if (allRegions.isEmpty()) {
			throw new ResourceNotFoundException("Region Not Found");
		} else {
			return allRegions.stream().map(reggionEntity -> mapper.map(reggionEntity, RegionsDto.class))
					.collect(Collectors.toList());
		}
	}

	@Override
	@AuditableAction(action = "DELETE_REGION")
	public void deleteRegion(String regionId) {
		dao.deleteRegion(regionId);

	}

	@Override
	public RegionsDto getRegionByName(String regionName) {
		var regionsEntity = dao.getRegionByName(regionName);
		if (regionsEntity != null) {
			return mapper.map(regionsEntity, RegionsDto.class);
		} else {
			throw new ResourceAlreadyExistsException(regionName + NOTEXITS);
		}
	}

	@Override
	@AuditableAction(action = "UPDATE_REGION")
	public void updateRegion(RegionsDto regionsDto) {

		var regionsEntity = mapper.map(regionsDto, RegionsEntity.class);

		dao.updateRegion(regionsEntity);
	}

	@Override
	public Map<String, Integer> getCountOfSystemUser() {
		List<Object[]> results = dao.getCountOfSystemUser();
		Map<String, Integer> userCountPerRole = new HashMap<>();

		for (Object[] result : results) {
			String roleName = (String) result[0];
			Long count = (Long) result[1];
			userCountPerRole.put(roleName.replace("ROLE_", ""), count.intValue());
		}

		return userCountPerRole;

	}

}
