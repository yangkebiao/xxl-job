package com.xxl.job.admin.controller;

import com.xxl.job.admin.controller.annotation.PermissionLimit;
import com.xxl.job.admin.core.conf.XxlJobAdminConfig;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.service.CustomerAdminBiz;
import com.xxl.job.admin.service.impl.dto.TaskInfo;
import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.GsonTool;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by xuxueli on 17/5/10.
 */
@Controller
@RequestMapping("/api")
public class JobApiController {

    @Resource
    private CustomerAdminBiz customerAdminBiz;

    /**
     * api
     *
     * @param uri
     * @param data
     * @return
     */
    @RequestMapping("/{uri}")
    @ResponseBody
    @PermissionLimit(limit=false)
    public ReturnT<? extends Object> api(HttpServletRequest request, @PathVariable("uri") String uri, @RequestBody(required = false) String data) {

        // valid
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, HttpMethod not support.");
        }
        if (uri==null || uri.trim().length()==0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, uri-mapping empty.");
        }
        if (XxlJobAdminConfig.getAdminConfig().getAccessToken()!=null
                && XxlJobAdminConfig.getAdminConfig().getAccessToken().trim().length()>0
                && !XxlJobAdminConfig.getAdminConfig().getAccessToken().equals(request.getHeader(XxlJobRemotingUtil.XXL_JOB_ACCESS_TOKEN))) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "The access token is wrong.");
        }

        // services mapping
        if ("callback".equals(uri)) {//执行器回调
            List<HandleCallbackParam> callbackParamList = GsonTool.fromJson(data, List.class, HandleCallbackParam.class);
            return customerAdminBiz.callback(callbackParamList);
        } else if ("registry".equals(uri)) {//执行器注册
            RegistryParam registryParam = GsonTool.fromJson(data, RegistryParam.class);
            return customerAdminBiz.registry(registryParam);
        } else if ("registryRemove".equals(uri)) {//执行器移除
            RegistryParam registryParam = GsonTool.fromJson(data, RegistryParam.class);
            return customerAdminBiz.registryRemove(registryParam);
        } else if ("jobGroupList".equals(uri)) {//获取执行器列表
            return customerAdminBiz.getXxlJobGroupList();
        }else if ("getJobInfo".equals(uri)) {//查询任务
        	TaskInfo param = GsonTool.fromJson(data, TaskInfo.class);
            return customerAdminBiz.getXxlJobInfo(param);
        }else if ("getJobInfoList".equals(uri)) {//模糊查询任务
        	TaskInfo param = GsonTool.fromJson(data, TaskInfo.class);
            return customerAdminBiz.getXxlJobInfoList(param);
        }else if ("addJobInfo".equals(uri)) {//创建任务
        	XxlJobInfo param = GsonTool.fromJson(data, XxlJobInfo.class);
            return customerAdminBiz.addXxlJobInfo(param);
        } else if ("updateJobInfo".equals(uri)) {//更新任务
        	XxlJobInfo param = GsonTool.fromJson(data, XxlJobInfo.class);
            return customerAdminBiz.updateXxlJobInfo(param);
        } else if ("removeJobInfo".equals(uri)) {//删除任务
        	TaskInfo param = GsonTool.fromJson(data, TaskInfo.class);
            return customerAdminBiz.removeXxlJobInfo(param);
        } else if ("startJobInfo".equals(uri)) {//开始任务
        	TaskInfo param = GsonTool.fromJson(data, TaskInfo.class);
            return customerAdminBiz.startXxlJobInfo(param);
        } else if ("stopJobInfo".equals(uri)) {//停止任务
        	TaskInfo param = GsonTool.fromJson(data, TaskInfo.class);
            return customerAdminBiz.stopXxlJobInfo(param);
        } else if ("triggerJob".equals(uri)) {//立即执行一次任务
        	TaskInfo param = GsonTool.fromJson(data, TaskInfo.class);
            return customerAdminBiz.triggerJob(param);
        } else if ("cronNextTriggerTime".equals(uri)) {//获取最近5次调度时间
        	TaskInfo param = GsonTool.fromJson(data, TaskInfo.class);
            return customerAdminBiz.nextTriggerTime(param);
        } else {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "invalid request, uri-mapping("+ uri +") not found.");
        }


    }

}
