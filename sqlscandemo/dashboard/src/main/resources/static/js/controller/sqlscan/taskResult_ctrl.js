define(['../module'], function (controllers) {
  'use strict';
  controllers.controller("TaskResultCtrl", ['$scope', '$http', '$location', '$timeout', '$route', '$rootScope', '$routeParams', '$config',
    function ($scope, $http, $location, $timeout, $route, $rootScope, $routeParams, $config) {
      $scope.taskResults = [];

      var taskId = $routeParams.id;
      queryTaskResult(taskId);

      /**
       * 文件解析
       */
      function queryTaskResult(taskId, page) {
        $http({
          method: 'GET',
          url: $config.base + '/api/v1/task/result/' + taskId,
          //data: JSON.stringify(page)
          //}).success(function(response) {
        }).then(function successCallback(response) {
          var result = response.data;
          //var html='<tr class="no-records-found"><td colspan="4">没有找到匹配的记录</td></tr>';

          $scope.taskResults = result;
          //$scope.$apply();
          //}).error(function(data, header, config, status) {
        }, function errorCallback(response) {
          layer.alert('查询后台数据异常,请刷新重试', {
            skin: 'layui-layer-lan', //样式类名
            closeBtn: 0
          });
        });
      }

      $scope.goBack = function () {
        history.go(-1);
      };


      $scope.exportExcel = function () {
        window.location.href = $config.base + "/api/v1/task/result/excel/" + taskId;
      };

    }]);


});