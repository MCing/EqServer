package com.eqsys.handler;

import org.apache.log4j.Logger;

import com.eqsys.dao.WavefDataDao;
import com.eqsys.msg.EqMessage;
import com.eqsys.msg.Header;
import com.eqsys.msg.MsgConstant;
import com.eqsys.util.TmpOblist;

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


		EqMessage eqMsg = (EqMessage) msg;
		Header hMsg = eqMsg.getHeader();
		String msgType = hMsg.getMsgType();
		String typeStr = "";
		
		if(msgType.equals(MsgConstant.TYPE_WC)){    //连续波形数据
			typeStr = "连续波形数据";
//			log.info("连续波形数据包->" + "stid:"+hMsg.getStationId()+" id:"+hMsg.getPid());
			//test 数据库操作封装成任务
			wavefDataDao.save(eqMsg);
			
		}else if(msgType.equals(MsgConstant.TYPE_WT)){	  //触发波形数据
			typeStr = "触发波形数据";
			log.info("触发波形数据包->" + "stid:"+hMsg.getStationId()+" id:"+hMsg.getPid());
		}else if(msgType.equals(MsgConstant.TYPE_WS)){    //时间段波形数据
			log.info("时间段波形数据->" + "stid:"+hMsg.getStationId()+" id:"+hMsg.getPid());
		}else if(msgType.equals(MsgConstant.TYPE_TI)){    //触发信息
			typeStr = "时间段波形数据";
			log.info("时间段波形数据->" + "stid:"+hMsg.getStationId()+" id:"+hMsg.getPid());
		}else if(msgType.equals(MsgConstant.TYPE_SI)){    //状态信息
			typeStr = "状态信息";
			log.info("状态信息包->" + "stid:"+hMsg.getStationId()+" id:"+hMsg.getPid());
		}
		TmpOblist.addToRecvList(hMsg.getStationId(), typeStr);
		
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}
	
}
