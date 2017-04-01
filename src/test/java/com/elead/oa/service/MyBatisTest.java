package com.elead.oa.service;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.elead.oa.vo.PagedResult;
import com.elead.oa.vo.User;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml"})
public class MyBatisTest {

	//private static UserService userService;
	//private static ApplicationContext ac;
	
	@Autowired
	private UserService userService;

	/**
	 * 这个before方法在所有的测试方法之前执行，并且只执行一次 所有做Junit单元测试时一些初始化工作可以在这个方法里面进行
	 * 比如在before方法里面初始化ApplicationContext和userService
	 */
	/*@BeforeClass
	public static void before() {
		ac = new ClassPathXmlApplicationContext(new String[] { "applicationContext.xml"});
		// 从Spring容器中根据bean的id取出我们要使用的userService对象
		userService = (UserService) ac.getBean("userService");
	}*/

	@Test
	public void testAddUser() {
		User user = new User();
		user.setUserId(UUID.randomUUID().toString().replaceAll("-", ""));
		user.setUserName("白虎神皇xdp");
		user.setUserBirthday("2017-03-18");
		user.setUserSalary(10000D);
		userService.addUser(user);
	}
	
	@Test
	public void testGetAll(){
		PagedResult<User> all = userService.getAll(new User(),1,10);
		for (User user : all.getRows()) {
			System.out.println(user);
		}
	}

}
