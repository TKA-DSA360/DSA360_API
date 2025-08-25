package com.dsa360.api.dao;
public interface DSAUserMetricsDao {
    long getTotalSystemUsers();
    long getActiveSystemUsers();
    long getDailyNewDsaRegistrations();
    long getWeeklyNewDsaRegistrations();
    long getMonthlyNewDsaRegistrations();
    long getPendingDsaRegistrations();
    long getDeactivedSystemUser(); 
}
