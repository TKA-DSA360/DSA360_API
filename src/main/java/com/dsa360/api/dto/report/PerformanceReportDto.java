package com.dsa360.api.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PerformanceReportDto {
    private double customerGrowthPercentage;   // vs last month
    private double loanGrowthPercentage;       // vs last month
    private double approvalRate;               // approved vs applied
    private double closureRate;                // closed vs sanctioned
}
