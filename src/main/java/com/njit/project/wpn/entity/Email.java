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
@Table(name="EMAIL")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter	
public class Email {
	
	@Id
	@Column(name="EMAILADD")
	private String emailId;
	
	@Column(name="SSN")
	private String ssn;
	
}

