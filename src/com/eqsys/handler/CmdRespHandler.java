package com.eqsys.handler;

import org.apache.log4j.Logger;

import com.eqsys.msg.CommandResp;
import com.eqsys.msg.EqMessage;
import com.eqsys.msg.Header;
import com.eqsys.msg.MsgConstant;
import com.eqsys.util.TmpOblist;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 处理控制应答消息 not finish
 *
 */
public class CmdRespHandler extends ChannelHandlerAdapter {

	private Logger log = Logger.getLogger(CmdRespHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		EqMessage eqMsg = (EqMessage) msg;
		Header hMsg = eqMsg.getHeader();
		String msgType = hMsg.getMsgType();

		if (msgType.equals(MsgConstant.TYPE_CR)) { // 控制应答
			CommandResp ccrsg = (CommandResp) eqMsg.getBody();
			TmpOblist.addToSysEventList(hMsg.getStationId(), ccrsg.getStateDetil());
			CtrlManager.getMagager().ctrlResp(eqMsg);
//			TmpOblist.addToRecvList(hMsg.getStationId(), "控制应答");
		} else {
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
