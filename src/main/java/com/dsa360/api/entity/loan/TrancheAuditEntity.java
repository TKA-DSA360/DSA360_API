package com.dsa360.api.entity.loan;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import com.dsa360.api.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tranche_audit")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
public class TrancheAuditEntity extends BaseEntity {

    @Id
    private String id;

    private String trancheId;
    private String eventType; // CREATED, INITIATED, CONFIRMED, FAILED, UPDATED
    private String eventBy;
    private LocalDateTime eventAt;
    @Lob
    private String data;

    // getters/setters
}
