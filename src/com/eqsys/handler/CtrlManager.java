package com.eqsys.handler;

import io.netty.channel.socket.SocketChannel;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import javafx.collections.ObservableList;

import com.eqsys.dao.ClientInfoDao;
import com.eqsys.model.ClientInfo;
import com.eqsys.model.CtrlEvent;
import com.eqsys.msg.CommandReq;
import com.eqsys.msg.CommandResp;
import com.eqsys.msg.EqMessage;
import com.eqsys.msg.MsgConstant;
import com.eqsys.msg.ThresholdReq;
import com.eqsys.msg.TransModeReq;
import com.eqsys.util.ClientConnList;
import com.eqsys.util.ClientWindowMgr;
import com.eqsys.util.ParseUtil;

public class CtrlManager {
	
	private Logger log = Logger.getLogger(CtrlManager.class);

	private static CtrlManager mThis;
	
//	private ClientInfoDao clientInfoDao;
	private LinkedList<CtrlEvent> list;
	
	public static CtrlManager getMagager(){
		if(mThis == null){
			mThis = new CtrlManager();
		}
		return mThis;
	}
	
	private CtrlManager(){
		
		list = new LinkedList<CtrlEvent>();
	}
	
	/** 负责记录并发送控制命令 
	 * @event 	要发送的控制事件
	 * 
	 */
	public void ctrlReq(CtrlEvent event){
		
		if(event == null){ return; }
		EqMessage msg = event.getReqMsg();
		CommandReq req = (CommandReq) msg.getBody();
		if(req.getSubCommand() == MsgConstant.CMD_TRANSMODE || req.getSubCommand()  == MsgConstant.CMD_TRGTHRESHOLD){
			list.add(event);
		}
		
		String clientId = event.getClient().getStationId();
		SocketChannel clientChannel = ClientConnList.getInstance().getChannelById(clientId);
		clientChannel.writeAndFlush(msg);
	}
	/** 负责接收控制命令的回应,并做相应处理 
	 * 	@respMsg 	控制应答消息包
	 * 
	 */
	public void ctrlResp(EqMessage respMsg){
		
		CommandResp resp = (CommandResp) respMsg.getBody();
//		log.info(respMsg.getHeader().getStationId()+" response: " + resp.getRspState() + " detail: " + resp.getStateDetil());
		//这些控制需要做额外数据更新(显示列表和数据库)
		if(resp.getSubCommand() == MsgConstant.CMD_TRANSMODE || resp.getSubCommand() == MsgConstant.CMD_TRGTHRESHOLD){
			ctrlResp0(respMsg.getHeader().getPid(), respMsg.getHeader().getStationId(), resp);
		}
		
	}
	/** 对控制命令的回应进一步处理
	 * @pid 		包序号
	 * @stationId 	台站Id
	 * @resp		控制应答数据包 
	 *
	 */
	private void ctrlResp0(int pid, String stationId, CommandResp resp) {
		
		CtrlEvent event = getEvent(pid, stationId);
		if(event == null){	return;	}
		if(resp.getRspState() == 0 || resp.getRspState() == 1){   //应答成功

			ObservableList<ClientInfo> obList = ClientConnList.getInstance().getObservableList();
			ClientInfo client = event.getClient();
			int index = obList.indexOf(client);
//			update clientinfo database
			if(resp.getSubCommand() == MsgConstant.CMD_TRANSMODE){
				TransModeReq req = (TransModeReq) event.getReqMsg().getBody();
				client.setTransMode(ParseUtil.parseTransMode(req.getSubTransMode()));
				ClientInfoDao.updateTransMode(client.getStationId(), req.getSubTransMode());
			}else{
				ThresholdReq req = (ThresholdReq) event.getReqMsg().getBody();
				client.setThreshold(req.getTriggleThreshold());
				ClientInfoDao.updateThreshold(client.getStationId(), req.getTriggleThreshold());
			}
			//update clientinfo tableview
			obList.set(index, client);
			
			ClientWindowMgr.getClientWindowMgr().update(client, resp.getStateDetil());
		}
		list.remove(event);
	}

	/** 通过包序号和台站id从事件列表获取事件对象 
	 * @pid	包序号
	 * @stationId 台站Id
	 */
	private CtrlEvent getEvent(int pid, String stationId) {

		for(CtrlEvent event : list){
			if(pid == event.getReqMsg().getHeader().getPid() || event.getClient().getStationId().equals(stationId)){
				return event;
			}
		}
		return null;
	}

}
