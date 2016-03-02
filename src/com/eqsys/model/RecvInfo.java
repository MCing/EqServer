package com.eqsys.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/** 
 * 配合TableView使用的数据类型
 * 用于显示接收数据信息
 *
 */
public class RecvInfo {

	private StringProperty srcId;
	private StringProperty type;
	private StringProperty time;
	
	public RecvInfo(String srcId, String type, String time) {
		
		this.srcId = new SimpleStringProperty(srcId);
		this.type = new SimpleStringProperty(type);
		this.time = new SimpleStringProperty(time);
	}

	public String getSrcId() {
		return srcId.get();
	}

	public void setSrcId(String srcId) {
		this.srcId.set(srcId);
	}
	public  StringProperty srcIdProperty(){
		return  this.srcId;
	}

	public String getType() {
		return type.get();
	}

	public void setType(String type) {
		this.type.set(type);
	}
	public  StringProperty typeProperty(){
		return  this.type;
	}

	public String getTime() {
		return time.get();
	}

	public void setTime(String time) {
		this.time.set(time);
	}
	public  StringProperty timeProperty(){
		return  this.time;
	}
}
