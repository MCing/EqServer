package com.eqsys.model;



import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ClientInfo {
	
	private StringProperty id;
	private StringProperty transMode;
//	private ObjectProperty<Date> linkTime;
	private IntegerProperty longitude;				//经度 度＊100000
	private IntegerProperty latitude;				//纬度 度＊100000
	private IntegerProperty altitude;				//高程 单位M
	private IntegerProperty sensitivity;			//灵敏度
//	private IntegerProperty transMode;			    //传输模式 1  设定为连续传输模式 2  设定为触发传输传波形 3  设定为触发传输不传波形
	private IntegerProperty triggerThreshold;		//触发阀值
	
	private short permit;
	
	
	public ClientInfo(String id, String transMode, int longitude, int latitude, int altitude, int sensitivity, int triggerThreshold){
		this.id = new SimpleStringProperty(id);
		this.transMode = new SimpleStringProperty(transMode);
		this.longitude = new SimpleIntegerProperty(longitude);
		this.latitude = new SimpleIntegerProperty(latitude);
		this.altitude = new SimpleIntegerProperty(altitude);
		this.sensitivity = new SimpleIntegerProperty(sensitivity);
		this.triggerThreshold = new SimpleIntegerProperty(triggerThreshold);
	}
	public ClientInfo(){
		this.id = new SimpleStringProperty();
		this.transMode = new SimpleStringProperty();
		this.longitude = new SimpleIntegerProperty();
		this.latitude = new SimpleIntegerProperty();
		this.altitude = new SimpleIntegerProperty();
		this.sensitivity = new SimpleIntegerProperty();
		this.triggerThreshold = new SimpleIntegerProperty();
	}
	
	//getter , settr and propertygeter
	public String getId() {
		return id.get();
	}
	public void setId(String id) {
		this.id.set(id);
	}
	public StringProperty idProperty(){
		return id;
	}
	
	
	public String getTransMode() {
		return transMode.get();
	}
	public void setTransMode(String transMode) {
		this.transMode.set(transMode);
	}
	public StringProperty transModeProperty(){
		return transMode;
	}

	public int getLongitude() {
		return longitude.get();
	}
	public void setLongitude(int longitude) {
		this.longitude.set(longitude);
	}
	public IntegerProperty longitudeProperty(){
		return longitude;
	}

	public int getLatitude() {
		return latitude.get();
	}
	public void setLatitude(int latitude) {
		this.latitude.set(latitude);;
	}
	public IntegerProperty latitudeProperty(){
		return latitude;
	}
	
	public int getAltitude() {
		return altitude.get();
	}
	public void setAltitude(int altitude) {
		this.altitude.set(altitude);;
	}

	public int getSensitivity() {
		return sensitivity.get();
	}
	public void setSensitivity(int sensitivity) {
		this.sensitivity.set(sensitivity);;
	}
	public IntegerProperty sensitivityProperty(){
		return sensitivity;
	}

	public int getTriggerThreshold() {
		return triggerThreshold.get();
	}

	public void setTriggerThreshold(int triggerThreshold) {
		this.triggerThreshold.set(triggerThreshold);;
	}
	public IntegerProperty triggerThresholdProperty(){
		return triggerThreshold;
	}
	
//	public Date getLinkTime() {
//		return linkTime.get();
//	}
//	public void setLinkTime(Date linkTime) {
//		this.linkTime.set(linkTime); ;
//	}
//	public ObjectProperty linkTimeProperty(){
//		return linkTime;
//	}
	
	public short getPermit(){
		return this.permit;
	}
	public void setPermit(short permit){
		this.permit = permit;
	}

}
