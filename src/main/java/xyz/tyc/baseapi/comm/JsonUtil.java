package xyz.tyc.baseapi.comm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import xyz.tyc.baseapi.exception.TycBaseException;


/**
 * json格式数据转化辅助类
 * 
 * @version 0.0.1 2013-7-6
 * @author taoych
 */
public class JsonUtil {
	public static void main(String[] args) {
		try {
			JSONObject result = new JSONObject();
			result.put("ii", "asdf");
			JSONObject se = new JSONObject();
			se.put("asdf", "adsf");
			result.put("user", se);
			System.out.println(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(genJsonString(
				new Object[][]{{1,1}
		, {4,genJsonObject(new Object[][]{{2,2},{3,3}})
			}
		}
		));
		System.out.println(genJsonString(new Object[][] { { 1, 2 } }));
	}
	
	
	/**
	 * 生成json对象的字符串
	 * @param items
	 * @return
	 */
	public static String genJsonString(Object[][] items)  throws TycBaseException {
		JSONObject j = genJsonObject(items);
		if (j.length() > 0)
			return j.toString();
		else return null;
	}

	/**
	 * 生成json对象
	 * @param items
	 * @return
	 */
	public static JSONObject genJsonObject(Object[][] items)  throws TycBaseException {
		if (items == null || items.length == 0)
			return null;
		JSONObject j = new JSONObject();
		for (Object[] item : items) {
			if (item == null || item.length < 2)
				break;
			
			try {
				j.put(String.valueOf(item[0]), item[1]);
			} catch (JSONException e) {
				System.out.println(e.getMessage());
			}
		}
		return j;
	}
	/**
	 * 将对象转化为json数组串，要求参数是LIST结构，即将list<bean>转化为[{},{}...]
	 * 
	 * @param bean 对象
	 * @return
	 */
	public static <T> String beans2JsonArrString(T bean)  throws TycBaseException {
		if (bean == null)
			return null;
		else {
			return beans2JsonArr(bean).toString();
		}
	}
	/**
	 * 将list的bean转化为json数组
	 * @param bean
	 * @return
	 */
	public static <T> JSONArray beans2JsonArr(T bean)  throws TycBaseException {
		if (bean == null)
			return null;
		else {
			JSONArray arrJson = new JSONArray();

			if (bean instanceof List) {
				for (Object item : (List) bean) {
					arrJson.put(_map2JsonObject(_bean2Map(item)));
				}
				return arrJson;
			} else {
				arrJson.put(_map2JsonObject(_bean2Map(bean)));
			}
			return arrJson;
		}
	}

	/**
	 * 将格式为[{},{}]的json字符串转化为list<map>
	 * 
	 * @param arr json字符串
	 * @return
	 */
	public static List<Map<String, Object>> jsonArrString2List(String arr) throws TycBaseException  {
		if (StringUtil.isEmpty(arr))
			return null;

		List<Map<String, Object>> list = null;
		try {
			JSONArray jArr = new JSONArray(arr);

			if (jArr != null) {
				int size = jArr.length();
				list = new ArrayList<Map<String, Object>>(size);

				for (int i = 0; i < size; i++) {
					list.add(_jsonObject2Map(jArr.getJSONObject(i)));
				}
			}
		} catch (JSONException e) {
			throw new TycBaseException(e);
		}
		return list;
	}

	/**
	 * 将json串转换为map数据格式
	 * 
	 * @param jsonData json数据串
	 * @return map格式数据
	 */
	public static Map<String, Object> jsonString2Map(String jsonData)  throws TycBaseException {
		if (StringUtil.isEmpty(jsonData))
			return null;
		JSONObject jO;

		try {
			jO = new JSONObject(jsonData);
		} catch (JSONException e) {
			//throw new IwitException(e);
			return null;
		}
		return _jsonObject2Map(jO);
	}

	/**
	 * 将对象转换为JSONObject对象
	 * @param bean
	 * @return
	 */
	public static <T> JSONObject bean2JsonObject(T bean) throws TycBaseException {
		if (bean == null)
			return null;
		return _map2JsonObject(_bean2Map(bean));
	}


	/**
	 * 将map数据转化为json字符串
	 * @param map
	 * @return
	 */
	public static String map2JsonString(Map<String, Object> map)  throws TycBaseException {
		JSONObject jo = _map2JsonObject(map);
		return  jo == null ? null : jo.toString();
	}


	private static Map<String, Object> _jsonObject2Map(JSONObject jo) throws TycBaseException {
		Iterator<String> it = jo.keys();
		Map<String, Object> map = new HashMap<String, Object>(jo.length());
		
		try {
			String key;
			// 不支持值里含数组
			while (it.hasNext()) {
				key = (String) it.next();
				map.put(key, jo.get(key));
			}
		} catch (JSONException e) {
			throw new TycBaseException("将json里的数据装载到map异常，发生在jsonutil/_jsonObject2Map", e);
		}
		return map;
	}


	/**
	 * 将map格式数据转换为json字符串
	 * 
	 * @param map
	 * @return JSON对象
	 */
	private static JSONObject _map2JsonObject(Map<String, Object> map) throws TycBaseException {
		if (map == null || map.isEmpty()) {
			return null;
		}
		JSONObject json = new JSONObject();
	
		try {
			Object field;
			JSONArray arrJson;
	
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				field = entry.getValue();
	
				if (field != null) {
					// 如果字段是一个对象集合
					if (field instanceof List) { // beanutil转化的map格式数据对list字段转化的结果有问题
						arrJson = new JSONArray();
	
						for (Object item : (List) field) {
							arrJson.put(_map2JsonObject(_bean2Map(item))); // 将对象转化为json对象并加到json数组中
						}
						json.put(entry.getKey(), arrJson);
					} else {
						json.put(entry.getKey(), field);
					}
				}
			}
		} catch (JSONException e) {
			throw new TycBaseException("将map里的数据装载到json异常，发生在jsonutil/_map2JsonObject", e);
		}
		return json;
	}


	/**
	 * 将bean对象转换为map数据
	 * 
	 * @param bean
	 * @return
	 */
	private static <T> Map<String, Object> _bean2Map(T bean) throws TycBaseException{
		Map<String, Object> fields;
		try {
			fields = BeanUtils.describe(bean);
			fields.remove("class");
			return fields;
		} catch (Exception e) {
			throw new TycBaseException("将bean转化为map数据异常，发生在jsonutil/_bean2Map方法里", e);
		}
	}
	
}
