define(['./module'], function (services) {
    services.factory('$chartOption', function(){
        var echarts = require('echarts');
        return {
            createPieOption:createPieOption,
            createHengBarOption:createHengBarOption,
            createShuBarOption:createShuBarOption

        };
        function createHengBarOption(dataBar){
            var option = null;
            option = {
                color: ['#8EC9EB'],
                textStyle:{
                    color:'#737373'
                },
                title: {
                    text: dataBar.title,
                    textStyle:{
                       // 'color':'#666666'
                    },
                    //left: 'left',
                    top:5,
                    left:5
                },
                grid: {
                    bottom:80
                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'line',
                        lineStyle: {
                          //  color: '#cccccc',
                          //  type:"dashed"//dotted
                        },
                        label:{
                            backgroundColor:"red"//坐标提示
                        },
                        textStyle:{

                        }

                    },
                    textStyle:{
                       /* color:*/
                    }
                },
                toolbox: {//工具栏组件
                    feature: {
                       // dataView: {show: true, readOnly: false},
                        magicType: {show: true, type: ['line', 'bar']},
                        //restore: {show: true},
                        saveAsImage: {show: true}
                    }
                },
                xAxis: //直角坐标系 grid 中的 x 轴，一般情况下单个 grid 组件最多只能放上下两个 x 轴，多于两个 x 轴需要通过配置 offset 属性防止同个位置多个 x 轴的重叠。
                    {
                        //show:false,
                        type: 'category',//类目轴，适用于离散的类目数据，为该类型时必须通过 data 设置类目数据。
                       /* name: dataBar.xAxis.name,
                        nameTextStyle:{
                            color:'#37c47a'//绿色
                        },*/
                        data: dataBar.xAxis.xKeys,
                        /*axisPointer: {
                            type: 'shadow'
                        },*/
                        axisLabel:{
                            formatter: '{value}',
                           /* formatter: function (value, index) {
                                var str = value.substring(0,value.lastIndexOf('规则'));
                                return str;
                            },*/
                            /*color:'#737373',*/
                            /*verticalAlign:'top',
                            align:'left',
                            inside:true,*/
                            interval:0,
                            rotate:-20
                        },
                        axisLine:{
                          //  lineStyle:
                        }
                    },
                yAxis:
                    {
                        type: 'value',//数值轴，适用于连续数据
                        //name: dataBar.yAxis.name,
                        //min: 0,
                        //max: 250,
                        //interval: 50,
                        axisLabel: {
                            formatter: '{value}'+dataBar.yAxis.company,
                           /* color:'#737373'*/
                        }
                    },
                series: [
                    {
                        name:dataBar.yAxis.name,
                        type:'bar',
                        barWidth:'50%',
                       /* barGap:'100%',*/
                        /*barCategoryGap:"50%",*/
                        data:dataBar.yAxis.yValues,
                        label: {
                            normal: {
                                show: true,
                                position: 'top',
                                offset: [0, -2],
                                textStyle: {
                                    color: '#F68300',
                                    fontSize: 12
                                }
                            }
                        }
                    }
                    ]
            };
            return option;
        }

        function createPieOption(dataPie) {
            var option = null;
            option= {
                title : {
                    text: dataPie.name,
                    textStyle:{
                        'color':'#555',
                        "fontFamily":"Arial"
                    },
                    top:5,
                    left:5
                },
                tooltip : {
                    //show:false,
                    trigger: 'item',
                    formatter: "{b} <br/> 个数：{c} ({d}%)"
                },
                grid: {
                    //top:50,
                    bottom: 10
                    //left: '20%',
                    // right: 0,
                    //containLabel: true
                },
                /*legend: {
                    orient: 'vertical',
                    left: 'right',
                    data: dataPie.legend
                },*/
                series : [
                    {
                        name: '访问来源',
                        type: 'pie',
                        radius: ['50%', '75%'],
                        //center: ['50%', '60%'],
                        data:dataPie.series,
                        //roseType: 'angle',
                        itemStyle: {
                            normal: {
                                shadowBlur: 20,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        },
                        label:{
                            position:'outside',
                            //formatter:"{b} : {c} ({d}%)"
                            formatter:"{b} : {c} ({d}%)"
                        },
                        labelLine:{
                            length:10,
                            length2:5
                        }
                    }
                ]
            };

            return option;
        }


        function createShuBarOption(dataSBar){
            if(dataSBar.yAxis.yValues==null){
                dataSBar.yAxis.yValues=[];
            }
            if(dataSBar.xAxis.xKeys==null){
                dataSBar.xAxis.xKeys=[];
            }
            var option = null;
            option = {
                color: ['#8EC9EB'],
                textStyle:{
                    color:'#737373'//坐标轴的字体

                },
                title: {
                    text: dataSBar.title,
                    textStyle:{
                        'color':'#555',
                        "fontFamily":"Courier New"
                    },
                    top:5,
                    left:5
                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'line',
                        lineStyle: {
                           // color: '#cccccc',
                            //type:"dashed"//dotted
                        },
                        label:{
                            backgroundColor:"#666666"//坐标提示
                        }
                    }

                },
                /*legend: {
                    data: ['']
                },*/
                grid: {
                    top:30,
                    bottom: 15,
                    left: 15,
                   // right: 0,
                    containLabel: true
                },
                xAxis: {
                    show:false,
                    type: 'value',
                    axisLabel:{
                       /* color:'#737373'*/
                      //  borderRadius:
                    }
                    //boundaryGap: [0, 0.01]
                },
                yAxis: {
                    type: 'category',
                    data: dataSBar.yAxis.yValues.reverse(),
                    axisLabel:{
                       /* color:'#737373'*/
                       /* rotate: 10*/

                    },
                    axisLine:{//坐标轴轴线
                        show:false
                    },
                    axisTick:{//坐标轴刻度
                        show:false
                    }
                },
                series: [{
                        name: '个数',
                        type: 'bar',
                        barWidth:10,
                        data: dataSBar.xAxis.xKeys.reverse(),
                        label: {
                            normal: {
                                show: true,
                                position: 'right',
                                offset: [0, -2],
                                textStyle: {
                                    color: '#F68300',
                                    fontSize: 12
                                }
                            }
                        },
                        itemStyle: {
                            normal: {
                                ////（顺时针左上，右上，右下，左下）
                                barBorderRadius:[10, 10, 10,10],
                                color: new echarts.graphic.LinearGradient(
                                    0, 0, 1, 0,//0,0,0,1可以从上到下渐变
                                    [
                                        {offset: 0, color: '#33CCFF'},
                                        {offset: 0.5, color: '#33FFFF'},
                                        {offset: 1, color: '#8EC9EB'}

                                    ]
                                )
                            },
                            emphasis: {
                                barBorderRadius:[10, 10, 10, 10]
                            }
                        }
                    }]
            };

            return option;
        }

    });
});