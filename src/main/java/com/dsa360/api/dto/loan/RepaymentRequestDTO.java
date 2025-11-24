package com.dsa360.api.dto.loan;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public class RepaymentRequestDTO {
	    private String disbursementId;
	    private BigDecimal repaymentAmount;
	    private LocalDate repaymentDate;
	    private String paidBy;
	}
