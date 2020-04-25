package com.njit.project.wpn.entity;

import java.io.Serializable;
import java.sql.Timestamp;

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
@Table(name="SEND_TRANSACTION")
public class SendTransaction implements Serializable{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="TX_ID")
	private long stId;
	
	@Column(name="AMOUNT")
	private String stAmount;
	
	@Column(name="DATETIME")
	private Timestamp stDateTime;
	
	@Column(name="MEMO")
	private String stmemo;
	
	@Column(name="CANCELREASON")
	private String cancelReason;
	
	@Column(name="IDENTIFIER")
	private String identifier;
	
	@Column(name="SSN")
	private String ssn;
	
	/*@ManyToOne(fetch = FetchType.LAZY)
	private UserAccount userAccount;*/
	
}
