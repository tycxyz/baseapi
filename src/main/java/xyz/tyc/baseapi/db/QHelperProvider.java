package xyz.tyc.baseapi.db;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;

import xyz.tyc.baseapi.exception.TycBaseException;


import java.util.HashMap;
import java.util.Map;
/**
 * QueryHelper的提供者，支持多数据源切换
 * 
 * @version 0.0.1 2013-6-7
 * @author taoych
 */
public class QHelperProvider {
	private Map<String, DataSource> mSources;
	private  GenericObjectPool pool;
	private static QHelperProvider dbPool;
	
	private QHelperProvider(){
		mSources = new HashMap<String, DataSource>();
	}
	
	public static QHelperProvider getInstance() {
		if (dbPool == null) {
			dbPool = new QHelperProvider();
		}
		return dbPool;
	}
	
	// initialize connection pool source
	private synchronized DataSource setUp(IDataSourceInfo dbInfo) throws TycBaseException {
		try {
			// Load JDBC Driver class.
			Class.forName(dbInfo.getDBDriver()).newInstance();

			// create GenericObjectPool instance to load pool Object
			pool = new GenericObjectPool();
			pool.setMaxActive(Integer.parseInt(dbInfo.getDBMaxNum()));

			// create connection factory
			ConnectionFactory cf = new DriverManagerConnectionFactory(
					dbInfo.getDBUrl(), dbInfo.getDBUser(),
					dbInfo.getDBPassword());

			// pack the connection pool object
			PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf,
					pool, null, null, false, true);
			return new PoolingDataSource(pool); // 返回一个新的DataSource
		} catch (Exception e) {
			throw new TycBaseException("初始化数据库连接池异常，发生在QHelperProvider/setUp", e);
		}
	}
	
	public synchronized QueryHelper getQueryHelper(IDataSourceInfo dbInfo){
		QueryHelper qh;
		String s = _pickProperties(dbInfo);
		DataSource ds = null;
		//System.out.println("map key--" + s);
		
		//DataSource没有变化
		if (mSources.containsKey(s)) {
			ds = mSources.get(s);
			//System.out.println("user exists---" + ds);
		} else {
			ds = setUp(dbInfo);
			mSources.put(s, ds);
			//System.out.println("use new---" + ds);
		}
		
		if (ds == null)
			return null;
		qh = QueryHelper.getInstance(ds);
		return qh;
	}
	
	private String _pickProperties(IDataSourceInfo dbInfo){
		return new StringBuilder(dbInfo.getDBDriver()).append(dbInfo.getDBUrl()).toString();
	}
}
