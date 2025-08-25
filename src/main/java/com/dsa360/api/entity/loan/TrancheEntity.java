package com.dsa360.api.entity.loan;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.dsa360.api.constants.TrancheStatus;
import com.dsa360.api.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Table(name = "tranches")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class TrancheEntity extends BaseEntity {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disbursement_id", nullable = false)
    private DisbursementEntity disbursement;

    private BigDecimal trancheAmount;
    private LocalDate trancheDate;
    @Enumerated(EnumType.STRING)
    private TrancheStatus status = TrancheStatus.PENDING;
    
    private String externalTransactionId;
    private String initiatedBy;
    private LocalDateTime initiatedAt;
    private String confirmedBy;
    private LocalDateTime confirmedAt;
    private String failureReason;
    private String idempotencyKey;

    @Version
    private Long version;
}

