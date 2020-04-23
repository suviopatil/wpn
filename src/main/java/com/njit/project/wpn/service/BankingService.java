package com.njit.project.wpn.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.json.JSONException;
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
import com.njit.project.wpn.mapping.TransactionResult;
import com.njit.project.wpn.repository.BankAccountRepo;
import com.njit.project.wpn.repository.EmailRepo;
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
	
	@Transactional
	public ResponseEntity<?> sendMoney(String senderSsn, String identifier, String amountToSend, String memo) {
		UserAccount reciepentUsers = null;
		String currentUserBalance = null;
		
		if(identifier.contains("@")) {
			String receiverSSN = emailRepo.findSsnByEmail(identifier);
			reciepentUsers = userAcctRepo.findUserBySSN(receiverSSN);
		}else {
			reciepentUsers = userAcctRepo.findUserByPhoneNumber(identifier);
		}

		if (!ObjectUtils.isEmpty(reciepentUsers)) {
			UserAccount sender = userAcctRepo.findUserBySSN(senderSsn);
			currentUserBalance = sender.getAccountBalance();
			Double senderUpdatedBalance = Double.valueOf(currentUserBalance) - Double.valueOf(amountToSend);
			WPNRepo.updateAccountBalance(senderUpdatedBalance.toString(), senderSsn);
			Double receiverUpdatedBalance = Double.valueOf(reciepentUsers.getAccountBalance()) + Double.valueOf(amountToSend);
			WPNRepo.updateAccountBalance(receiverUpdatedBalance.toString(), reciepentUsers.getSsn());
			WPNRepo.saveSentTransaction(sender.getSsn(), amountToSend, memo);
			return new ResponseEntity<>("Amount Successfully sent", HttpStatus.OK);
		} else {
			String errorMessage = "Couldn't send amount";
			return new ResponseEntity<String>(errorMessage, HttpStatus.NOT_FOUND);
		}

	}
	
	@Transactional
	public ResponseEntity<?> sendRequestedMoney(String senderSsn, String identifier, String amountToSend, Long rtId) {
		sendMoney(senderSsn, identifier, amountToSend, "Welcome");
		WPNRepo.updateReqTrnxStatus("Confirmed",rtId);
		
		return new ResponseEntity<>("Amount Successfully sent", HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<?> requestMoney(String identifier, String requesterSsn, String memo, String reqAmount) {

		LocalTime time = LocalTime.now();
		Long rtId = (long) time.getNano();
		WPNRepo.saveRequestTransaction(rtId, reqAmount , memo, requesterSsn, "Pending");
		WPNRepo.saveFromTransaction(rtId, identifier, "100");
		
		return new ResponseEntity<>("Amount requested Successfully", HttpStatus.OK);
	}

	public ResponseEntity<?> findTransactions(String ssn, String phoneNo, String emailId) throws Exception {

		List<SendTransaction> sendTransaction = transactionRepo.findSentTransactions(ssn);
		List<RequestTransaction> requestTransaction = reqTranxRepo.findReqTransactions(ssn);
		List<TransactionResult> res = txnResCommonMethod(sendTransaction,requestTransaction);
		
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<?> addNewUser(UserAccount userAccount, String emailId) {
		
		BankAccount bankAccount = null;
		if(userAccount.getBankId() != null && userAccount.getAccountNumber() != null) {
			bankAccount = bankAccountRepo.getByBankIdAndAcctNum(userAccount.getBankId(), userAccount.getAccountNumber());
		}
		if(ObjectUtils.isEmpty(bankAccount)) {
			WPNRepo.addNewAccount(userAccount.getBankId(), userAccount.getAccountNumber());
		}
		UserAccount userAcc = userAcctRepo.findUserBySSN(userAccount.getSsn());
		if(ObjectUtils.isEmpty(userAcc)) {
			WPNRepo.addNewUser(userAccount.getSsn(), userAccount.getName(), userAccount.getPhoneNo(), 
					userAccount.getAccountBalance(), userAccount.getBankId(), userAccount.getAccountNumber(), "true");
			addEmailId(emailId, userAccount.getSsn());
			WPNRepo.addNewUserIdentifier(userAccount.getPhoneNo());
		}
		HasAdditional hasAddnl = hasAdditionalRepo.findUserBySsnBidAcct(userAccount.getSsn(), userAccount.getBankId(), userAccount.getAccountNumber());
		 JSONObject response = new JSONObject();
		if(ObjectUtils.isEmpty(hasAddnl)) {
			WPNRepo.hasAdditionalAcc(userAccount.getSsn(),userAccount.getBankId(),userAccount.getAccountNumber(),"true");

			try {
				response.put("response", "Account created Successfully");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return new ResponseEntity<String>(response.toString(), HttpStatus.OK);
		}
		return new ResponseEntity<String>("Account is already present", HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<?> addEmailId(String emailId, String ssn) {
		
		WPNRepo.addNewUserEmailId(emailId, ssn);
		WPNRepo.addNewUserIdentifier(emailId);
		return new ResponseEntity<String>("Account is already present", HttpStatus.OK);
	}	
	
	public ResponseEntity<?> splitAmount(String ssn, String amountToSplit, List<String> identifier) {
		Integer amountToSplitInt = Integer.parseInt(amountToSplit);
		Integer individualSplitAmount = amountToSplitInt / (1+identifier.size());
		Integer percentage = (individualSplitAmount*100)/amountToSplitInt;
		String percentageString = percentage.toString();
		String individualSplitAmountString = individualSplitAmount.toString();
		for (int i = 0; i < identifier.size(); i++) {
			LocalTime time = LocalTime.now();
			Long rtId = (long) time.getNano();
			WPNRepo.saveRequestTransaction(rtId, individualSplitAmountString, "SplittingAmount", ssn, "Pending");
			WPNRepo.saveFromTransaction(rtId, identifier.get(i), percentageString);
		}
		return new ResponseEntity<>("Split request was sent successfully", HttpStatus.OK);
	}
	
	public ResponseEntity<?> getStatement(String fromDate, String toDate) {

		List<SendTransaction> sendTxn = transactionRepo.findSentTransactions(fromDate, toDate);
		List<RequestTransaction> reqTxn = reqTranxRepo.findReqTransactions(fromDate, toDate);
		List<TransactionResult> res = txnResCommonMethod(sendTxn,reqTxn);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
	
	public ResponseEntity<?> getUserDetails() {
		List<UserAccount> userAccount = userAcctRepo.getUserDetails();
		return new ResponseEntity<>(userAccount,HttpStatus.OK);
	}
	
	public ResponseEntity<?> login(String phoneNumber, String password) {
		JSONObject response = new JSONObject();
		response.put("response", "Successfully logged in");
		
		UserAccount userAccount = userAcctRepo.findByPhNoAndPwd(phoneNumber, password);
		if(ObjectUtils.isEmpty(userAccount)) {
			response.put("response", "User Not Found");
			return new ResponseEntity<>(response.toString(), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	private List<TransactionResult> txnResCommonMethod(List<SendTransaction> sendTxn, List<RequestTransaction> reqTxn){
		List<TransactionResult> txnResList = new ArrayList<>();
		for (int i = 0; i < sendTxn.size(); i++) {
			TransactionResult txn = new TransactionResult();
			txn.setId(sendTxn.get(i).getStId());
			txn.setAmount(sendTxn.get(i).getStAmount());
			txn.setDateTime(sendTxn.get(i).getStDateTime());
			txnResList.add(txn);
		}
		for (int i = 0; i < reqTxn.size(); i++) {
			TransactionResult txn = new TransactionResult();
			txn.setId(reqTxn.get(i).getRtId());
			txn.setAmount(reqTxn.get(i).getRtAmount());
			txn.setDateTime(reqTxn.get(i).getRtDateTime());
			txnResList.add(txn);
		}
		return txnResList;
	}


}
