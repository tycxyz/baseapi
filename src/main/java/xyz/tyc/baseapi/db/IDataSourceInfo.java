package xyz.tyc.baseapi.db;

/**
 * 定义客户端dbpool需要实现的初始化url/user/password等参数的接口
 *
 * @version 0.0.1 2013-7-11 
 * @author taoych  
 */
public interface IDataSourceInfo {
	public String getDBUrl();
	public String getDBDriver();
	public String getDBUser();
	public String getDBPassword();
	public String getDBMaxNum();
}
