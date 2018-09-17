define([ '../module', ], function(controllers) {
	'use strict';
	controllers.controller("ConfigRuleAndBlockCtrl", [ '$scope', '$http', '$location', '$route', '$rootScope', '$uibModal', '$ocLazyLoad', '$config',
		function($scope, $http, $location, $route, $rootScope, $uibModal, $ocLazyLoad, $config) {

			console.log("ConfigRuleAndBlockCtrl");

			$scope.ruleList = [];
			$scope.selectassociateVale = []; //被选中元素的id
			queryBlock();
			queryAllassociateRule();

			/**
			 * 查询所有的块
			 */
			function queryBlock() {
				$http({
					method : 'post',
					url : $config.base + '/configRuleAndBlock',
					data : {}
				//}).success(function(response) {
				}).then(function successCallback(response) {
					console.log(response);
					$scope.blockLists = response.data;
				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('查询后台数据异常,请刷新重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}
			/**
			 * 查询所有的股则
			 */
			function queryAllassociateRule() {
				$http({
					method : 'post',
					url : $config.base + '/queryAllassociateRule',
					data : {}
				//}).success(function(response) {
				}).then(function successCallback(response) {
					console.log(response);
					$scope.ruleList = response.data;
				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('查询后台数据异常,请刷新重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}

			/**
			 * 选中项目操作
			 */
			$scope.selectRule = function($event, rule) {
				var flag = $($event.target).find(".selectFlag").length;
				if (flag == 0) {
					$($event.target).append('<span class="selectFlag glyphicon glyphicon-ok disable"></span>');
					$scope.selectassociateVale.push(rule.id);
				} else {
					$($event.target).find(".selectFlag").remove();
					//$scope.ruleList.splice(ruleList.indexOf(rule),1);
					$scope.selectassociateVale.remove(rule.id);

				}
			}

			$scope.configRuleAndBlockSubmit = function(configRuleAndBlockFrom) {
				if ($scope.selectassociateVale == 0) {
					return;
				}
				layer.open({
					content : '确定提交吗？',
					btn : [ '确定', '取消' ],
					yes : function(index, layero) {
						var ruleAndBlock = {
							"block" : $scope.selectBlockValue,
							"associated" : $scope.selectassociateVale
						};
						$http({
							method : 'post',
							url : $config.base + '/addRuleAndBlock',
							//headers : { 'Content-Type': 'application/x-www-form-urlencoded' },
							data : JSON.stringify(ruleAndBlock)
						//}).success(function(response) {
						}).then(function successCallback(response) {
							layer.msg("提交成功");
							console.log(response);
						//}).error(function(data, header, config, status) {
						}, function errorCallback(response) {
							layer.alert('提交失败,请刷新重试', {
								skin : 'layui-layer-lan', //样式类名
								closeBtn : 0
							});
						});
						layer.close(index); //如果设定了yes回调，需进行手工关闭
					},
					btn2 : function(index, layero) {
						//return false 开启该代码可禁止点击该按钮关闭
					},
					cancel : function() {
						//右上角关闭回调
					}
				});
			}


			/**
			 * 重置
			 */
			$scope.resetValue1 = function() {
				$('select').prop('selectedIndex', 0);
				$scope.selectBlockValue = "";
				$scope.selectassociateVale = [];
				$("#ruleList").find(".selectFlag").remove();
			}
		} ]);
});