var app = angular.module("myApp", []);
 
app.controller("UserController", function($scope, $http, $window) {
 
    $scope.users = [];
    $scope.userAccount = {
    		ssn: "",
    		name: "",
    		phoneNo:"",
    		bankId:"",
    		accountNumber:"",
    		password:""
    };
    
    $scope.registerNewUser = function() {
    	 
        var method = "";
        var url = "";
 
        $http({
        	method : 'POST',
            url : '/wpn/registerNewUser',
            params: {"emailId": $scope.emailId},
            data: angular.toJson($scope.userAccount),
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(_success, _error);
    };
 
    
    function _success(res) {
         $window.location.href = '/home';
    }
 
    function _error(res) {
        alert("Couldn't register User");
    }
});