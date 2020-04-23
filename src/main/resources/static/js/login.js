var app = angular.module("myLogin", []);
app.controller("LoginController", function($scope, $http) {
 
    $scope.LoginUser = function() {
    	var method = "";
        var url = "";
        
        $http({
        	method : 'GET',
            url : '/wpn/login',
            params: {"phoneNumber": $scope.phoneNumber, "password": $scope.password},
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(_success, _error);
    };
 
    function _success(res) {
    	 var data = res.data;
         var status = res.status;
         alert("Success: " + status + ":" + data);
    }
 
    function _error(res) {
    	var data = res.data;
        var status = res.status;
        alert("Invalid PhoneNumber/Password");
    }
});