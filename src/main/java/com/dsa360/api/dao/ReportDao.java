package com.dsa360.api.dao;

import com.dsa360.api.dto.LoanDisbursementSummaryResponseDto;
import com.dsa360.api.dto.report.CustomerReportDto;
import com.dsa360.api.dto.report.DsaReportDto;
import com.dsa360.api.dto.report.FinancialReportDto;
import com.dsa360.api.dto.report.LoanReportDto;
import com.dsa360.api.dto.report.PerformanceReportDto;

public interface ReportDao {
	// 👥 Customer Reports
    CustomerReportDto getCustomerReport();

    // 💰 Loan Reports
    LoanReportDto getLoanReport();

    // 📈 Financial Reports
    FinancialReportDto getFinancialReport();

    // 🏦 DSA Reports
    DsaReportDto getDsaReport();

    // 📊 Performance Reports
    PerformanceReportDto getPerformanceReport();

    // 📊 Loan Disbursement Trend
    LoanDisbursementSummaryResponseDto getLoanDisbursementSummary();
}
