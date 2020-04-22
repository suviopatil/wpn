package com.njit.project.wpn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.njit.project.wpn.entity.RequestTransaction;

public interface ReqTransactionRepo extends JpaRepository<RequestTransaction, Long>{

	@Query(value="SELECT * FROM REQUEST_TRANSACTION WHERE SSN = ?1", nativeQuery = true)
	List<RequestTransaction> findReqTransactions(String ssn);

	@Query(value="SELECT * FROM REQUEST_TRANSACTION WHERE RTDATETIME BETWEEN ?1 AND ?2", nativeQuery = true)
	List<RequestTransaction> findReqTransactions(String fromDate, String toDate);
}
