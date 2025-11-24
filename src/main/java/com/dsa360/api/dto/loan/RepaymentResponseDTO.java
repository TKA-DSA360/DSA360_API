package com.dsa360.api.dto.loan;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentResponseDTO {
	private String disbursementId;
    private String repaymentId;
    private BigDecimal repaymentAmount;
    private LocalDate repaymentDate;
}
