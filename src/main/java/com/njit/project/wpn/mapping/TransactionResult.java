package com.njit.project.wpn.mapping;

import java.sql.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionResult{

	private long id;
	private String amount;
	private Date dateTime;
	
}
