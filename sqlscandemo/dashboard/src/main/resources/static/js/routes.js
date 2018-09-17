define(['./app'],function(app){
	'use strict';
	return app.config(['$routeProvider', '$httpProvider', '$ocLazyLoadProvider',function($routeProvider, $httpProvider,$ocLazyLoadProvider) {
		  console.log("config router");
		  
		  $routeProvider.when('/', {
	            redirectTo: '/workTab'
	        });
        /**
		 * sql问题报表
         */
        $routeProvider.when('/SQLReport', {
            templateUrl: 'pages/SQLScan/SQLReport.html',
            controller: 'SQLReportCtrl'
        });
		  /**
		   * SQLScan--Rule配置
		   */
		  $routeProvider.when('/SQLRule', {
	            templateUrl: 'pages/SQLScan/SQLRule.html',
	            controller: 'SQLRuleCtrl'
	        });
        $routeProvider.when('/tempSQLRule', {
            redirectTo: '/SQLRule'
        });
		  /**
		   * SQLScan--Rule配置--新增Rule
		   */
		  $routeProvider.when('/SQLRule/addRule', {
	            templateUrl: 'pages/SQLScan/addRule.html',
	            controller: 'AddSQLRuleCtrl'
	        });
		  /**
			   * SQLScan--Rule配置--编辑Rule
		   */
			  $routeProvider.when('/SQLRule/addRule/:id', {
	            templateUrl: 'pages/SQLScan/addRule.html',
	            controller: 'AddSQLRuleCtrl'
	        });
		  
		  /**
		   * SQLScan--业务维护
		   */
		  $routeProvider.when('/businessRules', {
	            templateUrl: 'pages/SQLScan/businessRules.html',
	            controller: 'BusinessRulesCtrl',
              resolve:{}
		  });
		  
		  /**
		   * SQLScan--任务查看
		   */
		  $routeProvider.when('/versionMaintain', {
	            templateUrl: 'pages/SQLScan/versionMaintain.html',
	            controller: 'VersionMaintainCtrl'
	        });

		  $routeProvider.when('/tempVersionMaintain', {
              redirectTo: '/versionMaintain'
		  });
		  /**
		   * SQLScan--项目版本维护--新增项目版本
		   */
		  $routeProvider.when('/versionMaintain/addVersion', {
	            templateUrl: 'pages/SQLScan/addVersion.html',
	            controller: 'AddVersionCtrl'
	        });
		  
		  /**
		   * SQLScan--项目版本维护--编辑项目版本
		   */
		  $routeProvider.when('/versionMaintain/addVersion/:id', {
	            templateUrl: 'pages/SQLScan/addVersion.html',
	            controller: 'AddVersionCtrl'
	        });

		  /**
		   * SQLScan--项目执行管理--执行结果
		   */
		  $routeProvider.when('/taskMaintain/taskResult/:id', {
	            templateUrl: 'pages/SQLScan/taskResult.html',
	            controller: 'TaskResultCtrl'
	        });

          $routeProvider.when('/workTab', {
              templateUrl: 'pages/SQLScan/workTab.html',
              controller: 'WorkTabCtrl'
          });

          $routeProvider.when('/allBusinessRules', {
              templateUrl: 'pages/SQLScan/allBusinessRules.html',
              controller: 'AllBusinessRulesCtrl'
          });

		  $routeProvider.when('/tempBusiness', {
			  redirectTo: '/allBusinessRules'
		   });



        /**
         * Rule配置
         */
        $routeProvider.when('/configureRule', {
            templateUrl: 'pages/configure/configRule.html',
            controller: 'ConfigRuleCtrl'
        });

        /**
         * Rule配置--新增Rule
         */
        $routeProvider.when('/configureRule/addRule', {
            templateUrl: 'pages/configure/addRule.html',
            controller: 'AddRuleCtrl'
        });


        /**
         * Rule配置--修改Rule
         */
        $routeProvider.when('/configureRule/updateRule/:id', {
            templateUrl: 'pages/configure/addRule.html',
            controller: 'AddRuleCtrl'
        });

        /**
         * 表信息配置
         */
        $routeProvider.when('/configureTableInfo', {
            templateUrl: 'pages/configure/configureTableInfo.html',
            controller: 'ConfigureTableInfoCtrl'
        });

        /**
         * 表信息配置---新增
         */
        $routeProvider.when('/configureTableInfo/add', {
            templateUrl: 'pages/configure/addTableInfo.html',
            controller: 'AddTableInfoCtrl'
        });

        /**
         * 表信息配置---编辑
         */
        $routeProvider.when('/configureTableInfo/update/:id', {
            templateUrl: 'pages/configure/addTableInfo.html',
            controller: 'AddTableInfoCtrl'
        });

        /**
         * 字段信息配置
         */
        $routeProvider.when('/configFieldInfo', {
            templateUrl: 'pages/configure/configureFieldInfo.html',
            controller: 'ConfigureFieldInfoCtrl'
        });

        /**
         * 字段信息配置--新增
         */
        $routeProvider.when('/configFieldInfo/add', {
            templateUrl: 'pages/configure/addFieldInfo.html',
            controller: 'AddFieldCtrl'
        });


        /**
         * 字段信息配置--新增
         */
        $routeProvider.when('/configFieldInfo/update/:id', {
            templateUrl: 'pages/configure/addFieldInfo.html',
            controller: 'AddFieldCtrl'
        });

		  $routeProvider.otherwise({
	            redirectTo: '/'
	        });
		  
	}]);
	
});