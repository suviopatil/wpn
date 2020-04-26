package com.njit.project.wpn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.njit.project.wpn.entity.SendTransaction;

@Repository
public interface SentTransactionRepo extends JpaRepository<SendTransaction, Long>{
	
	@Query(value="SELECT * FROM SEND_TRANSACTION WHERE SSN = ?1 AND DATETIME BETWEEN ?2 AND ?3", nativeQuery = true)
	List<SendTransaction> findSentTransactions(String ssn, String fromDate, String toDate);

	@Query(value="SELECT * FROM SEND_TRANSACTION WHERE DATETIME BETWEEN ?1 AND ?2", nativeQuery = true)
	List<SendTransaction> findSentTransactionsByDate(String fromDate, String toDate);
	
	@Query(value="SELECT SUM(AMOUNT), AVG(AMOUNT), MAX(AMOUNT) FROM SEND_TRANSACTION WHERE SSN =?1 AND DATETIME BETWEEN ?2 AND ?3", nativeQuery = true)
	List<String> getSumAvgMaxSentAmount(String ssn, String fromDate, String toDate);
	
	@Query(value="SELECT SSN, SUM(AMOUNT) AS TOTALAMOUNT FROM SEND_TRANSACTION WHERE IDENTIFIER IN ?1 AND DATETIME BETWEEN ?2 AND ?3 GROUP BY SSN ORDER BY TOTALAMOUNT DESC FETCH FIRST ROW ONLY", nativeQuery = true)
	List<String> getBestSentUser(List<String> identifier, String fromDate, String toDate);

	@Query(value="SELECT * FROM SEND_TRANSACTION WHERE IDENTIFIER IN ?1 AND DATETIME BETWEEN ?2 AND ?3", nativeQuery = true)
	List<SendTransaction> findReceivedTransactions(List<String> identifier, String fromDate, String toDate);
	
	@Query(value="SELECT SUM(AMOUNT), AVG(AMOUNT), MAX(AMOUNT) FROM SEND_TRANSACTION WHERE IDENTIFIER IN ?1 AND DATETIME BETWEEN ?2 AND ?3", nativeQuery = true)
	List<String> getSumAvgMaxReceivedAmount(List<String> identifier, String fromDate, String toDate);
	
	@Query(value="SELECT IDENTIFIER, SUM(AMOUNT) AS TOTALAMOUNT FROM SEND_TRANSACTION WHERE SSN=?1 AND DATETIME BETWEEN ?2 AND ?3 GROUP BY IDENTIFIER ORDER BY TOTALAMOUNT DESC FETCH FIRST ROW ONLY", nativeQuery = true)
	List<String> getBestReceivedUser(String ssn, String fromDate, String toDate);
}
