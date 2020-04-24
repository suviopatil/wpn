var app = angular.module("home", []);
app.controller("homeController", function($scope, $http, $window) {

	$scope.reqTxns = [];
	
	getRequests();
	
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
});