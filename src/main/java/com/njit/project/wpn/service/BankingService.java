package com.njit.project.wpn.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.njit.project.wpn.entity.BankAccount;
import com.njit.project.wpn.entity.HasAdditional;
import com.njit.project.wpn.entity.RequestTransaction;
import com.njit.project.wpn.entity.SendTransaction;
import com.njit.project.wpn.entity.UserAccount;
import com.njit.project.wpn.mapping.PendingRequest;
import com.njit.project.wpn.mapping.TransactionResult;
import com.njit.project.wpn.repository.BankAccountRepo;
import com.njit.project.wpn.repository.EmailRepo;
import com.njit.project.wpn.repository.FromUserRepo;
import com.njit.project.wpn.repository.HasAdditionalRepo;
import com.njit.project.wpn.repository.ReqTransactionRepo;
import com.njit.project.wpn.repository.SentTransactionRepo;
import com.njit.project.wpn.repository.UserAccountRepo;
import com.njit.project.wpn.repository.WPNRepo;

@Service
public class BankingService {

	@Autowired
	private UserAccountRepo userAcctRepo;

	@Autowired
	private SentTransactionRepo transactionRepo;

	@Autowired
	private ReqTransactionRepo reqTranxRepo;

	@Autowired
	BankAccountRepo bankAccountRepo;

	@Autowired
	private WPNRepo WPNRepo;

	@Autowired
	private EmailRepo emailRepo;

	@Autowired
	private HasAdditionalRepo hasAdditionalRepo;

	@Autowired
	private FromUserRepo fromUserRepo;

