package com.dsa360.api.dto.loan;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrancheRequestDTO {
    private String disbursementId;
    private BigDecimal trancheAmount;
    private LocalDate trancheDate;
    private String initiatedBy;
    private String idempotencyKey;
}
