define([ '../module' ], function(controllers) {
	'use strict';
	controllers.controller("VersionMaintainCtrl", [ '$scope', '$http', '$location', '$route', '$rootScope', '$uibModal', '$compile', '$filter', '$config',
		function($scope, $http, $location, $route, $rootScope, $uibModal, $compile, $filter, $config) {


            var versionCurrentPage=1;
            if($rootScope.currentVerPage>=0){
                versionCurrentPage=$rootScope.currentVerPage;
			}


			$(document).ready(function() {
				queryVersionLists(versionCurrentPage);
			});

			/**
			 * 动态搜索
			 */
			/*function dynamicSearch() {
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
			}*/

			/**
			 * 查询版本维护管理的列表
			 */
			function queryVersionLists(currentPageNumber) {
                $rootScope.currentVerPage=undefined;
				$("#projectVersionTable").bootstrapTable('destroy');
				// 初始化Table
				$('#projectVersionTable').bootstrapTable({
					url : $config.base + "/api/v1/task/find/page",
					//url : "/api/v1/product/queryVersion",
					method : 'POST', //请求方式（*）
					//toolbar: '#toolbar',              //工具按钮用哪个容器
					striped : true, //是否显示行间隔色
					cache : false, //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
					pagination : true, //是否显示分页（*）
					sortable : true, //是否启用排序
                    sortName : "created",
					sortOrder : "desc", //排序方式
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
					//得到查询的参数
					queryParams : function(params) {
						var pagination = {
							rows : params.limit, //页面大小
							pageSize : (params.offset / params.limit) + 1, //页码
							sort : params.sort, //排序列名
							sortOrder : params.order, //排位命令（desc，asc）
						    search:params.search
						};
						return pagination;
					},
					onLoadSuccess : function(response) { //加载成功时执行
						// layer.msg("加载成功");
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
                        formatter : function(value, row, index) {
                            var html = '<div title="' + value + '">' + value + '<div>';
                            return html;
                        },
						title : '项目名称'
					}, {
						field : 'business',
                        sortable : true,
						class : 'cellBusiness',
						title : '业务类型'
					}, {
						field : 'baseType',
                        sortable : true,
						title : '库类型'
					}, {
						field : 'fileName',
						class :'cellFileName',
                        sortable : true,
						title : '上传文件名'
					}, {
                        field : 'version',
                        sortable : true,
                        title : '版本号'
                    }, {
						field : 'created',
						class : 'cellCreated',
						title : '最后修改时间',
						formatter : function(value, row, index) {
							return $filter('date')(value, 'yyyy-MM-dd HH:mm:ss');
						}
					}, {
						field : 'status',
						class : 'cellStatus',
						title : '操作',
						width : 100,
						events : openUploadModel(),
						formatter : function(value, row, index) { //value是这个字段的值
							var taskExecute = '<li class="taskExecute"><a href="javascript:void(0);">任务执行</a></li>';
							if (value == "ACTIVE") {
                                taskExecute = '<li class="viewResult"><a href="javascript:void(0);">查看结果</a></li>';
							}

							var html = '<ul class="operation">' + taskExecute +
								//								'<li class="openUploadModel"><a href="javascript:void(0);">文件上传</a></li>' +
								//								'<li class=""><a href="#/versionMaintain/addVersion/' + row.id + '">编辑</a></li>' +
								//								'<li class="delectedTask"><a href="javascript:void(0);">删除</a></li>'+
								'</ul>';
							return html;
						}
					} ]
				});
			}




			function openUploadModel() {
				return {
					'click .viewResult' : function(e, value, row, index){
                        $rootScope.currentVerPage=$('#projectVersionTable').bootstrapTable('getOptions').pageNumber;
                        $location.path("/taskMaintain/taskResult/"+ row.id);
                        $scope.$apply();

					},
					'click .openUploadModel' : function(e, value, row, index) {
                       // $rootScope.version.currentPage=$('#projectVersionTable').bootstrapTable('getData');
						fileUpload(row.id);
						if (row.fileName != null && row.fileName != undefined) {
							layer.alert('只允许上传一个文件,您已上传了!', {
								icon : 0,
								skin : 'layer-ext-moon' //该皮肤由layer.seaning.com友情扩展。关于皮肤的扩展规则，去这里查阅
							});
							return;
						}7
						layer.open({
							title : row.name,
							type : 1,
							area : [ '600px', '180px' ],
							content : $("#template2")
						/*btn: ['上&nbsp;传', '取&nbsp;消'],
						yes: function(index, layero){
							$("#fileUploadModel").fileinput("upload");
							layer.close(index);
						},btn2: function(index, layero){

						    //return false 开启该代码可禁止点击该按钮关闭
						}*/
						});
					},
					'click .taskExecute' : function(e, value, row, index) {
						if (row.fileName == null) {
							layer.alert('必须先上传文件才可以执行！', {
								skin : 'layui-layer-lan', //样式类名
								closeBtn : 0
							});
							return;
						}
						taskExecute(e, row.id)
					},
					'click .delectedTask' : function(e, value, row, index) {
						layer.open({
							title : "提示",
							content : '确定删除吗？',
							btn : [ '确定', '取消' ],
							yes : function(index, layero) {
								/*var pageNumber = $("#projectVersionTable").bootstrapTable('getOptions').pageNumber;
								queryVersionLists(pageNumber);*/
								delectedTask(row.id);
								$('#projectVersionTable').bootstrapTable('remove', {
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
			 * 任务执行
			 */
			function taskExecute(event, taskId) {
				$http({
					method : 'GET',
					url : $config.base + '/api/v1/scan/launch/' + taskId,
				//data: JSON.stringify(page)

				//.success(function(response) {
				}).then(function successCallback(response) {

					layer.msg("执行成功");
					//$(event.currentTarget).find("a").text("查看结果");
					$(event.currentTarget).removeClass("taskExecute");
					$(event.currentTarget).unbind("click");
					$(event.currentTarget).html('<a href="#/taskMaintain/taskResult/' + taskId + '">查看结果</a>');

				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('任务执行异常,请重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});

			}

			/**
			 * 文件上传
			 */
			function fileUpload(id) {
				$("#fileUploadModel").fileinput({
					language : 'zh', //设置语言
					uploadUrl : $config.base + '/api/v1/package/upload/' + id,
					enctype : 'multipart/form-data',
					allowedFileExtensions : [ 'zip', 'rar', 'tar', 'sql' ],
					maxFileSize : 10000, //上传文件最大的尺寸;单位为kb
					maxFileCount : 1, //表示允许同时上传的最大文件个数
					minFileCount : 1,
					uploadAsync : true, //默认异步上传
					showPreview : false,
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
					console.log(data);
					if (data.response.status == "Success") {
						layer.msg("上传成功");
					} else {
						layer.msg("上传失败");
					}
					var pageNumber = $("#projectVersionTable").bootstrapTable('getOptions').pageNumber;
					queryVersionLists(pageNumber);
					layer.closeAll('page');
				}).on('fileerror', function(event, data, msg) { //一个文件上传失败
					layer.alert('上传失败,请重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}
			/**
			 * 删除
			 */
			function delectedTask(taskId) {
				$http({
					method : 'DELETE',
					url : $config.base + '/api/v1/task/delete/' + taskId,
					//data: JSON.stringify(page)

				//}).success(function(response) {
				}).then(function successCallback(response) {
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

			/*		$(document).on("click", ".delectedProjectVersion", function() {
						layer.open({
							title : "提示",
							content : '确定删除吗？',
							btn : [ '确定', '取消' ],
							yes : function(index, layero) {
								var pageNumber = $("#projectVersionTable").bootstrapTable('getOptions').pageNumber;
								queryVersionLists(pageNumber);
								layer.msg("删除成功");
								layer.close(index); //如果设定了yes回调，需进行手工关闭
							},
							btn2 : function(index, layero) {
								//return false 开启该代码可禁止点击该按钮关闭
							},
							cancel : function() {
								//右上角关闭回调
							}
						});
					});*/








		} ]);
});