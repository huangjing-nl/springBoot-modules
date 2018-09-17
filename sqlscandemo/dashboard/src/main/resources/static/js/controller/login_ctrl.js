define(['./module'], function (controllers) {
  'use strict';
  controllers.controller("LoginCtrl", ['$scope', '$http', '$location', '$timeout', '$route', '$rootScope', function ($scope, $http, $location, $timeout, $route, $rootScope) {
    $scope.logged = false;
    $scope.checkSecurity = function () {
      $http({
        method : 'GET',
        url : '/api/v1/security/check'
        //data:{"queryId":$routeParams.id}
        //}).success(function(response) {
      }).then(function successCallback(res) {
            console.log(res.data);
            $scope.logged = res.data;
            console.log("res--->" + res.data);
          }, function errorCallback(res) {

          });
    };
    $scope.loggedF = function () {
      return $scope.logged;
    };
  }]);
});