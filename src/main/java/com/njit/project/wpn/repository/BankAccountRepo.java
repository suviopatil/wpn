package com.njit.project.wpn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.njit.project.wpn.entity.BankAccount;

@Repository
public interface BankAccountRepo extends JpaRepository<BankAccount, Long>{
	
	@Query(value="SELECT * FROM BANKACCOUNT WHERE BANKID = ?1 AND ACCOUNTNUMBER = ?2", nativeQuery = true)
	BankAccount getByBankIdAndAcctNum(Integer bankId, Long accountNumber);

}
