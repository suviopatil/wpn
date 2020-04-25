package com.njit.project.wpn.mapping;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionResult{

	private long id;
	private String amount;
	private Timestamp dateTime;
	
}
