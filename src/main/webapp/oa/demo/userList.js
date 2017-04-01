//页面加载  
$(document).ready(function(){ 
    loadGrid();  
}); 
var optType = "view"; // 操作类型，view为查看、edit为编辑
var editRow = "";// 定义当前编辑的行
function loadGrid() {
	var datagrid = $("#dataGrid").datagrid({
		title : "用户管理",
		border : false,
		locale : "zh_CN",
		//iconCls : 'icon-save',
		striped : true,
		sortOrder : "desc",
		collapsible : false,
		height: 500,
		url : App.baseURL+"/list", // 数据接口URL
		singleSelect : false, // 是否单选
		checkOnSelect:true,
		selectOnCheck:true,
		queryParams : {
			userName : $("#userName").val(),
			userId: $("#userId").val()
		},
		columns : [[
			{field : 'ck',checkbox : true,width : '50px',algin : 'center'}, 
			{field : 'userId',title : 'ID',width : '5%',algin : 'center'},
			{field : 'userName',title : '用户名',width : '10%',align : 'center'}, 
			{field : 'userBirthday',title : '生日',width : '25%',align : 'center'}, 
			{field : 'userSalary',title : '薪水',width : '10%',align : 'center'}
			/*{field : "operateID",title : '操作',width : '27%',align : 'center',
				formatter : function(value, rowData, rowIndex) {
					return "a";
				}
			}*/]],
		toolbar : [ {
			id : 'btnAdd',
			text : "添加",
			iconCls : 'icon-add',
			handler : function() {
				openAddUserDialog();
			}
		},'-', {
			id : 'btnEdit',
			text : "修改",
			iconCls : 'icon-edit',
			handler : function() {
				openEditOrViewUserDialog("edit");
			}
		},'-', {
            id: 'btnDelete',
            text: '删除',
            iconCls: 'icon-remove',
            handler: function () {
                delUser();//实现直接删除数据的方法
            }
        }, '-', {
            id: 'btnView',
            text: '查看',
            iconCls: 'icon-table',
            handler: function () {
            	openEditOrViewUserDialog("view");//实现查看记录详细信息的方法
            }
        }, '-', {
            id: 'btnReload',
            text: '刷新',
            iconCls: 'icon-reload',
            handler: function () {
                $("#dataGrid").datagrid("reload");
            }
        } ],
		pagination : true,// 表示在datagrid设置分页
		rownumbers : true // 是否显示行号
	});

	$('#dataGrid').datagrid('getPager').pagination({
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
	var queryParameter = $('#dataGrid').datagrid("options").queryParams;
	queryParameter.userName = $("#userName").val();
	queryParameter.userId = $("#userId").val();
	$("#dataGrid").datagrid("reload");
};

//打开添加用户窗口
function openAddUserDialog(){
	$("#addUserWin").dialog({
        title: '添加用户',
        href: 'addUser.html',
        width:'400',
        height:'300',
        iconCls: 'icon-add',
        modal: true,
        closed: false
        /*buttons: [{
			text:'Ok',
			iconCls:'icon-ok',
			handler:function(){
				App.Module.submitForm();
			}
		},{
			text:'Cancel',
			handler:function(){
				alert('cancel');;
			}
		}]*/
    });
};

//打开查看用户窗口
function openEditOrViewUserDialog(type){
	optType = type;
	//获得所有选中的行数据数组
	var selrows = $("#dataGrid").datagrid("getSelections");
	if(selrows.length > 1){
		$.messager.alert("错误","请选中一条记录进行操作！","error");
		return false;
	}else{
		var icon = "icon-table"; //dialog图标
		var title = "查看用户"; // dialog标题
		if("edit"==optType){
			icon = "icon-edit";
			title = "编辑用户";
		}
		$("#editOrViewUserWin").dialog({
			title: title,
			href: 'editOrViewUser.html',
			width:'400',
			height:'300',
			iconCls: icon,
			modal: true,
			closed: false
		});
	}
};

//删除用户
function delUser(){
	//获得所有选中的行数据数组
	var selrows = $("#dataGrid").datagrid("getSelections");
	if(selrows.length <= 0){
		$.messager.alert("错误","请至少选中一条记录进行删除操作！","error");
		return false;
	}else{
		//二次确认
		$.messager.confirm('确认','您确认想要删除记录吗？',function(r){    
		    if (r){   
		    	var ids = ""; // 获取ID拼接成字符串，多个以“，”隔开
				$.each(selrows,function(index,data){
					if(ids == ""){
						ids = data.userId;
					}else{
						ids += "," + data.userId;
					}
				});
		    	$.ajax({
				    url: App.baseURL+'/user/del?ids='+ids,
				    data: ids,
				    type: 'GET',
				    success: function(result) {
				    	if(0==result.code){
				     		// 操作成功提示框，3秒后自动消失
					     	$.messager.show({
								title:'提示',
								msg:'删除用户成功.',
								showType:'fade',
								timeout:3000,
								style:{
									right:'',
									top:document.body.scrollTop+document.documentElement.scrollTop,
									bottom:''
								}
							});
				     		// 刷新列表
				     		$("#dataGrid").datagrid("reload");
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