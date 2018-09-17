define([ '../module' ], function(controllers) {
	'use strict';
	controllers.controller("AddTableInfoCtrl", [ '$scope', '$http', '$location', '$timeout', '$route', '$rootScope', '$routeParams', '$config', '$filter', function($scope, $http, $location, $timeout, $route, $rootScope, $routeParams, $config, $filter) {

		$scope.businessList = [];
		queryBussiness();


		function queryBussiness() {
			$.ajax({
				type : 'GET',
				async : false,
				url : $config.uri.queryAllBusiness,
				dataType : 'json',
				success : function(response) {
					if (response.total >= 0) {
						$scope.businessList = response.rows;
					} else {
						layer.alert('查询业务失败!', {
							skin : 'layui-layer-lan', //样式类名
							closeBtn : 0
						});
					}
				},
				error : function(XMLHttpRequest, msg, erro) {
					layer.alert('查询业务请求发生异常,请刷新重试!', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				}
			});

		/*       $http({
		           method:'GET',
		           url:$config.uri.queryAllBusiness,
		       }).success(function(response){
		       	if(response.result=="success"){
		       		$scope.serviceNameList=response.data;
		           }else{
		           	layer.alert('查询业务失败!', {
		                   skin: 'layui-layer-lan' ,//样式类名
		                   closeBtn: 0
		               });
		           }
		       }).error(function(data,header,config,status){
		           layer.alert('查询业务请求发生异常,请刷新重试!', {
		               skin: 'layui-layer-lan' ,//样式类名
		               closeBtn: 0
		           });
		       });	*/
		}



		$scope.addTableSubmit = function(from) {
			if ($routeParams.id === undefined) {
				layer.open({
					content : '请确定新增表信息配置吗？',
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
					content : '请确定修改表信息配置？',
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

		$scope.resetTableInfo = function() {
			$scope.tableName = "";
			$scope.serviceName = "";
			$scope.filePath = "";
			$scope.delimeter = "";
			$scope.colNumber = "";
			$scope.rowNumber = "";
		}

		var operation = {
			add : function() {
				var tableInfo = {
					"tableName" : $scope.tableName,
					"serviceName" : $scope.selectBusiness,
					"filePath" : $scope.filePath,
					"delimeter" : $scope.delimeter,
					"colNumber" : $scope.colNumber,
					"rowNumber" : $scope.rowNumber
				};
				$http({
					method : 'POST',
					url : $config.uri.addTableInfo,
					data : JSON.stringify(tableInfo)
				}).success(function(response) {
					if (response.result = "success") {
						$location.path("/configureTableInfo");
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
				var tableInfo = {
					"id" : $routeParams.id,
					"tableName" : $scope.tableName,
					"serviceName" : $scope.selectBusiness,
					"filePath" : $scope.filePath,
					"delimeter" : $scope.delimeter,
					"colNumber" : $scope.colNumber,
					"rowNumber" : $scope.rowNumber
				};
				$http({
					method : 'POST',
					url : $config.uri.updateTableInfo,
					data : JSON.stringify(tableInfo)
				}).success(function(response) {
					if (response.result == "success") {
						$location.path("/configureTableInfo");
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
					url : $config.uri.queryTableInfoById + "?id=" + id,
				}).success(function(response) {
					console.log(response);
					if (response.result == "success") {
						$scope.tableName = response.data.tableName;
						$scope.filePath = response.data.filePath;
						$scope.delimeter = response.data.delimeter;
						$scope.colNumber = response.data.colNumber;
						$scope.rowNumber = response.data.rowNumber;
						$scope.selectBusiness = response.data.serviceName;
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