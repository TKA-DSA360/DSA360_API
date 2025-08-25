package com.dsa360.api.daoimpl;

import java.nio.file.Path;
import java.util.List;

import javax.persistence.PersistenceException;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dsa360.api.constants.ReviewType;
import com.dsa360.api.dao.DSADao;
import com.dsa360.api.entity.CustomerEntity;
import com.dsa360.api.entity.DsaApplicationEntity;
import com.dsa360.api.entity.DsaKycEntity;
import com.dsa360.api.entity.SystemUserEntity;
import com.dsa360.api.entity.loan.LoanApplicationEntity;
import com.dsa360.api.exceptions.ResourceAlreadyExistsException;
import com.dsa360.api.exceptions.ResourceNotFoundException;
import com.dsa360.api.exceptions.SomethingWentWrongException;
import com.dsa360.api.utility.FileStorageUtility;
import com.dsa360.api.utility.MailAsyncServices;

/**
 * @author RAM
 *
 */
@Repository
public class DSADaoImpl implements DSADao {

    private final MailAsyncServices mailAsyncServices;

	private static final Logger logger = LoggerFactory.getLogger(DSADaoImpl.class);
	private static final String EMAILEXCPN = "Exception occurred during email verification ";
	private static final String EMAILVERFAILED = "Something went wrong  during email verification ";
	private static final String DSAAPPID = "dsaApplicationId";
	@Autowired
	private SessionFactory factory;

    DSADaoImpl(MailAsyncServices mailAsyncServices) {
        this.mailAsyncServices = mailAsyncServices;
    }

