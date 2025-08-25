package com.dsa360.api.daoimpl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.dsa360.api.dao.LoanConditionDao;
import com.dsa360.api.entity.loan.LoanConditioEntity;
import com.dsa360.api.exceptions.SomethingWentWrongException;

/**
 * @author RAM
 *
 */
@Repository
public class LoanConditionDaoImpl implements LoanConditionDao {
	private static final Logger logger = LoggerFactory.getLogger(DSADaoImpl.class);

	@Autowired
	private SessionFactory factory;

	@Override
	public LoanConditioEntity createLoanCondition(LoanConditioEntity loanConditioEntity) {
		try (var session = factory.openSession()) {
			session.save(loanConditioEntity);
			session.beginTransaction().commit();
			return loanConditioEntity;

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("");
			throw new SomethingWentWrongException("Something Went Wrong During Add Loan Contion");
		}

	}

	@Override
	public LoanConditioEntity getLoanConditionById(String id) {

		return null;
	}

	@Override
	public List<LoanConditioEntity> getAllLoanConditions() {

		return null;
	}

	@Override
	public LoanConditioEntity updateLoanCondition(LoanConditioEntity loanConditioEntity) {

		return null;
	}

	@Override
	public void deleteLoanCondition(String id) {
// implement it latter
	}

	@Override
	public LoanConditioEntity getLoanConditionByBank_Loantype(String bankName, String loanType) {
		try (var session = factory.openSession()) {

			var criteria = session.createCriteria(LoanConditioEntity.class);
			SimpleExpression bankNameExpression = Restrictions.eq("bankName", bankName);
			SimpleExpression loanTypeExpression = Restrictions.eq("loanType", loanType);

			criteria.add(Restrictions.and(bankNameExpression, loanTypeExpression));

			List<LoanConditioEntity> list = criteria.list();
			if (list.isEmpty()) {
				return null;
			} else {
				return (LoanConditioEntity) list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Something Went Wrong Retrive loan condtion");
			throw new SomethingWentWrongException("Something Went Wrong");
		}
	}

	@Override
	public List<String> getAllBanksNames() {
		return null;
	}

	@Override
	public double getIntrestRate(String bankName) {

		return 0;
	}

}
