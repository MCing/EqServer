package com.eqsys.util;

import java.util.HashMap;
import java.util.Map;

import com.eqsys.model.ClientInfo;
import com.eqsys.view.ClientDetailController;

import javafx.application.Platform;

public class ClientWindowMgr {
	
	
	private static ClientWindowMgr mThis;
	private Map<String, ClientDetailController> windowMap;
	
	private ClientWindowMgr(){
		windowMap = new HashMap<String, ClientDetailController>();
	}
	
	public static ClientWindowMgr getClientWindowMgr(){
		if(mThis == null){
			mThis = new ClientWindowMgr();
		}
		return mThis;
	}
	
	
	public void update(final ClientInfo client, final String msg){
		final ClientDetailController controller = windowMap.get(client.getStationId());
		if(controller != null){
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					controller.update(client, msg);
				}
			});
			
		}
	}
	
	public void add(String stationid, ClientDetailController controller){
		windowMap.put(stationid, controller);
	}
	
	public boolean isExist(String stationid){
		
		return windowMap.containsKey(stationid);
	}

	public void open(String stationId) {
		ClientDetailController controller = windowMap.get(stationId);
		if(controller != null){
			controller.show();
		}else{
			System.err.println("ClientWindowMgr error:open error");
		}
	}
}
