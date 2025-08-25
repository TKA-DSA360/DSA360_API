package com.dsa360.api.daoimpl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dsa360.api.dao.AdminDao;
import com.dsa360.api.entity.RegionsEntity;
import com.dsa360.api.entity.RoleEntity;
import com.dsa360.api.entity.SystemUserEntity;
import com.dsa360.api.exceptions.ResourceAlreadyExistsException;
import com.dsa360.api.exceptions.ResourceNotFoundException;
import com.dsa360.api.exceptions.SomethingWentWrongException;
import com.dsa360.api.security.CustomUserDetail;
import com.dsa360.api.service.SystemUserService;

@Repository
public class AdminDaoImpl implements AdminDao {

	private static final Logger logger = LoggerFactory.getLogger(AdminDaoImpl.class);
	private static final String DATANOTFOUND = "Data not found to delete with id = ";

	@Autowired
	private SessionFactory factory;

	@Autowired
	private SystemUserService userService;

	@Override
	public void createSystemUserProfile(SystemUserEntity userEntity) {

		Transaction transaction = null;
		try (var session = factory.openSession()) {
			CustomUserDetail user = userService.loadUserByUserId(userEntity.getUsername());
			if (user == null) {
				transaction = session.beginTransaction();
				session.save(userEntity);
				transaction.commit();
			} else {
				throw new ResourceAlreadyExistsException(
						"Profile is already created for this user {}" + user.getUsername());
			}

		} catch (ResourceAlreadyExistsException e) {
			throw new ResourceAlreadyExistsException(
					"Profile is already created for this user {}" + userEntity.getUsername());
		} catch (Exception e) {
			logger.error("Exception occurred during generate profile for {} ", userEntity.getUsername());
			throw new SomethingWentWrongException(
					"Exception occurred during get DSA with id = " + userEntity.getUsername());
		}

	}

	@Override
	public void deleteSystemUser(String username) {
		Transaction transaction = null;
		try (var session = factory.openSession()) {
			transaction = session.beginTransaction();
			SystemUserEntity user = session.get(SystemUserEntity.class, username);
			if (user != null) {

				session.delete(user);
				transaction.commit();
			} else {
				throw new ResourceNotFoundException("User Profile not found to delete with id = " + username);
			}

		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
			logger.error("User Profile not found to delete with id {}", username);
			throw new ResourceNotFoundException(DATANOTFOUND + username);
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			logger.error("Exception occurred during delete user profile {}", username);
			throw new SomethingWentWrongException(
					"Something went wrong during delete user profile with id = " + username);
		}

	}

	@Override
	public void addRole(RoleEntity roleEntity) {

		Transaction transaction = null;
		try (var session = factory.openSession()) {
			transaction = session.beginTransaction();

			RoleEntity role = getRoleByName(roleEntity.getName());
			if (role == null) {
				session.save(roleEntity);
				transaction.commit();

				logger.info("Role {} added successfully", roleEntity.getName());

			} else {
				throw new ResourceAlreadyExistsException(roleEntity.getName() + " is Already Exists");
			}

		} catch (ResourceAlreadyExistsException e) {
			logger.error("Role already exists with name {}", roleEntity.getName());
			throw new ResourceAlreadyExistsException(roleEntity.getName() + " is Already Exists");
		} catch (Exception e) {
			logger.error("Exception occurred during add role {}", e.getMessage());
			throw new SomethingWentWrongException("Something went wrong add role");
		}
	}

	@Override
	public RoleEntity getRollById(String rollId) {
		RoleEntity roleEntity = null;
		try (var session = factory.openSession()) {
			roleEntity = session.get(RoleEntity.class, rollId);

		} catch (Exception e) {
			logger.error("Exception occurred during retrive role by id  :{}", e);
			throw new SomethingWentWrongException("Something went wrong during retrive role by id = " + rollId);
		}
		return roleEntity;
	}

	@Override
	public boolean deleteRole(String rollId) {
		Transaction transaction = null;
		try (var session = factory.openSession()) {
			transaction = session.beginTransaction();
			RoleEntity role = session.get(RoleEntity.class, rollId);
			if (role != null) {
				session.delete(role);
				transaction.commit();
				logger.info("Role {} deleted successfully", role.getName());
				return true;
			} else {
				throw new ResourceNotFoundException("Role not found to delete with id = " + rollId);
			}

		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
			logger.error("Role not found to delete with id {}", rollId);
			throw new ResourceNotFoundException(DATANOTFOUND + rollId);
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			logger.error("Exception occurred during delete role {}", rollId);
			throw new SomethingWentWrongException("Something went wrong during delete role with id = " + rollId);
		}

	}

