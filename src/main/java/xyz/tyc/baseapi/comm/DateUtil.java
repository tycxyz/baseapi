package xyz.tyc.baseapi.comm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.SimpleTimeZone;

/**
 * 日期类接口
 *
 * @version 0.0.1 2013-8-12 
 * @author taoych  
 */
public class DateUtil {
	
	/**
	 * 返回系统时间对应的0时区时间的字符串，可指定格式
	 * @param format 时间格式，如yyyyMMddHHmmss
	 * @return
	 */
	public static String getGMTDateString(String format){
		if (StringUtil.isEmpty(format)) {
			format = "yyyy-MM-dd HH:mm:ss";
		}
		
		DateFormat df = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance(new SimpleTimeZone(0, "GMT"));
		df.setCalendar(cal);
		String sDate = df.format(System.currentTimeMillis());
        return sDate;
	}
	/**
	 * 取得当前0时区的时间，格式为yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getGMTDateString(){
		return getGMTDateString("yyyy-MM-dd HH:mm:ss");
	}
	
	
	public static void main(String[] args){
		System.out.println(getGMTDateString("yyyyMMddHHmmss"));
	}
}
