define([ '../module' ], function(controllers) {
	'use strict';
	controllers.controller("TaskMaintainCtrl", [ '$scope', '$http', '$location', '$route', '$rootScope', '$uibModal', '$filter', '$config',
		function($scope, $http, $location, $route, $rootScope, $uibModal, $filter, $config) {

			$(document).ready(function() {
				querytaskLists(1);
				dynamicSearch();
			});

			/**
			 * 动态搜索
			 */
			function dynamicSearch() {
				$(".search").keyup(function() {
					var searchTerm = $(".search").val();
					var listItem = $('.results tbody').children('tr');
					var searchSplit = searchTerm.replace(/ /g, "'):containsi('")

					$.extend($.expr[':'], {
						'containsi' : function(elem, i, match, array) {
							return (elem.textContent || elem.innerText || '').toLowerCase().indexOf((match[3] || "").toLowerCase()) >= 0;
						}
					});

					$(".results tbody tr").not(":containsi('" + searchSplit + "')").each(function(e) {
						$(this).attr('visible', 'false');
					});

					$(".results tbody tr:containsi('" + searchSplit + "')").each(function(e) {
						$(this).attr('visible', 'true');
					});

				});
			}

			/**
			 * 查询ruleConfig的列表
			 */
			function querytaskLists(currentPageNumber) {
				$('#taskMaintainTable').bootstrapTable({
					url : $config.base + "/api/v1/task/find/page",
					method : 'POST', //请求方式（*）
					//toolbar: '#toolbar',              //工具按钮用哪个容器
					striped : true, //是否显示行间隔色
					cache : false, //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
					pagination : true, //是否显示分页（*）
					sortable : true, //是否启用排序
					sortOrder : "asc", //排序方式
					sidePagination : "server", //分页方式：client客户端分页，server服务端分页（*）
					pageNumber : currentPageNumber, //初始化加载第一页，默认第一页,并记录
					pageSize : 10, //每页的记录行数（*）
					pageList : [ 10, 25, 50, 100 ], //可供选择的每页的行数（*）
					search : false, //是否显示表格搜索
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
					contentType : "application/json; charset=UTF-8",
					//得到查询的参数
					queryParams : function(params) {
						//这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
						var temp = {
							rows : params.limit, //页面大小
							pageSize : (params.offset / params.limit) + 1, //页码
							sort : 'created', //排序列名
							sortOrder : params.order //排位命令（desc，asc） 
						//search:params.search
						};
						return JSON.stringify(temp);
					},
					onLoadSuccess : function(response) { //加载成功时执行
						//layer.msg("加载成功");
						console.log(response);

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
						formatter : function(value, row, index) {
							return index + 1;
						}
					}, {
						field : 'name',
						title : '任务名称'
					}, {
						field : 'baseType',
						title : '执行类型'
					}, {
						field : 'created',
						title : '执行任务时间',
						formatter : function(value, row, index) {
							return $filter('date')(value, 'yyyy-MM-dd HH:mm:ss');
						}
					}, {
						field : 'taskOperation',
						title : '操作',
						width : 180,
						events : taskOperation(),
						formatter : function(value, row, index) { //value是这个字段的值
							var html = ' <ul class="operation"><li><a href="#/taskMaintain/taskResult/' + row.id + '">查看执行结果</a>' +
								'<li class="delectedOperation"><a href="javascript:void(0);">删除</a></li></ul>';
								//html += '　<a href="javascript:DeleteBook(' + value + ')">删除</a>'; 

							//var $html = $compile(html)($scope);  
							return html;
						}
					} ]
				});
			}

			function taskOperation() {
				return {
					'click .delectedOperation' : function(e, value, row, index) {
						layer.open({
							title : "提示",
							content : '确定删除吗？',
							btn : [ '确定', '取消' ],
							yes : function(index, layero) {
								/*var pageNumber = $("#projectVersionTable").bootstrapTable('getOptions').pageNumber;
								queryVersionLists(pageNumber);*/
								delectedOperation(row.id);
								$('#taskMaintainTable').bootstrapTable('remove', {
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
				};
			}
			/**
			 * 删除
			 */
			function delectedOperation(taskId) {
				$http({
					method : 'DELETE',
					url : $config.base + '/api/v1/task/delete/' + taskId,
				//data: JSON.stringify(page) 
				//}).success(function(response) {
				}).then(function successCallback(response) {
					console.log(response);
					layer.msg("删除成功");
				//$scope.$apply();
				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('删除异常,请重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}

			/*		$(document).on("click",".delectedTask",function(){  
						layer.open({
							  title :"提示",
							  content: '确定删除吗？',
							  btn: ['确定', '取消'],
							  yes: function(index, layero){
								  var pageNumber=$("#taskMaintainTable").bootstrapTable('getOptions').pageNumber;
								  querytaskLists(pageNumber);
								  layer.msg("删除成功");
								  layer.close(index); //如果设定了yes回调，需进行手工关闭
							  },
							  btn2: function(index, layero){
							    //return false 开启该代码可禁止点击该按钮关闭
							  },
							  cancel: function(){ 
							    //右上角关闭回调
							  }
							});
					});*/


		} ]);
});