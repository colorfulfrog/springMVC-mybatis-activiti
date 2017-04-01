package com.elead.oa.controller.workflow;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.elead.oa.common.CommConstants;
import com.elead.oa.common.RequestContext;
import com.elead.oa.service.WorkflowService;
import com.elead.oa.vo.PagedResult;
import com.elead.oa.vo.WSResult;

@RestController
@RequestMapping(value="/workflow",produces="application/json")
public class WorkflowController {
	private static final Logger LOGGER = LogManager.getLogger(WorkflowController.class);
	@Autowired
	private WorkflowService workflowService;
	
	/**
	 * 部署流程
	 * @param multiFile 流程定义zip文件，包含：*.bpmn/*.png
	 * @return WSResult<String> 部署是否成功
	 */
	@RequestMapping(value="/deploy",method=RequestMethod.POST)
	public WSResult<String> fileUpload(@RequestParam("file") MultipartFile multiFile){
		WSResult<String> res = new WSResult<String>();
		if(!multiFile.isEmpty()){
			try {
				Deployment deploy = workflowService.deploy(multiFile.getInputStream(), multiFile.getOriginalFilename());
				if(null != deploy)
				{
					res.setCode(CommConstants.SUCCESS);
				}
			} catch (FileNotFoundException e) {
				LOGGER.error("process deploy error{}",e.toString());
				res.setCode(CommConstants.FAILURE);
				res.setMsg(e.toString());
			} catch (IOException e) {
				LOGGER.error("process deploy error{}",e.toString());
				res.setCode(CommConstants.FAILURE);
				res.setMsg(e.toString());
			}
		}
		return res;
	}
	
	/**
	 * 分页查询流程定义列表
	 * @param user 查询参数
	 * @param curPage 当前页，第一页传1
	 * @param pageSize 每页多少条
	 * @return
	 */
	@RequestMapping(value="/processDefinition/list")
	public PagedResult<com.elead.oa.vo.workflow.ProcessDefinition> getProcessDefinitionList(com.elead.oa.vo.workflow.ProcessDefinition procDef,@RequestParam("page")int curPage,@RequestParam("rows")int pageSize){
		PagedResult<com.elead.oa.vo.workflow.ProcessDefinition> res = new PagedResult<com.elead.oa.vo.workflow.ProcessDefinition>();
		List<com.elead.oa.vo.workflow.ProcessDefinition> result = new ArrayList<com.elead.oa.vo.workflow.ProcessDefinition>();
		
		long total = workflowService.getProcessDefinitionPageCount(null!=procDef?procDef.getName():null, (curPage-1)*pageSize,pageSize);
		List<ProcessDefinition> list = workflowService.getProcessDefinitionPageList(null!=procDef?procDef.getName():null, (curPage-1)*pageSize,pageSize);
		
		com.elead.oa.vo.workflow.ProcessDefinition procDefi = null;
		for (ProcessDefinition processDefinition : list) {
			procDefi = new com.elead.oa.vo.workflow.ProcessDefinition();
			BeanUtils.copyProperties(processDefinition, procDefi);
			result.add(procDefi);
		}
		System.out.println("============================================================"+RequestContext.getValue("userAccount"));
		res.setRows(result);
		res.setTotal(new Long(total).intValue());
		return res;
	}
	
	/**
	 * 根据ID批量删除用户
	 * @param idList 批量ID
	 * @return
	 */
	@RequestMapping(value="/processDefinition/del",method=RequestMethod.GET)
	public WSResult<String> delUser(@RequestParam("id") String id) {
		WSResult<String> res = new WSResult<String>();
		workflowService.deleteProcDefByDeploymentId(id);
		res.setCode(CommConstants.SUCCESS);
		res.setMsg("删除流程成功.");
		return res;
	}
}
