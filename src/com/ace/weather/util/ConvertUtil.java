package com.ace.weather.util;


import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 
 * 用于json与maplist/beanList的转换
 * 
 */
public class ConvertUtil {

	/**
	 * 把非空属性拷贝到新对象
	 * 
	 * @param from
	 * @param to
	 */
	public static void copyProperties(Object from, Object to) {
		try {
			Class<? extends Object> clazz = from.getClass();
			Field[] fields = clazz.getDeclaredFields();
			Object fromV;
			Object toV;
			// 遍历字段
			for (Field field : fields) {
				try {
					field.setAccessible(true);
					fromV = field.get(from);
					toV = field.get(to);

					if (toV instanceof String) {
						if (!TextUtils.isEmpty((String) fromV)) {
							field.set(to, fromV);
						}
					} else if (toV instanceof ArrayList) {
						ArrayList fromArr = (ArrayList) fromV;
						if (fromArr != null && fromArr.size() > 0) {
							field.set(to, fromArr);
						}
					} else {
						if (fromV != null) {
							field.set(to, fromV);
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * json装为bean
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T json2Bean(JSONObject json, Class<T> clazz) {
		T obj = null;
		try {
			obj = newInstance(clazz);
			doJson2Bean(json, obj);
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return obj;
		}
	}

	/**
	 * beanlist转为mapList
	 * 
	 * @param beanList
	 * @return
	 */
	public static <T> ArrayList<HashMap<String, Object>> beanList2mapList(ArrayList<T> beanList) {
		ArrayList<HashMap<String, Object>> mapList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map;
		if (beanList != null) {
			for (T bean : beanList) {
				map = new HashMap<String, Object>();
				try {
					Field[] fields = bean.getClass().getDeclaredFields();
					Object value;
					// 遍历字段
					for (Field field : fields) {
						try {
							field.setAccessible(true);
							value = field.get(bean);
							if (value != null)
								map.put(field.getName(), value);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				mapList.add(map);
			}
		}
		return mapList;
	}

	/**
	 * beanlist转为array
	 * 
	 * @param beanList
	 * @return
	 */
	public static <T> JSONArray beanList2array(ArrayList<T> beanList, ArrayList<String> maskNames) {
		JSONArray array = new JSONArray();
		JSONObject obj = new JSONObject();
		if (beanList != null) {
			for (T bean : beanList) {
				obj = new JSONObject();
				try {
					Field[] fields = bean.getClass().getDeclaredFields();
					Object value;
					// 遍历字段
					for (Field field : fields) {
						try {
							field.setAccessible(true);
							value = field.get(bean);
							// 屏蔽字段
							if (value != null && maskNames != null && !maskNames.contains(field.getName()))
								obj.put(field.getName(), value);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				array.put(obj);
			}
		}
		return array;
	}

	/**
	 * json数组转为beanList
	 * 
	 * @param array
	 * @param clazz
	 * @return
	 */
	public static <T> ArrayList<T> array2BeanList(JSONArray array, Class<T> clazz) {
		ArrayList<T> list = new ArrayList();
		int len = array.length();
		try {
			// 遍历数组
			for (int i = 0; i < len; i++) {
				Object obj = newInstance(clazz);
				list.add((T) obj);
				doJson2Bean(array.getJSONObject(i), obj);
			}
		} catch (Exception e) {
		}
		return list;
	}

	/**
	 * json数组转为mapList
	 * 
	 * @param array
	 * @return
	 */
	public static <T> ArrayList<HashMap<String, Object>> array2MapList(JSONArray array) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		int len = array.length();
		HashMap<String, Object> map;
		try {
			// 遍历数组
			for (int i = 0; i < len; i++) {
				map = new HashMap<String, Object>();
				list.add(map);
				doJson2Map(array.getJSONObject(i), map);
			}
		} catch (Exception e) {
		}
		return list;
	}

	// ////////////////////////////////////////////////////////////////
	/**
	 * 递归
	 * 
	 * @param json
	 * @param map
	 */
	private static void doJson2Map(JSONObject json, HashMap<String, Object> map) {
		try {
			// 所有的key
			Iterator it = json.keys();
			Field field;
			// 遍历key
			while (it.hasNext()) {
				// key
				String key = (String) it.next();
				// json value
				Object value = json.get(key);

				try {
					if (value instanceof JSONObject) {
						HashMap<String, Object> m = new HashMap<String, Object>();
						map.put(key, m);
						doJson2Map((JSONObject) value, m);
					} else if (value instanceof JSONArray) {
						JSONArray array = (JSONArray) value;
						ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
						map.put(key, list);
						// 泛型信息
						int len = array.length();
						// 遍历数组
						for (int i = 0; i < len; i++) {
							HashMap<String, Object> m = new HashMap<String, Object>();
							list.add(m);
							doJson2Map(array.getJSONObject(i), m);
						}
					} else {
						map.put(key, value);
					}
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static <T> ArrayList<T> array2Bean(JSONArray array, Class<T> clazz, ArrayList list) {
		int len = array.length();
		try {
			// 遍历数组
			for (int i = 0; i < len; i++) {
				Object obj = newInstance(clazz);
				list.add((T) obj);
				doJson2Bean(array.getJSONObject(i), obj);
			}
		} catch (Exception e) {
		}
		return list;
	}

	private static <T> T newInstance(Class<T> clazz) {
		try {
			Constructor constructor = clazz.getDeclaredConstructors()[0];
			constructor.setAccessible(true);
			constructor.setAccessible(true);
			T obj = (T) constructor.newInstance();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static void doJson2Bean(JSONObject json, Object entity) {
		try {
			// 所有的key
			Iterator it = json.keys();
			Class clazz = entity.getClass();
			Field field;
			// 遍历key
			while (it.hasNext()) {
				// key
				String key = (String) it.next();
				// json value
				Object value = json.get(key);

				try {
					field = clazz.getDeclaredField(key);
					field.setAccessible(true);
					if (value instanceof JSONObject) {
						Object obj = field.get(entity);
						doJson2Bean((JSONObject) value, obj);
					} else if (value instanceof JSONArray) {
						JSONArray array = (JSONArray) value;
						List list = (List) field.get(entity);
						// 泛型信息
						Class genClazz = getSuperClassGenricType(field, 0);
						int len = array.length();
						// 遍历数组
						for (int i = 0; i < len; i++) {
							Object obj = newInstance(genClazz);
							list.add(obj);
							doJson2Bean(array.getJSONObject(i), obj);
						}
					} else {
						try {
							field.set(entity, value);
						} catch (Exception e) {
							try2SetField(field, entity, value);
						}
					}
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void try2SetField(Field field, Object entity, Object value) {
		try {
			if("null".equals(value+""))
				return;
			
			field.set(entity, value+"");
		} catch (Exception e0) {
			try {
				field.set(entity, Integer.valueOf(value.toString()));
			} catch (Exception e) {
				try {
					field.set(entity, Double.valueOf(value.toString()));
				} catch (Exception e2) {
					try {
						field.set(entity, Float.valueOf(value.toString()));
					} catch (Exception e3) {
						try {
							field.set(entity, Boolean.valueOf(value.toString()));
						} catch (Exception e4) {
							try {
								field.set(entity, Long.valueOf(value.toString()));
							} catch (Exception e5) {
							}
						}
					}
				}
			}
		}
	}

	public static RequestParams bean2Params(Object bean) {
		RequestParams params = new RequestParams();
		addParams(bean, params, "");
		return params;
	}

	private static void addParams(Object entity, RequestParams params, String prefix) {
		try {
			Field[] fields = entity.getClass().getDeclaredFields();
			Object value;
			// 遍历字段
			for (Field field : fields) {

				try {
					field.setAccessible(true);
					value = field.get(entity);

					// 不为空
					if (value != null) {
						// 是基本类型
						if ((value instanceof Number) || (value instanceof Boolean) || (value instanceof String)) {
							params.add(prefix + field.getName(), value.toString());
						} else {
							addParams(value, params, prefix + field.getName() + ".");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * 获取泛型信息
	 */
	@SuppressWarnings("unchecked")
	private static Class<Object> getSuperClassGenricType(Field field, int index) {
		ParameterizedType pt = (ParameterizedType) field.getGenericType();
		return (Class<Object>) pt.getActualTypeArguments()[index];
	}

	@SuppressWarnings("unchecked")
	public static Class<Object> getSuperClassGenricType(final Class clazz, final int index) {

		// 返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			return Object.class;
		}
		// 返回表示此类型实际类型参数的 Type 对象的数组。
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			return Object.class;
		}

		return (Class) params[index];
	}
}
