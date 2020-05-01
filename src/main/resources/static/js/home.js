var app = angular.module("home", []);
app.controller("homeController", function($scope, $http, $window) {

	$scope.reqTxns = [];
	$scope.transactions = [];
	$scope.statement =[];
	$scope.user = [];
	$scope.emailIdList = [];
	
	$scope.canShowMenu1 = false;
	$scope.canShowMenu2 = false;
	$scope.canShowMenu3 = false;
	$scope.showStmtDetails = false;
	$scope.showTxnHistory = false;
	$scope.showPendingReqTxns = false;
	$scope.showPendingReqInput = true;
	$scope.inputUserDetails = true;
	$scope.showUserDetails = false;
	
	
	//getRequests();
	//getUserDetails();
	emailIdList();
	
	$scope.openCity = function(evt, cityName) {
		var i, tabcontent, tablinks;
		tabcontent = document.getElementsByClassName("tabcontent");
		for (i = 0; i < tabcontent.length; i++) {
			tabcontent[i].style.display = "none";
		}
		tablinks = document.getElementsByClassName("tablinks");
		for (i = 0; i < tablinks.length; i++) {
			tablinks[i].className = tablinks[i].className
					.replace(" active", "");
		}
		document.getElementById(cityName).style.display = "block";
		evt.currentTarget.className += " active";
	}

	$scope.sendMoney = function() {
		var method = "";
		var url = "";

		$http({
			method : 'POST',
			url : '/wpn/sendMoney',
			params : {
				"senderIdentifier" : $scope.fromUser,
				"receiverIdentifier" : $scope.toUser,
				"amountToSend" : $scope.amount,
				"memo" : $scope.memo
			},
			headers : {
				'Content-Type' : 'application/json'
			}
		}).then(_success, _error);
	};

	function _success(res) {
		alert(res.data.response);
	}

	function _error(res) {
		alert(res.data.response);
	}
	
	
	$scope.requestMoney = function() {
		var method = "";
		var url = "";

		$http({
			method : 'POST',
			url : '/wpn/requestMoney',
			params : {
				"requestorIdentifier" : $scope.requestorIdentifier,
				"requesteeIdentifier" : $scope.requesteeIdentifier,
				"rtAmount" : $scope.rtAmount,
				"rtMemo" : $scope.rtMemo
			},
			headers : {
				'Content-Type' : 'application/json'
			}
		}).then(_success, _error);
	};

	function _success(res) {
		alert(res.data.response);
	}

	function _error(res) {
		alert(res.data.response);
	}
	
	$scope.split = function() {
		var method = "";
		var url = "";

		$http({
			method : 'POST',
			url : '/wpn/splitAmount',
			params : {
				"loggedInUserIdentifier" : $scope.loggedInUserIdentifier,
				"amountToSplit" : $scope.amountToSplit,
				"splitwithIdentifiers" : $scope.splitwithIdentifiers
			},
			headers : {
				'Content-Type' : 'application/json'
			}
		}).then(_success, _error);
	};

	function _success(res) {
		alert(res.data.response);
	}

	function _error(res) {
		alert(res.data.response);
	}
	
	$scope.getPendingReq = function(){
		$scope.showPendingReqTxns = true;
		$scope.showPendingReqInput = false;
        $http({
            method: 'GET',
            url: '/wpn/getPendingReq',
            params : {
				"loggedInUserSsn" : $scope.signInUserSSN
			}
        }).then(
            function(res) { // success
                $scope.reqTxns = res.data;
            },
            function(res) { // error
            	alert(res.data.response);
            }
        );
    }
	
	$scope.getTransactions = function() {
		$http({
            method: 'GET',
            url: '/wpn/searchTransactions',
            params : {
            	"loggedInUserSsn" : $scope.loggedInUserSsn,
				"fromDate" : $scope.fromDate,
				"toDate" : $scope.toDate
			}
        }).then(
            function(res) { 
            	$scope.showTxnHistory = true;
                $scope.transactions = res.data;
            },
            function(res) { 
            	$scope.showTxnHistory = false;
            	alert(res.data.response);
            }
        );
	}
	
	$scope.getStatement = function() {
		$http({
            method: 'GET',
            url: '/wpn/getStatement',
            params : {
            	"ssn" : $scope.ssn,
				"fromDate" : $scope.fromDate,
				"toDate" : $scope.toDate
			}
        }).then(
            function(res) { // success
            	$scope.showStmtDetails = true;
                $scope.statement = res.data;
            },
            function(res) { // error
            	$scope.showStmtDetails = false;
            	alert(res.data.response);
            }
        );
	}
	
		$scope.getUserDetails = function() {
			$scope.inputUserDetails = false;
			$scope.showUserDetails = true;
		$http({
            method: 'GET',
            url: '/wpn/getUserDetails',
            params : {
            	"loggedInUserSsn" : $scope.currentLoggedInUserSsn
			}
        }).then(
            function(res) { // success
                $scope.user = res.data;
            },
            function(res) { // error
            	alert(res.data.response);
            }
        );
	}
	
	function emailIdList() {
		$http({
            method: 'GET',
            url: '/wpn/getUserEmailId',
            params : {
            	"loggedInUserSsn" : "123456789"
			}
        }).then(
            function(res) { // success
                $scope.emailIdList = res.data;
            },
            function(res) { // error
            	alert(res.data.response);
            }
        );
	}
	
	$scope.addOrRemovePno = function() {
		$scope.canShowMenu1 = true;
		$scope.canShowMenu2 = false;
		$scope.canShowMenu3 = false;
	}
	$scope.addOrRemoveEmail = function() {
		$scope.canShowMenu2 = true;
		$scope.canShowMenu1 = false;
		$scope.canShowMenu3 = false;
	}
	$scope.addOrRemoveBankAccount = function() {
		$scope.canShowMenu3 = true;
		$scope.canShowMenu1 = false;
		$scope.canShowMenu2 = false;
	}
	
	$scope.updatePno = function() {
		$http({
            method: 'PUT',
            url: '/wpn/UpdatePno',
            params : {
            	"loggedInUserSsn" : "123456789",
            	"phoneNo" : $scope.phoneNo
			}
        }).then(
            function(res) { // success
            	alert(res.data.response);
            },
            function(res) { // error
            	alert(res.data.response);
            }
        );
	}
	
	$scope.deletePno = function() {
		alert("PhoneNumber can not be deleted");
	}
	
	$scope.addEmailId = function() {
		$http({
            method: 'POST',
            url: '/wpn/addEmailId',
            params : {
            	"loggedInUserSsn" : "123456789",
            	"emailId" : $scope.emailId
			}
        }).then(
            function(res) { // success
            	alert(res.data.response);
            },
            function(res) { // error
            	alert(res.data.response);
            }
        );
	}
	
	$scope.deleteEmailId = function() {
		$http({
            method: 'DELETE',
            url: '/wpn/deleteEmailId',
            params : {
            	"emailId" : $scope.emailId
			}
        }).then(
            function(res) { // success
            	alert(res.data.response);
            },
            function(res) { // error
            	alert(res.data.response);
            }
        );
	}

	$scope.addBankDetail = function() {
		$http({
            method: 'POST',
            url: '/wpn/addBankDetail',
            params : {
            	"loggedInUserSsn" : "123456789",
            	"bankId" : $scope.bankId,
            	"bankAccNumber" : $scope.bankAccNumber
			}
        }).then(
            function(res) { // success
            	alert(res.data.response);
            },
            function(res) { // error
            	alert(res.data.response);
            }
        );
	}
	
	$scope.deleteBankDetail = function() {
		$http({
            method: 'DELETE',
            url: '/wpn/deleteBankDetail',
            params : {
            	"loggedInUserSsn" : "123456789",
            	"bankId" : $scope.bankId,
            	"bankAccNumber" : $scope.bankAccNumber
			}
        }).then(
            function(res) { // success
            	alert(res.data.response);
            },
            function(res) { // error
            	alert(res.data.response);
            }
        );
	}
	
	$scope.sendRequestedMoney = function(ssn, rtId, amount) {
		$http({
			/*$scope.reqTxns*/
	            method: 'POST',
	            url: '/wpn/sendRequestedMoney',
	            params : {
	            	"loggenInUserSsn" : $scope.signInUserSSN,
	            	"requesterSsn" : ssn,
	            	"rtId" : rtId,
	            	"amountToSend" : amount
				}
        }).then(
            function(res) { // success
            	alert(res.data.response);
            },
            function(res) { // error
            	alert(res.data.response);
            }
        );
	}
	
	$scope.declineRequestedMoney = function(rtId) {
		$http({
			/*$scope.reqTxns*/
	            method: 'POST',
	            url: '/wpn/declineRequestedMoney',
	            params : {
	            	"rtId" : rtId
				}
        }).then(
            function(res) { // success
            	alert(res.data.response);
            },
            function(res) { // error
            	alert(res.data.response);
            }
        );
	}
	
	$scope.signOut = function(){
		 $window.location.href = '/login';
	}
	
});