package com.eqsys.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SysEvent {

	
	private StringProperty srcId;
	private StringProperty event;
	private StringProperty time; 
	
public SysEvent(String srcId, String event, String time) {
		
		this.srcId = new SimpleStringProperty(srcId);
		this.event = new SimpleStringProperty(event);
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

	public String getEvent() {
		return event.get();
	}

	public void setEvent(String event) {
		this.event.set(event);
	}
	public  StringProperty eventProperty(){
		return  this.event;
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
