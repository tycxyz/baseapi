package xyz.tyc.baseapi.comm;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.RandomStringUtils;


/**
 * @author ytao
 *
 * 2013-4-25 下午09:55:43
 *
 */
public class StringUtil {
	//检测是否是邮箱地址
	public static boolean isEmail(String input) {
		if (isEmpty(input))
			return false;
		Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}
	/**
	 * 生成14位随机数，前6位为日期yyMMdd，后8位为随机数字与字符串
	 * @return
	 */
	public static String getRadom14(){
		String day = DateUtil.getGMTDateString("yyMMdd");
		return day + getID(8);
	}
	/**
	 * 生成字母与数字混合的10位随机数字
	 * 
	 * @return
	 */
	public static String getID(){
		return getID(10);
	}
	public static String getID(int length){
		return RandomStringUtils.random(length, true, true).toLowerCase();
	}
	/**
	 * 判断字符串是否为空或空串
	 * @param input
	 * @return
	 */
	public static boolean isEmpty(String input){
		return input==null || "".equals(input);
	}
	
	/**
	 * 把list转换为一个用逗号分隔的字符串
	 * 
	 * @param list
	 * @return
	 */
	public static String listToString(List list) {
		if (list == null)
			return null;
		StringBuilder sb = new StringBuilder();
		
		if (list.size() > 0) {
			for (Object o : list) {
				sb.append(',').append(o);
			}
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	/**
	 * 用正则式判断字符是否是数字
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		if (str == null)
			return false;
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}
	
	public static void main(String[] args){
		System.out.println(getID(10));
	}
}
