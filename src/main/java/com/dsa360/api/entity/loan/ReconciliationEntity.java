package com.dsa360.api.entity.loan;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dsa360.api.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "reconciliation")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Entity
public class ReconciliationEntity extends BaseEntity {

    @Id
    private String id;

    private String trancheId;
    private String externalTxnId;
    private BigDecimal externalAmount;
    private LocalDate externalDate;
    private String status; // MATCHED, MISMATCH, PENDING
    private String remarks;
    private LocalDateTime reconciledAt;

    // getters/setters
}
