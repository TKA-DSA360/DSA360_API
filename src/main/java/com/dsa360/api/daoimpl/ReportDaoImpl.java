package com.dsa360.api.daoimpl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dsa360.api.dao.ReportDao;
import com.dsa360.api.dto.LoanDisbursementSummaryResponseDto;
import com.dsa360.api.dto.report.CustomerReportDto;
import com.dsa360.api.dto.report.DsaReportDto;
import com.dsa360.api.dto.report.FinancialReportDto;
import com.dsa360.api.dto.report.LoanReportDto;
import com.dsa360.api.dto.report.PerformanceReportDto;
import com.dsa360.api.exceptions.ResourceNotFoundException;

@Repository
public class ReportDaoImpl implements ReportDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public CustomerReportDto getCustomerReport() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LoanReportDto getLoanReport() {

		return null;
	}

	@Override 
	public FinancialReportDto getFinancialReport() {
		Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();

            String hql = "SELECT new com.dsa360.api.dto.report.FinancialReportDto( " +
                    "COALESCE(SUM(d.netDisbursedAmount), 0), " +
                    "COALESCE(SUM(d.netDisbursedAmount - l.emi), 0), " +
                    "COALESCE(SUM(r.repaymentAmount), 0), " +
                    "COUNT(CASE WHEN r.repaymentDate < CURRENT_DATE THEN 1 END), " +
                    "COALESCE(SUM(CASE WHEN r.repaymentDate < CURRENT_DATE THEN r.repaymentAmount ELSE 0 END), 0) " +
                    ") " +
                    "FROM LoanApplicationEntity l " +
                    "JOIN DisbursementEntity d ON d.loanApplication.id = l.id " +
                    "LEFT JOIN RepaymentEntity r ON r.disbursement.id = d.id " +
                    "WHERE l.approvalStatus = 'APPROVED'";

            FinancialReportDto result = session.createQuery(hql, FinancialReportDto.class)
                                               .getSingleResult();

            tx.commit();
            return result;

        } catch (Exception e) {
        	throw new RuntimeException(
        	        "Error while fetching Financial Report from database in ReportDao. Root cause: " + e.getMessage(),
        	        e
        	    );}
    }

	
	@Override
	public DsaReportDto getDsaReport() {
		// TODO Auto-generated method stub
				try (Session session = sessionFactory.openSession()) {
					List<Object[]> results = session
							.createQuery("SELECT COUNT(d.id), " + "SUM(CASE WHEN d.status = 'ACTIVE' THEN 1 ELSE 0 END), "
									+ "SUM(CASE WHEN d.status = 'SUSPENDED' THEN 1 ELSE 0 END), "
									+ "SUM(CASE WHEN d.status = 'DEACTIVATED' THEN 1 ELSE 0 END) "
									+ "FROM SystemUserEntity d", Object[].class)
							.getResultList();

					if (results.isEmpty() || results.get(0) == null) {
						throw new ResourceNotFoundException("No DSA data found");
					}
					
					Object[] result = results.get(0);
					DsaReportDto dto = new DsaReportDto();
					dto.setTotalDsas(((Number) result[0]).intValue());
					dto.setActiveDsas(((Number) result[1]).intValue());
					dto.setSuspendedDsas(((Number) result[2]).intValue());
					dto.setDeactivatedDsas(((Number) result[3]).intValue());
					return dto;
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Error while generating DSA report: " + e.getMessage(), e);
				}
	}

	@Override
	public PerformanceReportDto getPerformanceReport() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LoanDisbursementSummaryResponseDto getLoanDisbursementSummary() {
		// TODO Auto-generated method stub
		return null;
	}

}
