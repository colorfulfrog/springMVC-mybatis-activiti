//页面加载  
$(document).ready(function(){ 
    loadGrid();  
}); 
var optType = "view"; // 操作类型，view为查看、edit为编辑
var editRow = "";// 定义当前编辑的行
function loadGrid() {
	var datagrid = $("#deploymentDataGrid").datagrid({
		title : "流程部署列表",
		border : false,
		locale : "zh_CN",
		//iconCls : 'icon-save',
		striped : true,
		sortOrder : "desc",
		collapsible : false,
		height: 500,
		url : App.baseURL+"/workflow/processDefinition/list", // 数据接口URL
		singleSelect :true, // 是否单选
		checkOnSelect:true,
		selectOnCheck:true,
		queryParams : {
			name : $("#processDefinitionName").val()
		},
		columns : [[
			{field : 'ck',checkbox : true,width : '50px',algin : 'center'}, 
			{field : 'id',title : 'ID',width:"150px",algin : 'left'},
			{field : 'name',title : '流程定义名称',width : '200px',align : 'left'}, 
			{field : 'key',title : '流程定义Key',width : '150px',align : 'left'}, 
			{field : 'version',title : '版本',width : '80px',align : 'center'}, 
			{field : 'resourceName',title : '流程定义文件',width : '250px',align : 'center'}, 
			{field : 'diagramResourceName',title : '流程图',width : '250px',align : 'center'}, 
			{field : 'deploymentId',title : '流程部署ID',width : '150px',align : 'center'}, 
			{field : 'description',title : '描述',width : '200px',align : 'center'}
			/*{field : "operateID",title : '操作',width : '27%',align : 'center',
				formatter : function(value, rowData, rowIndex) {
					return "a";
				}
			}*/]],
		toolbar : [ {
			id : 'btnAdd',
			text : "部署",
			iconCls : 'icon-add',
			handler : function() {
				openDeployDialog();
			}
		},/*'-', {
			id : 'btnEdit',
			text : "修改",
			iconCls : 'icon-edit',
			handler : function() {
				openEditOrViewUserDialog("edit");
			}
		},*/'-', {
            id: 'btnDelete',
            text: '删除',
            iconCls: 'icon-remove',
            handler: function () {
                delProcess();//实现直接删除数据的方法
            }
        }, /*'-', {
            id: 'btnView',
            text: '查看',
            iconCls: 'icon-table',
            handler: function () {
            	openEditOrViewUserDialog("view");//实现查看记录详细信息的方法
            }
        },*/ '-', {
            id: 'btnReload',
            text: '刷新',
            iconCls: 'icon-reload',
            handler: function () {
                $("#deploymentDataGrid").datagrid("reload");
            }
        } ],
		pagination : true,// 表示在datagrid设置分页
		rownumbers : true // 是否显示行号
	});

	$('#deploymentDataGrid').datagrid('getPager').pagination({
		pageSize : 10,
		pageNumber : 1,
		pageList : [ 10, 20, 30, 40, 50 ],
		beforePageText : '第',// 页数文本框前显示的汉字
		afterPageText : '页    共 {pages} 页',
		displayMsg : '当前显示 {from} - {to} 条记录   共 {total} 条记录',
	});
};

// 条件查询
function Search() {
	var queryParameter = $('#deploymentDataGrid').datagrid("options").queryParams;
	queryParameter.name = $("#processDefinitionName").val();
	$("#deploymentDataGrid").datagrid("reload");
};

//打开添加用户窗口
function openDeployDialog(){
	$("#deployWin").dialog({
        title: '流程部署',
        href: 'addProcessDeploy.html',
        width:'400',
        height:'300',
        iconCls: 'icon-add',
        modal: true,
        closed: false
    });
};

//删除用户
function delProcess(){
	//获得所有选中的行数据数组
	var selrows = $("#deploymentDataGrid").datagrid("getSelections");
	if(selrows.length <= 0){
		$.messager.alert("错误","请至少选中一条记录进行删除操作！","error");
		return false;
	}else{
		//二次确认
		$.messager.confirm('确认','您确认想要删除记录吗？',function(r){    
		    if (r){   
		    	$.ajax({
				    url: App.baseURL+'/workflow/processDefinition/del?id='+selrows[0].deploymentId,
				    //data: id,
				    type: 'GET',
				    success: function(result) {
				    	if(0==result.code){
				     		// 操作成功提示框，3秒后自动消失
					     	$.messager.show({
								title:'提示',
								msg:'删除流程成功.',
								showType:'fade',
								timeout:3000,
								style:{
									right:'',
									top:document.body.scrollTop+document.documentElement.scrollTop,
									bottom:''
								}
							});
				     		// 刷新列表
				     		$("#deploymentDataGrid").datagrid("reload");
				     	}else{
				     		//返回码不是0，显示失败原因
				     		$.messager.alert("错误",data.msg,"error");
				     	}
				    }
				});   
		    }    
		});  
	}
}