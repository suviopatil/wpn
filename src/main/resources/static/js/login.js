var app = angular.module("myLogin", []);
app.controller("LoginController", function($scope, $http, $window) {
 
    $scope.LoginUser = function() {
    	var method = "";
        var url = "";
        
        $http({
        	method : 'GET',
            url : '/wpn/login',
            params: {
            	"ssn": $scope.ssn, 
            	"password": $scope.password
            	},
            headers: {
                'Content-Type': 'application/json'
            }
        }).then(_success, _error);
    };
 
    function _success(res) {
    	 var data = res.data;
         var status = res.status;
         $window.location.href = '/home';
    }
 
    function _error(res) {
    	var data = res.data;
        var status = res.status;
        $window.location.href = '/loginFailure';
    }
});