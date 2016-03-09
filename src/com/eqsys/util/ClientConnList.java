package com.eqsys.util;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.eqsys.dao.ClientInfoDao;
import com.eqsys.model.ClientInfo;
import com.eqsys.msg.RegMsg;

import io.netty.channel.socket.SocketChannel;
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
	private ClientInfoDao clientInfoDao;
	private ClientConnList(){
		map = new ConcurrentHashMap<String, SocketChannel>();
		clientList = FXCollections.observableArrayList();
		clientInfoDao = new ClientInfoDao();
	}
	
	public static ClientConnList getInstance(){
		if(list == null){
			list = new ClientConnList();
		}
		return list;
	}
	
	public void add(RegMsg client, SocketChannel socketChannel){
		
		map.put(client.getStId(), socketChannel);
		
		ClientInfo info = new ClientInfo();
		info.setId(client.getStId());
		info.setAltitude(client.getAltitude());
		info.setLatitude(client.getLatitude());
		info.setLongitude(client.getLongitude());
		info.setSensitivity(client.getSensitivity());
		info.setTransMode(deTransMode(client.getTransMode()));
		info.setTriggerThreshold(client.getTriggerThreshold());
		clientList.add(info);
		//加入数据库
		clientInfoDao.add(client);
	}
	
	public void remove(SocketChannel socketChannel){
		for(Map.Entry entry : map.entrySet()){
			if(entry.getValue() == socketChannel){
				map.remove(entry.getKey());
				Iterator<ClientInfo> it = clientList.iterator();
				while(it.hasNext()){
					if(it.next().getId().equals(entry.getKey())){
						it.remove();
						
						log.info("客户端:"+entry.getKey()+"退出");
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


	/**
	 * 解析传输模式
	 * 1：连续  2：触发波形 3：触发无波形
	 * @param mode
	 * @return
	 */
	private String deTransMode(int mode){
		switch(mode){
			case 1:
				return "连续传输";
			case 2:
				return "触发传输传波形";
			case 3:
				return "触发传输不传波形";
			default :return "连续传输";
		}
	}
	
	public boolean containsClient(String id){
		
		return map.containsKey(id);
	}
	
	public String getIdByChannel(SocketChannel channel){
		
		String stationId = null;
		for(Map.Entry entry : map.entrySet()){
			if(entry.getValue() == channel){
				Iterator<ClientInfo> it = clientList.iterator();
				while(it.hasNext()){
					stationId = it.next().getId();
					if(stationId.equals(entry.getKey())){
						return stationId;
					}
				}
				break;
			}
		}
		return null;
	}
}
