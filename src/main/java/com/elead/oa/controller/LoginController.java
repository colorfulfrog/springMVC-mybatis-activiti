package com.elead.oa.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.elead.oa.common.CommConstants;
import com.elead.oa.utils.HttpClientUtil;
import com.elead.oa.vo.User;
import com.elead.oa.vo.WSResult;

@RestController
@RequestMapping(value="/login",produces="application/json")
public class LoginController {
	
	@RequestMapping(value="/check",method=RequestMethod.POST)
	public WSResult<User> login(HttpServletRequest request,HttpServletResponse response,@RequestBody User user){
		WSResult<User> res = new WSResult<User>();
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("username", user.getUserName());
		params.put("password", user.getPassword());
		params.put("platform", "IOS");
		
		String result = HttpClientUtil.doPost(CommConstants.LOGIN_URL, params);
		JSONObject o = JSON.parseObject(result);
		int code = o.getIntValue("code");
		if(CommConstants.LOGIN_SUCCESS == code){ // 登录成功
			JSONObject userObj = o.getJSONObject("object");
			user.setUserId(userObj.getString("userid"));
			user.setUserName(userObj.getString("name"));
			user.setToken(userObj.getString("token"));
			user.setWorkNo(userObj.getString("work_no"));
			res.setCode(CommConstants.SUCCESS);
			res.setResult(user);
		}else{
			res.setCode(CommConstants.FAILURE);
			res.setMsg("登录失败！");
		}
		
		return res;
	}
}
