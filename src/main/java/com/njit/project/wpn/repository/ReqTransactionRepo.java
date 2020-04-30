package com.njit.project.wpn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.njit.project.wpn.entity.RequestTransaction;

public interface ReqTransactionRepo extends JpaRepository<RequestTransaction, Long>{

	@Query(value="SELECT * FROM REQUEST_TRANSACTION WHERE SSN = ?1", nativeQuery = true)
	List<RequestTransaction> findReqTransactionsBySsn(String ssn);

	@Query(value="SELECT * FROM REQUEST_TRANSACTION WHERE RTDATETIME BETWEEN ?1 AND ?2", nativeQuery = true)
	List<RequestTransaction> findReqTransactionsByDate(String fromDate, String toDate);
	
	@Query(value="SELECT U.SSN, U.NAME, R.RT_ID, R.RTAMOUNT FROM REQUEST_TRANSACTION R, FROMUSER F, USERACCOUNT U,EMAIL E WHERE R.RT_ID = F.RTID AND R.SSN=U.SSN AND E.SSN=U.SSN AND R.STATUS = 'Pending' AND F.IDENTIFIER IN ('8482197198','user@gmail.com')", nativeQuery = true)
	List<String> findReqTransactionsByIdentifier(List<String> identifier);
}
