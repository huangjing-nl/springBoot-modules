define([ '../module' ], function(controllers) {
	'use strict';
	controllers.controller("ProjectVTaskCtrl", [ '$scope', '$http', '$location', '$route', '$rootScope', '$uibModal', '$ocLazyLoad', '$config',
		function($scope, $http, $location, $route, $rootScope, $uibModal, $ocLazyLoad, $config) {
			/*$ocLazyLoad.load([{
				  files: ['js\/lib\/bootstrap-select-master\/dist\/js\/bootstrap-select.min.js'],
				  cache: false
				}]);*/
			$scope.versionLists = [];
			$scope.projectVNoLists = [];
			$scope.roundLists = [];
			$scope.flag1 = false;
			$scope.flag2 = false;
			$scope.flag3 = false;

			$(document).ready(function() {
				fileUpload("", "", "");
				queryVersionLists();
				queryProjectVNoLists();
				queryRoundLists();

			});

			/**
			 * 查询所有的版本
			 */
			function queryVersionLists() {
				$http({
					method : 'post',
					url : $config.base + '/configRuleAndBlock',
					data : {}
				//}).success(function(response) {
				}).then(function successCallback(response) {
					console.log(response);
					$scope.versionLists = response.data;
				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('查询后台数据异常,请刷新重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}

			/**
			 * 查询所有的版本号
			 */
			function queryProjectVNoLists() {
				$http({
					method : 'post',
					url : $config.base + '/configRuleAndBlock',
					data : {}
				//}).success(function(response) {
				}).then(function successCallback(response) {
					console.log(response);
					$scope.projectVNoLists = response.data;
				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('查询后台数据异常,请刷新重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}

			/**
			 * 查询所有的版本号
			 */
			function queryRoundLists() {
				$http({
					method : 'post',
					url : $config.base + '/configRuleAndBlock',
					data : {}
				//}).success(function(response) {
				}).then(function successCallback(response) {
					console.log(response);
					$scope.roundLists = response.data;
				//}).error(function(data, header, config, status) {
				}, function errorCallback(response) {
					layer.alert('查询后台数据异常,请刷新重试', {
						skin : 'layui-layer-lan', //样式类名
						closeBtn : 0
					});
				});
			}


			/**
			 * 提交
			 */
			$scope.taskSubmit = function() {
				var erroFlag = validDataErro();
				if (erroFlag) {
					return;
				}
				$("#projectTaskFileUpload").fileinput("upload");
			}
			/**
			 * 验证错误
			 */
			function validDataErro() {
				var flag = false;
				if ($scope.selectprojectV == undefined) {
					$scope.flag1 = true;
					flag = true;
				}
				if ($scope.selectprojectVNo == undefined) {
					$scope.flag2 = true;
					flag = true;
				}
				if ($scope.selectRound == undefined) {
					$scope.flag3 = true;
					flag = true;
				}
				return flag;
			}


			/**
			 * 文件上传
			 */
			function fileUpload(projectV, projectVNo, round) {
				$("#projectTaskFileUpload").fileinput({
					language : 'zh', //设置语言
					uploadUrl : $config.base + '/upload', // 
					enctype : 'multipart/form-data',
					allowedFileExtensions : [ 'jpg', 'png', 'txt', 'sql' ],
					maxFileSize : 10000, //上传文件最大的尺寸;单位为kb
					maxFileCount : 2, //表示允许同时上传的最大文件个数
					minFileCount : 1,
					//minImageWidth: 50, //图片的最小宽度
					//minImageHeight: 50,//图片的最小高度
					//maxImageWidth: 100,//图片的最大宽度
					maxImageHeight : 100, //图片的最大高度
					showRemove : true, //是否显示移除按钮
					showUpload : false, //是否显示上传按钮
					dropZoneEnabled : true, //是否显示拖拽区域
					//uploadAsync: false,
					overwriteInitial : true, //不覆盖已存在的图片
					removeFromPreviewOnError : true, //当选择的文件不符合规则时，例如不是指定后缀文件、大小超出配置等，选择的文件不会出现在预览框中，只会显示错误信息  
					autoReplace : true, //是否自动替换当前图片
					layoutTemplates : {
						actionDelete : '', //去除上传预览的缩略图中的删除图标
						actionUpload : '', //去除上传预览缩略图中的上传图片；
					//actionZoom:''   //去除上传预览缩略图中的查看详情预览的缩略图标。
					},
					uploadExtraData : function(previewId, index) {
						var obj = {};
						obj.projectV = $scope.selectprojectV;
						obj.projectVNo = $scope.selectprojectVNo;
						obj.round = $scope.selectRound;
						return obj;
					},
					//allowedFileTypes: ['image', 'video', 'flash'],
					slugCallback : function(filename) {
						return filename.replace('(', '_').replace(']', '_');
					}
				}).on("fileuploaded", function(event, data, previewId, index) {
					//var template='<div>'
					layer.msg('文件上传成功！');
				}).on('fileerror', function(event, data, msg) { //一个文件上传失败
					layer.msg('文件上传失败！' + msg);
				});
			}


		} ]);
});