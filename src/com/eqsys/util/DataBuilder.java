package com.eqsys.util;

import java.util.Random;

import com.eqsys.consts.Constants;
import com.eqsys.msg.MsgConstant;
import com.eqsys.msg.PeriodReqMsg;
import com.eqsys.msg.PeriodTriggleCtrlMsg;
import com.eqsys.msg.RegRspMsg;
import com.eqsys.msg.TransModeMsg;
import com.eqsys.msg.TriggerSettingMsg;

public class DataBuilder {

	Random random = new Random();
	
	
	/** 构造注册应答消息包 */
	public static RegRspMsg buildRegRspMsg(int packetId,short authenstate, String serverId, String stationId){
		
		RegRspMsg msg = new RegRspMsg();
		
		msg.setMsgType("RR");
		msg.setPacketId(packetId);
		msg.setStId(stationId);
		msg.setSrvId(serverId);
		msg.setAuthenState(authenstate);
		msg.setLastPacketNo(0xffffffff);
		return msg;
	}
	
	/** 构造传输模式控制包 */
	public static TransModeMsg buildTransModeMsg(int id, String stationId, short mode){
	
		TransModeMsg msg = new TransModeMsg();
		msg.setMsgType(MsgConstant.TYPE_CC);
		msg.setPacketId(id);
		msg.setSrvId(Constants.ServerId);
		msg.setStId(stationId);
		msg.setSubCommand(MsgConstant.CMD_TRANSMODE);
		msg.setSubTransMode(mode);
		return msg;
	}
	
	/** 构造申请时间段时间段数据*/
	public static PeriodReqMsg buildCtrlPeriodReqMsg(int id, String stationId, int period, int timeCode){
		
		PeriodReqMsg msg = new PeriodReqMsg();
		
		msg.setMsgType(MsgConstant.TYPE_CC);
		msg.setPacketId(id);
		msg.setSrvId(Constants.ServerId);
		msg.setStId(stationId);
		msg.setSubCommand(MsgConstant.CMD_PERIODDATA);
		msg.setPeriod(period);
		msg.setTimeCode(timeCode);
		
		return msg;
	
	}
	
	/** 构造触发控制 消息包*/
	public static TriggerSettingMsg buildTriggerSettingMsg(int id, String stationId, short triggleThreshold){
			
		TriggerSettingMsg msg = new TriggerSettingMsg();
			
			msg.setMsgType(MsgConstant.TYPE_CC);
			msg.setPacketId(id);
			msg.setSrvId(Constants.ServerId);
			msg.setStId(stationId);
			msg.setSubCommand(MsgConstant.CMD_TRGTHRESHOLD);
			msg.setTriggleThreshold(triggleThreshold);
			return msg;
		}
	
	/** 构造时间段触发控制*/
	public static PeriodTriggleCtrlMsg buildPeriodTriggleCtrlMsg(int id, String stationId, int startTime, int endTime){
		
		PeriodTriggleCtrlMsg msg = new PeriodTriggleCtrlMsg();
		
		msg.setMsgType(MsgConstant.TYPE_CC);
		msg.setPacketId(id);
		msg.setSrvId(Constants.ServerId);
		msg.setStId(stationId);
		msg.setSubCommand(MsgConstant.CMD_TRGPRIOD);
		msg.setEndTime(endTime);
		msg.setStartTime(startTime);
		return msg;
	}
}
