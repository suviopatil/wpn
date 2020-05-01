package com.njit.project.wpn.controller;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.njit.project.wpn.entity.UserAccount;
import com.njit.project.wpn.service.BankingService;

@RestController
@RequestMapping("/wpn")
public class AccountController {

	@Autowired
	private BankingService bankingService;

	@RequestMapping(value = "/registerNewUser", method = RequestMethod.POST, consumes="application/json")
	public ResponseEntity<?> registerNewUser(
			@RequestBody UserAccount userAccount,
			@RequestParam(value = "emailId") String emailId) {

		return bankingService.registerNewUser(userAccount, emailId);
	}
	
	@RequestMapping(value = "/addEmailId", method = RequestMethod.POST)
	public ResponseEntity<?> addEmailId(
			@RequestParam(value = "loggedInUserSsn") String loggedInUserSsn,
			@RequestParam(value = "emailId") String emailId) {

		return bankingService.addEmailId(loggedInUserSsn, emailId);
	}
	
	@RequestMapping(value = "/deleteEmailId", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteEmailId(
			@RequestParam(value = "emailId") String emailId) {

		return bankingService.deleteEmailId(emailId);
	}
	
	@RequestMapping(value = "/getUserEmailId", method = RequestMethod.GET)
	public ResponseEntity<?> getUserEmailId(
			@RequestParam(value = "loggedInUserSsn") String loggedInUserSsn) {

		return bankingService.getUserEmailId(loggedInUserSsn);
	}
	
	@RequestMapping(value = "/splitAmount", method = RequestMethod.POST)
	public ResponseEntity<?> splitAmount(
			@RequestParam(value = "loggedInUserIdentifier") String loggedInUserIdentifier,
			@RequestParam(value = "amountToSplit") String amountToSplit,
			@RequestParam(value = "splitwithIdentifiers") String splitwithIdentifiers) throws ParseException {

		return bankingService.splitAmount(loggedInUserIdentifier, amountToSplit, splitwithIdentifiers);
	}

	@RequestMapping(value = "/sendMoney", method = RequestMethod.POST)
	public ResponseEntity<?> sendMoney(
			@RequestParam(value = "senderIdentifier") String senderIdentifier,
			@RequestParam(value = "receiverIdentifier") String receiverIdentifier,
			@RequestParam(value = "amountToSend") String amountToSend,
			@RequestParam(value = "memo", required = false) String memo) throws Exception {

		return bankingService.sendMoney(senderIdentifier, receiverIdentifier, amountToSend, memo);
	}
	
	@RequestMapping(value = "/sendRequestedMoney", method = RequestMethod.POST)
	public ResponseEntity<?> sendRequestedMoney(
			@RequestParam(value = "loggenInUserSsn") String loggenInUserSsn,
			@RequestParam(value = "requesterSsn") String requesterSsn,
			@RequestParam(value = "amountToSend") String amountToSend,
			@RequestParam(value = "rtId") Long rtId) throws Exception {

		return bankingService.sendRequestedMoney(loggenInUserSsn, requesterSsn, amountToSend, rtId);
	}
	
	@RequestMapping(value = "/declineRequestedMoney", method = RequestMethod.POST)
	public ResponseEntity<?> declineRequestedMoney(
			@RequestParam(value = "rtId") Long rtId) throws Exception {

		return bankingService.declineRequestedMoney(rtId);
	}
	
	@RequestMapping(value = "/requestMoney", method = RequestMethod.POST)
	public ResponseEntity<?> requestMoney(
			@RequestParam(value = "requestorIdentifier") String requestorIdentifier,
			@RequestParam(value = "requesteeIdentifier") String requesteeIdentifier,
			@RequestParam(value = "rtMemo", required = false) String rtMemo,
			@RequestParam(value = "rtAmount") String rtAmount) throws ParseException {

		return bankingService.requestMoney(requestorIdentifier, requesteeIdentifier, rtMemo, rtAmount);
	}

	@RequestMapping(value = "/searchTransactions", method = RequestMethod.GET)
	public ResponseEntity<?> getSentTransaction(
			@RequestParam(value = "loggedInUserSsn", required = false) String loggedInUserSsn,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate)
			throws Exception {

		return bankingService.findTransactions(loggedInUserSsn, fromDate, toDate);
	}
	
	@RequestMapping(value = "/getStatement", method = RequestMethod.GET)
	public ResponseEntity<?> getStatement(
			@RequestParam(value = "ssn", required = false) String ssn,
			@RequestParam(value = "fromDate") String fromDate,
			@RequestParam(value = "toDate") String toDate)
			throws Exception {

		return bankingService.getStatement(ssn, fromDate, toDate);
	}
	
	@RequestMapping(value = "/getPendingReq", method = RequestMethod.GET)
	public ResponseEntity<?> getPendingReq(
			@RequestParam(value = "loggedInUserSsn") String loggedInUserSsn){

		return bankingService.getPendingReq(loggedInUserSsn);
	}
	
	@RequestMapping(value = "/getUserDetails", method = RequestMethod.GET)
	public ResponseEntity<?> getUserDetails(@RequestParam(value = "loggedInUserSsn") String loggedInUserSsn)
			throws Exception {

		return bankingService.getUserDetails(loggedInUserSsn);
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ResponseEntity<?> login(
			@RequestParam(value = "ssn") String ssn,
			@RequestParam(value = "password") String password) throws Exception {
		
		return bankingService.login(ssn, password);
	}
	
	@RequestMapping(value = "/UpdatePno", method = RequestMethod.PUT)
	public ResponseEntity<?> UpdatePno(
			@RequestParam(value = "phoneNo") String phoneNo,
			@RequestParam(value = "loggedInUserSsn") String loggedInUserSsn) throws Exception {

		return bankingService.updatePno(phoneNo,loggedInUserSsn);
	}

	@RequestMapping(value = "/addBankDetail", method = RequestMethod.POST)
	public ResponseEntity<?> addBankDetail(
			@RequestParam(value = "loggedInUserSsn") String loggedInUserSsn,
			@RequestParam(value = "bankId") Integer bankId,
			@RequestParam(value = "bankAccNumber") Long bankAccNumber
			) {

		return bankingService.addBankDetail(loggedInUserSsn, bankId, bankAccNumber);
	}
	
	@RequestMapping(value = "/deleteBankDetail", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteBankDetail(
			@RequestParam(value = "loggedInUserSsn") String loggedInUserSsn,
			@RequestParam(value = "bankId") Integer bankId,
			@RequestParam(value = "bankAccNumber") Long bankAccNumber
			) {

		return bankingService.deleteBankDetail(loggedInUserSsn, bankId, bankAccNumber);
	}
}
