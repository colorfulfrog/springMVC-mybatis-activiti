package com.elead.oa.common;

public interface CommConstants {
	int SUCCESS = 0; // 接口请求成功
	int FAILURE = 1; // 接口请求失败
	
	/**
	 * SSO登录地址
	 */
	String LOGIN_URL = "http://hr.e-lead.cn/sso/user/login"; 
	int LOGIN_SUCCESS = 1;  // 登录成功返回code
	String CURRENT_LOGIN_USER = "currentLoginUser"; // 当前登录用户
	String TOKEN = "token";
	String USER_ID = "userId"; // 用户ID
	String WORK_NO = "workNo"; // 工号
}
