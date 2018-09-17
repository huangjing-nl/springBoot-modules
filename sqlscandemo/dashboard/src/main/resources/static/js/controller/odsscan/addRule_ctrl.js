define([ '../module' ], function(controllers) {
	'use strict';
	controllers.controller("AddRuleCtrl", [ '$scope', '$http', '$location', '$timeout', '$route', '$rootScope', '$routeParams', '$config', '$filter', function($scope, $http, $location, $timeout, $route, $rootScope, $routeParams, $config, $filter) {


		$scope.addRuleSubmit = function(from) {
			if ($routeParams.id === undefined) {
				layer.open({
					content : '请确定新增rule到后台？',
					btn : [ '确定', '取消' ],
					yes : function(index, layero) {
						operation.add();
						layer.close(index);
					},
					btn2 : function(index, layero) {
						//return false 开启该代码可禁止点击该按钮关闭
					},
					cancel : function() {
						//右上角关闭回调
					}
				});
			} else {
				layer.open({
					content : '请确定修改rule？',
					btn : [ '确定', '取消' ],
					yes : function(index, layero) {
						operation.update();
						layer.close(index);
					},
					btn2 : function(index, layero) {
						//return false 开启该代码可禁止点击该按钮关闭
					},
					cancel : function() {
						//右上角关闭回调
					}
				});
			}
		};

		$scope.resetValue = function() {
			$scope.ruleName = "";
			$scope.ruleRegexp = "";
			$scope.ruleRemark = "";
		}
		var operation = {
			add : function() {
				var rule = {
					"ruleName" : $scope.ruleName,
					"ruleRegexp" : $scope.ruleRegexp,
					"ruleRemark" : $scope.ruleRemark
				};
				$http({
					method : 'POST',
					url : $config.uri.addRuleConfig,
					data : JSON.stringify(rule)
				}).success(function(response) {
					if (response > 0) {
						$location.path("/configureRule");
					} else {
						layer.alert('新增失败!', {
							skin : 'layui-layer-lan', //样式类名
							closeBtn : 0
						});
					}
				}).error(function(data, header, config, status) {
					layer.alert('新增请求发生异常,请刷新重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			},
			update : function() {
				var rule = {
					"id" : $routeParams.id,
					"ruleName" : $scope.ruleName,
					"ruleRegexp" : $scope.ruleRegexp,
					"ruleRemark" : $scope.ruleRemark
				};
				$http({
					method : 'POST',
					url : $config.uri.updateRuleConfig,
					data : JSON.stringify(rule)
				}).success(function(response) {
					if (response > 0) {
						$location.path("/configureRule");
					} else {
						layer.alert('修改失败!', {
							skin : 'layui-layer-lan', //样式类名
							closeBtn : 0
						});
					}
				}).error(function(data, header, config, status) {
					layer.alert('修改请求发生异常,请刷新重试!', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			},
			queryById : function(id) {
				$http({
					method : 'GET',
					url : $config.uri.queryRuleConfigById + "?id=" + id,
				}).success(function(response) {
					console.log(response);
					if (response.result == "success") {
						$scope.ruleName = response.data.ruleName;
						$scope.ruleRegexp = response.data.ruleRegexp;
						$scope.ruleRemark = response.data.ruleRemark;
					} else {
						layer.alert('查询失败!', {
							skin : 'layui-layer-lan', //样式类名
							closeBtn : 0
						});
					}
				}).error(function(data, header, config, status) {
					layer.alert('查询请求发生异常,请刷新重试!', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}
		};


		$scope.titleName = "新增";
		if ($routeParams.id === undefined) {
		} else {
			$scope.titleName = "编辑";
			operation.queryById($routeParams.id);
		}

	} ]);
});