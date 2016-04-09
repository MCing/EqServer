package com.eqsys.util;

import java.util.Random;

import com.eqsys.consts.Constants;
import com.eqsys.msg.CommandReq;
import com.eqsys.msg.EqMessage;
import com.eqsys.msg.Header;
import com.eqsys.msg.MsgConstant;
import com.eqsys.msg.PeriodDataReq;
import com.eqsys.msg.RegResp;
import com.eqsys.msg.ThresholdReq;
import com.eqsys.msg.TransModeReq;
import com.eqsys.msg.TriggerReq;

public class DataBuilder {

	private static Random random = new Random();
	
	
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
//	public static EqMessage buildTransModeMsg(int id, String stationId, short mode){
//	
//		
//		EqMessage msg = new EqMessage();
//		Header header = new Header();
//		header.setMsgType(MsgConstant.TYPE_CC);
//		header.setPid(id);
//		header.setServerId(Constants.ServerId);
//		header.setStationId(stationId);
//		
//		TransModeReq tmMsg = new TransModeReq();
//		tmMsg.setSubCommand(MsgConstant.CMD_TRANSMODE);
//		tmMsg.setSubTransMode(mode);
//		
//		msg.setHeader(header);
//		msg.setBody(tmMsg);
//		
//		return msg;
//	}
	
	/** 构造申请时间段时间段数据*/
//	public static EqMessage buildlPeriodDataReqMsg(int id, String stationId, int period, int timeCode){
//		
//		EqMessage msg = new EqMessage();
//		Header header = new Header();
//		header.setMsgType(MsgConstant.TYPE_CC);
//		header.setPid(id);
//		header.setServerId(Constants.ServerId);
//		header.setStationId(stationId);
//		
//		PeriodDataReq pdMsg = new PeriodDataReq();
//		pdMsg.setSubCommand(MsgConstant.CMD_PERIODDATA);
//		pdMsg.setPeriod(period);
//		pdMsg.setTimeCode(timeCode);
//		
//		msg.setHeader(header);
//		msg.setBody(pdMsg);
//		
//		return msg;
//	
//	}
	
	/** 构造设置触发阈值设定 消息包*/
//	public static EqMessage buildThresholdReqMsg(int id, String stationId, short threhold){
//		
//		EqMessage msg = new EqMessage();
//		Header header = new Header();
//		header.setMsgType(MsgConstant.TYPE_CC);
//		header.setPid(id);
//		header.setServerId(Constants.ServerId);
//		header.setStationId(stationId);
//		
//		ThresholdReq thMsg = new ThresholdReq();
//		thMsg.setSubCommand(MsgConstant.CMD_TRGTHRESHOLD);
//		thMsg.setTriggleThreshold(threhold);
//		
//		msg.setHeader(header);
//		msg.setBody(thMsg);
//		
//		return msg;
//	}
	
	/** 构造时间段触发控制*/
//	public static EqMessage buildTriggleReqMsg(int id, String stationId, int startTime, int endTime){
//		
//		EqMessage msg = new EqMessage();
//		Header header = new Header();
//		header.setMsgType(MsgConstant.TYPE_CC);
//		header.setPid(id);
//		header.setServerId(Constants.ServerId);
//		header.setStationId(stationId);
//		
//		TriggerReq trMsg = new TriggerReq();
//		trMsg.setSubCommand(MsgConstant.CMD_TRIGGER);
//		trMsg.setStartTime(startTime);
//		trMsg.setEndTime(endTime);
//		
//		msg.setHeader(header);
//		msg.setBody(trMsg);
//		
//		return msg;
//	}
	
	public static EqMessage buildCtrlReq(String stationId, CommandReq req){
		
		EqMessage msg = new EqMessage();
		Header header = new Header();
		header.setMsgType(MsgConstant.TYPE_CC);
		header.setPid(random.nextInt());  //包序号随机
		header.setServerId(Constants.ServerId);
		header.setStationId(stationId);
		
		msg.setHeader(header);
		msg.setBody(req);
		
		return msg;
	}
}
