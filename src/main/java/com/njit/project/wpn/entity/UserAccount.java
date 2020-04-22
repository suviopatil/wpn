package com.njit.project.wpn.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "USERACCOUNT")
public class UserAccount implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="SSN")
	private String ssn;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="PHONENO")
	private String phoneNo;
	
	@Column(name="ACCOUNTBALANCE")
	private String accountBalance;
	
	@Column(name="BANKID")
	private Integer bankId;
	
	@Column(name="ACCOUNTNUMBER")
	private Long accountNumber;
	
	@Column(name="PBAVERIFIED")
	private String pbaVerified;
	
	@Column(name="PASSWORD")
	private String password;
	
	
	
	/*@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<SendTransaction> transactionlist;*/
	
}
