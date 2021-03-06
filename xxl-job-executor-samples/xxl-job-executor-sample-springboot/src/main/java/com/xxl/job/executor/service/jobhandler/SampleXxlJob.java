package com.xxl.job.executor.service.jobhandler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.job.core.biz.model.HandleCallbackParam;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.biz.model.TriggerParam;
import com.xxl.job.core.context.XxlJobContext;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.executor.service.CustomerAdminBiz;

/**
 * XxlJob开发示例（Bean模式）
 *
 * 开发步骤：
 *      1、任务开发：在Spring Bean实例中，开发Job方法；
 *      2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 *      3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 *      4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 * @author xuxueli 2019-12-11 21:52:51
 */
@Component
public class SampleXxlJob {
    private static Logger logger = LoggerFactory.getLogger(SampleXxlJob.class);

    @Autowired
    private CustomerAdminBiz customerAdminBiz;

    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("demoJobHandler")
    public void demoJobHandler() throws Exception {
        XxlJobHelper.log("XXL-JOB, Hello World.");

        for (int i = 0; i < 5; i++) {
            XxlJobHelper.log("beat at:" + i);
            TimeUnit.SECONDS.sleep(2);
        }
        // default success
    }


    /**
     * 2、分片广播任务
     */
    @XxlJob("shardingJobHandler")
    public void shardingJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();

        XxlJobHelper.log("分片参数：当前分片序号 = {}, 总分片数 = {}", shardIndex, shardTotal);

