package test;

import java.util.Calendar;
import java.util.Date;

import com.eqsys.util.UTCTimeUtil;

public class CalendarTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("days of month 5:"+getDaysOfMonth(2016,1));
		System.out.println("first day of month:"+UTCTimeUtil.timeFormat1(getFirstDayTime(new Date().getTime())));
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
