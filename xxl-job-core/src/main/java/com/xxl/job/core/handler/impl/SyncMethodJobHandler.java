package com.xxl.job.core.handler.impl;

import java.lang.reflect.Method;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;

/**
 * @author mading
 */
public class SyncMethodJobHandler extends MethodJobHandler {

    private final Object target;
    private final Method method;
    private Method initMethod;
    private Method destroyMethod;

    public SyncMethodJobHandler(Object target, Method method, Method initMethod, Method destroyMethod) {
        super(target, method, initMethod, destroyMethod);
        
    	this.target = target;
        this.method = method;

        this.initMethod =initMethod;
        this.destroyMethod =destroyMethod;
    }

    public ReturnT<String> execute(TriggerParam triggerParam) throws Exception {
    	return (ReturnT<String>)method.invoke(target, new Object[]{triggerParam});
    }



    
    
}
