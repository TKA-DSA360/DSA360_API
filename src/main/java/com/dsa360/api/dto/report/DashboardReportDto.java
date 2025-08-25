package com.dsa360.api.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DashboardReportDto {
    private CustomerReportDto customerReports;
    private LoanReportDto loanReports;
    private FinancialReportDto financialReports;
    private DsaReportDto dsaReports;
    private PerformanceReportDto performanceReports;
}