	@Override
	public DsaApplicationEntity getDSAById(String dsaID) {
		var dataNotFound = "Data not found with id = ";
		DsaApplicationEntity dsaRegistrationEntity = null;
		try (var session = factory.openSession()) {
			dsaRegistrationEntity = session.get(DsaApplicationEntity.class, dsaID);

			if (dsaRegistrationEntity != null) {
				return dsaRegistrationEntity;
			} else {
				throw new ResourceNotFoundException(dataNotFound + dsaID);
			}

		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
			logger.error(dataNotFound + dsaID);
			throw new ResourceNotFoundException(dataNotFound + dsaID);
		}

		catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occurred during get DSA with id = " + dsaID, e);
			throw new SomethingWentWrongException("Exception occurred during get DSA with id = " + dsaID);
		}
	}

	@Override
	public DsaApplicationEntity dsaApplication(DsaApplicationEntity dsaRegistrationEntity) {
		Transaction transaction = null;

		try (var session = factory.openSession()) {

			transaction = session.beginTransaction();
			session.save(dsaRegistrationEntity);
			
			transaction.commit();
			logger.info("DSA registration successful for: {}",
					dsaRegistrationEntity.getDsaApplicationId() + " " + dsaRegistrationEntity.getFirstName());

			return dsaRegistrationEntity;
		} catch (PersistenceException e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace(); // for dev env
			logger.error("Duplicate entry error occurred during DSA registration - check unique fields");
			throw new ResourceAlreadyExistsException(
					"Duplicate entry error occurred during DSA registration - check unique fields");
		}

		catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			e.printStackTrace();
			logger.error("Exception occurred during DSA registration", e);
			throw new SomethingWentWrongException("Something went wrong during DSA application");
		}

	}

	@Override
	public DsaApplicationEntity notifyReview(String id, String approvalStatus, String type) {
		Transaction transaction = null;
		final var dataNotFound = "Data Not Found";
		try (var session = factory.openSession()) {

			if (ReviewType.APPLICATION.getValue().equals(type)) {
				DsaApplicationEntity dsaRegistrationEntity = session.get(DsaApplicationEntity.class, id);
				if (dsaRegistrationEntity != null) {
					transaction = session.beginTransaction();
					dsaRegistrationEntity.setApprovalStatus(approvalStatus);
					session.update(dsaRegistrationEntity);
					transaction.commit();
					return dsaRegistrationEntity;
				} else {
					throw new ResourceNotFoundException(dataNotFound);
				}

			} else if (ReviewType.KYC.getValue().equals(type)) {
				DsaKycEntity dsa_KYC_Entity = session.get(DsaKycEntity.class, id);

				if (dsa_KYC_Entity != null) {
					transaction = session.beginTransaction();
					dsa_KYC_Entity.setApprovalStatus(approvalStatus);
					session.update(dsa_KYC_Entity);
					transaction.commit();

					return getDSAById(dsa_KYC_Entity.getDsaApplicationId().getDsaApplicationId());
				} else {
					throw new ResourceNotFoundException(dataNotFound);
				}

			}
			else if (ReviewType.LOAN.getValue().equals(type)) {
			    LoanApplicationEntity dsaLoanEntity = session.get(LoanApplicationEntity.class, id);

			    if (dsaLoanEntity != null) {
			        transaction = session.beginTransaction();
			        dsaLoanEntity.setApprovalStatus(approvalStatus);
			        session.update(dsaLoanEntity);
			        transaction.commit();
			        CustomerEntity customer = dsaLoanEntity.getCustomer();
			        if (customer == null) {
			            throw new ResourceNotFoundException("Customer not linked to Loan");
			        }
			        String customerEmail = customer.getEmail();
			        String customerName  = customer.getName();
			        String customerId    = customer.getId();

			        mailAsyncServices.dsaReviewMail(
			            customerEmail,
			            customerName,
			            approvalStatus,
			            ReviewType.LOAN.getValue(),
			            customerId
			        );

			        return getDSAById(dsaLoanEntity.getCustomer().getDsaAgentId().getDsaApplicationId());
			    } else {
			        throw new ResourceNotFoundException(dataNotFound);
			    }
			}



		} catch (ResourceNotFoundException e) {
			throw new ResourceNotFoundException("Data not found ");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occurred during notify registration review status", e);
			throw new SomethingWentWrongException("Exception occurred during notify registration review status");
		}
		return null;

	}


	@Override
	public boolean systemUserKyc(DsaKycEntity dsa_KYC_Entity, List<Path> storedFilePaths) {
		Transaction transaction = null;
		try (var session = factory.openSession()) {
			transaction = session.beginTransaction();

			session.saveOrUpdate(dsa_KYC_Entity);
			transaction.commit();
			return true;
		}

		catch (Exception e) {

			// Rollback files if any error occurs in DAO layer
			FileStorageUtility.revokeAllFiles(storedFilePaths);
			e.printStackTrace();
			logger.error("Exception occurred during save KYC details in DB ", e);
			throw new SomethingWentWrongException("Exception occurred during save KYC detailsin DB");
		}

	}

	@Override
	public DsaKycEntity getDsaKycByDsaId(String dsaApplicationId) {
		try (var session = factory.openSession()) {

			@SuppressWarnings("deprecation")
			var criteria = session.createCriteria(DsaKycEntity.class, dsaApplicationId);
			criteria.add(Restrictions.eq("dsaApplicationId.dsaApplicationId", dsaApplicationId));
			@SuppressWarnings("unchecked")
			List<DsaKycEntity> list = criteria.list();
			if (!list.isEmpty()) {
				return (DsaKycEntity) list.get(0);

			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occurred fetch KYC data by registration id", e);
			throw new SomethingWentWrongException("Exception occurred fetch KYC data by registration id");
		}
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public List<DsaApplicationEntity> getAllDsaApplication() {
		List<DsaApplicationEntity> list = null;
		try (var session = factory.openSession()) {

			var criteria = session.createCriteria(DsaApplicationEntity.class);
			list = criteria.list();

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception occurred during get all DSA registration", e);
			throw new SomethingWentWrongException("Exception occurred during get all DSA registration");
		}
		return list;
	}

	@Override
	public DsaApplicationEntity emailVerificationRequest(String dsaId, String token) {
		Transaction transaction = null;
		try (var session = factory.openSession()) {
			transaction = session.beginTransaction();

			DsaApplicationEntity dsaEntity = session.get(DsaApplicationEntity.class, dsaId);
			dsaEntity.setEmailVerificationToken(token);
			dsaEntity.setEmailVerified(false);

			session.update(dsaEntity);
			transaction.commit();
			return dsaEntity;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(EMAILEXCPN +"request", e);
			throw new SomethingWentWrongException("Something went wrong  during email verification request");
		}

	}

	@Override
	public void verifyEmail(String dsaId, String token) {
		Transaction transaction = null;
		try (var session = factory.openSession()) {
			transaction = session.beginTransaction();

			DsaApplicationEntity dsaEntity = session.get(DsaApplicationEntity.class, dsaId);
			if (dsaEntity.getEmailVerificationToken().equals(token)) {
				dsaEntity.setEmailVerified(true);
				transaction.commit();
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(EMAILEXCPN, e);
			throw new SomethingWentWrongException(EMAILVERFAILED);
		}

	}

	@Override
	public List<String> getAllApprovedDsa() {
		List<String> dsaIdList = null;
		try (var session = factory.openSession()) {

			var criteria = session.createCriteria(DsaApplicationEntity.class);
			criteria.setProjection(Projections.property(DSAAPPID));
			criteria.add(Restrictions.and(Restrictions.eq("approvalStatus", "Approved"),
					Restrictions.eq("emailVerified", true)));

			dsaIdList = criteria.list();

//			Criteria criteria2 = session.createCriteria(SystemUserEntity.class);
//			criteria2.setProjection(Projections.property("dsaApplicationId.dsaApplicationId"));
//			List<SystemUserEntity> list2 = criteria2.list();

			// dsaIdList.removeAll(list2);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(EMAILEXCPN, e);
			throw new SomethingWentWrongException(EMAILVERFAILED);

		}
		return dsaIdList;
	}

	@Override
	public List<String> getAllApprovedDsaWithNoSystemUser() {
		List<String> dsaIdList = null;
		try (var session = factory.openSession()) {

			var criteria = session.createCriteria(DsaApplicationEntity.class);
			criteria.setProjection(Projections.property(DSAAPPID));
			criteria.add(Restrictions.and(Restrictions.eq("approvalStatus", "Approved"),
					Restrictions.eq("emailVerified", true)));
			dsaIdList = criteria.list();
			// restriction for check is this system user
			var criteria2 = session.createCriteria(SystemUserEntity.class)
				    .setProjection(Projections.property("dsaApplicationId.dsaApplicationId")) // Extracting ID from the entity
				    .add(Restrictions.isNotNull(DSAAPPID));

				List<String> dsaIDs = criteria2.list();
			
			
			System.out.println("********  IDSSSSSSS *******************");
			for (String string : dsaIDs) {
				System.out.println(string);
			}

			dsaIdList.removeAll(dsaIDs);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(EMAILEXCPN, e);
			throw new SomethingWentWrongException(EMAILVERFAILED);

		}
		return dsaIdList;
	}

}
