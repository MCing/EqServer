package com.eqsys.model;

import com.eqsys.msg.CommandReq;
import com.eqsys.msg.EqMessage;


/** 
 * 描述控制事件,交给CtrlManager管理
 *
 */
public class CtrlEvent {

	private ClientInfo client;
	private EqMessage reqMsg;
	public ClientInfo getClient() {
		return client;
	}
	public void setClient(ClientInfo client) {
		this.client = client;
	}
	public EqMessage getReqMsg() {
		return reqMsg;
	}
	public void setReqMsg(EqMessage reqMsg) {
		this.reqMsg = reqMsg;
	}
	
}
