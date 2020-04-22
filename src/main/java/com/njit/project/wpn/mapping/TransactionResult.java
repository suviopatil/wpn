package com.njit.project.wpn.mapping;

import java.io.Serializable;
import java.sql.Date;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TransactionResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	
	private String amount;
	
	private Date dateTime;
	
//	private String stmemo;
//	
//	private String cancelReason;
//	
//	private Integer identifier;
//	
//	private String ssn;
//	
//	private UUID rtId;
//	
//	private Double rtAmount;
//	
//	private Date rtDateTime;
//	
//	private String rtmemo;
	
//	private String ssn;

}
