package com.elead.oa.vo;

/**
 * Restful接口统一返回响应类
 * @author liwei
 *
 * @param <T>
 */
public class WSResult<T> {
	private int code; // 响应码，0：成功，1：失败，可自定义其他响应码
	private String msg; // 响应消息：一般设置失败原因等
	private T result; // 查询类接口设置查询结果列表
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public T getResult() {
		return result;
	}
	public void setResult(T result) {
		this.result = result;
	}
}
