package com.eqsys.dao;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.eqsys.msg.EqMessage;
import com.eqsys.msg.data.WavefData;
import com.eqsys.util.JDBCHelper;

public class WavefDataDao {

	private String mTableName = "wavefdata_t";
	private String mInsertSql = "insert into " + mTableName
			+ "(id, type, qid, stationid, localid, channid, starttime, samcount, samfactor, sammul, actid, iocflag, dataqflag, blockcount, timecorr, dataoffs, subblockoffs, subheadtype, nextblockid, codeformat, byteorder, datalen, subblocktype, dimension, sensfactor, datablock) "
			+ "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

	public boolean save(EqMessage msg) {

		boolean ret = false;
		PreparedStatement preStat = null;
		Connection conn = null;
//		if (msg == null) {
//			return false;
//		}
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
			preStat.setByte(12,t.getIocFlag());
			preStat.setByte(13,t.getDqFlag());
			preStat.setByte(14,t.getBlockNum());
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
//			System.out.println(preStat.toString());
			int insertRet = preStat.executeUpdate();
			ret = true;
		} catch (SQLException e) {

			ret = false;
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
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

	public boolean delete(WavefData t) {
		// TODO Auto-generated method stub
		return false;
	}
}
