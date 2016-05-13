package com.eqsys.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * UTC 时间转换工具类
 *
 */
public class UTCTimeUtil {

	/** 获取当前UTC 时间
	 * 
	 * @return  当前UTC 时间 ,毫秒
	 */
	public static long getCurrUTCTime(){
		
		return getUTCTimeLong(new Date().getTime());
	}
	
	/**
	 * 根据所给时间,获取UTC时间
	 * @param time
	 * @return
	 */
	public static long getUTCTimeLong(long time){
		Calendar cal = Calendar.getInstance();
		int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
		int dstOffset = cal.get(Calendar.DST_OFFSET);
		
		//计算UTC时间
		cal.setTimeInMillis(time);
		cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		return cal.getTimeInMillis();
	}
	
	
	/** 根据时间毫秒值转化成指定时间格式 */
	public static String timeFormat1(long time){
		
		String timeStr;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		timeStr = sdf.format(new Date(time));
		
		return timeStr;
	}
	
	public static String timeFormat2(long time){
		
		String timeStr;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		timeStr = sdf.format(new Date(time));
		
		return timeStr;
	}
	public static String timeFormat3(long time){
			
			String timeStr;
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			timeStr = sdf.format(new Date(time));
			
			return timeStr;
		}
	
	/** 计算某年某个月的天数 */
	public static int getDaysOfMonth(int year,int month){
		
		//注意：Calendar 的月份是从0开始算的
		Calendar c = Calendar.getInstance();
		c.set(year, month,1);				
		c.add(Calendar.DAY_OF_YEAR, -1); 	//calendar变为前一个月的最后一天，这里的前一个月即为我们所求的月份，因为Calendar的月份是从0开始算的
		return c.get(Calendar.DAY_OF_MONTH);
	}
	
	/** 获取给定日期（毫秒形式）那个月的第一天的毫秒值  */
	public static long getFirstDayTime(long time){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		int year = cal.get(Calendar.YEAR);
		int mon = cal.get(Calendar.MONTH);
		cal.set(year, mon, 1, 0, 0,0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTimeInMillis();
	}
}
