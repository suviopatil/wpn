package com.njit.project.wpn.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ssn;
	
	private String name;
	
	private String phoneNo;
	
	private String password;
	
	
	
	/*@OneToMany( cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<SendTransaction> transactionlist;*/
	
}
