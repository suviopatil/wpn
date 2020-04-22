package com.njit.project.wpn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.njit.project.wpn.entity.Email;

@Repository
public interface EmailRepo extends JpaRepository<Email, Long>{
	
	@Query(value="SELECT SSN FROM EMAIL WHERE EMAILADD = ?1", nativeQuery = true)
	String findSsnByEmail(String emailId);

}
