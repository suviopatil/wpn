package com.njit.project.wpn.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

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
	public ResponseEntity<?> sendMoney(String senderIdentifier, String receiverIdentifier, String amountToSend,
			String memo) {

		String currentUserBalance = null;

		UserAccount reciepentUsers = getUserByIdentifier(receiverIdentifier);
		if (!ObjectUtils.isEmpty(reciepentUsers)) {
			UserAccount senderUser = getUserByIdentifier(senderIdentifier);
			currentUserBalance = senderUser.getAccountBalance();
			Double senderUpdatedBalance = Double.valueOf(currentUserBalance) - Double.valueOf(amountToSend);
			WPNRepo.updateAccountBalance(senderUpdatedBalance.toString(), senderUser.getSsn());
			Double receiverUpdatedBalance = Double.valueOf(reciepentUsers.getAccountBalance())
					+ Double.valueOf(amountToSend);
			WPNRepo.updateAccountBalance(receiverUpdatedBalance.toString(), reciepentUsers.getSsn());
			WPNRepo.saveSentTransaction(senderUser.getSsn(), amountToSend, memo);
			return new ResponseEntity<>(response("Amount Successfully sent"), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(response("Couldn't send amount"), HttpStatus.NOT_FOUND);
		}

	}

	@Transactional
	public ResponseEntity<?> sendRequestedMoney(String senderIdentifier, String receiverIdentifier, String amountToSend,
			Long rtId) {
		sendMoney(senderIdentifier, receiverIdentifier, amountToSend, "Welcome");
		WPNRepo.updateReqTrnxStatus("Confirmed", rtId);

		return new ResponseEntity<>(response("Amount Successfully sent"), HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<?> requestMoney(String requesteeIdentifier, String requestorIdentifier, String memo,
			String reqAmount) {
		LocalTime time = LocalTime.now();
		Long rtId = (long) time.getNano();
		UserAccount requestor = getUserByIdentifier(requestorIdentifier);
		WPNRepo.saveRequestTransaction(rtId, reqAmount, memo, requestor.getSsn(), "Pending");
		WPNRepo.saveFromTransaction(rtId, requesteeIdentifier, memo);
		return new ResponseEntity<>(response("Amount requested Successfully"), HttpStatus.OK);
	}

	public ResponseEntity<?> findTransactions(String txnIdentifier, String fromDate, String toDate) throws Exception {
		String ssn = null;
		List<TransactionResult> response = new ArrayList<>();
		if (StringUtils.isNotBlank(txnIdentifier)) {
			if ((txnIdentifier.length() == 9 || txnIdentifier.length() == 11) && !txnIdentifier.contains("@")) {
				ssn = txnIdentifier;
			} else {
				UserAccount user = getUserByIdentifier(txnIdentifier);
				ssn = user.getSsn();
			}
			List<SendTransaction> sendTransaction = transactionRepo.findSentTransactions(ssn);
			List<RequestTransaction> requestTransaction = reqTranxRepo.findReqTransactionsBySsn(ssn);
			response = txnResCommonMethod(sendTransaction, requestTransaction);
		}
		else if(StringUtils.isNotBlank(fromDate) && StringUtils.isNotBlank(toDate)) {
			String formattedFromDate = formattedDateTime(fromDate);
			String formattedtoDate = formattedDateTime(toDate);
			List<SendTransaction> sendTxn = transactionRepo.findSentTransactions(formattedFromDate, formattedtoDate);
			List<RequestTransaction> reqTxn = reqTranxRepo.findReqTransactions(formattedFromDate, formattedtoDate);
			response = txnResCommonMethod(sendTxn, reqTxn);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Transactional
	public ResponseEntity<?> addNewUser(UserAccount userAccount, String emailId) {

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
			addEmailId(emailId, userAccount.getSsn());
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
	public ResponseEntity<?> addEmailId(String emailId, String ssn) {

		WPNRepo.addNewUserEmailId(emailId, ssn);
		WPNRepo.addNewUserIdentifier(emailId);
		return new ResponseEntity<>(response("EmailId added successfully"), HttpStatus.OK);
	}

	public ResponseEntity<?> splitAmount(String ssn, String amountToSplit, List<String> identifier) {
		Integer amountToSplitInt = Integer.parseInt(amountToSplit);
		Integer individualSplitAmount = amountToSplitInt / (1 + identifier.size());
		Integer percentage = (individualSplitAmount * 100) / amountToSplitInt;
		String percentageString = percentage.toString();
		String individualSplitAmountString = individualSplitAmount.toString();
		for (int i = 0; i < identifier.size(); i++) {
			LocalTime time = LocalTime.now();
			Long rtId = (long) time.getNano();
			WPNRepo.saveRequestTransaction(rtId, individualSplitAmountString, "SplittingAmount", ssn, "Pending");
			WPNRepo.saveFromTransaction(rtId, identifier.get(i), percentageString);
		}
		return new ResponseEntity<>(response("Split request was sent successfully"), HttpStatus.OK);
	}

	public ResponseEntity<?> getStatement(String fromDate, String toDate) throws ParseException {

		String formattedFromDate = formattedDateTime(fromDate);
		String formattedtoDate = formattedDateTime(toDate);
		List<SendTransaction> sendTxn = transactionRepo.findSentTransactions(formattedFromDate, formattedtoDate);
		List<RequestTransaction> reqTxn = reqTranxRepo.findReqTransactions(formattedFromDate, formattedtoDate);
		List<TransactionResult> res = txnResCommonMethod(sendTxn, reqTxn);
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	private String formattedDateTime(String dateTime) throws ParseException {
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Date date = inputFormat.parse(dateTime);
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yy hh.mm.ss.SSSSSSSSS a");
		String formattedDate = outputFormat.format(date).toUpperCase();
		return formattedDate;
	}

	public ResponseEntity<?> getUserDetails() {
		List<UserAccount> userAccount = userAcctRepo.getUserDetails();
		return new ResponseEntity<>(userAccount, HttpStatus.OK);
	}

	public ResponseEntity<?> login(String phoneNumber, String password) {
		UserAccount userAccount = userAcctRepo.findByPhNoAndPwd(phoneNumber, password);
		if (ObjectUtils.isEmpty(userAccount)) {
			return new ResponseEntity<>(response("User Not Found"), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response("Successfully logged in"), HttpStatus.OK);
	}

	public ResponseEntity<?> getRequests(String identifier) {
		List<String> pendingRequests = reqTranxRepo.findReqTransactionsByIdentifier(identifier);
		List<PendingRequest> response = new ArrayList<>();
		for (int i = 0; i < pendingRequests.size(); i++) {
			PendingRequest pendingRequest = new PendingRequest();
			pendingRequest.setName(pendingRequests.get(i).split(",", 0)[0]);
			pendingRequest.setAmount(pendingRequests.get(i).split(",", 0)[1]);
			response.add(pendingRequest);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	private List<TransactionResult> txnResCommonMethod(List<SendTransaction> sendTxn, List<RequestTransaction> reqTxn) {
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

	private String response(String inputResponse) {
		JSONObject response = new JSONObject();
		response.put("response", inputResponse);
		return response.toString();
	}

	private UserAccount getUserByIdentifier(String identifier) {
		UserAccount users = null;
		if (identifier.contains("@")) {
			String receiverSSN = emailRepo.findSsnByEmail(identifier);
			users = userAcctRepo.findUserBySSN(receiverSSN);
		} else {
			users = userAcctRepo.findUserByPhoneNumber(identifier);
		}
		return users;
	}

}
