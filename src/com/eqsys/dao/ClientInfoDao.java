package com.eqsys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.eqsys.msg.EqMessage;
import com.eqsys.msg.RegReq;
import com.eqsys.util.JDBCHelper;

public class ClientInfoDao {

	private String mTableName = "clientinfo_t";
	private String mInsertSql = "insert into " + mTableName
			+ "(stationid, permit, longitude, latitude, altitude, sensitivity, transmode, trigglethreshold, lastpacketid) "
			+ "values( ?, ?,?,?,?,?,?,?,?);";
	private String mFindIdSql = "select stationid from " + mTableName + " where stationid=?";
	private String mUpdataPermitSql = "update " + mTableName + " set permit=? where stationid=?;";
	private String mUpdataTransModeSql = "update " + mTableName + " set transmode=? where stationid=?;";
	private String mUpdataThresholdSql = "update " + mTableName + " set trigglethreshold=? where stationid=?;";

	private static final String TableName = "triggerdata_t";
	private String insertSql = "insert into "+TableName+
			"(starttimesec, starttimemsec, relattime, stalta, initmotiondir, "
			+ "ud2pga, ud2pgv, ud2pgd, ew2pga, ew2pgv,ew2pgd, ns2pga, ns2pgv, ns2pgd,"
			+ "ud2psa03, ud2psa10, ud2psa30, ew2psa03, ew2psa10,ew2psa30, ns2psa03, ns2psa10, ns2psa30,"
			+ "intensity) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
	
	public ClientInfoDao() {}
	
	/**
	 * 在数据库中添加一条信息
	 * 
	 * @param info
	 */
	public void add(EqMessage client) {
		
		String stationId = client.getHeader().getStationId();
		RegReq info = (RegReq) client.getBody();
//		if (client == null) {
//			return;
//		}
		PreparedStatement preStat = null;
		Connection conn = null;
		if (!isExist(stationId)) {
			// 数据库中不存在客户端信息
			try {
				conn = JDBCHelper.getDBConnection();
				preStat = conn.prepareStatement(mInsertSql);
				preStat.setString(1, stationId);
				preStat.setShort(2, info.getCtrlAuthority());
				preStat.setInt(3, info.getLongitude());
				preStat.setInt(4, info.getLatitude());
				preStat.setShort(5, info.getAltitude());
				preStat.setInt(6, info.getSensitivity());
				preStat.setShort(7, info.getTransMode());
				preStat.setShort(8, info.getTriggerThreshold());
				preStat.setInt(9, 0);       //客户端第一次连接时上一次包序号初始化为0
				preStat.executeUpdate();
			} catch (SQLException e) {
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
				JDBCHelper.closeDBConnection(conn);
			}
		} else {
			// 数据库中已存在已存在,更新控制权限(目前只有控制权限可能变)
			updatePermit(stationId, info.getCtrlAuthority());
		}
	}

	/**
	 * 查询数据库中是否已存在某条数据
	 */
	private boolean isExist(String stId) {
		boolean ret = false;
		PreparedStatement preStat = null;
		Connection conn = JDBCHelper.getDBConnection();
		try {
			preStat = conn.prepareStatement(mFindIdSql);
			preStat.setString(1, stId);
			ResultSet result = preStat.executeQuery();
			if (result.next()) {
				ret = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
			JDBCHelper.closeDBConnection(conn);
		}
		return ret;
	}

	/**
	 * 更新数据库中的控制权限值
	 * 
	 * @param id
	 *            站台id
	 * @param newValue
	 *            permit值
	 */
	private void updatePermit(String id, short newValue) {
		PreparedStatement preStat = null;
		Connection conn = JDBCHelper.getDBConnection();
		try {
			preStat = conn.prepareStatement(mUpdataPermitSql);
			preStat.setShort(1, newValue);
			preStat.setString(2, id);
			preStat.executeUpdate();

		} catch (SQLException e) {
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
			JDBCHelper.closeDBConnection(conn);
		}
	}
	public void updateTransMode(String id, short newValue) {
		PreparedStatement preStat = null;
		Connection conn = JDBCHelper.getDBConnection();
		try {
			preStat = conn.prepareStatement(mUpdataTransModeSql);
			preStat.setShort(1, newValue);
			preStat.setString(2, id);
			preStat.executeUpdate();

		} catch (SQLException e) {
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
			JDBCHelper.closeDBConnection(conn);
		}
	}
	public void updateThreshold(String id, short newValue) {
		PreparedStatement preStat = null;
		Connection conn = JDBCHelper.getDBConnection();
		try {
			preStat = conn.prepareStatement(mUpdataThresholdSql);
			preStat.setShort(1, newValue);
			preStat.setString(2, id);
			preStat.executeUpdate();

		} catch (SQLException e) {
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
			JDBCHelper.closeDBConnection(conn);
		}
	}
}
