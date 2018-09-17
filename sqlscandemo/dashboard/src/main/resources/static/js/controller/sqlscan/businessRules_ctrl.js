define([ '../module', ], function(controllers) {
	'use strict';
	controllers.controller("BusinessRulesCtrl", [ '$scope', '$http', '$routeParams', '$location', '$route', '$rootScope', '$uibModal', '$ocLazyLoad', '$config',
		function($scope, $http, $routeParams, $location, $route, $rootScope, $uibModal, $ocLazyLoad, $config) {
			console.log("BusinessRulesCtrl");
			// var businessValue = $routeParams.businessValue;
			// var typeValue = $routeParams.typeValue;
			// $scope.selectBusinessValue = businessValue;
			// $scope.selectTypeValue = typeValue;

			var levelList = [];
			var businessList = [];
			var businessName = null;
			getAllLevel();
			function getAllLevel() {
				$http({
					method : 'get',
					url : $config.base + '/api/v1/relation/find/allLevel'
				//}).success(function(response) {
				}).then(function successCallback(response) {
					console.log(response);
					levelList = response.data;
				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('查询后台数据异常,请刷新重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}

			$(document).ready(function() {
				queryAllBussiness();
				queryAllOperationType();
			});


			$scope.changeSelect = function() {
				console.log($scope.selectBusinessValue);
				console.log($scope.selectTypeValue);
				if ($scope.selectTypeValue != undefined && $scope.selectBusinessValue != undefined) {
					$("#ruleConfig").show();
					// querySelectRule();
					queryNoSelectRule();
				} else {
					$("#ruleConfig").hide();
				}
			};

			/**
			 * 提交
			 * getData--得到所有数据
			 */
			$scope.businessRulesFromSubmit = function() {
				var selectRows = $('#NoSelectedRuleLists').bootstrapTable('getSelections');
				if (selectRows.length == 0) {
					layer.alert('至少提交一条规则!', {
						icon : 0,
						skin : 'layer-ext-moon' //该皮肤由layer.seaning.com友情扩展。关于皮肤的扩展规则，去这里查阅
					});
					return;
				}
				var ruleList = [];
				for (var i = 0; i < selectRows.length; i++) {
					var dataList = {
						"ruleId" : selectRows[i].id,
						"levelId" : selectRows[i].level.id
					}
					ruleList.push(dataList);
				}
				var temp = {
					businessId : $scope.selectBusinessValue,
					operName : $scope.selectTypeValue,
					"ruleRelationLevels" : ruleList
				};
				$http({
					method : 'POST',
					url : $config.base + '/api/v1/rule/config',
					data : JSON.stringify(temp)
				//}).success(function(response) {
				}).then(function successCallback(response) {
					layer.msg('提交成功');
					getBusinessName();
					$location.path('/tempBusiness').search({
						businessName : businessName,
						operName : $scope.selectTypeValue
					});
				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('提交异常,请重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}
			/**
			 * 重置
			 */
			$scope.resetValue = function() {
				$scope.selectBusinessValue = undefined;
				$scope.selectTypeValue = undefined;
				$scope.changeSelect();
			}
			/**
			 * 业务
			 */
			function queryAllBussiness() {
				$http({
					method : 'GET',
					url : $config.base + '/api/v1/relation/find/allBusiness',
				//data:{"queryId":$routeParams.id}  
				//}).success(function(response) {
				}).then(function successCallback(response) {
					console.log(response);
					$scope.businessLists = response.data;
					businessList = response.data;
				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('查询后台数据异常,请刷新重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}
			/**
			 * 操作类型
			 */
			function queryAllOperationType() {
				$http({
					method : 'get',
					url : $config.base + '/api/v1/relation/find/opers',
				//data:{"queryId":$routeParams.id}  
				//}).success(function(response) {
				}).then(function successCallback(response) {
					console.log(response);
					$scope.typeLists = response.data;
				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('查询后台数据异常,请刷新重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}

			function queryNoSelectRule() {
				$("#NoSelectedRuleLists").bootstrapTable('destroy');
				$('#NoSelectedRuleLists').bootstrapTable({
					url : $config.base + "/api/v1/rule/find/unused",
					//url:"/queryAllassociateRule",
					method : 'POST', //请求方式（*）
					//toolbar: '#toolbar',              //工具按钮用哪个容器
					striped : true, //是否显示行间隔色
					cache : false, //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
					pagination : false, //是否显示分页（*）
					sortable : true, //是否启用排序
					// sortOrder : "asc", //排序方式
					// sidePagination : "server", //分页方式：client客户端分页，server服务端分页（*）
					// pageNumber : 1, //初始化加载第一页，默认第一页,并记录
					// pageSize : 10, //每页的记录行数（*）
					// pageList : [ 10, 25, 50, 100 ], //可供选择的每页的行数（*）
					search : true, //是否显示表格搜索
					height : 450,
					strictSearch : false,
					showColumns : true, //是否显示所有的列（选择显示的列）
					showRefresh : false, //是否显示刷新按钮
					minimumCountColumns : 1, //最少允许的列数
					clickToSelect : false, //是否启用点击选中行
					//height : 600, //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
					//uniqueId : "id", //每一行的唯一标识，一般为主键列
					showToggle : false, //是否显示详细视图和列表视图的切换按钮
					cardView : false, //是否显示详细视图
					detailView : false, //是否显示父子表
					contentType : "application/json; charset=UTF-8",
					//onClickRow : selectedNoSelectRow,
					/*onCheck:onCheckRight,
						    onUncheck:onUncheckRight,
						    onCheckAll:onCheckAllRight,
						    onUncheckAll:onUncheckAllRight,*/
					//得到查询的参数
					queryParams : function(params) {
						var business = {
							id : $scope.selectBusinessValue
						};
						var operType = {
							name : $scope.selectTypeValue
						};
						var temp = {
							business : business,
							operType : operType
						};
						return JSON.stringify(temp);
					},
					onLoadSuccess : function(response) { //加载成功时执行
						//layer.msg("加载成功");

					},
					onLoadError : function() { //加载失败时执行  
						layer.msg("加载数据失败", {
							time : 1500,
							icon : 2
						});
					},
					columns : [ {
						field : '选择',
						checkbox : true,
						title : "选择"
					}, {
						field : 'id',
						title : '序号',
						formatter : function(value, row, index) {
							return index + 1;
						}
					}, {
						field : 'name',
						class : 'cellName',
						// formatter:function (value, row, index) {
						//     var html ='<div title="'+row.description+'">'+value+'<div>';
						//     return html;
						// },
						title : '未选择规则'
					}, {
						field : 'description',
						class : 'cellDescription',
						formatter : function(value, row, index) {
							var html = '<div title="' + value + '">' + value + '<div>';
							return html;
						},
						title : '描述'
					}, {
						field : 'level',
						class : 'cellLevel',
						events : selectLevel(),
						title : '级别',
						formatter : function(value, row, index) { //value是这个字段的值
							var start = '<select class="test">';
							var options = '';
							for (var i = 0; i < levelList.length; i++) {
								options += '<option value="' + levelList[i].id + '">' + levelList[i].level + '</option>'
							}
							var end = '</select>';
							var html = start + options + end;
							//$(html).val(value.level);
							var selectHTML = $(html).find('option[value=' + row.level.id + ']').attr('selected', true).parent("select").prop("outerHTML");
							return selectHTML;
						}
					} ]
				});
			}

			function selectLevel() {
				return {
					'change .test' : function(e, value, row, index) {
						row.level.id = $(e.currentTarget).val();
					}
				}
			}

			function getBusinessName() {
				for (var i = 0; i < businessList.length; i++) {
					if ($scope.selectBusinessValue === businessList[i].id) {
						businessName = businessList[i].name;
						break;
					}
				}
			}

			/**
			 * 新增规则 right->left
			 */

			// $scope.addBusinessRules=function(){
			// 	var idsRightList=[];
			// 	var dataRightList=[];
			// 	var rightRows= $('#NoSelectedRuleLists').bootstrapTable('getSelections');
			// 	 if(rightRows.length==0){
			// 		 layer.alert('请至少选中一条数据!', {
			// 			  icon: 0,
			// 			  skin: 'layer-ext-moon' //该皮肤由layer.seaning.com友情扩展。关于皮肤的扩展规则，去这里查阅
			// 		});
			// 	 }
			// 	for(var i=0;i<rightRows.length;i++){
			// 		var dataRight={"id":rightRows[i].id,"name":rightRows[i].name,"level":{"id":"3","level":"INFO"}};
			// 		idsRightList.push(rightRows[i].id);
			// 		dataRightList.push(dataRight);
			// 	}
			// 	console.log(idsRightList);
			//
			//
			// 	$('#NoSelectedRuleLists').bootstrapTable('remove', {
			// 		field : 'id',
			// 		values : idsRightList
			// 	});
			// 	 $('#selectedRuleLists').bootstrapTable('append',dataRightList);
			// }





			/**
			 * 删除规则 left-->right
			 */
			// $scope.removeBusinessRules=function(){
			// 	var idsleftList=[];
			// 	var dataleftList=[];
			// 	 var leftRows= $('#selectedRuleLists').bootstrapTable('getSelections');
			// 	 if(leftRows.length==0){
			// 		 layer.alert('请至少选中一条数据!', {
			// 			  icon: 0,
			// 			  skin: 'layer-ext-moon' //该皮肤由layer.seaning.com友情扩展。关于皮肤的扩展规则，去这里查阅
			// 		});
			// 	 }
			// 	 for(var i=0;i<leftRows.length;i++){
			// 		 var dataleft={"id":leftRows[i].id,"name":leftRows[i].name}
			// 		 idsleftList.push(leftRows[i].id);
			// 		 dataleftList.push(dataleft);
			// 	}
			// 	console.log(dataleftList);
			//
			// 	$('#selectedRuleLists').bootstrapTable('remove', {
			// 		field : 'id',
			// 		values : idsleftList
			// 	});
			// 	 $('#NoSelectedRuleLists').bootstrapTable('append',dataleftList);
			//
			// }


			/**
			 * 左侧
			 */
			/*var idsleftList=[];
			var dataleftList=[];
			 
			function onCheckLeft(row){
				var dataleft={"id":row.id,"name":row.name}
				idsleftList.push(row.id);
				dataleftList.push(dataleft);
				
				console.log(idsleftList);
				console.log(dataleftList);
			}
			
			//取消每一个单选框时对应的操作；
			function onUncheckLeft(row){
				var dataleft={"id":row.id,"name":row.name}
				idsleftList.remove(row.id);
				deleteJson(dataleftList,"id",row.id);
				
				
				console.log(idsleftList);
				console.log(dataleftList);    
			}
			
			function onCheckAllLeft(rows){
				for(var i=0;i<rows.length;i++){
					onCheckLeft(rows[i]);
				}
				
				console.log(idsleftList);
				console.log(dataleftList);
			}
			
			function onUncheckAllLeft(rows){
				for(var i=0;i<rows.length;i++){
					onUncheckLeft(rows[i]);
				}
				
				console.log(idsleftList);
				console.log(dataleftList);
			}
			
			*/ /**
		 * 右侧
		 * 
		 */ /*
		var idsRightList=[];
		var dataRightList=[];
		function onCheckRight(row){
			var dataRight={"id":row.id,"name":row.name}
			idsRightList.push(row.id);
			dataRightList.push(dataRight);
			
			console.log(idsRightList);
			console.log(dataRightList);
		}
		
		//取消每一个单选框时对应的操作；
		function onUncheckRight(row){
			var dataRight={"id":row.id,"name":row.name}
			idsRightList.remove(row.id);
			dataRightList.remove(dataRight);
			
			console.log(idsRightList);
			console.log(dataRightList);   
		}
		
		function onCheckAllRight(rows){
			for(var i=0;i<rows.length;i++){
				onCheckRight(rows[i]);
			}
			
			console.log(idsRightList);
			console.log(dataRightList);  
		}
		
		function onUncheckAllRight(rows){
			for(var i=0;i<rows.length;i++){
				onUncheckRight(rows[i]);
			}
			
			console.log(idsRightList);
			console.log(dataRightList);  
		}*/

			// function deleteJson(array,field,value){
			// 	var result=new Array();
			//
			// 	for(var i=0;i<array.length;i++){
			// 		if(array[i][field] != value){
			// 			result.push(array[i])
			// 		}
			// 	}
			//
			// 	array=result;
			// }


			/*var selectedId=[];
			var selectData=[];*/
			/**
			 * 选中未选中的行
			 */
			/*function selectedNoSelectRow(row,$element,field){
				var flag=$($element).find(".addOk").is(':hidden');
				var data={"id":row.id,"name":row.name}
				if(flag){
					$($element).find(".addOk").show();
					selectedId.push(row.id);
					selectData.push(data)
					//selectassociateVale.push(rule.id);
				}else{
					$($element).find(".addOk").hide();
					//$scope.ruleList.splice(ruleList.indexOf(rule),1);
					//selectassociateVale.remove(rule.id);
					selectedId.remove(row.id);
					selectData.remove(data)
					
				}
			}*/


			/**
			 * 选中选中的行
			 */ /*
		function selectedSelectRow(row,$element,field){
			var flag=$($element).find(".addRemove").is(':hidden');
			var data={"id":row.id,"name":row.name}
			if(flag){
				$($element).find(".addRemove").show();
				selectedId.remove(row.id);
				selectData.remove(data)
			}else{
				$($element).find(".addRemove").hide();
				selectedId.push(row.id);
				selectData.push(data)
			}
		}*/

		} ]);
});