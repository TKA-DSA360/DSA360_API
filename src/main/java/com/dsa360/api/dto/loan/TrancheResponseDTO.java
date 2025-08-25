package com.dsa360.api.dto.loan;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrancheResponseDTO {
    private String trancheId;
    private String disbursementId;
    private BigDecimal trancheAmount;
    private LocalDate trancheDate;
    private String status;
    private String externalTransactionId;
    private String idempotencyKey;
}
