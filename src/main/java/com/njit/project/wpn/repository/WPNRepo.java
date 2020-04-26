package com.njit.project.wpn.repository;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class WPNRepo {
	
	@Autowired
	EntityManager em;
	
	public void updateAccountBalance(String updatedBalance, String ssn) {
		String query = "UPDATE USERACCOUNT SET ACCOUNTBALANCE = ?1 WHERE SSN = ?2";
		em.createNativeQuery(query)
			.setParameter(1, updatedBalance)
			.setParameter(2, ssn)
			.executeUpdate();
	}
	
	public void saveSentTransaction(String ssn, String identifier, String amountToSend, String memo) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
		String currentTimestampSql = formattedDateTime(currentTimestamp.toString());
		LocalTime time = LocalTime.now();
		String query = "INSERT INTO SEND_TRANSACTION(SSN, AMOUNT, DATETIME,ST_ID, MEMO, IDENTIFIER) VALUES(?1,?2,?3,?4,?5,?6)";
		em.createNativeQuery(query)
			.setParameter(1, ssn)
			.setParameter(2, amountToSend)
			.setParameter(3, currentTimestampSql)
			.setParameter(4, time.getNano())
			.setParameter(5, memo)
			.setParameter(6, identifier)
			.executeUpdate();
	}
	
	public void saveRequestTransaction(Long rtId, String requestedAmount, String memo, String ssn, String status) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		Date now = calendar.getTime();
		Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
		String currentTimestampSql = formattedDateTime(currentTimestamp.toString());

		String query = "INSERT INTO REQUEST_TRANSACTION(SSN, RTAMOUNT, RTDATETIME, RT_ID, RTMEMO, STATUS) VALUES(?1,?2,?3,?4,?5,?6)";
		em.createNativeQuery(query)
			.setParameter(1, ssn)
			.setParameter(2, requestedAmount)
			.setParameter(3, currentTimestampSql)
			.setParameter(4, rtId)
			.setParameter(5, memo)
			.setParameter(6, status)
			.executeUpdate();
	}
	
	private String formattedDateTime(String dateTime) throws ParseException {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:sss");
		Date date = inputFormat.parse(dateTime);
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yy hh.mm.ss.SSSSSSSSS a");
		String formattedDate = outputFormat.format(date).toUpperCase();
		return formattedDate;
	}
	public void addNewAccount(Integer bankId, Long accountNumber) {
		String query = "INSERT INTO BANKACCOUNT(BANKID, ACCOUNTNUMBER) VALUES(?1, ?2)";
		em.createNativeQuery(query)
			.setParameter(1, bankId)
			.setParameter(2, accountNumber)
			.executeUpdate();
	}
	
	public void addNewUser(String ssn, String name, String phoneNo, String balance, Integer bankId,
		Long accountNumber, String pbaVerified) {
		String query = "INSERT INTO USERACCOUNT (SSN, NAME, PHONENO, ACCOUNTBALANCE, BANKID, ACCOUNTNUMBER, PBAVERIFIED) VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7)";
		em.createNativeQuery(query)
			.setParameter(1, ssn)
			.setParameter(2, name)
			.setParameter(3, phoneNo)
			.setParameter(4, balance)
			.setParameter(5, bankId)
			.setParameter(6, accountNumber)
			.setParameter(7, pbaVerified)
			.executeUpdate();
	}
	
	public void saveFromTransaction(long rtId, String requesteeInfo,String percentage) {
		String query = "INSERT INTO FROMUSER(RTID, IDENTIFIER,PERCENTAGE) VALUES(?1, ?2, ?3)";
		em.createNativeQuery(query)
			.setParameter(1, rtId)
			.setParameter(2, requesteeInfo)
			.setParameter(3, percentage)
			.executeUpdate();
	}

	public void updateReqTrnxStatus(String status,Long rtId) {
		String query = "UPDATE REQUEST_TRANSACTION SET STATUS = ?1 WHERE RT_ID = ?2";
		em.createNativeQuery(query)
			.setParameter(1, status)
			.setParameter(2, rtId)
			.executeUpdate();
	}

	public void hasAdditionalAcc(String ssn, Integer bankId, Long accountNumber, String verified) {
		String query = "INSERT INTO HAS_ADDITIONAL(SSN, BANKID, ACCOUNTNUMBER, VERIFIED) VALUES(?1, ?2, ?3, ?4)";
		em.createNativeQuery(query)
			.setParameter(1, ssn)
			.setParameter(2, bankId)
			.setParameter(3, accountNumber)
			.setParameter(4, verified)
			.executeUpdate();
	}

	public void addNewUserEmailId(String emailId, String ssn) {
		String query = "INSERT INTO EMAIL(EMAILADD, SSN) VALUES(?1, ?2)";
		em.createNativeQuery(query)
			.setParameter(1, emailId)
			.setParameter(2, ssn)
			.executeUpdate();
	}

	public void addNewUserIdentifier(String identifier) {
		String query = "INSERT INTO ELEC_ADDRESS(IDENTIFIER, VERIFIED) VALUES(?1, ?2)";
		em.createNativeQuery(query)
			.setParameter(1, identifier)
			.setParameter(2, "false")
			.executeUpdate();
	}

}
