package com.eqsys.msg.data;

public class StatusData extends BaseData{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long startTime;			//计算峰峰值起始时间（绝对UTC时间）
	private int dur;				//峰峰值计算时间长度，单位：秒
	private int[] udPeakValue;		//计算时间长度内每一秒的加速度振幅峰峰值      40bYTES???????????
	private int[] ewPeakValue;		//计算时间长度内每一秒的加速度振幅峰峰值
	private int[] nsPeakValue;		//计算时间长度内每一秒的加速度振幅峰峰值
	
	//getter and setter
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public int getDur() {
		return dur;
	}
	public void setDur(int dur) {
		this.dur = dur;
	}
	public int[] getUdPeakValue() {
		return udPeakValue;
	}
	public void setUdPeakValue(int[] udPeakValue) {
		this.udPeakValue = udPeakValue;
	}
	public int[] getEwPeakValue() {
		return ewPeakValue;
	}
	public void setEwPeakValue(int[] ewPeakValue) {
		this.ewPeakValue = ewPeakValue;
	}
	public int[] getNsPeakValue() {
		return nsPeakValue;
	}
	public void setNsPeakValue(int[] nsPeakValue) {
		this.nsPeakValue = nsPeakValue;
	}
}
