package com.dsa360.api.dto.report;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MonthlyDisbursementDto {
    private String month;           // e.g., "January"
    private BigDecimal amountInLakhs; // e.g., 45.67
}
