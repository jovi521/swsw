package com.cdyw.swsw.system.service.act;

import cn.hutool.core.util.RandomUtil;
import com.cdyw.swsw.common.domain.entity.user.SysUserEntity;
import org.activiti.engine.*;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主要用来处理工作流
 *
 * @author jovi
 */
@Service
public class ActivityService {
    /**
     * 资源管理接口
     */
    private final RepositoryService repositoryService;
    /**
     * 流程运行管理接口
     */
    private final RuntimeService runtimeService;
    /**
     * 任务管理接口
     */
    private final TaskService taskService;
    /**
     * 历史管理接口
     */
    private final HistoryService historyService;
    /**
     * 引擎管理接口
     */
    private final ManagementService managementService;

    public ActivityService(RepositoryService repositoryService, TaskService taskService, RuntimeService runtimeService, HistoryService historyService, ManagementService managementService) {
        this.repositoryService = repositoryService;
        this.taskService = taskService;
        this.runtimeService = runtimeService;
        this.historyService = historyService;
        this.managementService = managementService;
    }

    public Deployment createDeployment(String deploymentName, String category) {
        String resource = "processes/process1.bpmn20.xml";
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        // 引入 bpmn 资源
        deploymentBuilder.addClasspathResource(resource);
        // 设置 key 和 name
        deploymentBuilder.key(RandomUtil.randomString(RandomUtil.BASE_CHAR_NUMBER, 20)).name(deploymentName).category(category);
        return deploymentBuilder.deploy();
    }

    public ProcessInstance startProcessInstance(SysUserEntity currentUser, String deploymentId) {
        // 获取当前用户信息
        String forecaster = "forecaster1";
        ProcessInstance processInstance = null;
        if (currentUser != null) {
            String assignee = currentUser.getUserName();
            ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId);
            ProcessDefinition processDefinition = processDefinitionQuery.singleResult();
            Map<String, Object> variables = new HashMap<>(4);
            // 验证权限：只能是气象台的 forecaster1 发起审批请求
//        UserDetails userDetails = authUserDetailsService.loadUserByUsername(userName);
            if (forecaster.equals(assignee)) {
                variables.put("user", assignee);
                processInstance = runtimeService.startProcessInstanceById(processDefinition.getId(), variables);
            }
        }
        return processInstance;
    }

    public String completeTask(SysUserEntity currentUser, String processInstanceId) {
        // 获取当前用户信息
        String forecaster = "approver2";
        if (currentUser != null) {
            String assignee = currentUser.getUserName();
            TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(processInstanceId);
            Map<String, Object> variables = new HashMap<>(4);
            if (forecaster.equals(assignee)) {
                variables.put("user", assignee);
                List<Task> taskList = taskQuery.taskAssignee(assignee).list();
                for (Task task : taskList) {
                    taskService.complete(task.getId(), variables);
                }
            }
        }
        return null;
    }

    public String listHistory() {

        return null;
    }
}
