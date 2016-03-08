package com.eqsys.handler;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutException;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.eqsys.application.EqServer;
import com.eqsys.model.RecvInfo;
import com.eqsys.msg.BaseMsg;
import com.eqsys.msg.MsgConstant;
import com.eqsys.msg.RegMsg;
import com.eqsys.security.Authenticator;
import com.eqsys.util.ClientConnList;
import com.eqsys.util.DataBuilder;
import com.eqsys.util.TmpOblist;
import com.eqsys.util.UTCTimeUtil;

/**
 * 验证客户端注册处理器
 * 处理连接中断
 * 处理读超时异常
 */
public class RegRespHandler extends ChannelHandlerAdapter {

	private Logger log = Logger.getLogger(RegRespHandler.class);

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {

		BaseMsg revMsg = (BaseMsg) msg;
		String msgType = revMsg.getMsgType();
		
		//处理注册信息
		if (msgType.equals(MsgConstant.TYPE_RE)) {

			RegMsg regMsg = (RegMsg) msg;
			log.info("接收到注册包:" + regMsg.getStId());
			TmpOblist.addToRecvList(regMsg.getStId(), "注册包");
			//验证,防止重复注册
			//关闭资源,不做应答
			if(ClientConnList.getInstance().containsClient(regMsg.getStId())){
				log.error(regMsg.getStId()+"重复登录");
				ctx.close();
				return;
			}
			// --------注册包验证、响应-----------
			if (Authenticator.validateReg(regMsg.getAuthenCode())) { // 验证认证密码
				
				ClientConnList.getInstance().add(regMsg,(SocketChannel) ctx.channel());
				// 应答注册包
				send(ctx, DataBuilder.buildRegRspMsg(regMsg.getId(),
						MsgConstant.REG_SUCCESS, regMsg.getSrvId(),
						regMsg.getSrvId()));
				TimeUnit.MILLISECONDS.sleep(500);
				// 注册成功后,设定客户端传输模式(默认不改变传输模式)
				send(ctx, DataBuilder.buildTransModeMsg(0, regMsg.getStId(),
						(short) 0));

			} else {  //注册失败
				send(ctx, DataBuilder.buildRegRspMsg(regMsg.getId(),
						MsgConstant.REG_FAILURE, regMsg.getSrvId(),
						regMsg.getSrvId()));
				//关闭连接资源
				ctx.close();
			}
		} else {
			ctx.fireChannelRead(msg);
		}

	}


	private void send(ChannelHandlerContext ctx, Object msg) {

		if (ctx != null && msg != null) {
			ctx.writeAndFlush(msg);
		}
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		
		ClientConnList.getInstance().remove((SocketChannel) ctx.channel());
		ctx.close();
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
		log.error("意外中断:"+cause.getMessage());
		ctx.close();
	}
	
}
