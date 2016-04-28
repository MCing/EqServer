package com.eqsys.handler;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.eqsys.msg.EqMessage;
import com.eqsys.msg.Header;
import com.eqsys.msg.MsgConstant;
import com.eqsys.msg.RegReq;
import com.eqsys.msg.TransModeReq;
import com.eqsys.security.Authenticator;
import com.eqsys.util.ClientConnList;
import com.eqsys.util.DataBuilder;
import com.eqsys.util.TmpOblist;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

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

		EqMessage eqMsg = (EqMessage) msg;
		Header hMsg = eqMsg.getHeader();
		String msgType = hMsg.getMsgType();
		//处理注册信息
		if (msgType.equals(MsgConstant.TYPE_RE)) {

			RegReq regMsg = (RegReq) eqMsg.getBody();
			TmpOblist.addToRecvList(hMsg.getStationId(), "注册包");
			//验证,防止重复注册
			//关闭资源,不做应答
			if(ClientConnList.getInstance().containsClient(hMsg.getStationId())){
				log.error(hMsg.getStationId()+" 重复登录");
				ctx.close();
				return;
			}
			// --------注册包验证、响应-----------
			if (Authenticator.validateReg(regMsg.getAuthenCode())) { // 验证认证密码
				
				ClientConnList.getInstance().add(eqMsg,(SocketChannel) ctx.channel());
				// 应答注册包
				send(ctx, DataBuilder.buildRegRspMsg(hMsg.getPid(),
						MsgConstant.REG_SUCCESS, hMsg.getStationId(),
						hMsg.getServerId()));
				TimeUnit.MILLISECONDS.sleep(500);
				// 注册成功后,设定客户端传输模式(默认不改变传输模式)
				TransModeReq req = new TransModeReq();
				req.setSubCommand(MsgConstant.CMD_TRANSMODE);
				req.setSubTransMode(MsgConstant.CMD_NULL);
				send(ctx, DataBuilder.buildCtrlReq(hMsg.getStationId(), req));
				log.info(hMsg.getStationId()+" 注册成功");
			} else {  //注册失败
				send(ctx, DataBuilder.buildRegRspMsg(hMsg.getPid(),
						MsgConstant.REG_FAILURE, hMsg.getStationId(),
						hMsg.getServerId()));
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
		log.error(ClientConnList.getInstance().getIdByChannel((SocketChannel) ctx.channel())+" 意外中断: "+cause.getMessage() + "  toString:"+cause.toString());
		cause.printStackTrace();
		ctx.close();
	}
	
}
