define([ '../module' ], function(controllers) {
	'use strict';
	controllers.controller("AddVersionCtrl", [ '$scope', '$http', '$location', '$route', '$rootScope', '$uibModal', '$routeParams', '$config',
		function($scope, $http, $location, $route, $rootScope, $uibModal, $routeParams, $config) {
			var paramId = $routeParams.id;
			$scope.titleName = "新增";
			pageInit();

			function pageInit() {
				/**
				 * 查询业务类型
				 */
				queryBusinessTypeList();
				/**
				 * 查询所有的库类型
				 */
				queryBaseTypeLists();

				if (paramId === undefined) {
					$scope.titleName = "新增";
					return;
				}
				$scope.titleName = "编辑";
				queryProjectVersion(paramId)

			}
			/**
			 * 查询所有的业务类型
			 */
			function queryBusinessTypeList() {
				$http({
					method : 'GET',
					url : $config.base + '/api/v1/relation/find/allBusiness',
				// data:{"queryId":$routeParams.id}  
				//}).success(function(response) {
				}).then(function successCallback(response) {

					console.log(response);
					$scope.selectBusinessTypeValue = "";
					$scope.businessTypeLists = response.data;

				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('查询后台数据异常,请刷新重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}

			/**
			 * 查询所有的库类型
			 */
			function queryBaseTypeLists() {
				$http({
					method : 'GET',
					url : $config.base + '/api/v1/relation//find/allBaseType',
				// data:{"queryId":$routeParams.id}  
				//}).success(function(response) {
				}).then(function successCallback(response) {

					console.log(response);
					$scope.selectBasetypeValue = "";
					$scope.baseTypeLists = response.data;

				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('查询后台数据异常,请刷新重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}
			/**
			 * 查询编辑的项目版本
			 */

			function queryProjectVersion(paramId) {
				$http({
					method : 'get',
					url : $config.base + '/api/v1/task/find/' + paramId
				//}).success(function(response) {
				}).then(function successCallback(response) {
					console.log(response);
					$scope.projectName = response.data.name;
					$scope.selectBusinessTypeValue = response.data.business;
					$scope.selectBasetypeValue = response.data.baseType;

				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('查询后台数据异常,请刷新重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}

			/**
			 * 重置
			 */

			$scope.resetValue = function() {
				layer.open({
					content : '确定取消吗？',
					btn : [ '确定', '取消' ],
					yes : function(index, layero) {
						$scope.projectName = "";
						$scope.selectBusinessTypeValue = "";
						$scope.selectBasetypeValue = "";
						$scope.$apply();
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
			 * 提交文件
			 */
			$scope.addVersionFromSubmit = function(addVersionFrom) {
				if (paramId === undefined) {
					$scope.titleName = "新增";
					addVersion();
				} else {
					$scope.titleName = "编辑";
					editorVersion();
				}



			}

			function addVersion() {
				layer.open({
					content : '确定提交吗？',
					btn : [ '确定', '取消' ],
					yes : function(index, layero) {
						var task = {
							"name" : $scope.projectName,
							"baseType" : $scope.selectBasetypeValue,
							"business" : $scope.selectBusinessTypeValue
						};
						$http({
							method : 'post',
							url : $config.base + '/api/v1/task/add',
							//headers : { 'Content-Type': 'application/x-www-form-urlencoded' },
							data : JSON.stringify(task)
						//}).success(function(response) {
						}).then(function successCallback(response) {
							layer.msg("提交成功");
							$location.path("/versionMaintain");
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

			function editorVersion() {
				layer.open({
					content : '确定提交吗？',
					btn : [ '确定', '取消' ],
					yes : function(index, layero) {
						var task = {
							"id" : paramId,
							"name" : $scope.projectName,
							"baseType" : $scope.selectBasetypeValue,
							"business" : $scope.selectBusinessTypeValue
						};
						$http({
							method : 'post',
							url : $config.base + '/api/v1/task/edit',
							//headers : { 'Content-Type': 'application/x-www-form-urlencoded' },
							data : JSON.stringify(task)
						//}).success(function(response) {
						}).then(function successCallback(response) {
							layer.msg("提交成功");
							$location.path("/versionMaintain");
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


		} ]);
});