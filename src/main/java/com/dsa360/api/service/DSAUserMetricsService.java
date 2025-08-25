package com.dsa360.api.service;

public interface DSAUserMetricsService {
	long getTotalSystemUsers();
    long getActiveSystemUsers();
    long getDailyNewDsaRegistrations();
    long getWeeklyNewDsaRegistrations();
    long getMonthlyNewDsaRegistrations();
    long getPendingDsaRegistrations();
    long getDeactivedSystemUser();
}
