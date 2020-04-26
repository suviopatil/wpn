var app = angular.module("home", []);
app.controller("homeController", function($scope, $http, $window) {

	$scope.reqTxns = [];
	$scope.transactions = [];
	getRequests();
	$scope.statement =[];
	
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
		var data = res.data;
		var status = res.status;
		alert(data.response);
	}

	function _error(res) {
		var data = res.data;
		var status = res.status;
		alert(data.response);
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
		var data = res.data;
		var status = res.status;
		alert(data.response);
	}

	function _error(res) {
		var data = res.data;
		var status = res.status;
		alert(data.response);
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
				"splitwithIdentifiers" : $scope.splitwithIdentifiers,
			},
			headers : {
				'Content-Type' : 'application/json'
			}
		}).then(_success, _error);
	};

	function _success(res) {
		var data = res.data;
		alert(data.response);
	}

	function _error(res) {
		var data = res.data;
		alert(data.response);
	}
	
	function getRequests() {
        $http({
            method: 'GET',
            params : {
				"identifier" : "8457509233"
			},
            url: '/wpn/getRequests'
        }).then(
            function(res) { // success
                $scope.reqTxns = res.data;
            },
            function(res) { // error
                console.log("Error: " + res.status + " : " + res.data);
            }
        );
    }
	
	$scope.getTransactions = function() {
		$http({
            method: 'GET',
            url: '/wpn/searchTransactions',
            params : {
            	"txnIdentifier" : $scope.txnIdentifier,
				"fromDate" : $scope.fromDate,
				"toDate" : $scope.toDate
			}
        }).then(
            function(res) { // success
                $scope.transactions = res.data;
            },
            function(res) { // error
                console.log("Error: " + res.status + " : " + res.data);
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
                $scope.statement = res.data;
            },
            function(res) { // error
                console.log("Error: " + res.status + " : " + res.data);
            }
        );
	}
});