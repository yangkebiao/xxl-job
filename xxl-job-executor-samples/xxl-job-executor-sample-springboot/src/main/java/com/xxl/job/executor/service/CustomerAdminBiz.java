package com.xxl.job.executor.service;

import java.util.List;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.executor.service.impl.dto.TaskInfo;
import com.xxl.job.executor.service.impl.dto.XxlJobGroup;
import com.xxl.job.executor.service.impl.dto.XxlJobInfo;

public interface CustomerAdminBiz extends AdminBiz{

	

	/**
	 * 获取执行器列表
	 * @return
	 */
	ReturnT<List<XxlJobGroup>> getXxlJobGroupList();
	
	/**
	 *查询任务
	 * @param jobInfo
	 * @return
	 */
	ReturnT<XxlJobInfo> getXxlJobInfo(TaskInfo taskInfo);

	/**
	 *模糊查询任务
	 * @param jobInfo
	 * @return
	 */
	ReturnT<List<XxlJobInfo>> getXxlJobInfoList(TaskInfo taskInfo);
	
	/**
	 * 创建任务
	 * @param jobInfo
	 * @return
	 */
	ReturnT<String> addXxlJobInfo(XxlJobInfo jobInfo);

	/**
	 * 更新任务
	 * @param jobInfo
	 * @return
	 */
	ReturnT<String> updateXxlJobInfo(XxlJobInfo jobInfo);

	/**
	 * 删除任务
	 * @param id
	 * @return
	 */
	ReturnT<String> removeXxlJobInfo(TaskInfo taskInfo);

	/**
	 * 开始任务
	 *
	 * @param id
	 * @return
	 */
	ReturnT<String> startXxlJobInfo(TaskInfo taskInfo);

	/**
	 * 停止任务
	 *
	 * @param id
	 * @return
	 */
	ReturnT<String> stopXxlJobInfo(TaskInfo taskInfo);
 
	/**
	 * 立即执行一次
	 * @return
	 */
	ReturnT<String> triggerJob(TaskInfo taskInfo);

	/**
	 * 获取最近5次调度时间
	 * @param cron
	 * @return
	 */
	ReturnT<List<String>> nextTriggerTime(TaskInfo taskInfo);
}
