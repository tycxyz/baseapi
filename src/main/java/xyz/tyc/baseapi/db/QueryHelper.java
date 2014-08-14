package xyz.tyc.baseapi.db;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import xyz.tyc.baseapi.exception.TycBaseException;


/**
 * 提供数据查询修改接口
 *
 * @version 0.0.1 2013-7-5 
 * @author taoych  
 */
public class QueryHelper {
	private static QueryHelper qh = null;
	private QueryRunner _qRunner = null;
	private DataSource _ds;
	
	private QueryHelper(){
		
	}
	
	private QueryHelper(DataSource ds){
		_qRunner = new QueryRunner();
		_ds = ds;
	}
	/**
	 * 取得QueryHelper对象，支持DataSource切换，外部不能访问，只能本包或子类访问
	 * @param ds
	 * @return
	 */
	protected static QueryHelper getInstance(DataSource ds){
		if (qh == null) {
			qh = new QueryHelper(ds); 
		} else {
			qh.setDataSource(ds);
		}
		return qh;
	}
	
	private void setDataSource(DataSource ds) {
		_ds = ds;
	}

	/**
	 * 查询第一条记录
	 * 
	 * @param beanClass 类
	 * @param sql 查询语句
	 * @param params 参数
	 * @return
	 */
	public <T> T queryFirst(Class<T> beanClass, String sql, Object... params) throws TycBaseException{
		Connection conn = null;
		try {
			conn = _ds.getConnection();
			return (T) _qRunner.query(conn, sql, _isPrimitive(beanClass) ? _g_scaleHandler : new BeanHandler(beanClass), params);
		} catch (SQLException e) {
			throw new TycBaseException("查询首个对象异常，发生在QueryHelper/queryFirst", e);
		} finally {
			_closeConn(conn);
		}
	}
	/**
	 * 插入对象并返回自增的ID，由于dbutils对自增的支持很无奈，所以这里用的是jdbc相关api
	 * @param sql 插入sql语句
	 * @param params 参数
	 * @return 数据记录自增ID
	 */
	public long insertAndReturnId(String sql, Object... params) throws TycBaseException{
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = _ds.getConnection();
			ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
			int i = 1;
			for (Object param : params) {
				ps.setObject(i++, param);
			}
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			return rs.next() ? rs.getLong(1) : -1;
		} catch (SQLException e) {
			throw new TycBaseException("插入并返回id发生异常，发生在QueryHelper/insertAndReturnId", e);
		} finally {
			_closeConn(conn);
		}
	}
	
	/**
	 * 查询数据集返回为对象集合
	 * 
	 * @param beanClass 对象类
	 * @param sql 查询语句
	 * @param params 查询参数
	 * @return 对象集合
	 */
	public <T> List<T> queryMulti(Class<T> beanClass, String sql,
			Object... params)  throws TycBaseException {
		Connection conn = null;
		try {
			conn = _ds.getConnection();
			return (List<T>) _qRunner.query(conn, sql, _isPrimitive(beanClass) ? _g_columnListHandler
					: new BeanListHandler(beanClass), params);
		}catch (Exception e){
			throw new TycBaseException("查询多个对象异常，发生在QueryHelper/queryMulti", e);
		} finally {
			_closeConn(conn);
		}
	}
	
	/**
	 * 可以执行INSERT/UPDATE/DELETE语句，delete/insert有更明确的调用方法。
	 * @param sql 查询语句
	 * @param params 预设参数
	 * @return 影响条数
	 */
	public int cud(String sql, Object... params)  throws TycBaseException{
		Connection conn = null;
		try {
			conn  = _ds.getConnection();
			return _qRunner.update(conn, sql, params);
		} catch (SQLException e) {
			throw new TycBaseException("cud异常，发生在QueryHelper/cud", e);
		} finally {
			_closeConn(conn);
		}
	}
	
	/**
	 * 批量执行update/delete
	 * @param sql 查询语句
	 * @param params 批量参数
	 * @return 一系列的受影响行数
	 */
	public int[] batchCUD(String sql, Object[][] params)  throws TycBaseException{  
		Connection conn = null;
		int[] affectedRows; 
		try {
			conn = _ds.getConnection();
			affectedRows = _qRunner.batch(conn, sql, params);
		} catch (SQLException e) {
			throw new TycBaseException("批量cud异常，发生在QueryHelper/batchCUD", e);
		} finally {
			_closeConn(conn);
		}
        return affectedRows;  
    } 
	
	/**
	 * 判断 是否是基本的数据类型，还包括Long/String/Integer/Date/Timestamp
	 * 
	 * @param cls 类
	 * @return 是否为基本类型
	 */
	private boolean _isPrimitive(Class<?> cls) {
		return cls.isPrimitive() || _primitiveClasses.contains(cls);
	}
	/**
	 * 定义几个数据库数据类型的类
	 */
	private final List<Class<?>> _primitiveClasses = new ArrayList<Class<?>>() {
		{
			add(Long.class);
			add(Integer.class);
			add(String.class);
			add(java.util.Date.class);
			add(java.sql.Date.class);
			add(java.sql.Timestamp.class);
		}
	};
	
	/**
	 * 将一个数据集中的列转换为一列对象，实现的这个方法仅是对一列中的一个对象作数据转换
	 */
	private final ColumnListHandler _g_columnListHandler = new ColumnListHandler() {
		@Override
		protected Object handleRow(ResultSet rs) throws SQLException {
			Object obj = super.handleRow(rs);
			if (obj instanceof BigInteger)
				return ((BigInteger) obj).longValue();
			return obj;
		}

	};
	/**
	 * 将一个数据集的列转换为一个对象（第一个）
	 */
	private final ScalarHandler _g_scaleHandler = new ScalarHandler() {
		@Override
		public Object handle(ResultSet rs) throws SQLException {
			Object obj = super.handle(rs);
			if (obj instanceof BigInteger)
				return ((BigInteger) obj).longValue();
			return obj;
		}
	};

	/**
	 * 关闭连接
	 * @param conn 连接对象
	 * @throws SQLException 
	 */
	private void _closeConn(Connection conn){
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				throw new TycBaseException("关闭connection异常，发生在QueryHelper/_closeConn", e);
			}
	}
}
