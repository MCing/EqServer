package com.eqsys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.eqsys.msg.EqMessage;
import com.eqsys.msg.data.TrgData;
import com.eqsys.util.JDBCHelper;

public class TrgDataDao{

	private static final String TableName = "triggerdata_t";
	private static String insertSql = "insert into "+TableName+
			"(pid, stationid, starttimesec, starttimemsec, relattime, stalta, initmotiondir, "
			+ "ud2pga, ud2pgv, ud2pgd, ew2pga, ew2pgv,ew2pgd, ns2pga, ns2pgv, ns2pgd,"
			+ "ud2psa03, ud2psa10, ud2psa30, ew2psa03, ew2psa10,ew2psa30, ns2psa03, ns2psa10, ns2psa30,"
			+ "intensity) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
	
	private String deleteSql = "delete from "+ TableName+ " where packetid=?;";
	
	
	public  static boolean save(EqMessage msg) {
		
		boolean ret = false;
		PreparedStatement preStat = null;
		Connection conn = null;
		try {
			TrgData t = (TrgData) msg.getBody();
			conn = JDBCHelper.getDBConnection();
			preStat = conn.prepareStatement(insertSql);
			//preStat.setLong(1, t.getStartTimeSec());
			preStat.setInt(1, t.getId());
			preStat.setString(2, msg.getHeader().getStationId());
			preStat.setLong(3, t.getStartTimeSec());
			preStat.setShort(4, t.getStartTimeMs());
			preStat.setShort(5, t.getRelTimeSec());
			preStat.setInt(6, t.getStaAndltaValue());
			preStat.setShort(7, t.getInitMotionDir());
			preStat.setInt(8, t.getUdToPga());
			preStat.setInt(9, t.getUdToPgv());
			preStat.setInt(10, t.getUdToPgd());
			preStat.setInt(11, t.getEwToPga());
			preStat.setInt(12, t.getEwToPgv());
			preStat.setInt(13, t.getEwToPgd());
			preStat.setInt(14, t.getNsToPga());
			preStat.setInt(15, t.getNsToPgv());
			preStat.setInt(16, t.getNsToPgd());
			preStat.setInt(17, t.getUdToPsa03());
			preStat.setInt(18, t.getUdToPsa10());
			preStat.setInt(19, t.getUdToPsa30());
			preStat.setInt(20, t.getEwToPsa03());
			preStat.setInt(21, t.getEwToPsa10());
			preStat.setInt(22, t.getEwToPsa30());
			preStat.setInt(23, t.getNsToPsa03());
			preStat.setInt(24, t.getNsToPsa10());
			preStat.setInt(25, t.getNsToPsa30());
			preStat.setInt(26, t.getIntensity());
			int insertRet = preStat.executeUpdate();
//			System.err.println("insert:"+insertRet);
			ret = true;
		} catch (SQLException e) {

			ret = false;
			e.printStackTrace();
		}finally{
			try {
				if(preStat != null){
					preStat.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//释放连接
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
	
	/**
	 * 获取数据
	 * @param stationid
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	public static List<TrgData> getRecord(String stationid, long starttime, long endtime){
		ArrayList<TrgData> list = new ArrayList<TrgData>();
		String sql = "select * from "+ TableName+" where stationid=? and starttimesec>? and starttimesec<?;";
		PreparedStatement preStat = null;
		Connection conn = null;
		try {
			conn = JDBCHelper.getDBConnection();
			preStat = conn.prepareStatement(sql);
			preStat.setString(1, stationid);
			preStat.setLong(2, starttime);
			preStat.setLong(3, endtime);
			ResultSet results = preStat.executeQuery();
			while (results.next()) {
				TrgData data = new TrgData();
				data.setId(results.getInt("pid"));
				data.setStartTimeSec(results.getLong("starttimesec"));
				data.setStartTimeMs(results.getShort("starttimemsec"));
				data.setRelTimeSec(results.getShort("relattime"));
				data.setStaAndltaValue(results.getInt("stalta"));
				data.setInitMotionDir(results.getShort("initmotiondir"));
				data.setUdToPga(results.getInt("ud2pga"));
				data.setUdToPgd(results.getInt("ud2pgd"));
				data.setUdToPgv(results.getInt("ud2pgv"));
				data.setEwToPga(results.getInt("ew2pga"));
				data.setEwToPgd(results.getInt("ew2pgd"));
				data.setEwToPgv(results.getInt("ew2pgv"));
				data.setNsToPga(results.getInt("ns2pga"));
				data.setNsToPgd(results.getInt("ns2pgd"));
				data.setNsToPgv(results.getInt("ns2pgv"));
				data.setIntensity(results.getShort("intensity"));
				data.setUdToPsa03(results.getInt("ud2psa03"));
				data.setUdToPsa10(results.getInt("ud2psa10"));
				data.setUdToPsa30(results.getInt("ud2psa30"));
				data.setEwToPsa03(results.getInt("ew2psa03"));
				data.setEwToPsa10(results.getInt("ew2psa10"));
				data.setEwToPsa30(results.getInt("ew2psa30"));
				data.setNsToPsa03(results.getInt("ns2psa03"));
				data.setNsToPsa10(results.getInt("ns2psa10"));
				data.setNsToPsa30(results.getInt("ns2psa30"));
				list.add(data);
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
		return list;
	}
	/** 获取时间段内的记录数量  
	 * 
	 * @param stationid	指定台站
	 * @param starttiem	指定开始时间,毫秒
	 * @param endtime	指定结束时间,毫秒
	 * @return			总记录数量
	 */
	public static int getCount(String stationid, long starttime, long endtime) {

		String sql = "select count(pid)  from " + TableName
				+ " where stationid = ? and starttimesec > ? and starttimesec < ?;";
		PreparedStatement preStat = null;
		Connection conn = null;
		int count = 0;
		try {
			conn = JDBCHelper.getDBConnection();
			preStat = conn.prepareStatement(sql);
			preStat.setString(1, stationid);
			preStat.setLong(2, starttime);
			preStat.setLong(3, endtime);
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
