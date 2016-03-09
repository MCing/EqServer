package com.eqsys.handler;

import java.util.Date;

import org.apache.log4j.Logger;

import com.eqsys.application.EqServer;
import com.eqsys.model.RecvInfo;
import com.eqsys.msg.BaseMsg;
import com.eqsys.msg.CtrlCmdRspMsg;
import com.eqsys.msg.MsgConstant;
import com.eqsys.util.TmpOblist;
import com.eqsys.util.UTCTimeUtil;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 处理控制应答消息
 * not finish
 *
 */
public class CmdRespHandler extends ChannelHandlerAdapter {

	private Logger log = Logger.getLogger(CmdRespHandler.class);
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		BaseMsg revMsg = (BaseMsg) msg;
		String msgType = revMsg.getMsgType();
		
		if(msgType.equals(MsgConstant.TYPE_CR)){    //控制应答
			CtrlCmdRspMsg ccrsg = (CtrlCmdRspMsg) msg;
//			log.info("控制应答->" + "stid:"+ccrsg.getStId()+" detil:"+ccrsg.getStateDetil());
			TmpOblist.addToRecvList(ccrsg.getStId(), "控制应答");
		}else{
			ctx.fireChannelRead(msg);
		}
		
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
		super.exceptionCaught(ctx, cause);
	}
	
}
