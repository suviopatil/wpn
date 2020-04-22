package com.njit.project.wpn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.njit.project.wpn.entity.User;
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
			@RequestParam(value = "senderSsn") String senderSsn,
			@RequestParam(value = "identifier") String identifier,
			@RequestParam(value = "amountToSend") String amountToSend,
			@RequestParam(value = "memo") String memo) {

		return bankingService.sendMoney(senderSsn, identifier, amountToSend, memo);
	}
	
	@RequestMapping(value = "/sendRequestedMoney", method = RequestMethod.POST)
	public ResponseEntity<?> sendRequestedMoney(
			@RequestParam(value = "senderSsn") String senderSsn,
			@RequestParam(value = "identifier") String identifier,
			@RequestParam(value = "amountToSend") String amountToSend,
			@RequestParam(value = "rtId") Long rtId) {

		return bankingService.sendRequestedMoney(senderSsn, identifier, amountToSend, rtId);
	}
	
	@RequestMapping(value = "/requestMoney", method = RequestMethod.POST)
	public ResponseEntity<?> requestMoney(
			@RequestParam(value = "identifier") String identifier,
			@RequestParam(value = "requesterSsn") String requesterSsn,
			@RequestParam(value = "memo") String memo,
			@RequestParam(value = "reqAmount") String reqAmount) {

		return bankingService.requestMoney(identifier, requesterSsn, memo, reqAmount);
	}

	@RequestMapping(value = "/searchTransactions", method = RequestMethod.GET)
	public ResponseEntity<?> getSentTransaction(@RequestParam(value = "ssn") String ssn,
			@RequestParam(value = "phoneNo") String phoneNo, @RequestParam(value = "emailId") String emailId)
			throws Exception {

		return bankingService.findTransactions(ssn, phoneNo, emailId);
	}
	
	@RequestMapping(value = "/getStatement", method = RequestMethod.GET)
	public ResponseEntity<?> getStatement(@RequestParam(value = "fromDate") String fromDate,
			@RequestParam(value = "toDate") String toDate)
			throws Exception {

		return bankingService.getStatement(fromDate, toDate);
	}
	
	@RequestMapping(value = "/getUserDetails", method = RequestMethod.GET)
	public ResponseEntity<?> getUserDetails()
			throws Exception {

		return bankingService.getUserDetails();
	}
	
	@RequestMapping(value = "/addNewUserAccount", method = RequestMethod.POST, consumes="application/json")
	public ResponseEntity<?> addBankDetails(
			@RequestBody User userAccount,
			@RequestParam(value = "email") String email) {

		return new ResponseEntity<>(userAccount,HttpStatus.OK);
	}

}
