define([ './module' ], function(controllers) {
	'use strict';
	controllers.controller("SidebarCtrl", [ '$scope', '$http', '$location', '$timeout', '$route', '$rootScope',
		function($scope, $http, $location, $timeout, $route, $rootScope) {
			console.log('SidebarCtrl controller');

			$scope.isHideMenu1 = false;
			//$scope.menuBar1=true;

			$scope.isHideMenu2 = false;
			//$scope.menuBar2=true;


			$scope.isHideMenu3 = false;
			//$scope.menuBar1=true;

			$scope.changeState1 = function() {
				$scope.isHideMenu1 = !$scope.isHideMenu1;
			//	$scope.menuBar1=!$scope.menuBar1;
			}
			$scope.changeState2 = function() {
				$scope.isHideMenu2 = !$scope.isHideMenu2;
			//	$scope.menuBar2=!$scope.menuBar2;
			}

			$scope.changeState3 = function() {
				$scope.isHideMenu3 = !$scope.isHideMenu3;
			//	$scope.menuBar2=!$scope.menuBar2;
			}

			/*$scope.isActive = function (route) {
          return $location.path().indexOf(route) == 0;
			}*/


		} ]);
});