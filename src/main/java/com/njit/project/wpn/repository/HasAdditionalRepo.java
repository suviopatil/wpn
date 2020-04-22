package com.njit.project.wpn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.njit.project.wpn.entity.HasAdditional;

@Repository
public interface HasAdditionalRepo extends JpaRepository<HasAdditional, Long>{
	
	@Query(value="SELECT * FROM HAS_ADDITIONAL WHERE SSN = ?1 AND BANKID = ?2 AND ACCOUNTNUMBER = ?3", nativeQuery = true)
	HasAdditional findUserBySsnBidAcct(String ssn, Integer bankId, Long accountNumber);

}
