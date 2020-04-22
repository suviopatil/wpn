package com.njit.project.wpn.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
	//@GeneratedValue(strategy=SEQUENCE, generator="SERVICE_ID_SEQ")
	@Column(name="TX_ID")
	private long stId;
	
	@Column(name="AMOUNT")
	private String stAmount;
	
	@Column(name="DATETIME")
	//@Temporal(TemporalType.TIME)
	private Date stDateTime;
	
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
