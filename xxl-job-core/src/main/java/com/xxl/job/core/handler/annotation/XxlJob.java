package com.xxl.job.core.handler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * annotation for method jobhandler
 *
 * @author xuxueli 2019-12-11 20:50:13
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface XxlJob {

    /**
     * jobhandler name
     */
    String value();

    /**
     * init handler, invoked when JobThread init
     */
    String init() default "";

    /**
     * destroy handler, invoked when JobThread destroy
     */
    String destroy() default "";
    
    /**
     * 是否为异步方法 <br> 默认false:不是，true是
     * 注意：是true时，处理方法可新增一个TriggerParam参数，需返回ReturnT.RUNNING;处理结果需要调用AdminBiz.callback()通知;<br>
     * 可参考：SampleXxlJob.demoJobHandler3
     * @return 
     */
    boolean isSync() default false;

}
