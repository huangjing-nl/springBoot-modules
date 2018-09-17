define([ '../module' ], function(controllers) {
	'use strict';
	controllers.controller("ConfigureRuleCtrl", [ '$scope', '$http', '$location', '$route', '$rootScope', '$uibModal', '$config',
		function($scope, $http, $location, $route, $rootScope, $uibModal, $config) {

			console.log("aaaa");
			$scope.winContent = "<div><h3></h3></div>";

			/**
			 * 展示表之间的关联关系窗口
			 */
			$scope.showAssociatedWindow = function() {
				layer.open({
					title : "关  联",
					shade : 0,
					type : 1, //页面层
					skin : 'layui-layer-rim', //加上边框
					area : [ '450px', '305px' ], //宽高
					content : $('#template1').html(),
					btn : [ '确定', '取消' ],
					btnAlign : 'c',
					yes : function(index, layero) {
						alert("确定");
					},
					btn2 : function(index, layero) {
						alert("取消");
					},
					success : function(layero, index) {
						console.log(index);
						$(layero).css("position", "initial");

						$("#associatedWindow").append(layero);
					}
				});
			};



		} ]);


		/*   var ModalInstanceCtrl = function($scope, items) {  
		       $scope.items = items;  
		       $scope.selected = {  
		           item : $scope.items 
		       };  
		       $scope.ok = function() {  
		           $modalInstance.close($scope.selected);  
		       };  
		       $scope.cancel = function() {  
		           $modalInstance.dismiss('cancel');  
		       };  
		   };  */

});