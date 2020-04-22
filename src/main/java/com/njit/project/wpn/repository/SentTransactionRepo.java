package com.njit.project.wpn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.njit.project.wpn.entity.SendTransaction;

@Repository
public interface SentTransactionRepo extends JpaRepository<SendTransaction, Long>{
	
	@Query(value="SELECT * FROM SEND_TRANSACTION WHERE SSN = ?1", nativeQuery = true)
	List<SendTransaction> findSentTransactions(String ssn);

	@Query(value="SELECT * FROM SEND_TRANSACTION WHERE DATETIME BETWEEN ?1 AND ?2", nativeQuery = true)
	List<SendTransaction> findSentTransactions(String fromDate, String toDate);

}
