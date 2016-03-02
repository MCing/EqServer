package com.eqsys.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.PropertyConfigurator;

/** 
 * 日志工具类
 * 使用log4j包
 *
 */
public class LogUtil {

	private final static String LOG_PROPERTY_FILE = "log4j.properties";
	
	public static void initLog(){
		
		try {
			URL url = loadPropertiesURL();
			if(url == null){
				throw new Exception("读不到配置文件");
			}
			PropertyConfigurator.configure(url);
		}  catch(Exception e){
			System.err.println("log 配置错误:"+e.getMessage());
		}
	}
	
	/** 提供接口,供多种方法获取log4j配置文件 */
	private static URL loadPropertiesURL(){
		
		//1. 通过系统路径获取配置文件
		String workDir = System.getProperty("user.dir");
		try {
			URL propURL = new File(workDir+"/"+LOG_PROPERTY_FILE).toURI().toURL();
			return propURL;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		//2. 通过classpath 获取配置文件,文件必须在classpath路径中(如bin目录下)
//		return ClassLoader.getSystemResource(LOG_PROPERTY_FILE);
	}
	
}
