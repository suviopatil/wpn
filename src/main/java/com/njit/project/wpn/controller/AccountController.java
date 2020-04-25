package com.njit.project.wpn.controller;

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

	@RequestMapping(value = "/addNewUser", method = RequestMethod.POST, consumes="application/json")
	public ResponseEntity<?> addNewUser(
			@RequestBody UserAccount userAccount,
			@RequestParam(value = "emailId") String emailId) {

		return bankingService.addNewUser(userAccount, emailId);
	}
	
	@RequestMapping(value = "/addEmailId", method = RequestMethod.POST, consumes="application/json")
	public ResponseEntity<?> addEmailId(
			@RequestParam(value = "ssn") String ssn,
			@RequestParam(value = "emailId") String emailId) {

		return bankingService.addEmailId(ssn, emailId);
	}
	
	@RequestMapping(value = "/splitAmount", method = RequestMethod.POST, consumes="application/json")
	public ResponseEntity<?> splitAmount(
			@RequestParam(value = "ssn") String ssn,
			@RequestParam(value = "amountToSplit") String amountToSplit,
			@RequestBody List<String> identifier) {

		return bankingService.splitAmount(ssn, amountToSplit, identifier);
	}

	@RequestMapping(value = "/sendMoney", method = RequestMethod.POST)
	public ResponseEntity<?> sendMoney(
			@RequestParam(value = "senderIdentifier") String senderIdentifier,
			@RequestParam(value = "receiverIdentifier") String receiverIdentifier,
			@RequestParam(value = "amountToSend") String amountToSend,
			@RequestParam(value = "memo", required = false) String memo) {

		return bankingService.sendMoney(senderIdentifier, receiverIdentifier, amountToSend, memo);
	}
	
	@RequestMapping(value = "/sendRequestedMoney", method = RequestMethod.POST)
	public ResponseEntity<?> sendRequestedMoney(
			@RequestParam(value = "senderIdentifier") String senderIdentifier,
			@RequestParam(value = "receiverIdentifier") String receiverIdentifier,
			@RequestParam(value = "amountToSend") String amountToSend,
			@RequestParam(value = "rtId") Long rtId) {

		return bankingService.sendRequestedMoney(senderIdentifier, receiverIdentifier, amountToSend, rtId);
	}
	
	@RequestMapping(value = "/requestMoney", method = RequestMethod.POST)
	public ResponseEntity<?> requestMoney(
			@RequestParam(value = "requesteeIdentifier") String requesteeIdentifier,
			@RequestParam(value = "requestorIdentifier") String requestorIdentifier,
			@RequestParam(value = "rtMemo", required = false) String rtMemo,
			@RequestParam(value = "rtAmount") String rtAmount) {

		return bankingService.requestMoney(requesteeIdentifier, requestorIdentifier, rtMemo, rtAmount);
	}

	@RequestMapping(value = "/searchTransactions", method = RequestMethod.GET)
	public ResponseEntity<?> getSentTransaction(
			@RequestParam(value = "txnIdentifier", required = false) String txnIdentifier, 
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate)
			throws Exception {

		return bankingService.findTransactions(txnIdentifier, fromDate, toDate);
	}
	
	@RequestMapping(value = "/getStatement", method = RequestMethod.GET)
	public ResponseEntity<?> getStatement(
			@RequestParam(value = "fromDate") String fromDate,
			@RequestParam(value = "toDate") String toDate)
			throws Exception {

		return bankingService.getStatement(fromDate, toDate);
	}
	
	@RequestMapping(value = "/getRequests", method = RequestMethod.GET)
	public ResponseEntity<?> getRequests(
			@RequestParam(value = "identifier") String identifier){

		return bankingService.getRequests(identifier);
	}
	
	@RequestMapping(value = "/getUserDetails", method = RequestMethod.GET)
	public ResponseEntity<?> getUserDetails()
			throws Exception {

		return bankingService.getUserDetails();
	}
	
/*	@RequestMapping(value = "/addNewUserAccount", method = RequestMethod.POST, consumes="application/json")
	public ResponseEntity<?> addBankDetails(
			@RequestBody User userAccount,
			@RequestParam(value = "email") String email) {

		return new ResponseEntity<>(userAccount,HttpStatus.OK);
	}*/

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ResponseEntity<?> login(
			@RequestParam(value = "phoneNumber") String phoneNumber,
			@RequestParam(value = "password") String password) throws Exception {
		
		return bankingService.login(phoneNumber, password);
	}
}
