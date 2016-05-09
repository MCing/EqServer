package com.eqsys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.eqsys.msg.EqMessage;
import com.eqsys.msg.data.StatusData;
import com.eqsys.msg.data.TrgData;
import com.eqsys.util.JDBCHelper;

public class StatusDataDao {

	// 峰峰值类型 分别为：ew,ns,ud
	private static short[] pvType = { 0, 1, 2 };

	private static final String TableName = "statusdata_t";
	private static final String PVTableName = "peakvalue_t";

	private static String insertSql = "insert into " + TableName + "(pid, stationid, starttime, duration)" + " values(?,?,?,?);";

	private static String pvInsertSql = "insert into " + PVTableName
			+ "(pid, stationid, pvtype, value1, value2, value3, value4, value5, value6, value7, value8, value9, value10) "
			+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?);";


	/** 存状态信息到数据库 */
	public  static boolean save(EqMessage msg) {

		boolean ret = false;
		PreparedStatement preStat = null;
		Connection conn = null;
		try {
			StatusData t = (StatusData) msg.getBody();
			conn = JDBCHelper.getDBConnection();
//			conn.setAutoCommit(false); 
			
			preStat = conn.prepareStatement(insertSql);
			// preStat.setLong(1, t.getStartTimeSec());
			preStat.setInt(1, t.getId());
			preStat.setString(2, msg.getHeader().getStationId());
			preStat.setLong(3, t.getStartTime());
			preStat.setInt(4, t.getDur());
//			preStat.addBatch();

//			preStat = conn.prepareStatement(pvInsertSql);
//			for (int i = 0; i < 3; i++) {
//				preStat.setInt(1, t.getId());
//				preStat.setString(2, msg.getHeader().getStationId());
//				preStat.setShort(3, pvType[i]);
//				for (int j = 0; j < 10; j++) {
//					if (i == 0) {
//						preStat.setInt(j + 4, (t.getEwPeakValue())[i]);
//					} else if (i == 1) {
//						preStat.setInt(j + 4, (t.getNsPeakValue())[i]);
//					} else {
//						preStat.setInt(j + 4, (t.getUdPeakValue())[i]);
//					}
//				}
//				preStat.addBatch();
//			}
//			preStat.executeBatch();
//			conn.commit();
			preStat.executeUpdate();
			ret = true;
		} catch (SQLException e) {

			ret = false;
			e.printStackTrace();
		} finally {
			try {
				if (preStat != null) {
					preStat.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 释放连接
			JDBCHelper.closeDBConnection(conn);
		}
		savePeakValue(msg);
		return ret;
	}
	
	/** 存峰峰值数组到数据库  */
	private  static boolean savePeakValue(EqMessage msg){
		boolean ret = false;
		PreparedStatement preStat = null;
		Connection conn = null;
		try {
			StatusData t = (StatusData) msg.getBody();
			conn = JDBCHelper.getDBConnection();
//			conn.setAutoCommit(false);
			
//			preStat = conn.prepareStatement(insertSql);
//			// preStat.setLong(1, t.getStartTimeSec());
//			preStat.setInt(1, t.getId());
//			preStat.setString(2, msg.getHeader().getStationId());
//			preStat.setLong(3, t.getStartTime());
//			preStat.setInt(4, t.getDur());
//			preStat.addBatch();

			preStat = conn.prepareStatement(pvInsertSql);
			for (int i = 0; i < 3; i++) {
				preStat.setInt(1, t.getId());
				preStat.setString(2, msg.getHeader().getStationId());
				preStat.setShort(3, pvType[i]);
				for (int j = 0; j < 10; j++) {
					if (i == 0) {
						preStat.setInt(j + 4, (t.getEwPeakValue())[i]);
					} else if (i == 1) {
						preStat.setInt(j + 4, (t.getNsPeakValue())[i]);
					} else {
						preStat.setInt(j + 4, (t.getUdPeakValue())[i]);
					}
				}
//				preStat.addBatch();
				preStat.executeUpdate();
			}
//			preStat.executeBatch();
//			conn.commit();
//			preStat.executeUpdate();
			ret = true;
		} catch (SQLException e) {

			ret = false;
			e.printStackTrace();
		} finally {
			try {
				if (preStat != null) {
					preStat.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 释放连接
			JDBCHelper.closeDBConnection(conn);
		}
		return ret;
	}
	
	/** 获取记录数量
	 * 
	 * @param stationid	指定台站
	 * @return			总记录数量
	 */
	public static int getCount(String stationid) {

		String sql = "select count(pid)  from " + TableName
				+ " where stationid = ?;";
		PreparedStatement preStat = null;
		Connection conn = null;
		int count = 0;
		try {
			conn = JDBCHelper.getDBConnection();
			preStat = conn.prepareStatement(sql);
			preStat.setString(1, stationid);
			ResultSet results = preStat.executeQuery();
			if (results.next()) {
				count = results.getInt(1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (preStat != null) {
					preStat.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			JDBCHelper.closeDBConnection(conn);
		}
		return count;
	}
}
