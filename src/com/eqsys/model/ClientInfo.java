package com.eqsys.model;



import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ClientInfo {
	
	private StringProperty stationId;
	private StringProperty transMode;
	private FloatProperty longitude;			//经度 度＊100000
	private FloatProperty latitude;				//纬度 度＊100000
	private IntegerProperty altitude;				//高程 单位M
	private IntegerProperty sensitivity;			//灵敏度
	private IntegerProperty threshold;				//触发阀值
	
	//下列属性不用于tableview显示
	private short permit;
	private int lastPid;
	
	
	
	public ClientInfo(String id, String transMode, int longitude, int latitude, int altitude, int sensitivity, int triggerThreshold){
		this.stationId = new SimpleStringProperty(id);
		this.transMode = new SimpleStringProperty(transMode);
		this.longitude = new SimpleFloatProperty(longitude);
		this.latitude = new SimpleFloatProperty(latitude);
		this.altitude = new SimpleIntegerProperty(altitude);
		this.sensitivity = new SimpleIntegerProperty(sensitivity);
		this.threshold = new SimpleIntegerProperty(triggerThreshold);
	}
	public ClientInfo(){
		this.stationId = new SimpleStringProperty();
		this.transMode = new SimpleStringProperty();
		this.longitude = new SimpleFloatProperty();
		this.latitude = new SimpleFloatProperty();
		this.altitude = new SimpleIntegerProperty();
		this.sensitivity = new SimpleIntegerProperty();
		this.threshold = new SimpleIntegerProperty();
	}
	
	//getter , settr and propertygeter
	public String getStationId() {
		return stationId.get();
	}
	public void setStationId(String id) {
		this.stationId.set(id);
	}
	public StringProperty stationIdProperty(){
		return stationId;
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

	public float getLongitude() {
		return longitude.get();
	}
	public void setLongitude(float longitude) {
		this.longitude.set(longitude);
	}
	public FloatProperty longitudeProperty(){
		return longitude;
	}

	public float getLatitude() {
		return latitude.get();
	}
	public void setLatitude(float latitude) {
		this.latitude.set(latitude);;
	}
	public FloatProperty latitudeProperty(){
		return latitude;
	}
	
	public int getAltitude() {
		return altitude.get();
	}
	public void setAltitude(int altitude) {
		this.altitude.set(altitude);
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

	public int getThreshold() {
		return threshold.get();
	}
	public void setThreshold(int triggerThreshold) {
		this.threshold.set(triggerThreshold);;
	}
	public IntegerProperty thresholdProperty(){
		return threshold;
	}
	
	public short getPermit(){
		return this.permit;
	}
	public void setPermit(short permit){
		this.permit = permit;
	}
	
	public int getLastPid() {
		return lastPid;
	}
	public void setLastPid(int lastPid) {
		this.lastPid = lastPid;
	}
}
