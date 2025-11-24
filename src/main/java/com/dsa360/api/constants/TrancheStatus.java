package com.dsa360.api.constants;

public enum TrancheStatus {
    PENDING("Pending"),
    INITIATED("Initiated"),
    SUCCESS("Success"),
    FAILED("Failed"),
    CANCELLED("Cancelled"),
    RECONCILED("Reconciled"),
    CONFIRMED("Confirmed");
	
	    private final String value;
	    
		TrancheStatus(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		
    
    
}
