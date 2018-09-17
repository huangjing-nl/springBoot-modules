define([
  'angular',
  './services/index',
  './controller/index',
  './ngAnimate',
  './ui-bootstrap-tpls',
  'angularRoute',
  'angularUiRouter',
  'angularCookies',
  'ocLazyLoad',
  'bootstrapSelect',
  'fileinput',
  'fileinputLocaleZh',
  'bootstrapTableMaster',
  './codemirror',
  './codemirrorSql',
  './codemirrorShowHint',
  './codemirrorSqlHint',
  'bootstrapTablezhCN',
  'bootstrap-datetimepicker',
  'bootstrap-datetimepickerzhCN',
  'echarts',
  'macarons'
], function (angular) {
  'use strict';
  console.log("app moudle");
  return angular.module('app', ["app.services", "app.controllers", "ngRoute", 'oc.lazyLoad', 'ngAnimate', 'ui.bootstrap', 'ui.router', 'ngCookies']);
});
