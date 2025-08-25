package com.dsa360.api.utility;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.security.SecureRandom;

public class DynamicID {

	private static final SecureRandom random = new SecureRandom();

	public static String generateUniqueId(String type, String firstName, String lastName) {

		// DSA-2025-AS123456 KYC-2025-AS123456
		int currentYear = LocalDate.now().getYear();
		String initials = (firstName.substring(0, 1) + lastName.substring(0, 1)).toUpperCase();

		int uniqueNumber = 100000 + random.nextInt(900000);

		return type + "-" + currentYear + "-" + initials + uniqueNumber;
	}

	public static String getGeneratedId() {
		// 20250101123456789
		String id = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new java.util.Date());
		return id;

	}

	public static String getGeneratedLoanId() {
		// LN-2025-00001
		int uniqueNumber = 1 + random.nextInt(99999);
		int currentYear = LocalDate.now().getYear();
		return "LN-" + currentYear + "-" + String.format("%05d", uniqueNumber);
	}

	public static String getGeneratedDocumentId() {
		//DOC-20250101123456789
		return "DOC-" + getGeneratedId();
	}

	public static String getGeneratedCustomerId(String firstName, String lastName) {
		// CUST-2025-AS000001
		int uniqueNumber = 1 + random.nextInt(999999);
		int currentYear = LocalDate.now().getYear();
		String initials = (firstName.substring(0, 1) + lastName.substring(0, 1)).toUpperCase();
		
		return "CUST-" + currentYear + "-" + initials + String.format("%06d", uniqueNumber);
	}

	public static String getGeneratedRoleId() {
		// ROLE-00001
		int uniqueNumber = 1 + random.nextInt(99999);
		return "ROLE-" + String.format("%05d", uniqueNumber);

	}

	public static String getGeneratedContactUsId() {
		// CONTACTUS-20250101123445789
		return "CONTACTUS-" + getGeneratedId();
	}

	public static String getGeneratedRegionId() {
		// REG-20250101123445734
		return "REG-" + getGeneratedId();
	}
	public static String getGeneratedDisbursementId() {
		// DISB-2025-000001
		int uniqueNumber = 1 + random.nextInt(999999);
		int currentYear = LocalDate.now().getYear();
		return "DISB-" + currentYear + "-" + String.format("%05d", uniqueNumber);
	}
	public static String getGeneratedLoanDisbursementId() {
		// LD-2025-000001
		int uniqueNumber = 1 + random.nextInt(999999);
		int currentYear = LocalDate.now().getYear();
		return "LD-" + currentYear + "-" + String.format("%05d", uniqueNumber);
	}
	public static String getGeneratedLoanApplicationId() {
		// LA-2025-000001
		int uniqueNumber = 1 + random.nextInt(999999);
		int currentYear = LocalDate.now().getYear();
		return "LA-" + currentYear + "-" + String.format("%05d", uniqueNumber);
	}
// Tranche
	public static String getGeneratedTrancheId() {
		// TR-20250101127845789
		return "TR-" + getGeneratedId();
	}

	public static String getGeneratedLoanTrancheId() {
		// LT-20250101156845789
		return "LT-" + getGeneratedId();
	}

	public static String getGeneratedTrancheAuditId() {
		// TA-20250101156765789
		return "TA-" + getGeneratedId();
	}

	public static String getGeneratedRepaymentId() {
		// REP-20250101345845789
		int uniqueNumber = 1 + random.nextInt(999999);
		int currentYear = LocalDate.now().getYear();
		return "REP-" + currentYear + "-" + String.format("%05d", uniqueNumber);
	}
	// 
	public static String getGeneratedReconciliationId() {
		// REC-20250101345845789
		return "REC-" + getGeneratedId();
	}
}
