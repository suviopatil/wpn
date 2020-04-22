package com.njit.project.wpn.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Table(name="BANKACCOUNT")
public class BankAccount {
	
	@Id
	@Column(name="BANKID")
	private Integer bankId;
	
	@Column(name="ACCOUNTNUMBER")
	private Long accountNumber;
	
}