        // 业务逻辑
        for (int i = 0; i < shardTotal; i++) {
            if (i == shardIndex) {
                XxlJobHelper.log("第 {} 片, 命中分片开始处理", i);
            } else {
                XxlJobHelper.log("第 {} 片, 忽略", i);
            }
        }

    }


    /**
     * 3、命令行任务
     */
    @XxlJob("commandJobHandler")
    public void commandJobHandler() throws Exception {
        String command = XxlJobHelper.getJobParam();
        int exitValue = -1;

        BufferedReader bufferedReader = null;
        try {
            // command process
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(command);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            //Process process = Runtime.getRuntime().exec(command);

            BufferedInputStream bufferedInputStream = new BufferedInputStream(process.getInputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream));

            // command log
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                XxlJobHelper.log(line);
            }

            // command exit
            process.waitFor();
            exitValue = process.exitValue();
        } catch (Exception e) {
            XxlJobHelper.log(e);
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        if (exitValue == 0) {
            // default success
        } else {
            XxlJobHelper.handleFail("command exit value("+exitValue+") is failed");
        }

    }

    /**
     * 3、新命令行任务
     */
    @XxlJob("newCommandJobHandler") 
    public void newCommandJobHandler(String param) throws Exception {
    	ObjectMapper objectMapper = new ObjectMapper();
    	objectMapper.configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER.mappedFeature(), true);  
    	
    	Map<String,Object> readValue = objectMapper.readValue(param, Map.class);
    	
    	String cmd = (String)readValue.get("cmd");
    	Map<String,String> env = (Map)readValue.get("env");
    	String dir = (String)readValue.get("dir");
    	int exitValue = cmdExce(cmd, env, dir);
    	
    	if (exitValue == 0) {
            // default success
        } else {
            XxlJobHelper.handleFail("command exit value("+exitValue+") is failed");
        }
    	
    }
    private int cmdExce(String cmd,Map<String, String> env,String dir) {
		
		Process pop = null;
		int waitFor = -1;
		try {
			XxlJobHelper.log("执行cmd："+cmd);
			
			List<String> envArr = new ArrayList<>();
			Map<String, String> sysEnv = System.getenv();
			if(env != null && !env.isEmpty()) {
				sysEnv.putAll(env);
			}
			Set<Entry<String, String>> entrySet = sysEnv.entrySet();
			for (Entry<String, String> entry : entrySet) {
				String e = entry.getKey()+"="+entry.getValue();
//				log.info(e);
				envArr.add(e);
			}
			pop = Runtime.getRuntime().exec(cmd, envArr.toArray(new String[envArr.size()]),dir != null?new File(dir):null);
			
			//获取进程的标准输入流  
			final InputStream is1 = pop.getInputStream();  
			//获取进程的错误流  
			final InputStream is2 = pop.getErrorStream(); 
			
			XxlJobHelper.log("执行cmd响应："); 
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					 
					BufferedReader br1 = null;
					try {
						br1 = new BufferedReader(new InputStreamReader(is1,"UTF-8"));
					} catch (UnsupportedEncodingException e1) {
						throw new RuntimeException(e1);
					}  
			         try {  
			            String line1 = null;  
			            while ((line1 = br1.readLine()) != null) {  
			                  if (line1 != null){
			                	  XxlJobHelper.log("cmd info result："+line1);
			                  }  
			              }  
			        } catch (IOException e) {  
			             e.printStackTrace();  
			        } finally{  
			             try {  
			               is1.close();  
			             } catch (IOException e) {  
			                e.printStackTrace();  
			            }  
			          }  
				}
			}).start();
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					BufferedReader br2 = null;
					try {
						br2 = new BufferedReader(new InputStreamReader(is2,"UTF-8"));
					} catch (UnsupportedEncodingException e1) {
						throw new RuntimeException(e1);
					}  
			         try {  
			            String line2 = null;  
			            while ((line2 = br2.readLine()) != null) {  
			                  if (line2 != null){
			                	  XxlJobHelper.log("cmd error result："+line2);
			                  }  
			              }  
			        } catch (IOException e) {  
			             e.printStackTrace();  
			        } finally{  
			             try {  
			               is2.close();  
			             } catch (IOException e) {  
			                e.printStackTrace();  
			            }  
			          }  
				}
			}).start();

           waitFor = pop.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }finally {
        	if(pop!=null) {
        		pop.destroy();
        	}
		}
		return waitFor;
		
	}
    
    
    /**
     * 4、跨平台Http任务
     *  参数示例：
     *      "url: http://www.baidu.com\n" +
     *      "method: get\n" +
     *      "data: content\n";
     */
    @XxlJob("httpJobHandler")
    public void httpJobHandler() throws Exception {

        // param parse
        String param = XxlJobHelper.getJobParam();
        if (param==null || param.trim().length()==0) {
            XxlJobHelper.log("param["+ param +"] invalid.");

            XxlJobHelper.handleFail();
            return;
        }

        String[] httpParams = param.split("\n");
        String url = null;
        String method = null;
        String data = null;
        for (String httpParam: httpParams) {
            if (httpParam.startsWith("url:")) {
                url = httpParam.substring(httpParam.indexOf("url:") + 4).trim();
            }
            if (httpParam.startsWith("method:")) {
                method = httpParam.substring(httpParam.indexOf("method:") + 7).trim().toUpperCase();
            }
            if (httpParam.startsWith("data:")) {
                data = httpParam.substring(httpParam.indexOf("data:") + 5).trim();
            }
        }

        // param valid
        if (url==null || url.trim().length()==0) {
            XxlJobHelper.log("url["+ url +"] invalid.");

            XxlJobHelper.handleFail();
            return;
        }
        if (method==null || !Arrays.asList("GET", "POST").contains(method)) {
            XxlJobHelper.log("method["+ method +"] invalid.");

            XxlJobHelper.handleFail();
            return;
        }
        boolean isPostMethod = method.equals("POST");

        // request
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        try {
            // connection
            URL realUrl = new URL(url);
            connection = (HttpURLConnection) realUrl.openConnection();

            // connection setting
            connection.setRequestMethod(method);
            connection.setDoOutput(isPostMethod);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(5 * 1000);
            connection.setConnectTimeout(3 * 1000);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "application/json;charset=UTF-8");

            // do connection
            connection.connect();

            // data
            if (isPostMethod && data!=null && data.trim().length()>0) {
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(data.getBytes("UTF-8"));
                dataOutputStream.flush();
                dataOutputStream.close();
            }

            // valid StatusCode
            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                throw new RuntimeException("Http Request StatusCode(" + statusCode + ") Invalid.");
            }

            // result
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            String responseMsg = result.toString();

            XxlJobHelper.log(responseMsg);

            return;
        } catch (Exception e) {
            XxlJobHelper.log(e);

            XxlJobHelper.handleFail();
            return;
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e2) {
                XxlJobHelper.log(e2);
            }
        }

    }

    /**
     * 5、生命周期任务示例：任务初始化与销毁时，支持自定义相关逻辑；
     */
    @XxlJob(value = "demoJobHandler2", init = "init", destroy = "destroy")
    public void demoJobHandler2() throws Exception {
        XxlJobHelper.log("XXL-JOB, Hello World.");
    }
    public void init(){
        logger.info("init");
    }
    public void destroy(){
        logger.info("destory");
    }
    
    /**
     * 6.异步调用回调
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob(value = "demoJobHandler3",isSync = true)
    public void demoJobHandler3(TriggerParam triggerParam) throws Exception {
    	XxlJobHelper.log("XXL-JOB, Hello World.");
    	String param = XxlJobHelper.getJobParam();
        System.out.println("param："+param+",triggerParam:"+triggerParam);
        new Thread(() -> {
        	try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			List<HandleCallbackParam> callbackParamList = new ArrayList<>();
			callbackParamList.add(new HandleCallbackParam(triggerParam.getLogId(), triggerParam.getLogDateTime(), 
					ReturnT.SUCCESS_CODE,null));
			ReturnT<String> callback = customerAdminBiz.callback(callbackParamList);
			System.out.println("回调结束："+callback);
        }).start();
        
        boolean handleResult = XxlJobHelper.handleResult(ReturnT.RUNNING_CODE, null);
        System.out.println("handleResult:"+handleResult+"code1:"+XxlJobContext.getXxlJobContext().getHandleCode());
    }


}
