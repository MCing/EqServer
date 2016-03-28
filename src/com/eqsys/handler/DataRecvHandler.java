package com.eqsys.handler;

import java.util.Date;

import org.apache.log4j.Logger;

import com.eqsys.application.EqServer;
import com.eqsys.dao.WavefDataDao;
import com.eqsys.model.RecvInfo;
import com.eqsys.msg.BaseMsg;
import com.eqsys.msg.MsgConstant;
import com.eqsys.msg.StatusDataMsg;
import com.eqsys.msg.TriggleMsg;
import com.eqsys.msg.WavefDataMsg;
import com.eqsys.util.TmpOblist;
import com.eqsys.util.UTCTimeUtil;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 处理数据接收,存储
 *
 */
public class DataRecvHandler extends ChannelHandlerAdapter {


	private Logger log = Logger.getLogger(DataRecvHandler.class);
	
	private WavefDataDao wavefDataDao = new WavefDataDao();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		BaseMsg revMsg = (BaseMsg) msg;
		String msgType = revMsg.getMsgType();
		String typeStr = "";
		
		if(msgType.equals(MsgConstant.TYPE_WC)){    //连续波形数据
			typeStr = "连续波形数据";
			WavefDataMsg wavefMsg = (WavefDataMsg) msg;
			log.info("连续波形数据包->" + "stid:"+wavefMsg.getStId()+" id:"+wavefMsg.getWavefData().getId());
			//test 数据库操作封装成任务
			wavefDataDao.save(wavefMsg);
			
		}else if(msgType.equals(MsgConstant.TYPE_WT)){	  //触发波形数据
			typeStr = "触发波形数据";
			WavefDataMsg wavefMsg = (WavefDataMsg) msg;
			log.info("触发波形数据包->" + "stid:"+wavefMsg.getStId()+" id:"+wavefMsg.getWavefData().getId());
		}else if(msgType.equals(MsgConstant.TYPE_WS)){    //时间段波形数据
			WavefDataMsg wavefMsg = (WavefDataMsg) msg;
			log.info("时间段波形数据->" + "stid:"+wavefMsg.getStId()+" id:"+wavefMsg.getWavefData().getId());
		}else if(msgType.equals(MsgConstant.TYPE_TI)){    //触发信息
			typeStr = "时间段波形数据";
			TriggleMsg trgMsg = (TriggleMsg) msg;
			log.info("时间段波形数据->" + "stid:"+trgMsg.getStId()+" id:"+trgMsg.getTriggerData().getId());
		}else if(msgType.equals(MsgConstant.TYPE_SI)){    //状态信息
			typeStr = "状态信息";
			StatusDataMsg statMsg = (StatusDataMsg)msg;
			log.info("状态信息包->" + "stid:"+statMsg.getStId()+" id:"+statMsg.getStatusData().getId());
		}
		TmpOblist.addToRecvList(revMsg.getStId(), typeStr);
		
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}
	
}
