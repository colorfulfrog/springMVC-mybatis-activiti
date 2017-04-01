package com.elead.oa.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml"})
public class ActivitiHelloTest {
	@Autowired
	private ProcessEngine processEngine;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private WorkflowService workflowService;
	
	@Autowired
	private IdentityService identityService;
	
	/**1、部署一个流程，会自动生成流程定义*/
	@Test
	public void deployProcessTest(){
		Deployment deployment = processEngine.getRepositoryService()//与流程定义和部署对象相关的Service
				.createDeployment()//创建一个部署对象
				.name("helloworld入门程序")//添加部署的名称
				.addClasspathResource("diagrams/HelloWorldProcess.bpmn")//从classpath的资源中加载，一次只能加载一个文件
				.addClasspathResource("diagrams/HelloWorldProcess.png")//从classpath的资源中加载，一次只能加载一个文件
				.deploy();//完成部署
		System.out.println("部署ID："+deployment.getId());//22501
		System.out.println("部署名称："+deployment.getName());//helloworld入门程序  
	}
	
	
	/**2、启动流程实例*/
	@Test
	public void startProcessInstance(){
		//流程定义的key
		String processDefinitionKey = "HelloWorld"; 
		Map<String,Object> variables = new HashMap<String,Object>(); //定义流程变量
		variables.put("userID", "001"); // 001：员工；002：项目经理；003：总经理
		ProcessInstance pi = processEngine.getRuntimeService()//与正在执行的流程实例和执行对象相关的Service
				//.startProcessInstanceByKey(processDefinitionKey, businessKey, variables) 可以传入businessKey，让流程关联业务
						.startProcessInstanceByKey(processDefinitionKey,variables);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，使用key值启动，默认是按照最新版本的流程定义启动
		
		System.out.println("流程实例ID:"+pi.getId());//流程实例ID    101
		System.out.println("流程定义ID:"+pi.getProcessDefinitionId());//流程定义ID   helloworld:1:4
	}
	
	/**3、查询当前人的个人任务*/
	@Test
	public void findMyPersonalTask(){
		String assignee = "001";
		List<Task> list = processEngine.getTaskService()//与正在执行的任务管理相关的Service
						.createTaskQuery()//创建任务查询对象
						.taskAssignee(assignee)//指定个人任务查询，指定办理人
						.list();
		if(list!=null && list.size()>0){
			for(Task task:list){
				System.out.println("########################################################");
				System.out.println("任务ID:"+task.getId());  //25005
				System.out.println("任务名称:"+task.getName());
				System.out.println("任务的创建时间:"+task.getCreateTime());
				System.out.println("任务的办理人:"+task.getAssignee());
				System.out.println("流程实例ID："+task.getProcessInstanceId());
				System.out.println("执行对象ID:"+task.getExecutionId());
				System.out.println("流程定义ID:"+task.getProcessDefinitionId());
				System.out.println("########################################################");
			}
		}
	}
	
	/**4、完成我的任务*/
	@Test
	public void completeMyPersonalTask(){
		//任务ID
		String taskId = "50003"; // 任务ID
		String comment = "感冒太严重，工作不了"; // 审批意见
		String curUserID = "001"; // 当前登录人，一般从session中获取
		String outGoing = "提交"; // 前台传过来下一步操作；提交、驳回
		String nextUserID = "002"; //下一任务处理人，即流程走给谁
		String processInstanceId = taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId();
		
		//1、添加审批意见
		Authentication.setAuthenticatedUserId(curUserID);
		taskService.addComment(taskId, processInstanceId, comment);
		
		//2、流程变量
		Map<String,Object> variables = new HashMap<String,Object>(); //定义流程变量
		
		//2、获取下一个环节连线，然后根据前台传过的的连线名称走流程,可以在查看任务时传给前台，前台动态生成按钮：提交、驳回等操作
		//List<String> outGoingList = workflowService.getOutGoingListByTaskId(taskId);
		if(StringUtils.isNotEmpty(outGoing) && !outGoing.equalsIgnoreCase("提交")) // 驳回，处理人指定为驳回节点的历史处理人
		{
			//根据连线名称获取下一步任务Key
			String targetTaskKey = workflowService.getTargetTaskId(taskId, outGoing);
			String historyAssignee = workflowService.getHistoryTaskByKey(processInstanceId, targetTaskKey);
			variables.put("userID", historyAssignee);
			variables.put("next", "no"); // 通过
		}else{
			//正常提交，指定处理人
			if(StringUtils.isNotEmpty(nextUserID)){
				variables.put("userID", nextUserID); // 001：员工；002：项目经理；003：总经理  
			}
			variables.put("next", "ok"); // 通过
		}
		
		processEngine.getTaskService()//与正在执行的任务管理相关的Service
					.complete(taskId,variables);
		
		//4、TODO 如果流程完成了，修改下业务的状态
		System.out.println("完成任务：任务ID："+taskId);
	}
	
