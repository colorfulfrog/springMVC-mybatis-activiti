var App = {};
// 当前环境，取值：local、dev、sit、uat、product
App.profile = "local";
App.Module = {};

function baseURL(){
	var baseURL = "http://localhost:8080/elead-oa";
	if("local" === App.profile){
		return baseURL;
	}else if("dev" === App.profile){
		baseURL = "http://localhost:8080/elead-oa";
		return baseURL;
	}else if("sit" === App.profile){
		baseURL = "http://localhost:8080/elead-oa";
		return baseURL;
	}else if("uat" === App.profile){
		baseURL = "http://localhost:8080/elead-oa";
		return baseURL;
	}else if("product" === App.profile){
		baseURL = "http://hr.e-lead.cn:8080/elead-oa";
		return baseURL;
	}
};

// 获取From表单数据并转化为json对象
function getFormJson(formID){
	var paramObj = {};
    var t = $('#'+formID).serializeArray();
    $.each(t, function() {
    	paramObj[this.name] = this.value;
    });
    console.log("===========================表单："+formID+"数据为："+JSON.stringify(paramObj));
    return JSON.stringify(paramObj);
}

//格式化日期
function myformatter(date){
	var y = date.getFullYear();
	var m = date.getMonth()+1;
	var d = date.getDate();
	return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);
}

//解析
function myparser(s){
	if (!s) return new Date();
	var ss = (s.split('-'));
	var y = parseInt(ss[0],10);
	var m = parseInt(ss[1],10);
	var d = parseInt(ss[2],10);
	if (!isNaN(y) && !isNaN(m) && !isNaN(d)){
		return new Date(y,m-1,d);
	} else {
		return new Date();
	}
}

$.fn.formfill = function(jsonObj) {
	for ( var o in jsonObj) {
		debugger;
		// alert(kvs.length);
		var k = o;
		var v = jsonObj[o];
		selector = "[name='" + k + "']";
		// alert(selector);
		// alert($(selector).length);
		if ($(selector).length > 0) {
			for (j = 0; j < $(selector).length; j++) {
				// text or password
				if ($(selector).attr("type") == "text"
						|| $(selector).attr("type") == "password") {
					$(selector).val(v);
				}
				// combo select
				if ($(selector).attr("type") == null && $(selector).length == 1) {
					if ($("select" + selector).length == 1) {
						for (n = 0; n < $(selector + " option").length; n++) {
							// alert(n+":"+$(selector+"
							// option:eq("+n+")").val()+"["+$(selector+"
							// option:eq("+n+")").html()+"]==?=="+v);
							if ($(selector + " option:eq(" + n + ")").val() == v) {
								$(selector + " option:eq(" + n + ")").attr(
										"selected", true);
								break;
							}
						}
						;
					}
				}
				// checkbox
				if ($(selector).attr("type") == "checkbox") {
					var checkboxselector = "input[type='checkbox'][name='" + k
							+ "']";
					var options = v.split('|');
					for (m = 0; m < options.length; m++) {
						for (k = 0; k < $(checkboxselector).length; k++) {
							if ($(checkboxselector + ":eq(" + k + ")").val() == options[m]) {
								$(checkboxselector + ":eq(" + k + ")").attr(
										"checked", true);
							}
						}
					}
				}
				// readio
				if ($(selector).attr("type") == "radio") {
					var radioselector = "input[type='radio'][name='" + k + "']";
					for (k = 0; k < $(radioselector).length; k++) {
						if ($(radioselector + ":eq(" + k + ")").val() == v) {
							$(radioselector + ":eq(" + k + ")").attr("checked",
									true);
						}
					}
				}

				// textarea
				if ($("textarea[name='" + k + "']").length == 1) {
					$("textarea[name='" + k + "']").val(v);
				}

			}
		}

	}
} 

/**
 * 设置本地缓存
 * @param key 键
 * @param value 值
 * @returns
 */
function setStorage(key,value){
	localStorage.setItem(key,value);
}

/**
 * 获取本地缓存
 * @param key 键
 * @returns 从本地缓存中获取的缓存
 */
function getStorage(key){
	return localStorage.getItem(key);
}

/**
 * 获得当前登录用户
 * @returns User
 */
function getLoginUser(){
	var userStr = getStorage("LoginUser");
	return JSON.parse(userStr);
}

//加密
function encrypt(pwd){
	var publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC5G9R5HzQA5YLtxNhNyTX7bduDFVE+RZvHASc5lCYl5SRclyt0TTiDnvc8l5v4lYXruf38IARMri6P5oR5zfhf1lT/AnOhmA/2NNGfXJo4Nx3j3Msg/0eTklFqDyVDtb8yW/5h9HkGe0PSYuSw9k1PJz386eyZhC/2tp8T+61e8wIDAQAB" ;
	var encrypt = new JSEncrypt();
    encrypt.setPublicKey(publicKey);
    return encrypt.encrypt(pwd);
}

$(function(){
	App.baseURL = baseURL();
	console.log("---------------------------current environment is : " + App.profile);
	console.log("---------------------------baseURL : " + App.baseURL);
});

