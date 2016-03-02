package com.eqsys.handler;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.eqsys.dao.WavefDataDao;
import com.eqsys.model.RecvInfo;
import com.eqsys.msg.BaseMsg;
import com.eqsys.msg.CtrlCmdRspMsg;
import com.eqsys.msg.MsgConstant;
import com.eqsys.msg.RegMsg;
import com.eqsys.msg.StatusDataMsg;
import com.eqsys.msg.TriggleMsg;
import com.eqsys.msg.WavefDataMsg;
import com.eqsys.security.Authenticator;
import com.eqsys.server.EqServer;
import com.eqsys.util.ClientConnList;
import com.eqsys.util.DataBuilder;
import com.eqsys.util.UTCTimeUtil;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;


public class ServerHandler extends ChannelHandlerAdapter {
	
	private Logger log = Logger.getLogger(ServerHandler.class);
	
	private WavefDataDao wavefDataDao = new WavefDataDao();
	private EqServer main;
	
	public ServerHandler(EqServer eqServer) {

		this.main = eqServer;
	}

	/**
	 * 链路创建成功后调用，在channelregister之后
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
//		System.out.println("channel active:"+ctx.channel().toString());
	}
	
	/**
	 * 自定义业务处理handler
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		BaseMsg revMsg = (BaseMsg) msg;
		String msgType = revMsg.getMsgType();
		String typeStr = "";
		
		
		if(msgType.equals(MsgConstant.TYPE_RE)){   //注册包
			
			typeStr = "注册包";
			RegMsg regMsg = (RegMsg)msg;
			//System.out.println("接收到注册包:"+regMsg.toString());
			log.info("接收到注册包:"+regMsg.getStId());
			
			//--------注册包验证、响应-----------
			if(Authenticator.validateReg(regMsg.getAuthenCode())){   //验证认证密码
				ClientConnList.getInstance().add(regMsg, (SocketChannel) ctx.channel());
				//应答注册包
//				ctx.writeAndFlush(DataBuilder.buildRegRspMsg(regMsg.getId(), MsgConstant.REG_SUCCESS, regMsg.getSrvId(), regMsg.getSrvId()));
				send(ctx, DataBuilder.buildRegRspMsg(regMsg.getId(), MsgConstant.REG_SUCCESS, regMsg.getSrvId(), regMsg.getSrvId()));
				TimeUnit.MILLISECONDS.sleep(1000);
				//注册成功后,立刻设定客户端传输模式(默认不改变传输模式)
//				ctx.writeAndFlush(DataBuilder.buildTransModeMsg(0, regMsg.getStId(), (short) 0));
				send(ctx, DataBuilder.buildTransModeMsg(0, regMsg.getStId(), (short) 0));
				
			}else{
//				ctx.writeAndFlush(DataBuilder.buildRegRspMsg(regMsg.getId(), MsgConstant.REG_FAILURE, regMsg.getSrvId(), regMsg.getSrvId()));
				send(ctx, DataBuilder.buildRegRspMsg(regMsg.getId(), MsgConstant.REG_FAILURE, regMsg.getSrvId(), regMsg.getSrvId()));
			}
			
			//------------------------------
		}else if(msgType.equals(MsgConstant.TYPE_CR)){    //控制应答
			typeStr = "控制应答包";
			CtrlCmdRspMsg ccrsg = (CtrlCmdRspMsg) msg;
//			System.out.println("控制应答:"+ccrsg.getStateDetil());
			log.info("控制应答->" + "stid:"+ccrsg.getStId()+" detil:"+ccrsg.getStateDetil());
		}else if(msgType.equals(MsgConstant.TYPE_WC)){    //连续波形数据
			typeStr = "连续波形数据";
			WavefDataMsg wavefMsg = (WavefDataMsg) msg;
//			System.out.println("连续波形数据包:" + wavefMsg.getWavefData().getId());
			log.info("连续波形数据包->" + "stid:"+wavefMsg.getStId()+" id:"+wavefMsg.getWavefData().getId());
			wavefDataDao.save(wavefMsg);
			
		}else if(msgType.equals(MsgConstant.TYPE_WT)){	  //触发波形数据
			typeStr = "触发波形数据";
			WavefDataMsg wavefMsg = (WavefDataMsg) msg;
			log.info("触发波形数据包->" + "stid:"+wavefMsg.getStId()+" id:"+wavefMsg.getWavefData().getId());
//			System.out.println("触发波形数据包:");
		}else if(msgType.equals(MsgConstant.TYPE_WS)){    //时间段波形数据
			WavefDataMsg wavefMsg = (WavefDataMsg) msg;
			log.info("时间段波形数据->" + "stid:"+wavefMsg.getStId()+" id:"+wavefMsg.getWavefData().getId());
//			System.out.println("时间段波形数据:");
		}else if(msgType.equals(MsgConstant.TYPE_TI)){    //触发信息
			typeStr = "时间段波形数据";
			TriggleMsg trgMsg = (TriggleMsg) msg;
			log.info("时间段波形数据->" + "stid:"+trgMsg.getStId()+" id:"+trgMsg.getTriggerData().getId());
//			System.out.println("触发信息包:");
		}else if(msgType.equals(MsgConstant.TYPE_SI)){    //状态信息
			typeStr = "状态信息";
//			System.out.println("状态信息包:");
			StatusDataMsg statMsg = (StatusDataMsg)msg;
			log.info("状态信息包->" + "stid:"+statMsg.getStId()+" id:"+statMsg.getStatusData().getId());
		}
		
		updateRecvInfoTable(revMsg.getStId(), typeStr);
		
	}
	
	/**
	 * 处理异常，如通信另一方链路意外断开
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		
		log.error(cause.getMessage());
//		System.err.println("exceptionCaught:"+ctx.channel().toString());
		ctx.close();
	}
	
	/**
	 * 处理客户端链路断开后逻辑，无论是正常断开还是因为故障（在exceptionCaught捕获到异常后），这个方法都会被调用
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		
//		System.err.println("channelInactive:"+ctx.channel().toString());
		ClientConnList.getInstance().remove((SocketChannel) ctx.channel());
		ctx.close();
		
	}
	
	/** 收到一个数据包后,更新收到数据显示列表 */
	private void updateRecvInfoTable(String id, String type){
		
		RecvInfo info = new RecvInfo(id, type, UTCTimeUtil.getStringTime(new Date().getTime()));
		EqServer.recvInfoList.add(info);
	}
	
	private void send(ChannelHandlerContext ctx, Object msg){
		
		if(ctx != null && msg != null){
			ctx.writeAndFlush(msg);
		}
	}
}
