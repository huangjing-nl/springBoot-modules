define([ '../module' ], function(controllers) {
	'use strict';
	controllers.controller("ConfigureFieldInfoCtrl", [ '$scope', '$http', '$location', '$timeout', '$route', '$rootScope', '$routeParams', '$config', '$filter', function($scope, $http, $location, $timeout, $route, $rootScope, $routeParams, $config, $filter) {
		$scope.uploadFieldResults=[];
		$(document).ready(function() {
			queryFileInfo(1);
            $('#myModal').on('hidden.bs.modal', function () {
                $("#erroDescript").hide();
            });
		});

		function queryFileInfo(pageNumber) {
			$("#configFieldInfo").bootstrapTable('destroy');
			$("#configFieldInfo").bootstrapTable({
				url : $config.uri.queryFileInfo,
				method : 'POST', //请求方式（*）
				//toolbar : '#toolbarField', //工具按钮用哪个容器
				striped : true, //是否显示行间隔色
				cache : false, //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
				pagination : true, //是否显示分页（*）
				sortable : true, //是否启用排序
                sortName : 'tableName',
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
						sortCol : params.sort,
						sortOrder : params.order, //排位命令（desc,asc） 
						search : params.search
					};
					return JSON.stringify(temp);
				},
				onLoadSuccess : function(response) { //加载成功时执行
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
                    width : 50,
					title : '序号',
					formatter : function(value, row, index) {
						return index + 1;
					}
				}, {
					field : 'tableName',
					class : 'cellTableName',
					title : '表名称'
				},  {
					field : 'colId',
                    width : 80,
					title : '字段序列号'
				},{
                    field : 'colName',
                    class : 'cellColName',
                    title : '字段名称'
                }, {
					field : 'colRegexp',
					class : 'cellColRegexp',
                    formatter : function(value, row, index) {
                        var html = '<div title="' + value + '">' + value + '<div>';
                        return html;
                    },
					title : '正则表达式'
				}, {
					field : 'ruleName',
					title : '规则名称'
				}, {
					field : 'operation',
					title : '操作',
					width : 100,
					class : "operation",
					events : fieldOperation(),
					formatter : function(value, row, index) { //value是这个字段的值
						var html = '<ul><li><a href="#/configFieldInfo/update/' + row.id + '">编辑</a>' +
							'<li class="delected"><a href="javascript:void(0);">删除</a></li></ul>';
						return html;
					}
				} ]
			});
		}

		function fieldOperation() {
			return {
				'click .delected' : function(e, value, row, index) {
					layer.open({
						title : "提示",
						content : '确定删除吗？',
						btn : [ '确定', '取消' ],
						yes : function(index, layero) {
							var pageNumber = $("#configFieldInfo").bootstrapTable('getOptions').pageNumber;
							deleted(row.id);
							$('#configFieldInfo').bootstrapTable('remove', {
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


		function deleted(colId) {
			$http({
				method : 'GET',
				url : $config.uri.delecteFileById + "?id=" + colId,
			// data: JSON.stringify(param) 
			}).success(function(response) {
				if (response.result = 'success') {
					layer.msg("删除成功");
					var pageNumber = $("#configFieldInfo").bootstrapTable('getOptions').pageNumber;
					queryFileInfo(pageNumber);

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

        $("#excelFileUpload").fileinput({
            language : 'zh', //设置语言
            uploadUrl : $config.uri.importExcelFile,
            enctype : 'multipart/form-data',
            allowedFileExtensions : [ 'xlsx' ],
            uploadExtraData : function(previewId, index) {
                return {
                    business : $scope.selectBusinessTypeValue,
                    baseType : $scope.selectTypeValue,
                    version : $scope.scriptVersion
                };

            },
            maxFileSize : 10000, //上传文件最大的尺寸;单位为kb
            maxFileCount : 1, //表示允许同时上传的最大文件个数
            minFileCount : 1,
            uploadAsync : false, //默认异步上传
            showPreview : false,
            showUpload : true, //是否显示上传按钮
            showCaption : true, //是否显示标题
            //minImageWidth: 50, //图片的最小宽度
            //minImageHeight: 50,//图片的最小高度
            //maxImageWidth: 100,//图片的最大宽度
            maxImageHeight : 100, //图片的最大高度
            showRemove : true, //是否显示移除按钮
            //showUpload:false,
            dropZoneEnabled : false, //是否显示拖拽区域
            //uploadAsync: false,
            // overwriteInitial: true,//不覆盖已存在的图片
            removeFromPreviewOnError : true, //当选择的文件不符合规则时，例如不是指定后缀文件、大小超出配置等，选择的文件不会出现在预览框中，只会显示错误信息
            autoReplace : true, //是否自动替换当前图片
            layoutTemplates : {
                actionDelete : '', //去除上传预览的缩略图中的删除图标
                actionUpload : '', //去除上传预览缩略图中的上传图片；
                //actionZoom:''   //去除上传预览缩略图中的查看详情预览的缩略图标。
            },
            //allowedFileTypes: ['image', 'video', 'flash'],
            slugCallback : function(filename) {
                return filename.replace('(', '_').replace(']', '_');
            }
        }).on("filebatchuploadsuccess", function(event, data, previewId, index) { //fileuploaded
        	
			if (data.response.result == 'success') {
				layer.msg('导入成功' , {time: 3000});
                $("#myModal").modal('hide');
            } else{
            	if(data.response.data == null || data.response.data.length == 0){
	            	layer.alert('导入失败：' + data.response.message, {
	                    skin : 'layui-layer-lan', //样式类名
	                    closeBtn : 0
	                });
            	}else{
            		$scope.uploadFieldResults=data.response.data;
            		/*var strArr=data.response.data.split(';');;
            		var newStr
            		for(var i=0;i<strArr.length;i++){
            			var 
            		}*/
            		$scope.$apply();
            		$("#erroDescript").show();
            	}
            }

        }).on('filebatchuploaderror', function(event, data, msg) { //一个文件上传失败
            layer.alert('导入失败，请重试：' + data.response.message, {
                skin : 'layui-layer-lan', //样式类名
                closeBtn : 0
            });
        });

    } ]);
});