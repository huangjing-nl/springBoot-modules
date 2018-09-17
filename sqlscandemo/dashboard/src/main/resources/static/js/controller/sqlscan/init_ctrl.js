/*$("#file-1").fileinput({
	        uploadUrl: '#', // you must set a valid URL here else you will get an error
	        allowedFileExtensions : ['jpg', 'png','gif'],
	        overwriteInitial: false,
	        maxFileSize: 1000,
	        maxFilesNum: 10,
	        dropZoneEnabled: true,//是否显示拖拽区域
	        //allowedFileTypes: ['image', 'video', 'flash'],
	        slugCallback: function(filename) {
	            return filename.replace('(', '_').replace(']', '_');
	        }
		});*/

		$(document).ready(function () {          
			$('#SQLruleTable').bootstrapTable({
				url:"data1.json",
				method: 'POST',                      //请求方式（*）
                //toolbar: '#toolbar',              //工具按钮用哪个容器
                striped: true,                      //是否显示行间隔色
                cache: false,                       //是否使用缓存，默认为true，所以一般情况下需要设置一下这个属性（*）
                pagination: true,                   //是否显示分页（*）
                sortable: true,                     //是否启用排序
                sortOrder: "asc",                   //排序方式
                sidePagination: "server",           //分页方式：client客户端分页，server服务端分页（*）
                pageNumber: 1,                      //初始化加载第一页，默认第一页,并记录
                pageSize: 3,                     //每页的记录行数（*）
                pageList: [10, 25, 50, 100],        //可供选择的每页的行数（*）
                search: false,                      //是否显示表格搜索
                strictSearch: true,
                showColumns: true,                  //是否显示所有的列（选择显示的列）
                showRefresh: true,                  //是否显示刷新按钮
                minimumCountColumns: 1,             //最少允许的列数
                clickToSelect: true,                //是否启用点击选中行
                height: 580,                      //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
                uniqueId: "id",                     //每一行的唯一标识，一般为主键列
                showToggle: true,                   //是否显示详细视图和列表视图的切换按钮
                cardView: false,                    //是否显示详细视图
                detailView: false,                  //是否显示父子表
                contentType:"application/x-www-form-urlencoded; charset=UTF-8",
              //得到查询的参数
                queryParams : function (params) {
                    //这里的键的名字和控制器的变量名必须一直，这边改动，控制器也需要改成一样的
                    var temp = {   
                        rows: params.limit,                         //页面大小
                        page: (params.offset / params.limit) + 1,   //页码
                        sort: 'id',      //排序列名  
                        sortOrder: params.order //排位命令（desc，asc） 
                        
                    };
                    return temp;
                },
				columns: [{
			        field: 'id',
			        title: '序号'
			    }, {
			        field: 'name',
			        title: '规则名称'
			    }, {
			        field: 'describe',
			        title: '规则描述'
			    },{
			        field: 'expression',
			        title: '规则表达式'
			    },{
			        field: 'checkLevel',
			        title: '规则校验级别'
			    },{
			        field: 'type',
			        title: '库类型'
			    },{
			        field: 'lastUpdateTime',
			        title: '最后修改时间'
			    },{
			        field: 'operation',
			        title: '操作',
			        formatter:function (value, row, index) {//value是这个字段的值
			        	var html = ' <ul class="operation"><li><a>查看</a></li><li><a>编辑</a></li><li><a>删除</a></li></ul>';  
                        //html += '　<a href="javascript:DeleteBook(' + value + ')">删除</a>';  
                        return html;
			        }
			    }]
		 });
      });  