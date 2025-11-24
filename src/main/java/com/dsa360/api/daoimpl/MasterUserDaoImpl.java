
package com.dsa360.api.daoimpl;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.dsa360.api.dao.MasterUserDao;
import com.dsa360.api.entity.master.MasterUserEntity;
import com.dsa360.api.exceptions.ResourceNotFoundException;
import com.dsa360.api.exceptions.SomethingWentWrongException;
import com.dsa360.api.security.CustomUserDetail;

@Repository
public class MasterUserDaoImpl implements MasterUserDao {

    private static final Logger log = LogManager.getLogger(MasterUserDaoImpl.class);

    @Autowired
    @Qualifier("masterSessionFactory")
    private SessionFactory sessionFactory;

    @Override
    public Optional<MasterUserEntity> findByUsername(String username) {
        try (Session session = sessionFactory.getCurrentSession()) {
            return Optional.ofNullable(session.createQuery("FROM MasterUserEntity WHERE username = :username", MasterUserEntity.class)
                    .setParameter("username", username)
                    .uniqueResult());
        } catch (Exception e) {
            log.error("Exception occurred during findByUsername for username = {}", username, e);
            throw new SomethingWentWrongException("Error fetching master user: " + username);
        }
    }

    @Override
    public CustomUserDetail loadUserByUserId(String userId) {
        CustomUserDetail user = null;
        MasterUserEntity usr = null;
        try (var session = sessionFactory.openSession()) {
            session.setDefaultReadOnly(false);
            usr = session.get(MasterUserEntity.class, userId);
            if (usr != null) {
                user = new CustomUserDetail();
                user.setId(usr.getUsername());
                user.setUsername(userId);
                user.setPassword(usr.getPassword());
                user.setRoles(usr.getRoles());
                user.setStatus(usr.getStatus());
                user.setUserType("master");
            }
            log.info("Loaded Master User ={}", userId);
        } catch (Exception e) {
            log.error("Exception = {}", e.getMessage());
            e.printStackTrace();
        }
        if (user == null) {
            throw new ResourceNotFoundException("Master user not found with username = " + userId);
        }
        return user;
    }
}
