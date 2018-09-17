define([
    'require',
    'jquery',
    'layer',
    'bootstrap',
    'angular',
    'base64',
    'app',
    'routes'
], function (require, $, layer,bootstrap,angular) {
    'use strict';
    console.log("load bs");
   require(['domReady!'], function (document) {

        $('#mainWindow').height($('#mainContent').height());
        
        Array.prototype.remove = function(val) {
        	var index = this.indexOf(val);
        	if (index > -1) {
        	this.splice(index, 1);
        	}
        };
        angular.bootstrap(document, ['app']);
    });

});
