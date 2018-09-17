define(['./module'], function (controllers) {
	'use strict';
    controllers.controller("MainCtrl",['$scope', '$http', '$location','$timeout', '$route',  '$rootScope', function ($scope, $http, $location, $timeout, $route, $rootScope) {
    	  console.log('MainCtrl controller');
    	  $scope.resizeMainWindow = function() {
              $('#mainWindow').height(window.innerHeight-50-80);
          };


          $scope.$on('initReq', function(e) {
              $scope.resizeMainWindow();
          });
    	
    	
    	
    	
    }]);
	
	
	
	
});