package com.njit.project.wpn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.njit.project.wpn.entity.UserAccount;

@Repository
public interface UserAccountRepo extends JpaRepository<UserAccount, Long>{

	@Query(value = "SELECT * FROM USERACCOUNT WHERE SSN = ?1", nativeQuery = true)
	UserAccount findUserBySSN(String ssn);
	
	@Query(value = "SELECT * FROM USERACCOUNT WHERE PHONENO = ?1", nativeQuery = true)
	UserAccount findUserByPhoneNumber(String phoneNo);
	
	@Query(value = "UPDATE USERACCOUNT SET ACCOUNTBALANCE = ?1 WHERE PHONENO = ?2 OR EMAIL = ?3", nativeQuery = true)
	void updateBalance(String currentUserBalance, String phoneNo, String email);
	
	@Query(value="SELECT * FROM USERACCOUNT WHERE SSN = ?1 OR PHONENO = ?2 OR EMAIL = ?3 ", nativeQuery = true)
	UserAccount findByPhNoOrEmailorSsn(String ssn, String phoneNo, String emailId);

	@Query(value="SELECT * FROM USERACCOUNT WHERE SSN = ?1 OR PASSWORD = ?2 ", nativeQuery = true)
	UserAccount findBySsnAndPwd(String ssn, String password);
}
