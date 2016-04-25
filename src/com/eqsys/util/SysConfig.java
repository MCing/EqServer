package com.eqsys.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 配置类,用于读取配置文件,配置数据库连接,配置网络ip及端口
 *
 */
public class SysConfig {

	private static Logger log = Logger.getLogger(SysConfig.class);
	
	//jdbc 配置
	private static String jdbcUser;
	private static String jdbcPasswd;
	private static String jdbcDb;
	private static int jdbcPort;
	public static String jdbcServerName;

	//网络参数配置
	public static String serverIp;
	private static int serverPort;
	
	//台网信息
	public static String serverId;
	
	//配置文件路径
	public static final String CONFIG_PATH = System.getProperty("user.dir") + "/config.properties";

	/** 预配置,读取文件,设置相应参数值 */
	public static void preConfig(){
		Properties  prop = getPropertiesFromFile();
		if(prop != null){
			jdbcUser = prop.getProperty("JDBC_USER");
			jdbcPasswd = prop.getProperty("JDBC_PASSWORD");
			jdbcDb = prop.getProperty("JDBC_DATABASE");
			jdbcPort = Integer.valueOf(prop.getProperty("JDBC_PORT"));
			jdbcServerName = prop.getProperty("JDBC_SERVERNAME");
			
			serverIp = prop.getProperty("SERVER_IP", "localhost");
			serverPort = Integer.valueOf(prop.getProperty("SERVER_PORT", "3306"));
			
			serverId = prop.getProperty("SERVER_ID", "00");
			
		}else{
			log.error("配置文件读取失败!");
		}
		
	}
	
	/** 通过配置文件读取配置 */
	private static Properties getPropertiesFromFile(){
		
		Properties prop = new Properties();
		
		//根据工程路径找到配置文件(目前配置文件放在工程根路径下)
		try{
			InputStream inputStream = new FileInputStream(CONFIG_PATH);
			prop.load(inputStream);
		}catch(IOException e){
			e.printStackTrace();
			return null;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
		}
		return prop;
	}
	
	/** 保存Properties对象到配置文件 */
	private static void saveProperty(Properties prop){
		try {
			OutputStream out = new FileOutputStream(CONFIG_PATH);
			prop.store(out, new Date().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/** 保存新的数据库配置到配置文件 */
	public static void saveDbConfig(String serverName, String port, String userName, String password, String dbName){
		Properties pro = getPropertiesFromFile();
		
		jdbcUser = userName;
		jdbcPasswd = password;
	    jdbcDb = dbName;
		jdbcPort = Integer.valueOf(port);
		jdbcServerName = serverName;
		
		pro.put("JDBC_SERVERNAME", serverName);
		pro.put("JDBC_PORT", port);
		pro.put("JDBC_USER", userName);
		pro.put("JDBC_PASSWORD", password);
		pro.put("JDBC_DATABASE", dbName);
		
		saveProperty(pro);
	}
	
	public static String getJdbcUser() {
		return jdbcUser;
	}

	public static String getJdbcPasswd() {
		return jdbcPasswd;
	}

	public static String getJdbcDb() {
		return jdbcDb;
	}

	public static int getJdbcPort() {
		return jdbcPort;
	}

	public static String getJdbcServerName() {
		return jdbcServerName;
	}

	public static String getServerIp() {
		return serverIp;
	}

	public static int getServerPort() {
		return serverPort;
	}
	
	
}
