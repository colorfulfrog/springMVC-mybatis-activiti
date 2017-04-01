package com.elead.oa.controller.demo;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elead.oa.common.CommConstants;
import com.elead.oa.service.UserService;
import com.elead.oa.vo.PagedResult;
import com.elead.oa.vo.User;
import com.elead.oa.vo.WSResult;

@RestController
public class UserController {
	@Autowired
	private UserService userService;
	
	/**
	 * 分页查询用户列表
	 * @param user 查询参数
	 * @param curPage 当前页，第一页传1
	 * @param pageSize 每页多少条
	 * @return
	 */
	@RequestMapping(value="/list",produces="application/json")
	public PagedResult<User> getAll(User user,@RequestParam("page")int curPage,@RequestParam("rows")int pageSize){
		return userService.getAll(user,curPage,pageSize);
	}
	
	@RequestMapping(value="/single/{id}",method=RequestMethod.GET)
	public User getUserByID(@PathVariable("id") String userID)
	{
		return userService.getUserById(userID);
	}
	
	@RequestMapping(value="/user/add",method=RequestMethod.POST)
	public WSResult<String> addUser(@RequestBody User user){
		WSResult<String> res = new WSResult<String>();
		int count = userService.addUser(user);
		if(count > 0){
			res.setCode(CommConstants.SUCCESS);
			res.setMsg("添加用户成功.");
		}else{
			res.setCode(CommConstants.FAILURE);
			res.setMsg("添加用户失败！");
		}
		return res;
	}
	
	@RequestMapping(value="/user/edit",method=RequestMethod.PUT)
	public WSResult<String> editUser(@RequestBody User user){
		WSResult<String> res = new WSResult<String>();
		int count = userService.editUser(user);
		if(count > 0){
			res.setCode(CommConstants.SUCCESS);
			res.setMsg("修改用户成功.");
		}else{
			res.setCode(CommConstants.FAILURE);
			res.setMsg("修改用户失败！");
		}
		return res;
	}
	
	/**
	 * 根据ID批量删除用户
	 * @param idList 批量ID
	 * @return
	 */
	@RequestMapping(value="/user/del",method=RequestMethod.GET,produces="application/json")
	public WSResult<String> delUser(@RequestParam("ids") String ids){
		WSResult<String> res = new WSResult<String>();
		List<String> idList = Arrays.asList(ids.split(","));
		int count = userService.batchDelUsers(idList);
		if(count > 0){
			res.setCode(CommConstants.SUCCESS);
			res.setMsg("删除用户成功.");
		}else{
			res.setCode(CommConstants.FAILURE);
			res.setMsg("删除用户失败！");
		}
		return res;
	}
}
