package com.elead.oa.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.elead.oa.vo.User;

/**
 * 请求上下文，保存登录用户信息等
 * @author liwei
 *
 */
public class RequestContext {

	/**
	 * 线程本地变量
	 */
	private static final ThreadLocal<ConcurrentMap<String, Object>> local = new InheritableThreadLocal<ConcurrentMap<String, Object>>();
	
	/**
	 * 设置变量到上下文中
	 * @param key 键
	 * @param o 需要设置到上下文中的变量
	 */
	public static void setValue(String key,Object o){
		ConcurrentMap<String, Object> concurrentMap = local.get();
		if(null != concurrentMap){
			concurrentMap.put(key, o);
		}else{
			concurrentMap = new ConcurrentHashMap<String,Object>();
			concurrentMap.put(key, o);
			local.set(concurrentMap);
		}
	}
	
	/**
	 * 获取上下文中保存的变量
	 * @param key 键
	 * @return Object 从上下文取出的变量
	 */
	public static Object getValue(String key){
		return null!=local.get()?local.get().get(key) : null;
	}
	
	/**
	 * 获取当前登录用户信息
	 * @return user 登录用户信息
	 */
	public User getLoginUser(){
		User user = new User();
		Object value = getValue(CommConstants.CURRENT_LOGIN_USER);
		if(null != value){
			user = (User)value;
		}
		return user;
	}
}
