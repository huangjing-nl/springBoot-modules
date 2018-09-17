define([ '../module' ], function(controllers) {
	'use strict';
	controllers.controller("AddFieldCtrl", [ '$scope', '$http', '$location', '$timeout', '$route', '$rootScope', '$routeParams', '$config', '$filter', function($scope, $http, $location, $timeout, $route, $rootScope, $routeParams, $config, $filter) {
		$scope.ruleList = [];
		queryAllRuleName();

		function queryAllRuleName() {
			$http({
				method : 'GET',
				url : $config.uri.queryAllRuleName,
			}).success(function(response) {
				if (response.total >= 0) {
					$scope.ruleList = response.rows;
				} else {
					layer.alert('查询正则表达式失败!', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				}
			}).error(function(data, header, config, status) {
				layer.alert('查询正则表达式请求发生异常,请刷新重试!', {
					skin : 'layui-layer-lan', //样式类名
					closeBtn : 0
				});
			});
		}

		$scope.addFieldInfoSubmit = function(from) {
			if ($routeParams.id === undefined) {
				layer.open({
					content : '请确定新增字段信息配置吗？',
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
					content : '请确定修改字段信息配置？',
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

		$scope.resetField = function() {
			$scope.tableName = "";
			$scope.ruleId = "";
			$scope.colName = "";
			$scope.colId = "";
		}

		var operation = {
			add : function() {
				var rule = {
					"tableName" : $scope.tableName,
					"colName" : $scope.colName,
					"colId" : $scope.colId,
					"ruleId" : $scope.selectRule
				};
				$http({
					method : 'POST',
					url : $config.uri.addFileInfo,
					data : JSON.stringify(rule)
				}).success(function(response) {
					if (response.result = "success") {
						$location.path("/configFieldInfo");
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
					"tableName" : $scope.tableName,
					"colName" : $scope.colName,
					"colId" : $scope.colId,
					"ruleId" : $scope.selectRule
				};
				$http({
					method : 'POST',
					url : $config.uri.updateFileInfo,
					data : JSON.stringify(rule)
				}).success(function(response) {
					if (response.result = "success") {
						$location.path("/configFieldInfo");
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
					url : $config.uri.queryFileInfoById + "?id=" + id,
				}).success(function(response) {
					console.log(response);
					if (response.result == "success") {
						$scope.tableName = response.data.tableName;
						$scope.colName = response.data.colName;
						$scope.colId = response.data.colId;
						$scope.selectRule = response.data.ruleId;
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
			return;
		} else {
			$scope.titleName = "编辑";
			operation.queryById($routeParams.id);
		}
	} ]);

});