package com.dsa360.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsa360.api.dto.DSAMetricsCombinedResponse;
import com.dsa360.api.dto.DSAMetricsResponseDto;
import com.dsa360.api.dto.DSAMetricsResponseDto.NewRegistrations;
import com.dsa360.api.service.DSAUserMetricsService;

@RestController
@RequestMapping("/admin/metrics")
public class DSAUserMetricsController {

	@Autowired
	private DSAUserMetricsService dsaUserMetricsService;

	@GetMapping("/all")

	@PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_SUB_ADMIN')")
	public ResponseEntity<DSAMetricsCombinedResponse> getAllMetrics() {
		long totalSystemUsers = dsaUserMetricsService.getTotalSystemUsers();
		long activeSystemUsers = dsaUserMetricsService.getActiveSystemUsers();
		long daily = dsaUserMetricsService.getDailyNewDsaRegistrations();
		long weekly = dsaUserMetricsService.getWeeklyNewDsaRegistrations();
		long monthly = dsaUserMetricsService.getMonthlyNewDsaRegistrations();
		long pendingDsa = dsaUserMetricsService.getPendingDsaRegistrations();
		long deactivated = dsaUserMetricsService.getDeactivedSystemUser();

		DSAMetricsResponseDto.NewRegistrations newRegs = new NewRegistrations(daily, weekly, monthly);
		DSAMetricsResponseDto dto = new DSAMetricsResponseDto(totalSystemUsers, activeSystemUsers, newRegs, pendingDsa,
				deactivated);

		return ResponseEntity.ok(new DSAMetricsCombinedResponse("success", dto));
	}

	@GetMapping("/total-users")
	@PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_SUB_ADMIN')")
	public ResponseEntity<Long> getTotalDSAUsers() {
		return ResponseEntity.ok(dsaUserMetricsService.getTotalSystemUsers());
	}

	@GetMapping("/active-users")
	@PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_SUB_ADMIN')")
	public ResponseEntity<Long> getActiveDSAs() {
		return ResponseEntity.ok(dsaUserMetricsService.getActiveSystemUsers());
	}

	@GetMapping("/deactivated-users")
	@PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_SUB_ADMIN')")
	public ResponseEntity<Long> getDeactivedSystemUser() {
		return ResponseEntity.ok(dsaUserMetricsService.getDeactivedSystemUser());
	}

	@GetMapping("/new-registrations/daily")
	@PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_SUB_ADMIN')")
	public ResponseEntity<Long> getDailyNewRegistrations() {
		return ResponseEntity.ok(dsaUserMetricsService.getDailyNewDsaRegistrations());
	}

	@GetMapping("/new-registrations/weekly")
	@PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_SUB_ADMIN')")
	public ResponseEntity<Long> getWeeklyNewRegistrations() {
		return ResponseEntity.ok(dsaUserMetricsService.getWeeklyNewDsaRegistrations());
	}

	@GetMapping("/new-registrations/monthly")
	@PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_SUB_ADMIN')")
	public ResponseEntity<Long> getMonthlyNewRegistrations() {
		return ResponseEntity.ok(dsaUserMetricsService.getMonthlyNewDsaRegistrations());
	}

	@GetMapping("/pending-dsa-registrations")
	@PreAuthorize("hasRole('ROLE_ADMIN') OR hasRole('ROLE_SUB_ADMIN')")
	public ResponseEntity<Long> getPendingDsaRegistrations() {
		return ResponseEntity.ok(dsaUserMetricsService.getPendingDsaRegistrations());
	}
}
