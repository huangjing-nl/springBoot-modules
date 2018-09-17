define([ '../module' ], function(controllers) {
	'use strict';
	controllers.controller("WorkTabCtrl", [ '$scope', '$http', '$location', '$route', '$rootScope', '$uibModal', '$ocLazyLoad', '$config',
		function($scope, $http, $location, $route, $rootScope, $uibModal, $ocLazyLoad, $config) {
			/*$ocLazyLoad.load([{
			 files: ['js\/lib\/bootstrap-fileinput-master\/js\/fileinput.js'],
			 cache: false
			 }]);*/
			$scope.fileInfos = [];
			$scope.singleSQLs = []; //单个SQL的执行
			$scope.SQLScripts = [];
            $scope.scriptVersion="";
			queryTypeData();
			queryBusinessTypeList();
			$(document).ready(function() {

				require([ "./codemirror" ], function(CodeMirror) {
					var myTextarea = document.getElementById('editor');
					var codeMirrorEditor = CodeMirror.fromTextArea(myTextarea, {
						mode : {
							name : "text/x-mysql"
						},
						lineNumbers : true,
						extraKeys : {
							"Tab" : "autocomplete"
						},
						theme : "base16-light"
					});

					$scope.submitSQL = function() {
						var sqlText = codeMirrorEditor.getValue();
						if (sqlText == "") {
							layer.open({
								title : '提示',
								content : '请输入SQL语句'
							});
							return;
						}
						//TODO MODIFY
						var param = {
							business : $scope.selectBusinessTypeValue,
							baseType : $scope.selectTypeValue,
                            version  : $scope.SQLVersion,
							sqlString : $.base64.encode(encodeURIComponent(sqlText))
                           //sqlString : sqlText
						};
                        $(".loading1").show();
						$http({
							method : 'post',
							url : $config.base + '/api/v1/scan/string/parse/version',
                            headers: {'Content-Type': 'application/x-www-form-urlencoded;charset=utf-8'},
							data :  $.param(param)
						//}).success(function(response) {
						}).then(function successCallback(response) {
                            $(".loading1").hide();
							$scope.SQLScripts = response.data;
							$(".SQLTable").show();
						//}).error(function(data, header, config, status) {
						}, function errorCallback(response) {
                            $(".loading1").hide();
							layer.alert('解析SQL异常,请刷新重试', {
								skin : 'layui-layer-lan',
								closeBtn : 0
							});
						});
					};

					$scope.cancelSQL = function() {
						layer.open({
							content : '确定取消吗？',
							btn : [ '确 定', '取消' ],
							yes : function(index, layero) {
								codeMirrorEditor.setValue("");
								$(".SQLTable").hide();
								$scope.SQLScripts = [];
								$scope.$apply();
								layer.close(index);
							},
							btn2 : function(index, layero) {
								//按钮【按钮二】的回调

								//return false 开启该代码可禁止点击该按钮关闭
							},
							cancel : function() {
								//右上角关闭回调
								//return false 开启该代码可禁止点击该按钮关闭
							}
						});
					}
				});

				$("#SQLFileUpload").fileinput({
					language : 'zh', //设置语言
					uploadUrl : $config.base + '/api/v1/package/upload/taskFile/version',
					enctype : 'multipart/form-data',
                    elErrorContainer:'#elErrorContainer',
					allowedFileExtensions : [ 'zip', 'rar', 'tar', 'sql' ],
					uploadExtraData : function(previewId, index) {
						return {
							business : $scope.selectBusinessTypeValue,
							baseType : $scope.selectTypeValue,
                            version  : $scope.scriptVersion
						};
					},
					maxFileSize : 2*1024*1024, //上传文件最大的尺寸;单位为kb
					maxFileCount : 1, //表示允许同时上传的最大文件个数
					minFileCount : 1,
					uploadAsync : false, //默认异步上传
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
				});
                $("#SQLFileUpload").on("filebatchuploadsuccess", function(event, data) { //fileuploaded
					$scope.fileInfos = [];
					$scope.fileInfos.push(data.response);
                    $scope.isParse="解 析";
					$scope.$apply();


                    $(".parseButton").css("pointer-events","auto");
					$(".singleFile .fileTable").show();
					$(".singleFile .fileSQLTable").hide();

                    //$("#SQLFileUpload").fileinput('clear');
				});
                $("#SQLFileUpload").on('filebatchuploaderror', function(event, data, msg) { //一个文件上传失败fileerror
					if(data.jqXHR.responseText!=undefined){
                        msg=JSON.parse(data.jqXHR.responseText).message;
					}
					layer.alert('上传失败,错误信息:'+msg, {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
                   // document.getElementById("fileFrom").reset()
                    $("#SQLFileUpload").fileinput('reset');
				});
                /*$("#SQLFileUpload").on('fileuploaded',function(event, data){
						alert(1111);
				});
                $("#SQLFileUpload").on('fileerror',function(event, data){
                    alert(222222);
				});*/
			});


			/**
			 * 文件解析
			 */
			$scope.parseFileInfo = function(fileInfo) {
                $(".loading").show();
				$http({
					method : 'GET',
					url : $config.base + '/api/v1/scan/launch/'+fileInfo.id
				//}).success(function(response) {
				}).then(function successCallback(response) {
                    $(".loading").hide();
					$scope.singleSQLs = response.data;
					$(".singleFile .fileSQLTable").show().prop('scrollTop','0');
                    $scope.isParse="已解析";
                    $(".parseButton").css("pointer-events","none");
				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('解析异常,请刷新重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});

			};

			/**
			 * 查询库类型
			 */
			function queryTypeData() {
				$http({
					method : 'get',
					url : $config.base + '/api/v1/relation/find/allBaseType'
				//}).success(function(response) {
				}).then(function successCallback(response) {
					console.log(response);
					$scope.typeLists = response.data;
                    $scope.selectTypeValue = response.data[0].name;
				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('查询后台数据异常,请刷新重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}

			/**
			 * 查询所有的业务类型
			 */
			function queryBusinessTypeList() {
				$http({
					method : 'GET',
					url : $config.base + '/api/v1/relation/find/allBusiness'
				//}).success(function(response) {
				}).then(function successCallback(response) {
					console.log(response);
					$scope.businessTypeLists = response.data;
                    $scope.selectBusinessTypeValue = response.data[0].name;

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