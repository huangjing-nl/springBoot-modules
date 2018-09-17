define([ '../module' ], function(controllers) {
    'use strict';
    controllers.controller("SQLReportCtrl", [ '$scope', '$http', '$location', '$timeout', '$route', '$rootScope','$chartOption','$config',function($scope, $http, $location, $timeout, $route, $rootScope,$chartOption,$config) {
        var echarts = require('echarts');
        /**
         * 创建图
         * @type {{ruleReport: ruleReport, topReport: topReport, businessReport: businessReport}}
         */
        var createBarGraph = echarts.init($(".createBarGraph")[0],'macarons');
        var createShuBarGraph = echarts.init($(".createBarRanking")[0],'macarons');
        var createPieGraph=echarts.init($(".createPieGraph")[0],'macarons');

        initDate();

        function initDate(){
            setDateFormat();//设置时间格式
            var report={"startTimeStr":$scope.startDate,"endTimeStr":$scope.endDate,"business":$scope.businessbTypeValue};
            queryBusinessData(report,initCallBackFun);
        }

        function initCallBackFun(response){
            $scope.businessbTypes=response.data.businesses;
            $scope.businessbTypeValue=$scope.businessbTypes[0];
            createChart.businessReport(response.data.businessReport);
            createChart.ruleReport(response.data.ruleReport);
            createChart.topReport(response.data.resultTop);
        }
        //改变业务类型
        $scope.changeBusinessType=function(){
            var report={"startTimeStr":$scope.startDate,"endTimeStr":$scope.endDate,"business":$scope.businessbTypeValue};
            queryBusinessData(report,changeBusinessTypeCallBackFun)
        };

        function changeBusinessTypeCallBackFun(response){
            createChart.ruleReport(response.data.ruleReport);//柱状图
        }

        //改变时间
        $scope.changeSetDateData=function(){
            var report={"startTimeStr":$scope.startDate,"endTimeStr":$scope.endDate,"business":$scope.businessbTypeValue};
            queryBusinessData(report,changeSetDateDataCallBackFun);
        };
        function changeSetDateDataCallBackFun(response){
            createChart.businessReport(response.data.businessReport);
            createChart.ruleReport(response.data.ruleReport);
            createChart.topReport(response.data.resultTop);
        }

        /**
         * 查询报表数据
         */
        function queryBusinessData(report,callBackFun) {
            $http({
                method : 'POST',
                url : $config.base + '/api/vi/report/produce',
                data : JSON.stringify(report)
            }).then(function successCallback(response) {
                console.log(response);
                response.data.businessReport.name="SQL问题--业务占比";
                response.data.ruleReport.title="SQL问题类型统计";
                response.data.resultTop.title="排行榜";
                callBackFun(response);
                /*createChart.businessReport(response.data.businessReport);
                createChart.ruleReport(response.data.ruleReport);
                createChart.topReport(response.data.resultTop);*/
            }, function errorCallback(response) {
                layer.alert('查询后台数据异常,请刷新重试', {
                    skin : 'layui-layer-lan', //样式类名
                    closeBtn : 0
                });
            });
        }


        var createChart={
            ruleReport:function(dataHBar){
                dataHBar.yAxis.company="个";
                var option= $chartOption.createHengBarOption(dataHBar);
                if (option && typeof option === "object") {
                    createBarGraph.setOption(option, true);
                }
            },
            topReport:function(dataSBar){
                var option=$chartOption.createShuBarOption(dataSBar);
                if (option && typeof option === "object") {
                    createShuBarGraph.setOption(option, true);
                }
            },

            businessReport:function (dataPie) {
                var reportMaps=dataPie.reportMaps;
                var legend=[];
                var series=[];
                for(var i=0;i<reportMaps.length;i++){
                    legend.push(reportMaps[i].key);
                    series.push({"name":reportMaps[i].key,"value":reportMaps[i].value})
                }
                dataPie.legend=legend;
                dataPie.series=series;

                var option= $chartOption.createPieOption(dataPie);
                if (option && typeof option === "object") {
                    createPieGraph.setOption(option, true);
                }
            }
        };

        $scope.$watch('endDate',function(newValue,oldValue){
            //console.log(newValue);
            $('.startDate').datetimepicker('setEndDate', newValue);
            if(compareDate($scope.startDate,newValue)){
                $scope.startDate=newValue;
            }
        });

            /**
         * 设置时间
         */
        function setDateFormat(){
            var preMonthDate=new Date();
            preMonthDate.setMonth(new Date().getMonth()-1);

            $(".startDate").datetimepicker({
                format: 'yyyy-mm-dd' ,
                minView: 'month',
                startView: 'month',
                endDate:new Date(),
                //maxView:new Date(),
                autoclose: true,
                language:  'zh-CN'
            });

            $(".endDate").datetimepicker({
                format: 'yyyy-mm-dd' ,
                minView: 'month',
                startView: 'month',
                endDate:new Date(),
                // maxView:new Date(),
                autoclose: true,
                language:  'zh-CN'
            });

            $scope.startDate=formatDate(preMonthDate);
            $scope.endDate=formatDate(new Date());
        }


       function formatDate(date) {
            var y = date.getFullYear();
            var m = date.getMonth() + 1;
            m = m < 10 ? '0' + m : m;
            var d = date.getDate();
            d = d < 10 ? ('0' + d) : d;
            return y + '-' + m + '-' + d;
        }

        function compareDate(dateStr1,datetr2){
            return ((new Date(dateStr1.replace(/-/g,"\/"))) > (new Date(datetr2.replace(/-/g,"\/"))));
        }


    }]);
});