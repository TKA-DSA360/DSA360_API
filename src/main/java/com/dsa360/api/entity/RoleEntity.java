package com.dsa360.api.entity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "com.dsa360.api.entity.RoleEntity")
public class RoleEntity extends BaseEntity implements Role {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;
}