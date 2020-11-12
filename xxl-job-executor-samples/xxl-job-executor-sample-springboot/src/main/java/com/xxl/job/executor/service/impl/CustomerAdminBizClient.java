package com.xxl.job.executor.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.xxl.job.core.biz.client.AdminBizClient;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.RegistryParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.XxlJobRemotingUtil;
import com.xxl.job.executor.core.config.XxlJobConfig;
import com.xxl.job.executor.service.CustomerAdminBiz;
import com.xxl.job.executor.service.impl.dto.TaskInfo;
import com.xxl.job.executor.service.impl.dto.XxlJobGroup;
import com.xxl.job.executor.service.impl.dto.XxlJobInfo;

@Component
public class CustomerAdminBizClient extends AdminBizClient implements CustomerAdminBiz {

	@Autowired
	private XxlJobConfig xxlJobConfig;
	
	@PostConstruct
    public void init() {
    	
    	this.addressUrl = xxlJobConfig.getAdminAddresses();
        this.accessToken = xxlJobConfig.getAccessToken();

        // valid
        if (!this.addressUrl.endsWith("/")) {
            this.addressUrl = this.addressUrl + "/";
        }
    	
    }
   
	private String addressUrl ;
    private String accessToken;
    private int timeout = 10;

    public CustomerAdminBizClient(String addressUrl, String accessToken) {
    	super();
    	this.addressUrl = addressUrl;
    	this.accessToken = accessToken;
    }
    public CustomerAdminBizClient() {
    }

    @Override
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        return XxlJobRemotingUtil.postBody(addressUrl+"api/callback", accessToken, timeout, callbackParamList, String.class);
    }

    @Override
    public ReturnT<String> registry(RegistryParam registryParam) {
        return XxlJobRemotingUtil.postBody(addressUrl + "api/registry", accessToken, timeout, registryParam, String.class);
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {
        return XxlJobRemotingUtil.postBody(addressUrl + "api/registryRemove", accessToken, timeout, registryParam, String.class);
    }

	@Override
	public ReturnT<List<XxlJobGroup>> getXxlJobGroupList() {
		ReturnT<List<Object>> result = XxlJobRemotingUtil.postBody(addressUrl + "api/jobGroupList", accessToken, timeout, null, List.class);
		
		ReturnT<List<XxlJobGroup>> rt = new ReturnT<>();
		List<XxlJobGroup> jgList = new ArrayList<>();
		rt.setCode(result.getCode());
		rt.setMsg(result.getMsg());
		rt.setContent(jgList);
		
		List<Object> content = result.getContent();
		if(content != null) {
			for (Object o : content) {
				 Gson gson = new Gson();
				 XxlJobGroup g = gson.fromJson(gson.toJson(o), XxlJobGroup.class);
				 jgList.add(g);
			}
		}
		return rt;
	}

	@Override
	public ReturnT<XxlJobInfo> getXxlJobInfo(TaskInfo taskInfo){
		return XxlJobRemotingUtil.postBody(addressUrl + "api/getJobInfo", accessToken, timeout, taskInfo, XxlJobInfo.class);
	}
	
	@Override
	public ReturnT<List<XxlJobInfo>> getXxlJobInfoList(TaskInfo taskInfo){
		
		ReturnT<List<Object>> result = XxlJobRemotingUtil.postBody(addressUrl + "api/getJobInfoList", accessToken, timeout, taskInfo, List.class);
		
		ReturnT<List<XxlJobInfo>> rt = new ReturnT<>();
		List<XxlJobInfo> infoList = new ArrayList<>();
		rt.setCode(result.getCode());
		rt.setMsg(result.getMsg());
		rt.setContent(infoList);
		
		List<Object> content = result.getContent();
		if(content != null) {
			for (Object o : content) {
				 Gson gson = new Gson();
				 XxlJobInfo info = gson.fromJson(gson.toJson(o), XxlJobInfo.class);
				 infoList.add(info);
			}
		}
		
		return rt;
	}
	
	@Override
	public ReturnT<String> addXxlJobInfo(XxlJobInfo jobInfo) {
		return XxlJobRemotingUtil.postBody(addressUrl + "api/addJobInfo", accessToken, timeout, jobInfo, String.class);
	}

	@Override
	public ReturnT<String> updateXxlJobInfo(XxlJobInfo jobInfo) {
		return XxlJobRemotingUtil.postBody(addressUrl + "api/updateJobInfo", accessToken, timeout, jobInfo, String.class);
	}

	@Override
	public ReturnT<String> removeXxlJobInfo(TaskInfo taskInfo) {
		return XxlJobRemotingUtil.postBody(addressUrl + "api/removeJobInfo", accessToken, timeout, taskInfo, String.class);
	}

	@Override
	public ReturnT<String> startXxlJobInfo(TaskInfo taskInfo) {
		return XxlJobRemotingUtil.postBody(addressUrl + "api/startJobInfo", accessToken, timeout, taskInfo, String.class);
	}

	@Override
	public ReturnT<String> stopXxlJobInfo(TaskInfo taskInfo) {
		return XxlJobRemotingUtil.postBody(addressUrl + "api/stopJobInfo", accessToken, timeout, taskInfo, String.class);
	}

	@Override
	public ReturnT<String> triggerJob(TaskInfo taskInfo) {
		return XxlJobRemotingUtil.postBody(addressUrl + "api/triggerJob", accessToken, timeout, taskInfo, String.class);
	}

	@Override
	public ReturnT<List<String>> nextTriggerTime(TaskInfo taskInfo) {
		return XxlJobRemotingUtil.postBody(addressUrl + "api/cronNextTriggerTime", accessToken, timeout, taskInfo, List.class);
	}

	
	
}
