package com.eqsys.util;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.eqsys.dao.ClientInfoDao;
import com.eqsys.model.ClientInfo;
import com.eqsys.msg.EqMessage;
import com.eqsys.msg.RegReq;
import com.sun.media.jfxmediaimpl.platform.Platform;

import io.netty.channel.socket.SocketChannel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 客户端连接表，当客户端连接、断开连接时，客户端列表的操作都在该类中完成
 *
 */
public class ClientConnList {
	
	private Logger log = Logger.getLogger(ClientConnList.class);

	//单例模式
	private static ClientConnList list;
	/**
	 * 需要维护两个表：一个是客户端id与channelsocket 的映射表，管理链路的连接；
	 * 第二个用于填充javafx表格的客户信息列表；
	 * 
	 */
	private Map<String, SocketChannel> map;
	private ObservableList<ClientInfo> clientList;
	//用于显示在线数量
	private StringProperty onLineNumber = new SimpleStringProperty("0");  
	private ClientConnList(){
		map = new ConcurrentHashMap<String, SocketChannel>();
		clientList = FXCollections.observableArrayList();
	}
	
	public static ClientConnList getInstance(){
		if(list == null){
			list = new ClientConnList();
		}
		return list;
	}
	
	public void add(EqMessage client, SocketChannel socketChannel){
		
		String stationId = client.getHeader().getStationId();
		RegReq msg = (RegReq) client.getBody();
		map.put(stationId, socketChannel);
		
		ClientInfo info = new ClientInfo();
		info.setStationId(stationId);
		info.setAltitude(msg.getAltitude());
		info.setLatitude((float)msg.getLatitude()/100000);
		info.setLongitude((float)msg.getLongitude()/100000);
		info.setSensitivity(msg.getSensitivity());
		info.setTransMode(ParseUtil.parseTransMode(msg.getTransMode()));
		info.setThreshold(msg.getTriggerThreshold());
		info.setPermit(msg.getCtrlAuthority());
		
		clientList.add(info);
		//加入数据库
		ClientInfoDao.add(info);
		updateOnlineNumber();
	}
	
	public void remove(SocketChannel socketChannel){
		for(Map.Entry<String,SocketChannel> entry : map.entrySet()){
			if(entry.getValue() == socketChannel){
				map.remove(entry.getKey());
				Iterator<ClientInfo> it = clientList.iterator();
				while(it.hasNext()){
					if(it.next().getStationId().equals(entry.getKey())){
						it.remove();
						TmpOblist.addToSysEventList(entry.getKey(), "退出");
						log.error("客户端: "+entry.getKey()+" 退出");
						updateOnlineNumber();
						break;
					}
				}
				break;
			}
		}
	}
	
	public ObservableList<ClientInfo> getObservableList(){
		return this.clientList;
	}


	public boolean containsClient(String id){
		
		return map.containsKey(id);
	}
	
	public String getIdByChannel(SocketChannel channel){
		
		for(Map.Entry<String, SocketChannel> entry : map.entrySet()){
			if(entry.getValue() == channel){
				return entry.getKey();
			}
		}
		return null;
	}
	
	public SocketChannel getChannelById(String id){
		return map.get(id);
	}
	
	public StringProperty getOnlineNumber(){
		
		return onLineNumber;
	}
	
	private void updateOnlineNumber(){
		javafx.application.Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				onLineNumber.setValue(String.valueOf(clientList.size()));
			}
		});
	}
	
	/** 
	 * 获取客户端状态
	 * @param stationid	台站号
	 * @return	true:已连接	false:未连接
	 */
	public boolean getState(String stationid){
		return map.containsKey(stationid);
	}
}
