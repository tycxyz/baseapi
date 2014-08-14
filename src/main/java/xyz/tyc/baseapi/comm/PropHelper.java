package xyz.tyc.baseapi.comm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import xyz.tyc.baseapi.exception.TycBaseException;


public class PropHelper {
	private Properties props;
	private static PropHelper ph; 
	private String file; //缓存配置文件全路径
	
	private PropHelper(String fileName) throws TycBaseException{
		file = fileName;
		props = new Properties();
		try {
			props.load(new InputStreamReader(new FileInputStream(new File(fileName)), "UTF-8"));
		} catch (IOException e ) {
			throw new TycBaseException("加载配置文件内容异常，发生在PropHelper类的构造方法里", e);
		}
	}
	
	public static PropHelper getInstance(String fileName) throws TycBaseException{
		if (ph == null) {
			ph = new PropHelper(fileName);
		} else {
			if (!ph.getFile().equals(fileName)) {
				ph.reload(fileName);
			}
		}
		return ph;
	}
	
	/**
	 * 文件不同，重新加载文件内容
	 * @param fileName
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void reload(String fileName) throws TycBaseException {
		file = fileName;
		props.clear();
		try {
			props.load(new InputStreamReader(new FileInputStream(new File(fileName)), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new TycBaseException("加载配置文件内容异常，错误的编码，发生在PropHelper/reload", e);
		} catch (FileNotFoundException e) {
			throw new TycBaseException("加载配置文件内容异常，文件无法找到，发生在PropHelper/reload", e);
		} catch (IOException e) {
			throw new TycBaseException("加载配置文件内容异常，I/O异常，发生在PropHelper/reload", e);
		}
	}

	private String getFile() {
		return file;
	}
	
	public String get(String key){
		return props.getProperty(key);
	}
}