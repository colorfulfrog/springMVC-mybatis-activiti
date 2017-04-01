package com.elead.oa.intercepter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.elead.oa.common.CommConstants;
import com.elead.oa.common.RequestContext;
import com.elead.oa.utils.HttpClientUtil;
import com.elead.oa.vo.User;

@Component
public class LoginIntercepter implements HandlerInterceptor {
	@Value("${token_verify_url}")
	private String tokenVerifyUrl; //token校验URL
	private List<String> excludes;
	
	@Value("${redirect_url}")
	private String loginUrl; //重定向到登录页面
	
	public List<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		boolean isLogin = false;
		String requestURI = request.getRequestURI();
		requestURI = requestURI.substring(requestURI.lastIndexOf("/") + 1);
		if(excludes.contains(requestURI)){
			return true;
		}
	
		Cookie[] cookies = request.getCookies();
		if(null != cookies){
			User user = new User();
			Map<String,Object> params = new HashMap<String,Object>();
			for (Cookie cookie : cookies) {
				String name = cookie.getName();
				String value = cookie.getValue();
				if(name.equals(CommConstants.TOKEN)){
					user.setToken(value);
					params.put(CommConstants.TOKEN, value);
					String userName = HttpClientUtil.doPost(tokenVerifyUrl, params);
					if(StringUtils.isNotEmpty(userName)){
						isLogin = true;
					}
				}else if(name.equals(CommConstants.USER_ID)){
					user.setUserId(value);
				}else if(name.equals(CommConstants.WORK_NO)){
					user.setWorkNo(value);
				}
			}
			RequestContext.setValue(CommConstants.CURRENT_LOGIN_USER, user);
		}else{ // 没有token，重定向到登录页面
			response.sendRedirect(loginUrl);
			return false;
		}
		
		// 判断是否登录
		if(isLogin){
			return true;
		}else{
			response.sendRedirect(loginUrl);
			return false;
		}
	}

}
