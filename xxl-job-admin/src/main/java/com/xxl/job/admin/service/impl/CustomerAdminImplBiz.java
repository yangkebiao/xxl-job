package com.xxl.job.admin.service.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xxl.job.admin.core.cron.CronExpression;
import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.core.util.I18nUtil;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.dao.XxlJobInfoDao;
import com.xxl.job.admin.dao.XxlJobLogDao;
import com.xxl.job.admin.dao.XxlJobLogGlueDao;
import com.xxl.job.admin.dao.XxlJobLogReportDao;
import com.xxl.job.admin.service.CustomerAdminBiz;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.admin.service.impl.dto.TaskInfo;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.DateUtil;

@Service
public class CustomerAdminImplBiz extends AdminBizImpl implements CustomerAdminBiz {

	@Resource
	private XxlJobService xxlJobService;
	@Resource
	private XxlJobGroupDao xxlJobGroupDao;
	@Resource
	private XxlJobInfoDao xxlJobInfoDao;
	@Resource
	public XxlJobLogDao xxlJobLogDao;
	@Resource
	private XxlJobLogGlueDao xxlJobLogGlueDao;
	@Resource
	private XxlJobLogReportDao xxlJobLogReportDao;

	@Override
	public ReturnT<List<XxlJobGroup>> getXxlJobGroupList() {
		// 执行器列表
		List<XxlJobGroup> jobGroupList_all =  xxlJobGroupDao.findAll();
		return new ReturnT<>(jobGroupList_all);
	}
	
	@Override
	public ReturnT<XxlJobInfo> getXxlJobInfo(TaskInfo taskInfo) {
		XxlJobInfo jobInfo = xxlJobInfoDao.loadById(taskInfo.getId());
		return new ReturnT<>(jobInfo);
	}
	
	@Override
	public ReturnT<List<XxlJobInfo>> getXxlJobInfoList(TaskInfo taskInfo) {

		int list_count = xxlJobInfoDao.pageListCount(0, 0, 
				taskInfo.getJobGroup(), 
				taskInfo.getTriggerStatus(), 
				taskInfo.getJobDesc(), 
				taskInfo.getExecutorHandler(), 
				taskInfo.getAuthor());
		List<XxlJobInfo> list = xxlJobInfoDao.pageList(0, list_count,
				taskInfo.getJobGroup(), 
				taskInfo.getTriggerStatus(), 
				taskInfo.getJobDesc(), 
				taskInfo.getExecutorHandler(), 
				taskInfo.getAuthor());
		
		return new ReturnT<>(list);
	}
	
	@Override
	public ReturnT<String> addXxlJobInfo(XxlJobInfo jobInfo) {
		return xxlJobService.add(jobInfo);
	}

	@Override
	public ReturnT<String> updateXxlJobInfo(XxlJobInfo jobInfo) {
		return xxlJobService.update(jobInfo);
	}

	@Override
	public ReturnT<String> removeXxlJobInfo(TaskInfo taskInfo) {
		return xxlJobService.remove(taskInfo.getId());
	}

	@Override
	public ReturnT<String> startXxlJobInfo(TaskInfo taskInfo) {
		return xxlJobService.start(taskInfo.getId());
	}

	@Override
	public ReturnT<String> stopXxlJobInfo(TaskInfo taskInfo) {
		return xxlJobService.stop(taskInfo.getId());
	}

	@Override
	public ReturnT<String> triggerJob(TaskInfo taskInfo) {
		int id = taskInfo.getId();
		String executorParam = taskInfo.getExecutorParam();
		String addressList = taskInfo.getAddressList();
		
		if (executorParam == null) {
			executorParam = "";
		}

		JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam, addressList);
		return ReturnT.SUCCESS;
	}

	@Override
	public ReturnT<List<String>> nextTriggerTime(TaskInfo taskInfo) {
		String cron = taskInfo.getCron();
		List<String> result = new ArrayList<>();
		try {
			CronExpression cronExpression = new CronExpression(cron);
			Date lastTime = new Date();
			for (int i = 0; i < 5; i++) {
				lastTime = cronExpression.getNextValidTimeAfter(lastTime);
				if (lastTime != null) {
					result.add(DateUtil.formatDateTime(lastTime));
				} else {
					break;
				}
			}
		} catch (ParseException e) {
			return new ReturnT<List<String>>(ReturnT.FAIL_CODE, I18nUtil.getString("jobinfo_field_cron_unvalid"));
		}
		return new ReturnT<List<String>>(result);
	}

}
