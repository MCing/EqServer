package com.eqsys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.eqsys.model.ClientInfo;
import com.eqsys.msg.EqMessage;
import com.eqsys.msg.RegReq;
import com.eqsys.util.JDBCHelper;
import com.eqsys.util.ParseUtil;

public class ClientInfoDao {

	private static final String mTableName = "clientinfo_t";
	private  static final String mInsertSql = "replace into " + mTableName
			+ "(stationid, permit, longitude, latitude, altitude, sensitivity, transmode, threshold, lastpid) "
			+ "values( ?, ?,?,?,?,?,?,?,?);";
	private  static final String mFindIdSql = "select stationid from " + mTableName + " where stationid=?";
	private  static final String mUpdataPermitSql = "update " + mTableName + " set permit=? where stationid=?;";
	private  static final String mUpdataTransModeSql = "update " + mTableName + " set transmode=? where stationid=?;";
	private  static final String mUpdataThresholdSql = "update " + mTableName + " set trigglethreshold=? where stationid=?;";

	/**
	 * 在数据库中添加一条信息
	 * 
	 * @param info
	 */
//	public void add(EqMessage client) {
//		
//		String stationId = client.getHeader().getStationId();
//		RegReq info = (RegReq) client.getBody();
////		if (client == null) {
////			return;
////		}
//		PreparedStatement preStat = null;
//		Connection conn = null;
//		if (!isExist(stationId)) {
//			// 数据库中不存在客户端信息
//			try {
//				conn = JDBCHelper.getDBConnection();
//				preStat = conn.prepareStatement(mInsertSql);
//				preStat.setString(1, stationId);
//				preStat.setShort(2, info.getCtrlAuthority());
//				preStat.setInt(3, info.getLongitude());
//				preStat.setInt(4, info.getLatitude());
//				preStat.setShort(5, info.getAltitude());
//				preStat.setInt(6, info.getSensitivity());
//				preStat.setShort(7, info.getTransMode());
//				preStat.setShort(8, info.getTriggerThreshold());
//				preStat.setInt(9, 0);       //客户端第一次连接时上一次包序号初始化为0
//				preStat.executeUpdate();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					if (preStat != null) {
//						preStat.close();
//					}
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				JDBCHelper.closeDBConnection(conn);
//			}
//		} else {
//			// 数据库中已存在已存在,更新控制权限(目前只有控制权限可能变)
//			updatePermit(stationId, info.getCtrlAuthority());
//		}
//	}
	
	public  static void add(ClientInfo info){
		
		PreparedStatement preStat = null;
		Connection conn = null;
			try {
				conn = JDBCHelper.getDBConnection();
				preStat = conn.prepareStatement(mInsertSql);
				preStat.setString(1, info.getStationId());
				preStat.setShort(2, info.getPermit());
				preStat.setFloat(3, info.getLongitude());
				preStat.setFloat(4, info.getLatitude());
				preStat.setShort(5, (short) info.getAltitude());
				preStat.setInt(6, info.getSensitivity());
				preStat.setShort(7, ParseUtil.parseTransMode(info.getTransMode()));
				preStat.setShort(8, (short) info.getThreshold());
				preStat.setInt(9, info.getLastPid());
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

	/**
	 * 查询数据库中是否已存在某条数据
	 */
	private  static boolean isExist(String stId) {
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
	private  static void updatePermit(String id, short newValue) {
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
	public  static void updateTransMode(String id, short newValue) {
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
	public  static void updateThreshold(String id, short newValue) {
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
	
	/** 删除一个节点
	 * 
	 * @param id 要删除记录的Id
	 * @return 删除成功 true, 反之false
	 */
	public  static boolean del(String id){
		
		String sql = "delete from " + mTableName + " where stationid=?;";
		PreparedStatement preStat = null;
		Connection conn = null;
		try {
			conn = JDBCHelper.getDBConnection();
			preStat = conn.prepareStatement(sql);
			preStat.setString(1, id);
			int ret = preStat.executeUpdate();
			if(ret == 1) return true;

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
		return false;
	}
	/** 获取记录
	 * 
	 * @return
	 */
	public  static List<ClientInfo> get(){
		String sql = "select * from " + mTableName + ";";
		
		PreparedStatement preStat = null;
		Connection conn = null;
		ArrayList<ClientInfo> list = new ArrayList<ClientInfo>();
		try {
			conn = JDBCHelper.getDBConnection();
			preStat = conn.prepareStatement(sql);
			ResultSet results = preStat.executeQuery();
			while(results.next()){
				ClientInfo node = new ClientInfo();
				node.setStationId(results.getString("stationid"));
				node.setLongitude(results.getFloat("longitude"));
				node.setLatitude(results.getFloat("latitude"));
				node.setAltitude(results.getShort("altitude"));
				node.setThreshold(results.getShort("threshold"));
				node.setSensitivity(results.getInt("sensitivity"));
				list.add(node);
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
}
