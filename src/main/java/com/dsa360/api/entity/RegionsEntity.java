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

/**
 * @author RAM
 */
@Entity
@Table(name = "regions")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor

@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "com.dsa360.api.entity.loan.RegionsEntity")
public class RegionsEntity extends  BaseEntity{

	@Id
	private String id;

	@Column(name = "region_name", nullable = false, unique = true)
	private String regionName;

	@Column(name = "region_code", nullable = false, unique = true)
	private String regionCode;

	public RegionsEntity(String id) {
		this.id = id;
	}

}
