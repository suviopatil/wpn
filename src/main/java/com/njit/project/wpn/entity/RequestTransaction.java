package com.njit.project.wpn.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="REQUEST_TRANSACTION")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter	
public class RequestTransaction implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="RT_ID")
	private long rtId;
	
	@Column(name="RTAMOUNT")
	private String rtAmount;
	
	@Column(name="RTDATETIME")
	//@Temporal(TemporalType.TIME)
	private Date rtDateTime;
	
	@Column(name="RTMEMO")
	private String rtmemo;
	
	@Column(name="SSN")
	private String ssn;

	@Column(name="STATUS")
	private String status;
}