	@Override
	public RoleEntity updateRole(RoleEntity roleEntity) {
		Transaction transaction = null;
		try (var session = factory.openSession()) {
			transaction = session.beginTransaction();
			session.update(roleEntity);
			transaction.commit();

		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			logger.error("Exception occurred during update role  : {}", e);
			throw new SomethingWentWrongException("Something went wrong during update role");
		}
		logger.info("Role {} updated successfully", roleEntity.getName());
		// return updated role entity
		return roleEntity;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<RoleEntity> getAllRole() {
		List<RoleEntity> list;
		try (var session = factory.openSession()) {
			list = session.createCriteria(RoleEntity.class).list();

		} catch (Exception e) {
			logger.error("Exception occurred during retrive all roles :{}", e);
			throw new SomethingWentWrongException("Something went wrong during retrive all roles");
		}

		return list;
	}

	@SuppressWarnings("deprecation")
	@Override
	public RoleEntity getRoleByName(String roleName) {
		RoleEntity role = null;
		try (var session = factory.openSession()) {
			role = (RoleEntity) session.createCriteria(RoleEntity.class).add(Restrictions.eq("name", roleName))
					.uniqueResult();

		} catch (Exception e) {
			logger.error("Exception occurred during delete user profile :{}", e);
			throw new SomethingWentWrongException("Something went wrong during retrive role with name = " + roleName);
		}
		return role;
	}

	@Override
	public List<RoleEntity> getAllRoleByIds(List<String> ids) {
		List<RoleEntity> roles;
		try (var session = factory.openSession()) {
			roles = session.byMultipleIds(RoleEntity.class).multiLoad(ids);

		} catch (Exception e) {
			logger.error("Exception occurred during retrive roles by Ids :{}", e);
			throw new SomethingWentWrongException("Something went wrong during retrive roles by Ids = " + ids);
		}
		return roles;
	}

	@Override
	public void addRegion(RegionsEntity regionsEntity) {
		Transaction transaction = null;
		try (var session = factory.openSession()) {

			RegionsEntity entity = getRegionByName(regionsEntity.getRegionName());
			if (entity == null) {
				session.save(regionsEntity);
				transaction = session.beginTransaction();

				transaction.commit();
			} else {
				throw new ResourceAlreadyExistsException(regionsEntity.getRegionName() + " is Already Exists");
			}

		} catch (Exception e) {
			e.printStackTrace();
			if (transaction != null) {
				transaction.rollback();
			}
			logger.error("Exception occurred during add region :{}", e);
			throw new SomethingWentWrongException("Something went wrong during add region");
		}

	}

	@Override
	public RegionsEntity getRegionById(String regionId) {
		RegionsEntity regionsEntity = null;
		try (var session = factory.openSession()) {
			regionsEntity = session.get(RegionsEntity.class, regionId);

		} catch (Exception e) {

			logger.error("Exception occurred during retrive region by id :{}", e);
			throw new SomethingWentWrongException("Something went wrong during retrive region by id " + regionId);
		}
		return regionsEntity;
	}

	@Override
	public List<RegionsEntity> getAllRegionsByIds(List<String> ids) {
		List<RegionsEntity> regions;
		try (var session = factory.openSession()) {
			regions = session.byMultipleIds(RegionsEntity.class).multiLoad(ids);

		} catch (Exception e) {
			logger.error("Exception occurred during retrive roles by Ids :{}", e);
			throw new SomethingWentWrongException("Something went wrong during retrive regions by Ids = " + ids);
		}
		return regions;
	}

	@SuppressWarnings("deprecation")
	@Override
	public RegionsEntity getRegionByName(String regionName) {
		RegionsEntity regionsEntity = null;
		try (var session = factory.openSession()) {
			regionsEntity = (RegionsEntity) session.createCriteria(RegionsEntity.class)
					.add(Restrictions.eq("regionCode", regionName)).uniqueResult();

		} catch (Exception e) {
			logger.error("Exception occurred during retrive region by name :{}", e);
			throw new SomethingWentWrongException("Something went wrong during retrive region by name " + regionName);
		}
		return regionsEntity;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	@Override
	public List<RegionsEntity> getAllRegions() {
		List<RegionsEntity> list;
		try (var session = factory.openSession()) {
			list = session.createCriteria(RegionsEntity.class).list();

		} catch (Exception e) {
			logger.error("Exception occurred during retrive all region  : {}", e);
			throw new SomethingWentWrongException("Something went wrong during retrive all region");
		}
		return list;
	}

	@Override
	public void deleteRegion(String regionId) {

		try {
			Transaction transaction = null;
			try (var session = factory.openSession()) {
				transaction = session.beginTransaction();
				RegionsEntity region = session.get(RegionsEntity.class, regionId);
				if (region != null) {
					session.delete(region);
					transaction.commit();
				} else {
					throw new ResourceNotFoundException("Region not found to delete with id = " + regionId);
				}

			} catch (ResourceNotFoundException e) {
				e.printStackTrace();
				logger.error("Region not found to delete with id {}", regionId);
				throw new ResourceNotFoundException(DATANOTFOUND + regionId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occurred during delete region {}", regionId);
			throw new SomethingWentWrongException("Something went wrong during delete region with id = " + regionId);
		}

	}

	@Override
	public void updateRegion(RegionsEntity regionsEntity) {
		Transaction transaction = null;
		try (var session = factory.openSession()) {
			transaction = session.beginTransaction();
			session.update(regionsEntity);
			transaction.commit();

		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			logger.error("Exception occurred during update region  : {}", e);
			throw new SomethingWentWrongException("Something went wrong during update region");
		}

	}

	@SuppressWarnings("deprecation")
	@Override
	public List<Object[]> getCountOfSystemUser() {
		try (var session = factory.openSession()) {
			var hql = "SELECT r.name, COUNT(su.username) FROM SystemUserEntity su JOIN su.roles r GROUP BY r.name";
			Query<Object[]> query = session.createQuery(hql, Object[].class);
			return query.list();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occurred during fetch user count: {}", e);
			throw new SomethingWentWrongException("Something went wrong during fetch user count");
		}
	}

}
