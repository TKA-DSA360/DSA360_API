package com.dsa360.api.entity.loan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.dsa360.api.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "loan_disbursements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "com.dsa360.api.entity.loan.LoanDisbursementEntity")

public class LoanDisbursementEntity extends BaseEntity {

    @Id
    private String id;  

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalDisbursed = BigDecimal.ZERO;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_application_id", nullable = false, unique = true)
    private LoanApplicationEntity loanApplication;

    @OneToMany(mappedBy = "disbursement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanTrancheEntity> tranches = new ArrayList<>();

    @Version
    private Long version;
}
