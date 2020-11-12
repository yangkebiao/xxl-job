package com.xxl.job.executor.service.impl.dto;

public class TaskInfo {

	

	private int id;
	
	private String executorParam;
	private String addressList;
	
	private int jobGroup;
	private int triggerStatus;
	private String jobDesc;
	private String executorHandler;
	private String author;
	
	
	private String cron;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getExecutorParam() {
		return executorParam;
	}
	public void setExecutorParam(String executorParam) {
		this.executorParam = executorParam;
	}
	public String getAddressList() {
		return addressList;
	}
	public void setAddressList(String addressList) {
		this.addressList = addressList;
	}
	public String getCron() {
		return cron;
	}
	public void setCron(String cron) {
		this.cron = cron;
	}
	public String getJobDesc() {
		return jobDesc;
	}
	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}
	public int getJobGroup() {
		return jobGroup;
	}
	public void setJobGroup(int jobGroup) {
		this.jobGroup = jobGroup;
	}
	public int getTriggerStatus() {
		return triggerStatus;
	}
	public void setTriggerStatus(int triggerStatus) {
		this.triggerStatus = triggerStatus;
	}
	public String getExecutorHandler() {
		return executorHandler;
	}
	public void setExecutorHandler(String executorHandler) {
		this.executorHandler = executorHandler;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	
}
