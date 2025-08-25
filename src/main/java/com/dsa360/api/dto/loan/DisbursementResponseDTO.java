package com.dsa360.api.dto.loan;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisbursementResponseDTO {
    private String disbursementId;
    private String loanId;
    private BigDecimal sanctionedAmount;
    private BigDecimal fees;
    private BigDecimal netDisbursedAmount;
    private LocalDate disbursementDate;
    private List<TrancheResponseDTO> tranches;
}
