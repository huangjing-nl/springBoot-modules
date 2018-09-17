define(['./module'], function (services) {
  'use strict';
  //version
  services.value('version', '1.0');

  services.factory('$config', function () {

    //var SERVER_NAME_SQLSCAN = 'http://106.39.223.129:48082';
    //var SERVER_NAME_SQLSCAN="http://10.88.150.52:8080";
    //var SERVER_NAME_SQLSCAN = "http://10.21.17.52:8082";
    var SERVER_NAME_SQLSCAN = "";
    //var SERVER_NAME_ODSSCAN = "http://10.88.150.52:8088";
    //var SERVER_NAME_ODSSCAN="http://10.21.17.52:7070";
    var SERVER_NAME_ODSSCAN="";
    var pathConfig = {
      base: SERVER_NAME_SQLSCAN,
      uri: {
        queryRuleConfig: SERVER_NAME_ODSSCAN + '/rule/listbypage',
        queryRuleConfigById: SERVER_NAME_ODSSCAN + '/rule/selectbyprimarykey',
        addRuleConfig: SERVER_NAME_ODSSCAN + '/rule/add',
        deleteRuleConfig: SERVER_NAME_ODSSCAN + '/rule/deletebyid',
        updateRuleConfig: SERVER_NAME_ODSSCAN + '/rule/update',


        queryTableInfo: SERVER_NAME_ODSSCAN + '/tablecfg/listbypage',
        queryTableInfoById: SERVER_NAME_ODSSCAN + '/tablecfg/selectbyprimarykey',
        updateTableInfo: SERVER_NAME_ODSSCAN + '/tablecfg/update',
        addTableInfo: SERVER_NAME_ODSSCAN + '/tablecfg/add',
        delecteTableInfo: SERVER_NAME_ODSSCAN + '/tablecfg/deletebyid',


        queryFileInfo: SERVER_NAME_ODSSCAN + '/fieldcfg/listbypage',
        queryFileInfoById: SERVER_NAME_ODSSCAN + '/fieldcfg/selectbyprimarykey',
        updateFileInfo: SERVER_NAME_ODSSCAN + '/fieldcfg/update',
        addFileInfo: SERVER_NAME_ODSSCAN + '/fieldcfg/add',
        delecteFileById: SERVER_NAME_ODSSCAN + '/fieldcfg/deletebyid',

        queryAllBusiness: SERVER_NAME_ODSSCAN + '/deptservice/listall',
        queryAllRuleName: SERVER_NAME_ODSSCAN + '/rule/listallrulename',

        importExcelFile: SERVER_NAME_ODSSCAN + '/fieldcfg/importfile'
      }

    };
    return pathConfig;

  });
});
