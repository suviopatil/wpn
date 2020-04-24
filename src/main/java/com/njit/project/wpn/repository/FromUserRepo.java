package com.njit.project.wpn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.njit.project.wpn.entity.FromUser;

public interface FromUserRepo extends JpaRepository<FromUser, Long>{

//	@Query(value="SELECT RT_ID FROM REQUEST_TRANSACTION WHERE IDENTIFIER = ?1", nativeQuery = true)
//	List<Integer> findReqTransactionsByIdentifier(String identifier);
}
