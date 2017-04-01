package com.elead.oa.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elead.oa.dao.UserDao;
import com.elead.oa.vo.PagedResult;
import com.elead.oa.vo.User;

@Service
public class UserService {
	// 日志对象
	private static final Logger LOGGER = LogManager.getLogger(UserService.class);
	
	@Autowired
	private UserDao userDao;

	public int addUser(User user) {
		return userDao.insert(user);
	}
	
	public int editUser(User user) {
		return userDao.updateByPrimaryKey(user);
	}

	public User getUserById(String userId) {
		return userDao.selectByPrimaryKey(userId);
	}

	/**
	 * 分页查询用户列表
	 * @param user 查询参数
	 * @param curPage 当前页，第一页传1
	 * @param pageSize 每页多少条
	 * @return
	 */
	public PagedResult<User> getAll(User user,int curPage,int pageSize){
		LOGGER.info("=========get params:=========curPage:" + curPage +",pageSize:" + pageSize + ",user:" + user);
		PagedResult<User> res = new PagedResult<User>();
		int total = userDao.allCount(user); // 总记录数
		List<User> all = userDao.all(user,(curPage-1)*pageSize,pageSize); //查询到的列表
		res.setTotal(total);
		res.setRows(all);
		return res;
	}
	
	public int batchDelUsers(List<String> ids){
		return userDao.batchDelUsers(ids);
	}
}
