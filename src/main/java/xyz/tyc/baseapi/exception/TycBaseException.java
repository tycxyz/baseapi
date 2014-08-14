package xyz.tyc.baseapi.exception;

/**
 * 定工具包里的处理异常，通常可以作一些参数检查，异常归类
 *
 * @version 0.0.1 2013-7-11 
 * @author taoych  
 */
public class TycBaseException extends RuntimeException {
	private static final long serialVersionUID = 3908219895070136095L;

	public TycBaseException(Exception e){
		super(e);
	}

	/**
	 * @param msg
	 * @param e
	 */
	public TycBaseException(String msg, Exception e) {
		super(msg, e);
	}
}
