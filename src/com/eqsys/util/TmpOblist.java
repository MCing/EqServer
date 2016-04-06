package com.eqsys.util;

import java.util.Date;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.eqsys.application.EqServer;
import com.eqsys.model.RecvInfo;

/**
 * 暂时用于管理界面表格的ObservableList对象
 *
 */
public class TmpOblist {
	
	//接收数据显示列表
	private static ObservableList<RecvInfo> recvInfoList = FXCollections.observableArrayList();
	
	public static ObservableList<RecvInfo>  getRecvObserList(){
		return recvInfoList;
	}
	
	/**
	 * 
	 * @param id     台站id
	 * @param type   消息类型
	 */
	public static void addToRecvList(String id, String type){
		
		RecvInfo info = new RecvInfo(id, type, UTCTimeUtil.getStringTime(new Date().getTime()));
		synchronized (recvInfoList) {
			//列表超过一定数目后应该保存起来,然后清空
			if(recvInfoList.size() > 1000){
				//保存(未完成),清空
				recvInfoList.clear();
			}
			recvInfoList.add(info);
		}
		
	}

}
