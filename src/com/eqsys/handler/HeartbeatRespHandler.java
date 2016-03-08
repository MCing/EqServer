package com.eqsys.handler;

import com.eqsys.consts.Constants;
import com.eqsys.msg.BaseMsg;
import com.eqsys.msg.MsgConstant;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutException;

/**
 * 心跳请求应答
 * 处理心跳超时
 */
public class HeartbeatRespHandler extends ChannelHandlerAdapter {

	
	private BaseMsg nullMsg;
	
	public HeartbeatRespHandler() {

		nullMsg = new BaseMsg();
		nullMsg.setMsgType(MsgConstant.TYPE_HBR);
		nullMsg.setSrvId(Constants.ServerId);
		nullMsg.setStId(null);
	}
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		BaseMsg revMsg = (BaseMsg) msg;
		String msgType = revMsg.getMsgType();
		if(MsgConstant.TYPE_HB.equals(msgType)){
			send(ctx, nullMsg);
		}else{
			ctx.fireChannelRead(msg);
		}
	}
	
	/** 发送数据出口
	 * 
	 * @param ctx   ChannelHandlerContext对象
	 * @param msg   数据包对象
	 */
	private void send(ChannelHandlerContext ctx, Object msg){
		
		if(ctx != null && msg != null){
			ChannelFuture f = ctx.writeAndFlush(msg);
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		if (cause instanceof ReadTimeoutException) {
            // The connection was OK but there was no traffic for last period.
			cause.printStackTrace();
        } else {
            cause.printStackTrace();
        }
		ctx.close();
	}
}
