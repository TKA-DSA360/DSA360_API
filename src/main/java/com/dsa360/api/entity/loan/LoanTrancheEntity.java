package com.dsa360.api.entity.loan;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.dsa360.api.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "loan_tranches")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "com.dsa360.api.entity.loan.LoanTrancheEntity")

public class LoanTrancheEntity extends BaseEntity {

    @Id
    private String id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal trancheAmount;   

    private LocalDate disbursementDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_disbursement_id", nullable = false)
    private LoanDisbursementEntity disbursement;
}

