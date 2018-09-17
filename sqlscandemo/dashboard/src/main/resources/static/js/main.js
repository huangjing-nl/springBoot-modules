'use strict';

require.config({
  baseUrl: "js/",
  paths: {//用于映射不存在根路径下面的模块路径。
    'domReady': 'lib/domReady/domReady',
    'angular': 'lib/angular/angular',
    'angularUiRouter': 'lib/angular-ui-router',
    'ocLazyLoad': 'lib/ocLazyLoad.min',
    'angularRoute': 'lib/angular-route/angular-route',
    'angularCookies':'lib/angular-cookies/angular-cookies',
    'ngAnimate': 'lib/angular-animate/angular-animate',
    'jquery': 'lib/jquery/dist/jquery',
    'base64': 'lib/jquery.base64',
    'layer': 'lib/layer-v3.1.0/layer/layer',
    'layerui': 'lib/layui-v2.2.2/layui/layui',
    'bootstrap': 'lib/bootstrap-3.3.7-dist/js/bootstrap.min',
    'fileinput': 'lib/bootstrap-fileinput-master/js/fileinput',
    'fileinputLocaleZh': 'lib/bootstrap-fileinput-master/js/zh',
    'ui-bootstrap-tpls': 'lib/bootstrap/dist/js/ui-bootstrap-tpls-2.5.0.min',
    'bootstrapTableMaster': 'lib/bootstrap-table-master/dist/bootstrap-table',
    'bootstrapTablezhCN': 'lib/bootstrap-table-master/dist/locale/bootstrap-table-zh-CN.min',
    'bootstrapSelect': 'lib/bootstrap-select-master/dist/js/bootstrap-select.min',
    'codemirror': 'lib/codemirror-5.32.0/lib/codemirror',
    'codemirrorSql': 'lib/codemirror-5.32.0/mode/sql/sql',
    'codemirrorShowHint': 'lib/codemirror-5.32.0/addon/hint/show-hint',
    'codemirrorSqlHint': 'lib/codemirror-5.32.0/addon/hint/sql-hint',
    'bootstrap-datetimepicker': 'lib/bootstrap-datetimepicker/js/bootstrap-datetimepicker',
    'bootstrap-datetimepickerzhCN': 'lib/bootstrap-datetimepicker/js/locales/bootstrap-datetimepicker.zh-CN',
    'echarts': 'lib/echarts/echarts',
    'macarons': 'lib/echarts/theme/macarons'
  },
  shim: {
    'angular': {
      deps: ['jquery'],
      exports: 'angular'
    },
    'angularRoute': {
      deps: ['angular'],
      exports: 'angularRoute'
    },
    'angularCookies': {
      deps: ['angular'],
      exports: 'angularCookies'
    },
    'angularUiRouter': {
      deps: ['angular'],
      exports: 'angularUiRouter'

    },
    'ocLazyLoad': {
      deps: ['angular'],
      exports: 'ocLazyLoad'

    },
    'ngAnimate': {
      deps: ['angular'],
      exports: 'ngAnimate'
    },
    'jquery': {
      exports: 'jquery'
    },
    'base64': {
      exports: 'base64',
      deps: ['jquery']
    },
    'layer': {
      exports: 'layer',
      deps: ['jquery']
    },
    'layerui': {
      exports: 'layerui',
      deps: ['jquery']
    },
    'bootstrap': {
      exports: 'bootstrap',
      deps: ['jquery']
    },
    'fileinput': {
      exports: 'fileinput',
      deps: ['bootstrap']

    },
    'fileinputLocaleZh': {
      exports: 'fileinputLocaleZh',
      deps: ['fileinput']
    },
    'bootstrapTableMaster': {

      exports: 'bootstrapTableMaster',
      deps: ['bootstrap']

    },
    'bootstrapTablezhCN': {
      exports: 'bootstrapTablezhCN',
      deps: ['jquery', 'bootstrapTableMaster']

    },
    "bootstrapSelect": {
      exports: 'bootstrapSelect',
      deps: ['jquery']
    },
    "ui-bootstrap-tpls": {
      deps: ['angular']
    },
    "codemirror": {
      exports: 'codemirror',
      deps: []
    },
    "codemirrorSql": {
      deps: ['codemirror'],
      exports: 'codemirrorSql'
    },
    "codemirrorShowHint": {
      deps: ['codemirror'],
      exports: 'codemirrorShowHint'
    },
    "codemirrorSqlHint": {
      deps: ['codemirror', 'codemirrorSql'],
      exports: 'codemirrorSqlHint'
    },
    "bootstrap-datetimepicker": {
      exports: 'bootstrap-datetimepicker',
      deps: ['jquery']
    },
    "bootstrap-datetimepickerzhCN": {
      exports: 'bootstrap-datetimepickerzhCN',
      deps: ['jquery', 'bootstrap-datetimepicker']
    }
  },

  deps: ['browser']

});