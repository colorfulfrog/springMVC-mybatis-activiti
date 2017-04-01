package com.elead.oa.dao;

import java.util.List;

import com.elead.oa.vo.User;

public interface UserDao {
    int deleteByPrimaryKey(String userId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(String userId);

    int updateByPrimaryKeySelective(User record);

    /**
     * 修改用户
     * @param record
     * @return
     */
    int updateByPrimaryKey(User record);
    
    /**
	 * 分页查询用户列表
	 * @param user 查询参数
	 * @param curPage 当前页，第一页传1
	 * @param pageSize 每页多少条
	 * @return
	 */
    List<User> all (User user,int curPage,int pageSize);
    
    /**
     * 查询总记录数，跟all()成对出现
     * @param user 查询条件
     * @return 总记录数
     */
    int allCount (User user);
    
    /**
     * 批量删除用户
     * @param ids
     * @return
     */
    int batchDelUsers(List<String> ids);
}
