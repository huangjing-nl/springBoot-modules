define(['./module'], function (controllers) {
	 'use strict';
	 controllers.controller('NavCtrl',['$scope', '$http','$location',  '$rootScope',function($scope, $http, $location, $rootScope){
		  console.log('Navigation controller');
		 /* $scope.isActive = function (route) {
	          return $location.path().indexOf(route) == 0;
	      }*/
		  
		  $scope.logOut=function(){
			  window.location.replace('https://portal.data.hicloud.com/OmsReport/');
		  };
		  
		  
		  
		 
	 }]);
	
});