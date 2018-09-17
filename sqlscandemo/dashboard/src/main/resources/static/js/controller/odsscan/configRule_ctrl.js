define([ '../module' ], function(controllers) {
	'use strict';
	controllers.controller("ConfigRuleCtrl", [ '$scope', '$http', '$location', '$timeout', '$route', '$rootScope', '$routeParams', '$config', '$filter', function($scope, $http, $location, $timeout, $route, $rootScope, $routeParams, $config, $filter) {
		$(document).ready(function() {
			queryTable(1);
		});

		function queryTable(pageNumber) {
			$("#configRuleTable").bootstrapTable('destroy');
			$("#configRuleTable").bootstrapTable({
				url : $config.uri.queryRuleConfig,
				method : 'POST', //请求方式（*）
				//toolbar : '#toolbar', //工具按钮用哪个容器
				striped : true, //是否显示行间隔色
				cache : false, //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
				pagination : true, //是否显示分页（*）
				sortable : true, //是否启用排序
                sortName : 'ruleName',
				sortOrder : "asc", //排序方式
				sidePagination : "server", //分页方式：client客户端分页，server服务端分页（*）
				pageNumber : pageNumber, //初始化加载第一页，默认第一页,并记录
				pageSize : 10, //每页的记录行数（*）
				pageList : [ 10, 25, 50, 100 ], //可供选择的每页的行数（*）
				search : true, //是否显示表格搜索
				searchOnEnterKey : false, // The search method will be executed until the Enter key is pressed.
				strictSearch : true, // Enable the strict search.
				searchText : '', // When set search property, initialize the search text.
				searchTimeOut : 100, // Set timeout for search fire.
				trimOnSearch : true,
				showColumns : true, //是否显示所有的列（选择显示的列）
				showRefresh : true, //是否显示刷新按钮
				minimumCountColumns : 1, //最少允许的列数
				clickToSelect : false, //是否启用点击选中行
				height : 600, //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
				//uniqueId : "id", //每一行的唯一标识，一般为主键列
				showToggle : false, //是否显示详细视图和列表视图的切换按钮
				cardView : false, //是否显示详细视图
				detailView : false, //是否显示父子表
				contentType : "application/json; charset=UTF-8",
				//得到查询的参数
				queryParams : function(params) {
					//这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
					var temp = {
						pageSize : params.limit,
						pageNo : (params.offset / params.limit) + 1,
						sortCol :  params.sort,
						sortOrder : params.order, //排位命令（desc,asc） 
						search : params.search
					};
					return JSON.stringify(temp);
				},
				onLoadSuccess : function(response) { //加载成功时执行
					//layer.msg("加载成功");
					console.log(response);

				},
				onLoadError : function() { //加载失败时执行  
					layer.msg("加载数据失败,请刷新重试!", {
						time : 1500,
						icon : 2
					});
				},
				columns : [ {
					field : 'id',
					title : '序号',
                    width : 50,
					formatter : function(value, row, index) {
						return index + 1;
					}
				}, {
					field : 'ruleName',
					class : 'cellRuleName',
					title : '规则名称'
				}, {
					field : 'ruleRegexp',
					title : '正则表达式',
					class : 'cellRegexp',
                    formatter : function(value, row, index) {
                        var html = '<div title="' + value + '">' + value + '<div>';
                        return html;
                    }
				}, {
					field : 'ruleRemark',
					class : 'remarks',
					formatter : function(value, row, index) {
						var html = '<div title="' + value + '">' + value + '<div>';
						return html;
					},
					title : '备注'
				}, {
					field : 'operation',
					title : '操作',
					width : 100,
					class : "operation",
					events : ruleOperation(),
					formatter : function(value, row, index) { //value是这个字段的值
						var html = '<ul><li><a href="#/configureRule/updateRule/' + row.id + '">编辑</a>' +
							'<li class="delected"><a href="javascript:void(0);">删除</a></li></ul>';
						//var $html = $compile(html)($scope);  
						return html;
					}
				} ]
			});
		}

		function ruleOperation() {
			return {
				'click .delected' : function(e, value, row, index) {
					layer.open({
						title : "提示",
						content : '确定删除吗？',
						btn : [ '确定', '取消' ],
						yes : function(index, layero) {
							deleted(row.id);
							$('#configRuleTable').bootstrapTable('remove', {
								field : 'id',
								values : [ row.id ]
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
			}
		}


		function deleted(ruleId) {
			$http({
				method : 'GET',
				url : $config.uri.deleteRuleConfig + "?id=" + ruleId,
			// data: JSON.stringify(param) 
			}).success(function(response) {
				if (response > 0) {
					layer.msg("删除成功");
					var pageNumber = $("#configRuleTable").bootstrapTable('getOptions').pageNumber;
					queryTable(pageNumber);

				} else {
					layer.msg("删除失败");
				}

			}).error(function(data, header, config, status) {
				layer.alert('删除异常,请刷新重试!', {
					skin : 'layui-layer-lan', //样式类名
					closeBtn : 0
				});
			});
		}


	} ]);

});