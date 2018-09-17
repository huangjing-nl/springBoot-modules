define(['../module'], function (controllers) {
  'use strict';
  controllers.controller("AddSQLRuleCtrl", ['$scope', '$http', '$location', '$route', '$rootScope', '$uibModal', '$routeParams', '$config',
    function ($scope, $http, $location, $route, $rootScope, $uibModal, $routeParams, $config) {

      $scope.titleName = "新增";
      $scope.levelLists = [];
      $scope.typeLists = [];
      pageInit();

      function pageInit() {
        queryRuleCheckLevelData();
        queryTypeData();
        var paramID = $routeParams.id;
        if (paramID === undefined) {
          $scope.titleName = "新增";
          $scope.ruleDescribe = "";
          $scope.ruleCondition = "";
          $scope.setSelect = "0";
          return;
        }
        $scope.titleName = "编辑";
        $http({
          method: 'get',
          url: $config.base + '/api/v1/rule/find/' + paramID
          //}).success(function(response) {
        }).then(function successCallback(response) {
          console.log(response);
          $scope.ruleName = response.data.name;
          $scope.ruleCategory = response.data.category;
          $scope.ruleDescribe = response.data.description;
          $scope.ruleType = response.data.typeRegexp;
          $scope.ruleCondition = response.data.conditionRegexp;
          $scope.selectLevelValue = response.data.defaultLevel;
          $scope.selectTypeValue = response.data.baseType;
          $scope.groupByCondition = response.data.ruleGroup;
          $scope.methodName = response.data.method;
          if ($scope.methodName != null && $scope.methodName != "") {
            $scope.setSelect = "1";
          } else {
            $scope.setSelect = "0";
          }
          //}).error(function(data, header, config, status) {
        }, function errorCallback(response) {
          layer.alert('查询后台数据异常,请刷新重试', {
            skin: 'layui-layer-lan', //样式类名
            closeBtn: 0
          });
        });

      }

      /**
       * 查询库类型
       */
      function queryTypeData() {
        $http({
          method: 'get',
          url: $config.base + '/api/v1/relation/find/allBaseType'
          //data:{"queryId":$routeParams.id}
          //}).success(function(response) {
        }).then(function successCallback(response) {
          console.log(response);
          $scope.selectTypeValue = "";
          $scope.typeLists = response.data;
          //}).error(function(data, header, config, status) {
        }, function errorCallback(response) {
          layer.alert('查询后台数据异常,请刷新重试', {
            skin: 'layui-layer-lan', //样式类名
            closeBtn: 0
          });
        });
      }

      /**
       * 查询规则校验级别数据
       */
      function queryRuleCheckLevelData() {
        $http({
          method: 'get',
          url: $config.base + '/api/v1/relation/find/allLevel',
          //data:{"queryId":$routeParams.id}
          //}).success(function(response) {
        }).then(function successCallback(response) {
          $scope.selectLevelValue = "";
          $scope.levelLists = response.data;
          //}).error(function(data, header, config, status) {
        }, function errorCallback(response) {
          layer.alert('查询后台数据异常,请刷新重试', {
            skin: 'layui-layer-lan', //样式类名
            closeBtn: 0
          });
        });
      }

      /**
       * 提交表单
       */
      $scope.addRuleSubmit = function (addRuleFrom) {
        if ($routeParams.id === undefined) {
          $scope.titleName = "新增";
          addRule();
        } else {
          $scope.titleName = "编辑";
          editorRule();
        }

      };

      function addRule() {
        layer.open({
          content: '请确定新增rule到后台？',
          btn: ['确定', '取消'],
          yes: function (index, layero) {
            var rule = {
              "name": $scope.ruleName,
              "category": $scope.ruleCategory,
              "description": $scope.ruleDescribe,
              "typeRegexp": $scope.ruleType,
              "conditionRegexp": $scope.ruleCondition,
              "defaultLevel": $scope.selectLevelValue,
              "baseType": $scope.selectTypeValue,
              "method": $scope.methodName,
              "ruleGroup": $scope.groupByCondition,
              "ruleSwitch": 1
            };
            $http({
              method: 'post',
              url: $config.base + '/api/v1/rule/create',
              //headers : { 'Content-Type': 'application/x-www-form-urlencoded' },
              data: JSON.stringify(rule)
              //}).success(function(response) {
            }).then(function successCallback(response) {
              layer.msg("新增成功");
              $location.path("/SQLRule");
              console.log(response);
              //}).error(function(data, header, config, status) {
            }, function errorCallback(response) {
              layer.alert('查询后台数据异常,请刷新重试', {
                skin: 'layui-layer-lan', //样式类名
                closeBtn: 0
              });
            });
            layer.close(index); //如果设定了yes回调，需进行手工关闭

          },
          btn2: function (index, layero) {
            //return false 开启该代码可禁止点击该按钮关闭
          },
          cancel: function () {
            //右上角关闭回调
          }
        });
      }

      function editorRule() {
        layer.open({
          content: '请确定修改rule到后台？',
          btn: ['确定', '取消'],
          yes: function (index, layero) {
            var rule = {
              "id": $routeParams.id,
              "name": $scope.ruleName,
              "category": $scope.ruleCategory,
              "description": $scope.ruleDescribe,
              "typeRegexp": $scope.ruleType,
              "conditionRegexp": $scope.ruleCondition,
              "defaultLevel": $scope.selectLevelValue,
              "baseType": $scope.selectTypeValue,
              "method": $scope.methodName,
              "ruleGroup": $scope.groupByCondition,
              "ruleSwitch": 1
            };
            $http({
              method: 'post',
              url: $config.base + '/api/v1/rule/update',
              //headers : { 'Content-Type': 'application/x-www-form-urlencoded' },
              data: JSON.stringify(rule)
              //}).success(function(response) {
            }).then(function successCallback(response) {
              layer.msg("修改成功");
              $location.path("/SQLRule");
              console.log(response);
              //}).error(function(data, header, config, status) {
            }, function errorCallback(response) {
              layer.alert('查询后台数据异常,请刷新重试', {
                skin: 'layui-layer-lan', //样式类名
                closeBtn: 0
              });
            });
            layer.close(index); //如果设定了yes回调，需进行手工关闭
          },
          btn2: function (index, layero) {
            //return false 开启该代码可禁止点击该按钮关闭
          },
          cancel: function () {
            //右上角关闭回调
          }
        });
      }

      /**
       * 重置
       */
      $scope.resetValue = function () {
        $scope.ruleName = "";
        $scope.ruleCategory = "";
        $scope.ruleDescribe = "";
        $scope.ruleType = "";
        $scope.ruleCondition = "";
        $scope.selectLevelValue = "";
        $scope.selectTypeValue = "";

      };


      $scope.goBack = function () {
        history.go(-1);
      }
    }]);
});