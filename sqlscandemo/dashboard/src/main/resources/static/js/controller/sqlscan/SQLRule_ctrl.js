define([ '../module' ], function(controllers) {
	'use strict';
	controllers.controller("SQLRuleCtrl", [ '$scope', '$http', '$location', '$route', '$rootScope', '$uibModal', '$ocLazyLoad', '$compile', '$config',
		function($scope, $http, $location, $route, $rootScope, $uibModal, $ocLazyLoad, $compile, $config) {


            var ruleCurrentPage=1;
            if($rootScope.currentRulePage>=0){
                ruleCurrentPage=$rootScope.currentRulePage;
            }

			$(document).ready(function() {
				queryRuleconfigLists(ruleCurrentPage);
			});


			/**
			 * 查询ruleConfig的列表
			 */
			function queryRuleconfigLists(currentPageNumber) {
                $rootScope.currentRulePage=undefined;
                $("#SQLruleTable").bootstrapTable('destroy');
				$('#SQLruleTable').bootstrapTable({
					url : $config.base + "/api/v1/rule/find/page",
					method : 'POST', //请求方式（*）
					//toolbar: '#toolbar',              //工具按钮用哪个容器
					striped : true, //是否显示行间隔色
					cache : false, //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
					pagination : true, //是否显示分页（*）
					sortable : true, //是否启用排序
                    sortName : "id",
					sortOrder : "asc", //排序方式
					sidePagination : "server", //分页方式：client客户端分页，server服务端分页（*）
					pageNumber : currentPageNumber, //初始化加载第一页，默认第一页,并记录
					pageSize : 10, //每页的记录行数（*）
					pageList : [ 10, 25, 50, 100 ], //可供选择的每页的行数（*）
					search : true, //是否显示表格搜索
                    searchOnEnterKey:false,
                    searchText : "",
					strictSearch : true,
					showColumns : true, //是否显示所有的列（选择显示的列）
					showRefresh : true, //是否显示刷新按钮
					minimumCountColumns : 1, //最少允许的列数
					clickToSelect : true, //是否启用点击选中行
					height : 600, //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
					uniqueId : "id", //每一行的唯一标识，一般为主键列
					showToggle : false, //是否显示详细视图和列表视图的切换按钮
					cardView : false, //是否显示详细视图
					detailView : false, //是否显示父子表
					contentType : "application/x-www-form-urlencoded; charset=UTF-8",
					//contentType:"application/json; charset=UTF-8",
					//得到查询的参数
					queryParams : function(params) {
						//这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
						var temp = {
							rows : params.limit, //页面大小
							pageSize : (params.offset / params.limit) + 1, //页码
							sort : params.sort, //排序列名
							sortOrder : params.order,//排位命令（desc，asc）
                            search:params.search
						};
						return temp;
					},
					onLoadSuccess : function() { //加载成功时执行  
						//layer.msg("加载成功");  
					},
					onLoadError : function() { //加载失败时执行  
						layer.msg("加载数据失败", {
							time : 1500,
							icon : 2
						});
					},
					columns : [ {
						field : 'id',
						title : '序号',
						class : 'cellNumber',
						formatter : function(value, row, index) {
							return index + 1;
						}
					}, {
						field : 'name',
						class : 'cellName',
                        sortable : true,
						title : '名称'
					}, {
						field : 'category',
						class : 'cellCategory',
                        sortable : true,
						title : '分类'
					}, {
						field : 'typeRegexp',
						class : 'cellTypeRegexp',
						formatter : function(value, row, index) {
							var html = '<div title="' + value + '">' + value + '<div>';
							return html;
						},
						title : '类型规则'
					}, {
						field : 'conditionRegexp',
						class : 'cellConditionRegexp',
						title : '条件规则',
						formatter : function(value, row, index) {
							var html = '<div title="' + value + '">' + value + '<div>';
							return html;
						}
					},  {
            field : 'ruleGroup',
            class : '',
            /*  sortable : true,*/
            title : '分组规则'
          }, {
          field : 'method',
              class : '',
              /*  sortable : true,*/
              title : '方法名'
        },{
						field : 'defaultLevel',
						class : 'cellLevel',
                      /*  sortable : true,*/
						title : '校验级别'
					}, {
						field : 'baseType',
						class : 'cellBaseType',
                        sortable : true,
						title : '库类型'
					}, {
						field : 'description',
						class : 'cellDescription',
						formatter : function(value, row, index) {
							var html = '<div title="' + value + '">' + value + '<div>';
							return html;
						},
						title : '描述'
					}, {
						field : 'operation',
						title : '操作',
						class : 'cellOperation',
						events : getIdValue(),
						formatter : function(value, row, index) { //value是这个字段的值
							var html = '<ul class="operation">' +
								'<li class="edit"><a href="javascript:void(0);">编辑</a></li>' +
								'<li class="remove"><a href="javascript:void(0);">删除</a></li>' +
								'</ul>';
							return html;
						}
					} ]
				});
			}


			function getIdValue() {
				return {
					'click .edit':function(e, value, row, index){
                        $rootScope.currentRulePage=$('#SQLruleTable').bootstrapTable('getOptions').pageNumber;
                        $location.path("/SQLRule/addRule/"+ row.id);
                        $scope.$apply();
					},
					'click .remove' : function(e, value, row, index) {

						layer.open({
							title : "提示",
							content : '确定删除吗？',
							btn : [ '确定', '取消' ],
							yes : function(index, layero) {
								$http({
									method : 'get',
									url : $config.base + '/api/v1/rule/delete/' + row.id
								//}).success(function(response) {
								}).then(function successCallback(response) {
									layer.msg("删除成功");
                                    var pageNumber=$("#SQLruleTable").bootstrapTable('getOptions').pageNumber;
                                    queryRuleconfigLists(pageNumber);
								//}).error(function(data, header, config, status) {
								}, function errorCallback(response) {
									layer.alert('删除后台数据异常,请刷新重试', {
										skin : 'layui-layer-lan', //样式类名
										closeBtn : 0
									});
								});
							/*	$('#SQLruleTable').bootstrapTable('remove', {
									field : 'id',
									values : [ row.id ]
								});*/
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
				};
			}




		} ]);
});