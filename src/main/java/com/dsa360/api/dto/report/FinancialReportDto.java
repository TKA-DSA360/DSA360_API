package com.dsa360.api.dto.report;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Accessors(chain = true)
public class FinancialReportDto {
    private BigDecimal totalLoanAmountDisbursed;
    private BigDecimal totalOutstandingAmount;
    private BigDecimal totalEmiCollectedThisMonth;
    private long overdueEmiCount;
    private BigDecimal overdueEmiValue;
}
