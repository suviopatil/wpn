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
@Table(name = "ELEC_ADDRESS")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ELECAddress {

	@Id
	@Column(name="IDENTIFIER")
	private String identifier;
	
	@Column(name="VERIFIED")
	private Boolean verified;
}
