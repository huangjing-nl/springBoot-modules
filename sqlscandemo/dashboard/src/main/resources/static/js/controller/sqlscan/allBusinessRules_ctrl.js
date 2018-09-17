define([ '../module' ], function(controllers) {
	'use strict';
	controllers.controller("AllBusinessRulesCtrl", [ '$scope', '$http', '$routeParams', '$location', '$route', '$rootScope', '$uibModal', '$ocLazyLoad', '$config',
		function($scope, $http, $routeParams, $location, $route, $rootScope, $uibModal, $ocLazyLoad, $config) {

			var bui = $routeParams.businessName;
			var opn = $routeParams.operName;
			var levelList = [];
			$(document).ready(function() {
				getAllLevel();
				queryAllBussiness();
				queryAllOperationType();
				if ($routeParams.businessName!=undefined&&$routeParams.operName!=undefined){
	                $scope.selectBusinessValue=bui;
	                $scope.selectTypeValue=opn;
	            }
			});


			function getAllLevel() {
				$http({
					method : 'get',
					url : $config.base + '/api/v1/relation/find/allLevel'
				//}).success(function(response) {
				}).then(function successCallback(response) {
					levelList = response.data;
					queryBusinessRulesLists(bui, opn);
					
				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('查询后台数据异常,请刷新重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}

			$scope.serchBusinessRules = function(selectBusinessValue, selectTypeValue) {
				queryBusinessRulesLists(selectBusinessValue, selectTypeValue);
			};
            /**
			 * 全部删除
             */
			$scope.deletedAll=function(){
                var selectRows = $('#businessRuleTable').bootstrapTable('getSelections');
                if (selectRows.length == 0) {
                    layer.alert('至少选中一条删除!', {
                        icon : 0,
                        skin : 'layer-ext-moon' //该皮肤由layer.seaning.com友情扩展。关于皮肤的扩展规则，去这里查阅
                    });
                    return;
                }

                layer.open({
                    title : "提示",
                    content : '确定全部删除吗？',
                    btn : [ '确定', '取消' ],
                    yes : function(index, layero) {
                        var idList=[];
                        for (var i = 0; i < selectRows.length; i++) {
                            var id =  selectRows[i].id;
                            idList.push(id);
                        }
                        deleteAllServer(idList);
                        layer.close(index); //如果设定了yes回调，需进行手工关闭
                    },
                    btn2 : function(index, layero) {
                        //return false 开启该代码可禁止点击该按钮关闭
                    },
                    cancel : function() {
                        //右上角关闭回调
                    }
                });
			};

            /**
			 * 删除全部
             */
			function deleteAllServer(idList){
                $http({
                    method : 'POST',
                    url : $config.base + '/api/v1/rule/config/delete',
                    data : idList
                }).then(function successCallback(response) {
                    layer.msg('删除成功');
                    var pageNumber=$("#businessRuleTable").bootstrapTable('getOptions').pageNumber;
                    queryBusinessRulesLists($scope.selectBusinessValue,$scope.selectTypeValue,pageNumber);
                }, function errorCallback(response) {
                    layer.alert('删除异常,请重试', {
                        skin : 'layui-layer-lan', //样式类名
                        closeBtn : 0
                    });
                });
			}



			function queryBusinessRulesLists(business, operType,currPage) {
				$("#businessRuleTable").bootstrapTable('destroy');
				$('#businessRuleTable').bootstrapTable({
					url : $config.base + "/api/v1/rule/find/config/page",
					method : 'POST',
                    toolbar: '#toolbarBusiness',
                    striped : true, //是否显示行间隔色
					cache : false, //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
					pagination : true, //是否显示分页（*）
					sortable : true, //是否启用排序
                    sortName : "id",
					sortOrder : "asc", //排序方式
					sidePagination : "server", //分页方式：client客户端分页，server服务端分页（*）
					pageNumber : currPage, //初始化加载第一页，默认第一页,并记录
					pageSize : 10, //每页的记录行数（*）
					pageList : [ 10, 25, 50, 100 ], //可供选择的每页的行数（*）
					search : false, //是否显示表格搜索
					strictSearch : true,
					showColumns : true, //是否显示所有的列（选择显示的列）
					showRefresh : true, //是否显示刷新按钮
					minimumCountColumns : 1, //最少允许的列数
					clickToSelect : false, //是否启用点击选中行
					height : 600, //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
					uniqueId : "id", //每一行的唯一标识，一般为主键列
					showToggle : false, //是否显示详细视图和列表视图的切换按钮
					cardView : false, //是否显示详细视图
					detailView : false, //是否显示父子表
					contentType : "application/x-www-form-urlencoded; charset=UTF-8",
					//得到查询的参数
					queryParams : function(params) {
						var page = {
							rows : params.limit, //页面大小
							pageSize : (params.offset / params.limit) + 1, //页码
							sort : params.sort,//排序列名
							sortOrder : params.order, //排位命令（desc，asc）
							business : business, //业务
							operType : operType
						//search:params.search
						};
						return page;
					},
					onLoadSuccess : function(response) { //加载成功时执行
						// layer.msg("加载成功");
						console.log(response);
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
                    },{
						field : 'id',
						title : '序号',
                        width : 50,
						formatter : function(value, row, index) {
							return index + 1;
						}
					}, {
						field : 'business.name',
                        /*sortable : true,*/
						title : '业务'
					}, {
						field : 'operType.name',
                       /* sortable : true,*/
						title : '操作类型'
					}, {
						field : 'rule.name',
						title : '规则名'
					}, {
						field : 'level.level',
						events : setLevel(),
						formatter : function(value, row, index) { //value是这个字段的值
							var start = '<div><select class="selectLevel" dataId="'+row.level.id+'">';
							var options = '';
							for (var i = 0; i < levelList.length; i++) {
								options += '<option value="' + levelList[i].id + '">' + levelList[i].level + '</option>'
							}
							var selectEnd = '</select>';
							var operaEnd = '<div class="operaSelect" style="display:none;">' +
								'<a href="javascript:void(0);" class="removeLevel"><span class="glyphicon glyphicon-remove"></span></a>' +
								'<a href="javascript:void(0);" class="okLevel"><span class="glyphicon glyphicon-ok"></span></a></div></div>';
							var Whole = start + options + selectEnd + operaEnd;
							var html = $(Whole).find('option[value=' + row.level.id + ']').attr('selected', true).parent("select").parent().prop("outerHTML");
							return html;
						},
						title : '级别'
					}, {
						field : 'operation',
						title : '操作',
						width : 100,
						events : deleteBusinessRule(),
						formatter : function(value, row, index) { //value是这个字段的值
							var html = '<ul class="operation">' +
								'<li class="deleteBusiness"><a href="javascript:void(0);">删除</a></li></ul>';
							return html;
						}
					} ]
				});
			}
			function setLevel() {
				return {
					'change .selectLevel' : function(e, value, row, index) {
						row.level.id = $(e.currentTarget).val();
						row.level.level = $(e.currentTarget).find("option:selected").text();
						$(e.currentTarget).siblings(".operaSelect").show();
					},
					'click .removeLevel' : function(e, value, row, index) {
                        var selectHTML=$(e.currentTarget).parent(".operaSelect").siblings(".selectLevel");
                        var originalVal=selectHTML.attr("dataId");
                        selectHTML.val(originalVal);
                        $(e.currentTarget).parent(".operaSelect").hide();
					},
					'click .okLevel' : function(e, value, row, index) {
                        submitLevel(row,e);
						$(e.currentTarget).parent(".operaSelect").hide();
					}
				}
			}

			function submitLevel(row,e) {
				var params = {
					"id" : row.id,
					"business" : {
						"id" : null,
						"name" : null
					},
					"operType" : {
						"id" : null,
						"name" : null
					},
					"level" : {
						"id" : row.level.id,
						"level" : row.level.level
					},
					"rule" : {
						"id" : null,
						"name" : null
					}
				};
				$http({
					method : 'POST',
					url : $config.base + '/api/v1/rule/config/edit',
					data : params
				}).then(function successCallback(response) {
                    var selectHTML=$(e.currentTarget).parent(".operaSelect").siblings(".selectLevel");
                    var selectVal=selectHTML.val();
                    selectHTML.attr("dataId",selectVal);
				}, function errorCallback(response) {
					layer.alert('修改异常,请刷新重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}





			/**
			 *
			 * 删除
			 */
			function deleteBusinessRule() {
				return {
					'click .deleteBusiness' : function(e, value, row, index) {

						layer.open({
							title : "提示",
							content : '确定删除吗？',
							btn : [ '确定', '取消' ],
							yes : function(index, layero) {
								$http({
									method : 'POST',
									url : $config.base + '/api/v1/rule/config/delete',
									data : [ row.id ]
								//}).success(function(response) {
								}).then(function successCallback(response) {
									layer.msg("删除成功");
                                    var pageNumber=$("#businessRuleTable").bootstrapTable('getOptions').pageNumber;
                                    queryBusinessRulesLists($scope.selectBusinessValue,$scope.selectTypeValue,pageNumber);
								//}).error(function(data, header, config, status) {
								}, function errorCallback(response) {
									layer.alert('查询后台数据异常,请刷新重试', {
										skin : 'layui-layer-lan', //样式类名
										closeBtn : 0
									});
								});
								/*$('#businessRuleTable').bootstrapTable('remove', {
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
				}
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
					$scope.businessLists = response.data;
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
					$scope.typeLists = response.data;
				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('查询后台数据异常,请刷新重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}


		} ]);
});