package com.eqsys.util;

import java.util.Random;

import com.eqsys.consts.Constants;
import com.eqsys.msg.EqMessage;
import com.eqsys.msg.Header;
import com.eqsys.msg.MsgConstant;
import com.eqsys.msg.RegResp;
import com.eqsys.msg.TransModeReq;

public class DataBuilder {

	Random random = new Random();
	
	
	/** 构造注册应答消息包 */
	public static EqMessage buildRegRspMsg(int pid,short authenstate, String serverId, String stationId){
		
		EqMessage msg = new EqMessage();
		Header header = new Header();
		header.setMsgType(MsgConstant.TYPE_RR);
		header.setPid(pid);
		header.setServerId(serverId);
		header.setStationId(stationId);
		
		RegResp rrMsg = new RegResp();
		
		rrMsg.setAuthenState(authenstate);
		rrMsg.setLastPacketNo(0xffffffff);  //默认不续传
		
		msg.setHeader(header);
		msg.setBody(rrMsg);
		
		return msg;
	}
	
	/** 构造传输模式控制包 */
	public static EqMessage buildTransModeMsg(int id, String stationId, short mode){
	
		
		EqMessage msg = new EqMessage();
		Header header = new Header();
		header.setMsgType(MsgConstant.TYPE_CC);
		header.setPid(id);
		header.setServerId(Constants.ServerId);
		header.setStationId(stationId);
		
		TransModeReq tmMsg = new TransModeReq();
		tmMsg.setSubCommand(MsgConstant.CMD_TRANSMODE);
		tmMsg.setSubTransMode(mode);
		
		msg.setHeader(header);
		msg.setBody(tmMsg);
		
		return msg;
	}
	
	/** 构造申请时间段时间段数据*/
//	public static PeriodReqMsg buildCtrlPeriodReqMsg(int id, String stationId, int period, int timeCode){
//		
//		PeriodReqMsg msg = new PeriodReqMsg();
//		
//		msg.setMsgType(MsgConstant.TYPE_CC);
//		msg.setPacketId(id);
//		msg.setSrvId(Constants.ServerId);
//		msg.setStId(stationId);
//		msg.setSubCommand(MsgConstant.CMD_PERIODDATA);
//		msg.setPeriod(period);
//		msg.setTimeCode(timeCode);
//		
//		return msg;
//	
//	}
	
	/** 构造触发控制 消息包*/
//	public static TriggerSettingMsg buildTriggerSettingMsg(int id, String stationId, short triggleThreshold){
//			
//		TriggerSettingMsg msg = new TriggerSettingMsg();
//			
//			msg.setMsgType(MsgConstant.TYPE_CC);
//			msg.setPacketId(id);
//			msg.setSrvId(Constants.ServerId);
//			msg.setStId(stationId);
//			msg.setSubCommand(MsgConstant.CMD_TRGTHRESHOLD);
//			msg.setTriggleThreshold(triggleThreshold);
//			return msg;
//		}
	
	/** 构造时间段触发控制*/
//	public static PeriodTriggleCtrlMsg buildPeriodTriggleCtrlMsg(int id, String stationId, int startTime, int endTime){
//		
//		PeriodTriggleCtrlMsg msg = new PeriodTriggleCtrlMsg();
//		
//		msg.setMsgType(MsgConstant.TYPE_CC);
//		msg.setPacketId(id);
//		msg.setSrvId(Constants.ServerId);
//		msg.setStId(stationId);
//		msg.setSubCommand(MsgConstant.CMD_TRGPRIOD);
//		msg.setEndTime(endTime);
//		msg.setStartTime(startTime);
//		return msg;
//	}
}
