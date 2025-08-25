package com.dsa360.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsa360.api.dto.LoanDisbursementSummaryResponseDto;
import com.dsa360.api.dto.report.CustomerReportDto;
import com.dsa360.api.dto.report.DashboardReportDto;
import com.dsa360.api.dto.report.DsaReportDto;
import com.dsa360.api.dto.report.FinancialReportDto;
import com.dsa360.api.dto.report.LoanReportDto;
import com.dsa360.api.dto.report.PerformanceReportDto;
import com.dsa360.api.service.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportsAnalyticsController {

	@Autowired
	private ReportService reportService;

	// Suvidha
	@GetMapping("/customer")
	public ResponseEntity<CustomerReportDto> getCustomerReport() {
		return null;

	}

	// aade akash
	@GetMapping("/loan")
	public ResponseEntity<LoanReportDto> getLoanReport() {

		return null;

	}

	// akash khandare
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/financials")
	public ResponseEntity<FinancialReportDto> getFinancialReport() {
		try {
	        FinancialReportDto frd = reportService.getFinancialReport();
	        return ResponseEntity.ok(frd);
	    } catch (Exception e) {
	        // log the error
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();}
	    	}

	// ashok
	@GetMapping("/dsas")
	public ResponseEntity<DsaReportDto> getDsasReport() {
		 
		DsaReportDto dsaReport = reportService.getDsaReport();
		if (dsaReport != null) {
			return ResponseEntity.ok(dsaReport);
		}
		// If no data found, return an appropriate response
		return ResponseEntity.notFound().build();
	}

	// Roshani
	@GetMapping("/performance")
	public ResponseEntity<PerformanceReportDto> getPerformanceReport() {
		return null;
	}

	@GetMapping("/dashboard")
	public ResponseEntity<DashboardReportDto> getDashboardReport() {
		return null;
	}

	// prachi dixit dixit
	@GetMapping("/loan/disbursement-summary")
	public ResponseEntity<LoanDisbursementSummaryResponseDto> getLoanDisbursementSummary() {
		return null;
	}

}