	@Transactional
	public ResponseEntity<?> sendMoney(String senderIdentifier, String receiverIdentifier, String amountToSend, String memo) throws Exception {

		if(StringUtils.isBlank(senderIdentifier) && StringUtils.isBlank(receiverIdentifier) && StringUtils.isBlank(amountToSend)) {
			return new ResponseEntity<>(response("One or more empty input(s)"), HttpStatus.NOT_FOUND);
		}
		String currentUserBalance = null;
		try {
			UserAccount reciepentUsers = getUserByIdentifier(receiverIdentifier);
	
			if (!ObjectUtils.isEmpty(reciepentUsers)) {
				UserAccount senderUser = getUserByIdentifier(senderIdentifier);
				currentUserBalance = senderUser.getAccountBalance();
				if(Double.valueOf(currentUserBalance)<Double.valueOf(amountToSend)) {
					return new ResponseEntity<>(response("Insufficient balance"), HttpStatus.NOT_FOUND);
				}
				Double senderUpdatedBalance = Double.valueOf(currentUserBalance) - Double.valueOf(amountToSend);
				WPNRepo.updateAccountBalance(senderUpdatedBalance.toString(), senderUser.getSsn());
				Double receiverUpdatedBalance = Double.valueOf(reciepentUsers.getAccountBalance()) + Double.valueOf(amountToSend);
				WPNRepo.updateAccountBalance(receiverUpdatedBalance.toString(), reciepentUsers.getSsn());
				WPNRepo.saveSentTransaction(senderUser.getSsn(), receiverIdentifier, amountToSend, memo);
			} else {
				return new ResponseEntity<>(response("Recipient does not exists"), HttpStatus.NOT_FOUND);
			}
		}catch(ParseException e){
			return new ResponseEntity<>(response(e.getMessage()), HttpStatus.NOT_FOUND);
		}
		catch(Exception e){
			return new ResponseEntity<>(response("Couldn't send amount"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response("Amount Successfully sent"), HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<?> sendRequestedMoney(String loggenInUserSsn, String requesterSsn, String amountToSend,
			Long rtId) throws Exception {
		sendMoney(loggenInUserSsn, requesterSsn, amountToSend, "Welcome");
		WPNRepo.updateReqTrnxStatus("Sent", rtId);

		return new ResponseEntity<>(response("Amount Successfully sent"), HttpStatus.OK);
	}
	
	@Transactional
	public ResponseEntity<?> declineRequestedMoney(Long rtId){
		try {
			WPNRepo.updateStatusToDecline("Declined", rtId);
		}catch(Exception e) {
			return new ResponseEntity<>(response("Could not Decline request"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response("Request Declined"), HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<?> requestMoney(String requestorIdentifier, String requesteeIdentifier , String memo, String reqAmount){
		
		if(StringUtils.isBlank(requestorIdentifier) && StringUtils.isBlank(requesteeIdentifier) && StringUtils.isBlank(reqAmount)) {
			return new ResponseEntity<>(response("One or more empty input(s)"), HttpStatus.NOT_FOUND);
		}
		
		LocalTime time = LocalTime.now();
		Long rtId = (long) time.getNano();
		try {
			UserAccount requestee = getUserByIdentifier(requesteeIdentifier);
			if(ObjectUtils.isEmpty(requestee)) {
				return new ResponseEntity<>(response("Requestee does not exists"), HttpStatus.NOT_FOUND);
			}
			
			UserAccount requestor = getUserByIdentifier(requestorIdentifier);
			WPNRepo.saveRequestTransaction(rtId, reqAmount, memo, requestor.getSsn(), "Pending");
			WPNRepo.saveFromTransaction(rtId, requesteeIdentifier, memo);
		}
		catch(Exception e) {
			return new ResponseEntity<>(response("Could not request amount"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response("Amount requested Successfully"), HttpStatus.OK);
	}

	public ResponseEntity<?> findTransactions(String loggedInUserSsn, String fromDate, String toDate) {
		
		if(StringUtils.isBlank(loggedInUserSsn) && StringUtils.isBlank(fromDate) && StringUtils.isBlank(toDate)) {
			return new ResponseEntity<>(response("One or more empty input(s)"), HttpStatus.NOT_FOUND);
		}
		
		List<String> loggedInUserIdentifier = new ArrayList<>();
		List<SendTransaction> sentTransaction = new ArrayList<>();
		try {
			if (StringUtils.isNotBlank(loggedInUserSsn) && StringUtils.isNotBlank(fromDate) && StringUtils.isNotBlank(toDate)) {
				String formattedFromDate = formattedDateTime(fromDate);
				String formattedtoDate = formattedDateTime(toDate);
				if(ObjectUtils.isEmpty(userAcctRepo.findUserBySSN(loggedInUserSsn))) {
					return new ResponseEntity<>(response("Invalid SSN"), HttpStatus.NOT_FOUND);
				}
				loggedInUserIdentifier = getAllIdentifiers(loggedInUserSsn);
				sentTransaction = transactionRepo.findSentTransactions(loggedInUserSsn, formattedFromDate, formattedtoDate);
				List<SendTransaction> receivedTransaction = transactionRepo.findReceivedTransactions(loggedInUserIdentifier, formattedFromDate, formattedtoDate);
				sentTransaction.addAll(receivedTransaction);
			}else {
				return new ResponseEntity<>(response("One or more empty input(s)"), HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			return new ResponseEntity<>(response(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<>(sentTransaction, HttpStatus.OK);
	}
	
	private List<String> getAllIdentifiers(String loggedInUserSsn) throws Exception{
		List<String> loggedInUserIdentifier = new ArrayList<>();
		UserAccount user = userAcctRepo.findUserBySSN(loggedInUserSsn);
		List<String> emialIds = emailRepo.findEmailBySsn(loggedInUserSsn);
		loggedInUserIdentifier.add(user.getPhoneNo());
		loggedInUserIdentifier.addAll(emialIds);
		return loggedInUserIdentifier;
	}
	
	public ResponseEntity<?> getStatement(String loggedInUserSsn, String fromDate, String toDate) throws ParseException {
		
		if(StringUtils.isBlank(loggedInUserSsn) && StringUtils.isBlank(fromDate) && StringUtils.isBlank(toDate)) {
			return new ResponseEntity<>(response("One or more empty input(s)"), HttpStatus.NOT_FOUND);
		}
		
		String formattedFromDate = formattedDateTime(fromDate);
		String formattedtoDate = formattedDateTime(toDate);
		List<String> loggedInUserIdentifier;
		JSONObject response = new JSONObject();
		try {
			loggedInUserIdentifier = getAllIdentifiers(loggedInUserSsn);
			List<String> SumAvgMax_Sent = transactionRepo.getSumAvgMaxSentAmount(loggedInUserSsn, formattedFromDate, formattedtoDate);
			List<String> bestSentUser = transactionRepo.getBestSentUser(loggedInUserIdentifier, formattedFromDate, formattedtoDate);
			List<String> SumAvgMax_Received = transactionRepo.getSumAvgMaxReceivedAmount(loggedInUserIdentifier, formattedFromDate, formattedtoDate);
			List<String> bestReceivedUser = transactionRepo.getBestReceivedUser(loggedInUserSsn, formattedFromDate, formattedtoDate);
			response = formJsonResponseObject(SumAvgMax_Sent, bestSentUser, SumAvgMax_Received, bestReceivedUser);
		} catch (Exception e) {
			return new ResponseEntity<>(response("Could not retrieve statement"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}

	private JSONObject formJsonResponseObject(List<String> SumAvgMax_Sent, List<String> bestSentUser, List<String> SumAvgMax_Received, List<String> bestReceivedUser) {
		JSONObject response = new JSONObject();
		response.put("sentSum", SumAvgMax_Sent.get(0).split(",",0)[0]);
		response.put("sentAvg", SumAvgMax_Sent.get(0).split(",",0)[1]);
		response.put("sentMax", SumAvgMax_Sent.get(0).split(",",0)[2]);
		response.put("bestSentUser", userAcctRepo.findUserBySSN(bestSentUser.get(0).split(",",0)[0]).getName());
		response.put("receivedSum", SumAvgMax_Received.get(0).split(",",0)[0]);
		response.put("receivedAvg", SumAvgMax_Received.get(0).split(",",0)[1]);
		response.put("receivedMax", SumAvgMax_Received.get(0).split(",",0)[2]);
		response.put("bestreceivedUser", getUserByIdentifier(bestReceivedUser.get(0).split(",",0)[0]).getName());
		return response;
	}
	
	@Transactional
	public ResponseEntity<?> registerNewUser(UserAccount userAccount, String emailId) {

		BankAccount bankAccount = null;
		if (userAccount.getBankId() != null && userAccount.getAccountNumber() != null) {
			bankAccount = bankAccountRepo.getByBankIdAndAcctNum(userAccount.getBankId(),
					userAccount.getAccountNumber());
		}
		if (ObjectUtils.isEmpty(bankAccount)) {
			WPNRepo.addNewAccount(userAccount.getBankId(), userAccount.getAccountNumber());
		}
		UserAccount userAcc = userAcctRepo.findUserBySSN(userAccount.getSsn());
		if (ObjectUtils.isEmpty(userAcc)) {
			WPNRepo.addNewUser(userAccount.getSsn(), userAccount.getName(), userAccount.getPhoneNo(),
					userAccount.getAccountBalance(), userAccount.getBankId(), userAccount.getAccountNumber(), "true");
			addEmailId(userAccount.getSsn(), emailId);
			WPNRepo.addNewUserIdentifier(userAccount.getPhoneNo());
		}
		HasAdditional hasAddnl = hasAdditionalRepo.findUserBySsnBidAcct(userAccount.getSsn(), userAccount.getBankId(),
				userAccount.getAccountNumber());
		if (ObjectUtils.isEmpty(hasAddnl)) {
			WPNRepo.hasAdditionalAcc(userAccount.getSsn(), userAccount.getBankId(), userAccount.getAccountNumber(),
					"true");
			return new ResponseEntity<>(response("Account created Successfully"), HttpStatus.OK);
		}
		return new ResponseEntity<>(response("Account is already present"), HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<?> addEmailId(String loggedInUserSsn, String emailId) {
		
		try {
			if(StringUtils.isBlank(loggedInUserSsn) && StringUtils.isBlank(emailId)) {
				return new ResponseEntity<>(response("Please enter emailId"), HttpStatus.NOT_FOUND);
			}else if(StringUtils.isNotBlank(emailRepo.findSsnByEmail(emailId))){
				return new ResponseEntity<>(response("emailId already exists"), HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			WPNRepo.addNewUserEmailId(emailId, loggedInUserSsn);
			WPNRepo.addNewUserIdentifier(emailId);
		} catch (Exception e) {
			return new ResponseEntity<>(response("Could not add emailId"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<>(response("EmailId added successfully"), HttpStatus.OK);
	}
	
	@Transactional
	public ResponseEntity<?> deleteEmailId(String emailId) {
		try {
			if(StringUtils.isBlank(emailId)) {
				return new ResponseEntity<>(response("Please enter emailId"), HttpStatus.NOT_FOUND);
			}else if(StringUtils.isBlank(emailRepo.findSsnByEmail(emailId))){
				return new ResponseEntity<>(response("emailId does not exists"), HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
			WPNRepo.deleteUserEmailId(emailId);
			WPNRepo.deleteUserIdentifier(emailId);
		} catch (Exception e) {
			return new ResponseEntity<>(response("Could not delete emailId"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<>(response("EmailId Deleted successfully"), HttpStatus.OK);
	}
	
	public ResponseEntity<?> getUserEmailId(String loggedInUserSsn) {
		List<String> emailIdList = emailRepo.findEmailBySsn(loggedInUserSsn);
		return new ResponseEntity<>(emailIdList, HttpStatus.OK);
	}
	
	@Transactional
	public ResponseEntity<?> splitAmount(String loggedInUserIdentifier, String amountToSplit, String splitwithIdentifiers) throws ParseException {
		
		if(StringUtils.isBlank(loggedInUserIdentifier) && StringUtils.isBlank(amountToSplit) && StringUtils.isBlank(splitwithIdentifiers)) {
			return new ResponseEntity<>(response("One or more empty input(s)"), HttpStatus.NOT_FOUND);
		}
		
		Integer amountToSplitInt = Integer.parseInt(amountToSplit);
		String loggedInUserSsn = getUserByIdentifier(loggedInUserIdentifier).getSsn();
		String[] arrOfStr = splitwithIdentifiers.split(",",0);
		Integer individualSplitAmount = amountToSplitInt / (1 + arrOfStr.length);
		Integer percentage = (individualSplitAmount * 100) / amountToSplitInt;
		String individualSplitAmountString = individualSplitAmount.toString();
		for (int i = 0; i < arrOfStr.length; i++) {
			LocalTime time = LocalTime.now();
			Long rtId = (long) time.getNano();
			WPNRepo.saveRequestTransaction(rtId, individualSplitAmountString, "SplittingAmount", loggedInUserSsn, "Pending");
			WPNRepo.saveFromTransaction(rtId, arrOfStr[i], percentage.toString());
		}
		return new ResponseEntity<>(response("Split request was sent successfully"), HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<?> updatePno(String phoneNo,String loggedInUserSsn) throws Exception{

		try {
			if(StringUtils.isBlank(phoneNo)) {
				return new ResponseEntity<>(response("phoneNo is empty"), HttpStatus.NOT_FOUND);
			}else if(!ObjectUtils.isEmpty(userAcctRepo.findUserByPhoneNumber(phoneNo))){
				return new ResponseEntity<>(response("Phone number is not Unique"), HttpStatus.INTERNAL_SERVER_ERROR);
			}
			WPNRepo.updatePno(phoneNo,loggedInUserSsn);
			return new ResponseEntity<>(response("Phone number updated successfully"), HttpStatus.OK);
		}catch (Exception e) {
			return new ResponseEntity<>(response("Phone number was not updated"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	private String formattedDateTime(String dateTime) throws ParseException {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date date = inputFormat.parse(dateTime);
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yy hh.mm.ss.SSSSSSSSS a");
		String formattedDate = outputFormat.format(date).toUpperCase();
		return formattedDate;
	}

	public ResponseEntity<?> getUserDetails(String loggedInUserSsn) {
		UserAccount userAccount = userAcctRepo.findUserBySSN(loggedInUserSsn);
		return new ResponseEntity<>(userAccount, HttpStatus.OK);
	}

	public ResponseEntity<?> login(String ssn, String password) {
		if(StringUtils.isBlank(ssn) && StringUtils.isBlank(password)) {
			return new ResponseEntity<>(response("One or more empty input(s)"), HttpStatus.NOT_FOUND);
		}
		
		UserAccount userAccount = userAcctRepo.findBySsnAndPwd(ssn, password);
		if (ObjectUtils.isEmpty(userAccount)) {
			return new ResponseEntity<>(response("User Not Found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response("Successfully logged in"), HttpStatus.OK);
	}

	public ResponseEntity<?> getPendingReq(String loggedInUserSsn) {
		List<String> loggedInUserIdentifier = new ArrayList<>();
		try {
			loggedInUserIdentifier = getAllIdentifiers(loggedInUserSsn);
		} catch (Exception e) {
			return new ResponseEntity<>(response("Invalid User SSN"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		List<String> pendingRequests = reqTranxRepo.findReqTransactionsByIdentifier(loggedInUserIdentifier);
		List<PendingRequest> response = new ArrayList<>();
		for (int i = 0; i < pendingRequests.size(); i++) {
			PendingRequest pendingRequest = new PendingRequest();
			pendingRequest.setSsn(pendingRequests.get(i).split(",", 0)[0]);
			pendingRequest.setName(pendingRequests.get(i).split(",", 0)[1]);
			pendingRequest.setRtId(pendingRequests.get(i).split(",", 0)[2]);
			pendingRequest.setAmount(pendingRequests.get(i).split(",", 0)[3]);
			response.add(pendingRequest);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@Transactional
	public ResponseEntity<?> addBankDetail(String loggedInUserSsn, Integer bankId, Long bankAccNumber) {
		try {
			if(StringUtils.isBlank(bankId.toString()) && StringUtils.isBlank(bankAccNumber.toString())) {
				return new ResponseEntity<>(response("One or more empty input(s)"), HttpStatus.NOT_FOUND);
			}
			WPNRepo.addNewAccount(bankId, bankAccNumber);
			WPNRepo.hasAdditionalAcc(loggedInUserSsn, bankId, bankAccNumber, "true");
		} catch (Exception e) {
			return new ResponseEntity<>(response("could not add Bank details"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(response("Successfully Added Bank details"), HttpStatus.OK);
	}
	
	@Transactional
	public ResponseEntity<?> deleteBankDetail(String loggedInUserSsn, Integer bankId, Long bankAccNumber) {
		try {
			if(StringUtils.isBlank(bankId.toString()) && StringUtils.isBlank(bankAccNumber.toString())) {
				return new ResponseEntity<>(response("One or more empty input(s)"), HttpStatus.NOT_FOUND);
			}
			Integer count = WPNRepo.deleteBankDetails(bankId, bankAccNumber);
			if(count==0) {
				return new ResponseEntity<>(response("bank details do not exist"), HttpStatus.INTERNAL_SERVER_ERROR);
			}
			WPNRepo.deleteAdditionalAccDetails(loggedInUserSsn, bankId, bankAccNumber);
		} catch (Exception e) {
			return new ResponseEntity<>(response("could not delete Bank details"), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(response("Successfully Deleted Bank details"), HttpStatus.OK);
	}

	private String response(String inputResponse) {
		JSONObject response = new JSONObject();
		response.put("response", inputResponse);
		return response.toString();
	}

	private UserAccount getUserByIdentifier(String identifier) {
		UserAccount user = null;
		if (identifier.contains("@")) {
			String receiverSSN = emailRepo.findSsnByEmail(identifier);
			user = userAcctRepo.findUserBySSN(receiverSSN);
		} else if(identifier.length() == 9 || identifier.length() == 11) {
			user = userAcctRepo.findUserBySSN(identifier);
		}else {
			user = userAcctRepo.findUserByPhoneNumber(identifier);
		}
		return user;
	}

}
