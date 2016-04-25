package com.eqsys.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

public class JDBCHelper {

	private static Logger log = Logger.getLogger(JDBCHelper.class);
	
	private static MiniConnectionPoolManager  poolMgr;
	//数据库连接池连接容量
	private static final int connectionPoolCapacity = 50;
	
	private static boolean isDbConnect;
	
	public static boolean initDB(){
		//使用连接池,数据源
		MysqlConnectionPoolDataSource ds = new MysqlConnectionPoolDataSource();
		
		ds.setServerName(SysConfig.getJdbcServerName());
		ds.setPort(SysConfig.getJdbcPort());
		ds.setDatabaseName(SysConfig.getJdbcDb());
		ds.setUser(SysConfig.getJdbcUser());
		ds.setPassword(SysConfig.getJdbcPasswd());
		
		poolMgr = new MiniConnectionPoolManager(ds, connectionPoolCapacity);
		try{
			Connection testConn = poolMgr.getConnection();
			testConn.close();
			
			isDbConnect = true;
		}catch(Exception e){
			isDbConnect = false;
			log.error("数据库配置错误:"+e.getMessage());
		}
		return isDbConnect;
	}
	
	/**
	 * 获取一个Connection 连接
	 * @return
	 */
	public static Connection getDBConnection(){
		
		Connection conn = null;
		try {
			conn = poolMgr.getConnection();
		} catch (SQLException e) {
			log.error("数据库异常:"+e.getMessage());
		}
		return conn;
	}
	
	
	/**
	 * 关闭jdbc Connection 连接
	 * @param conn
	 */
	public static void closeDBConnection(Connection conn){
		
		try{
			if(conn != null){
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			log.error("数据库关闭异常:"+e.getMessage());
		}
	}
	
	/**
	 * 关闭连接池
	 */
	public static void closeDB(){
		try {
			poolMgr.dispose();
		} catch (SQLException e) {
			log.error("数据库异常:"+e.getMessage());
		}
	}
	
	public static boolean getDbState(){
		return isDbConnect;
	}
}
