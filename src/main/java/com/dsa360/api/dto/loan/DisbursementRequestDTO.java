package com.dsa360.api.dto.loan;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisbursementRequestDTO {
    private String loanId;
    private BigDecimal sanctionedAmount;
    private BigDecimal fees;
    private BigDecimal netDisbursedAmount;
}
