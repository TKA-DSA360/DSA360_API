package com.dsa360.api.dto.report;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class YearlyDisbursementSummaryDto {
    private int year;                                // e.g., 2025
    private List<MonthlyDisbursementDto> monthlyData; // Jan-Dec list
}
