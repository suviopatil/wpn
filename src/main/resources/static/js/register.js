var app = angular.module("myApp", []);
 
// Controller Part
app.controller("UserController", function($scope, $http) {
 
	/*$scope.bankId = "";
	$scope.accountNumber = "";*/
    $scope.users = [];
    $scope.userAccount = {
    		ssn: "",
    		name: "",
    		phoneNo:"",
    		bankId:"",
    		accountNumber:"",
    		password:""
    };
    
    /*var jsonData = JSON.stringify(
    	    {
    	        email: $scope.email
    	    }
    	);*/
 
    // Now load the data from server
   // _refreshEmployeeData();
 
    $scope.registerUser = function() {
    	 
        var method = "";
        var url = "";
 
        $http({
        	method : 'POST',
            url : '/wpn/addNewUser',
            params: {"emailId": $scope.emailId},
            data: angular.toJson($scope.userAccount),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(_success, _error);
    };
 
    
    // Private Method  
    // HTTP GET- get all employees collection
    // Call: http://localhost:8080/employees
    /*function _refreshEmployeeData() {
        $http({
            method: 'GET',
            url: '/getEmployees'
        }).then(
            function(res) { // success
                $scope.employees = res.data;
            },
            function(res) { // error
                console.log("Error: " + res.status + " : " + res.data);
            }
        );
    }*/
 
    function _success(res) {
    	 var data = res.data;
         var status = res.status;
         $window.location.href = '/home';
    }
 
    function _error(res) {
        var data = res.data;
        var status = res.status;
        alert("Error: " + status + ":" + data);
    }
});