package com.dsa360.api.entity.loan;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import com.dsa360.api.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Table(name = "loan_disbursements")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class LoanDisbursementEntity extends BaseEntity {

    @Id
    private String id;

    private Double totalDisbursed = 0.0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id", nullable = false, unique = true)
    private LoanApplicationEntity loanApplication;

    @OneToMany(mappedBy = "disbursement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoanTrancheEntity> tranches;

    @Version
    private Long version;
}
