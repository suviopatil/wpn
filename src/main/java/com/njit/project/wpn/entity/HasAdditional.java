package com.njit.project.wpn.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="HAS_ADDITIONAL")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HasAdditional {

	@Id
	@Column(name="SSN")
	private String ssn;
	
	@Column(name="BANKID")
	private Integer bankId;
	
	@Column(name="ACCOUNTNUMBER")
	private Long accountNumber;
	
	@Column(name="VERIFIED")
	private String verified;
}