	/**根据流程定义key查询正在运行的流程实例，不存在即说明流程已经完成*/
	@Test
	public void isProcessFinished(){
		String processDefinitionKey = "HelloWorld";
		ProcessInstance processInstance = processEngine.getRuntimeService()
					.createProcessInstanceQuery()
					.processDefinitionKey(processDefinitionKey)
					.singleResult();
		if(null == processInstance){
			System.out.println("流程已经结束！");
		}else{
			System.out.println("流程还未结束！");
		}
	}
	
	@Test
	public void userGroupTest(){
		//创建角色
		Group g1 = new GroupEntity();
		g1.setId("001");
		g1.setName("部门经理");
		g1.setType("test3");
		Group g2 = new GroupEntity();
		g2.setId("002");
		g2.setName("总经理");
		g2.setType("test4");
		
		identityService.saveGroup(g1); // 部门经理
		identityService.saveGroup(g2); // 总经理
		
		//创建用户
		User user1 = new UserEntity();
		user1.setId("0001");
		user1.setFirstName("三");
		user1.setLastName("张");
		user1.setEmail("zhangsan@163.com");
		user1.setPassword("1234567");
		User user2 = new UserEntity();
		user2.setId("0002");
		user2.setFirstName("四");
		user2.setLastName("李");
		user2.setEmail("lisi@163.com");
		user2.setPassword("1234567");
		
		identityService.saveUser(user1); // 张三
		identityService.saveUser(user2); // 李四
		//建立用户和角色的关联关系
		//identityService.deleteMembership("0001", "001");
		//identityService.deleteMembership("0002", "002");
		identityService.createMembership("0001", "001");
		identityService.createMembership("0002", "002");
	}
	
	/**
	 * 根据任务ID，查询任务的outGoing名称
	 */
	@Test
	public void getOutGoingListByTaskIdTest(){
		String taskId = "32503";
		List<String> list = workflowService.getOutGoingListByTaskId(taskId);
		System.out.println(list);
	}
	
	/**
	 * 查询批注
	 */
	@Test
	public void getComment(){
		String taskId = "35003";
		List<Comment> list = workflowService.getCommentByTaskId(taskId);
		for (Comment comment : list) {
			System.out.println("user:"+comment.getUserId() + ",time:"+comment.getTime()+",comment:"+comment.getFullMessage());
		}
	}
	
	@Test
	public void getTargetTaskID()
	{
		String taskId = "32503";
		String sequenceFlowName = "驳回";
		String targetTaskID = workflowService.getTargetTaskId(taskId,sequenceFlowName);
		System.out.println("#######################################TargetID:"+targetTaskID);
	}
	
	/**
	 * 根据流程实例ID，查询历史任务执行人，用于驳回时指定处理人
	 */
	@Test
	public void getHistoryTaskByKey(){
		System.out.println(workflowService.getHistoryTaskByKey("25001", "usertask3"));
	}
	
	@Test
	public void isFinished(){
		//流程定义的key
		String processDefinitionKey = "HelloWorld"; 
		System.out.println("############################## finished ? --> "+workflowService.isProcessFinishedByProcDefKey(processDefinitionKey));
	}
}
