package com.eqsys.dao;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.eqsys.model.ClientInfo;
import com.eqsys.model.WavefDataModel;
import com.eqsys.msg.EqMessage;
import com.eqsys.msg.data.WavefData;
import com.eqsys.util.JDBCHelper;
import com.eqsys.util.ParseUtil;
import com.eqsys.util.UTCTimeUtil;

public class WavefDataDao {

	private static String mTableName = "wavefdata_t";
	private static String mInsertSql = "insert into "
			+ mTableName
			+ "(id, type, qid, stationid, localid, channid, starttime, samcount, samfactor, sammul, actid, iocflag, dataqflag, blockcount, timecorr, dataoffs, subblockoffs, subheadtype, nextblockid, codeformat, byteorder, datalen, subblocktype, dimension, sensfactor, datablock) "
			+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

	public static boolean save(EqMessage msg) {

		boolean ret = false;
		PreparedStatement preStat = null;
		Connection conn = null;
		try {
			WavefData t = (WavefData) msg.getBody();
			conn = JDBCHelper.getDBConnection();
			preStat = conn.prepareStatement(mInsertSql);
			preStat.setInt(1, t.getId());
			preStat.setString(2, msg.getHeader().getMsgType());
			preStat.setString(3, t.getQuality());
			preStat.setString(4, msg.getHeader().getStationId());
			preStat.setString(5, t.getLocId());
			preStat.setString(6, t.getChannId());
			preStat.setLong(7, t.getStartTime());
			preStat.setShort(8, t.getSamNum());
			preStat.setShort(9, t.getSamFactor());
			preStat.setShort(10, t.getSamMul());
			preStat.setByte(11, t.getActFlag());
			preStat.setByte(12, t.getIocFlag());
			preStat.setByte(13, t.getDqFlag());
			preStat.setByte(14, t.getBlockNum());
			preStat.setInt(15, t.getTimeCorr());
			preStat.setShort(16, t.getStartOffs());
			preStat.setShort(17, t.getSubBlockOffs());
			preStat.setShort(18, t.gethBlockType());
			preStat.setShort(19, t.getBlockId());
			preStat.setByte(20, t.getCodeFormat());
			preStat.setByte(21, t.getOrder());
			preStat.setByte(22, t.getDataLen());
			preStat.setShort(23, t.getBlockType());
			preStat.setByte(24, t.getDim());
			preStat.setInt(25, t.getSensFactor());
			preStat.setBlob(26, new ByteArrayInputStream(t.getDataBlock()));
			// System.out.println(preStat.toString());
			int insertRet = preStat.executeUpdate();
			ret = true;
		} catch (SQLException e) {

			ret = false;
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (preStat != null) {
					preStat.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// 释放连接
			JDBCHelper.closeDBConnection(conn);
		}
		return ret;
	}

	/** 获取时间段波形数据记录 */
	public static List<WavefDataModel> getRecord(String stationid, long starttime, long endtime) {

		String sql = "select id, type, starttime  from " + mTableName
				+ " where stationid = ? and starttime > ? and starttime < ?;";
		PreparedStatement preStat = null;
		Connection conn = null;
		ArrayList<WavefDataModel> list = new ArrayList<WavefDataModel>();
		try {
			conn = JDBCHelper.getDBConnection();
			preStat = conn.prepareStatement(sql);
			preStat.setString(1, stationid);
			preStat.setLong(2, starttime);
			preStat.setLong(3, endtime);
			ResultSet results = preStat.executeQuery();
			while (results.next()) {
				WavefDataModel node = new WavefDataModel();
				node.setId(results.getInt("id"));
				node.setType(ParseUtil.parseDataType(results.getString("type")));
				node.setTime(UTCTimeUtil.timeFormat1(results.getLong("starttime")));
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

	/** 获取时间段内的记录数量  
	 * 
	 * @param stationid	指定台站
	 * @param starttiem	指定开始时间,毫秒
	 * @param endtime	指定结束时间,毫秒
	 * @return			总记录数量
	 */
	public static int getCount(String stationid, long starttime, long endtime) {

		String sql = "select count(id)  from " + mTableName
				+ " where stationid = ? and starttime > ? and starttime < ?;";
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
				//test
				System.err.println("getCount:"+count);
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
