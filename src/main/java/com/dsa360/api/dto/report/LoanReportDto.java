	package com.dsa360.api.dto.report;
	
	import lombok.AllArgsConstructor;
	import lombok.Data;
	import lombok.NoArgsConstructor;
	import lombok.ToString;
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@ToString
	public class LoanReportDto {
	    private int totalLoanApplications;
	    private int activeLoans;
	    private int closedLoans;
	    private int pendingApplications;
	    private int rejectedApplications;
	    private int approvedLoans;
	    private int disbursedLoans;
	}
