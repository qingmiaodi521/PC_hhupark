/**
 * 
 * Bmob移动后端云服务RestAPI工具类ForAndroid
 * 
 * 提供简单的RestAPI增删改查工具，可直接对表、云函数、支付订单、消息推送进行操作。
 * 使用方法：先初始化initBmob，后调用其他方法即可。
 * 具体使用方法及传参格式详见Bmob官网RestAPI开发文档。
 * http://docs.bmob.cn/restful/developdoc/index.html?menukey=develop_doc&key=develop_restful
 * 
 * @author 金鹰
 * @version V1.3.2
 * @since 2015-11-10
 * 
 */
package com.hhupark.bmob.restapi;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import com.hhupark.bmob.bson.BSON;
import com.hhupark.bmob.bson.BSONObject;

public class BmobA {

	private boolean IS_INIT = false;
	private int TIME_OUT = 10000;

	private static String STRING_EMPTY = "";
	private static String APP_ID = STRING_EMPTY;
	private static String REST_API_KEY = STRING_EMPTY;
	private static String MASTER_KEY = STRING_EMPTY;

	private static final String BMOB_APP_ID_TAG = "X-Bmob-Application-Id";
	private static final String BMOB_REST_KEY_TAG = "X-Bmob-REST-API-Key";
	private static final String BMOB_MASTER_KEY_TAG = "X-Bmob-Master-Key";
	private static final String BMOB_EMAIL_TAG = "X-Bmob-Email";
	private static final String BMOB_PASSWORD_TAG = "X-Bmob-Password";
	private static final String CONTENT_TYPE_TAG = "Content-Type";
	private static final String CONTENT_TYPE_JSON = "application/json";

	private static final String METHOD_GET = "GET";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_PUT = "PUT";
	private static final String METHOD_DELETE = "DELETE";

	private static final String UTF8 = "UTF-8";
	private static final String CHAR_RISK = ":";

	public static final String MSG_NOT_FOUND = "Not Found";
	public static final String MSG_FILE_NOT_FOUND = "file Not Found";
	public static final String MSG_ERROR = "Error";
	public static final String MSG_UNREGISTERED = "Unregistered";


	public BmobA(){}
	
	public BmobA(String appId, String restKey){
		initBmob(appId, restKey, 10000);
	}

	public BmobA(String appId, String restKey, int timeout) {
		initBmob(appId, restKey, timeout);
	}
	
	/**
	 * 是否初始化Bmob
	 * 
	 * @return 初始化结果
	 */
	public boolean isInit() {
		return IS_INIT;
	}

	/**
	 * 初始化Bmob
	 * 
	 * @param appId
	 *            填写 Application ID
	 * @param apiKey
	 *            填写 REST API Key
	 * @return 注册结果
	 */
	public boolean initBmob(String appId, String restKey) {
		return initBmob(appId, restKey, 10000);
	}

	/**
	 * 初始化Bmob
	 * 
	 * @param appId
	 *            填写 Application ID
	 * @param apiKey
	 *            填写 REST API Key
	 * @param timeout
	 *            设置超时（1000~20000ms）
	 * @return 注册结果
	 */
	public boolean initBmob(String appId, String restKey, int timeout) {
		APP_ID = appId;
		REST_API_KEY = restKey;
		if (!APP_ID.equals(STRING_EMPTY) && !REST_API_KEY.equals(STRING_EMPTY)) {
			IS_INIT = true;
		}
		if (timeout > 1000 && timeout < 20000) {
			TIME_OUT = timeout;
		}
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, null, null);
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			IS_INIT = false;
		}
		return isInit();
	}

	/**
	 * 初始化Bmob Master权限
	 * 
	 * @param masterKey
	 *            填写 Master Key
	 */
	public void initMaster(String masterKey) {
		MASTER_KEY = masterKey;
	}

	/**
	 * 查询表全部记录(最多仅查询1000条记录)
	 * 
	 * @param tableName
	 *            表名
	 * @return BSONObject格式结果
	 */
	public BSONObject findAll(String tableName) {
		return find(tableName, STRING_EMPTY);
	}

	/**
	 * 条件查询表全部记录(最多仅查询1000条记录)
	 * 
	 * @param tableName
	 *            表名
	 * @param where
	 *            条件JOSN格式
	 * @return BSONObject格式结果
	 */
	public BSONObject findAll(String tableName, String where) {
		return find(tableName, where, STRING_EMPTY);
	}

	/**
	 * 查询表单条记录
	 * 
	 * @param tableName
	 *            表名
	 * @param objectId
	 *            objectId
	 * @return BSONObject格式结果
	 */
	public BSONObject findOne(String tableName, String objectId) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/classes/" + tableName + "/"
					+ objectId;
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
				conn.connect();
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(findOne)"
						+ e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(findOne)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return resultForBSONObject(result);
	}

	/**
	 * 查询表限定数量记录
	 * 
	 * @param tableName
	 *            表名
	 * @param limit
	 *            查询记录数（1~1000）
	 * @return BSONObject格式结果
	 */
	public BSONObject find(String tableName, int limit) {
		return find(tableName, "{}", 0, limit, STRING_EMPTY);
	}

	/**
	 * 条件查询表限定数量记录
	 * 
	 * @param tableName
	 *            表名
	 * @param where
	 *            条件JOSN格式
	 * @param limit
	 *            查询记录数（1~1000）
	 * @return BSONObject格式结果
	 */
	public BSONObject find(String tableName, String where, int limit) {
		return find(tableName, where, 0, limit, STRING_EMPTY);
	}

	/**
	 * 条件查询表限定数量记录，返回指定列
	 * 
	 * @param tableName
	 *            表名
	 * @param keys
	 *            返回列 （例：score,name）
	 * @param where
	 *            条件JOSN格式
	 * @param limit
	 *            查询记录数（1~1000）
	 * @return BSONObject格式结果
	 */
	public BSONObject findColumns(String tableName, String keys, String where,
			int limit) {
		return findColumns(tableName, keys, where, 0, limit, STRING_EMPTY);
	}

	/**
	 * 查询表区间记录
	 * 
	 * @param tableName
	 *            表名
	 * @param skip
	 *            跳过记录数
	 * @param limit
	 *            查询记录数（1~1000）
	 * @return BSONObject格式结果
	 */
	public BSONObject find(String tableName, int skip, int limit) {
		return find(tableName, "{}", skip, limit, STRING_EMPTY);
	}

	/**
	 * 条件查询表区间记录
	 * 
	 * @param tableName
	 *            表名
	 * @param where
	 *            条件JOSN格式
	 * @param skip
	 *            跳过记录数
	 * @param limit
	 *            查询记录数（1~1000）
	 * @return BSONObject格式结果
	 */
	public BSONObject find(String tableName, String where, int skip, int limit) {
		return find(tableName, where, skip, limit, STRING_EMPTY);
	}

	/**
	 * 条件查询表区间记录,返回指定列
	 * 
	 * @param tableName
	 *            表名
	 * @param keys
	 *            返回列 （例：score,name）
	 * @param where
	 *            条件JOSN格式
	 * @param skip
	 *            跳过记录数
	 * @param limit
	 *            查询记录数（1~1000）
	 * @return BSONObject格式结果
	 */
	public BSONObject findColumns(String tableName, String keys, String where,
			int skip, int limit) {
		return findColumns(tableName, keys, where, skip, limit, STRING_EMPTY);
	}

	/**
	 * 排序查询表记录
	 * 
	 * @param tableName
	 *            表名
	 * @param order
	 *            排序字段（例：score,-name）
	 * @return BSONObject格式结果
	 */
	public BSONObject find(String tableName, String order) {
		return find(tableName, "{}", 0, 1000, order);
	}

	/**
	 * 条件排序查询表记录
	 * 
	 * @param tableName
	 *            表名
	 * @param where
	 *            条件JOSN格式
	 * @param order
	 *            排序字段（例：score,-name）
	 * @return BSONObject格式结果
	 */
	public BSONObject find(String tableName, String where, String order) {
		return find(tableName, where, 0, 1000, order);
	}

	/**
	 * 条件排序查询表记录,返回指定列
	 * 
	 * @param tableName
	 *            表名
	 * @param keys
	 *            返回列 （例：score,name）
	 * @param where
	 *            条件JOSN格式
	 * @param order
	 *            排序字段（例：score,-name）
	 * @return BSONObject格式结果
	 */
	public BSONObject findColumns(String tableName, String keys, String where,
			String order) {
		return findColumns(tableName, keys, where, 0, 1000, order);
	}

	/**
	 * 排序查询表限定数量记录
	 * 
	 * @param tableName
	 *            表名
	 * @param limit
	 *            查询记录数（1~1000）
	 * @param order
	 *            排序字段（例：score,-name）
	 * @return BSONObject格式结果
	 */
	public BSONObject find(String tableName, int limit, String order) {
		return find(tableName, "{}", 0, limit, order);
	}

	/**
	 * 条件排序查询表限定数量记录
	 * 
	 * @param tableName
	 *            表名
	 * @param where
	 *            条件JOSN格式
	 * @param limit
	 *            查询记录数（1~1000）
	 * @param order
	 *            排序字段（例：score,-name）
	 * @return BSONObject格式结果
	 */
	public BSONObject find(String tableName, String where, int limit, String order) {
		return find(tableName, where, 0, limit, order);
	}

	/**
	 * 条件排序查询表限定数量记录,返回指定列
	 * 
	 * @param tableName
	 *            表名
	 * @param keys
	 *            返回列 （例：score,name）
	 * @param where
	 *            条件JOSN格式
	 * @param limit
	 *            查询记录数（1~1000）
	 * @param order
	 *            排序字段（例：score,-name）
	 * @return BSONObject格式结果
	 */
	public BSONObject findColumns(String tableName, String keys, String where,
			int limit, String order) {
		return findColumns(tableName, keys, where, 0, limit, order);
	}

	/**
	 * 条件排序查询表区间记录
	 * 
	 * @param tableName
	 *            表名
	 * @param where
	 *            条件JOSN格式
	 * @param skip
	 *            跳过记录数
	 * @param limit
	 *            查询记录数（1~1000）
	 * @param order
	 *            排序字段（例：score,-name）
	 * @return BSONObject格式结果
	 */
	public BSONObject find(String tableName, String where, int skip, int limit,
			String order) {
		return findColumns(tableName, STRING_EMPTY, where, skip, limit, order);
	}

	/**
	 * 条件排序查询表区间记录,返回指定列
	 * 
	 * @param tableName
	 *            表名
	 * @param keys
	 *            返回列 （例：score,name）
	 * @param where
	 *            条件JOSN格式
	 * @param skip
	 *            跳过记录数
	 * @param limit
	 *            查询记录数（1~1000）
	 * @param order
	 *            排序字段（例：score,-name）
	 * @return BSONObject格式结果
	 */
	public BSONObject findColumns(String tableName, String keys, String where,
			int skip, int limit, String order) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			skip = skip < 0 ? 0 : skip;
			limit = limit < 0 ? 0 : limit;
			limit = limit > 1000 ? 1000 : limit;
			where = where.equals(STRING_EMPTY) ? "{}" : where;
			String mURL = "https://api.bmob.cn/1/classes/" + tableName
					+ "?where=" + urlEncoder(where) + "&limit=" + limit
					+ "&skip=" + skip + "&order=" + order + "&keys=" + keys;

			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
				conn.connect();
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(findColumns)"
						+ e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(findColumns)"
						+ e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return resultForBSONObject(result);
	}

	/**
	 * BQL查询表记录
	 * 
	 * @param BQL
	 *            SQL语句。例如：select * from Student where name=\"张三\" limit 0,10
	 *            order by name
	 * @return BSONObject格式结果
	 */
	public BSONObject findBQL(String BQL) {
		return findBQL(BQL, STRING_EMPTY);
	}

	/**
	 * BQL查询表记录
	 * 
	 * @param BQL
	 *            SQL语句。例如：select * from Student where name=? limit ?,? order by
	 *            name
	 * @param value
	 *            参数对应SQL中?以,为分隔符。例如"\"张三\",0,10"
	 * @return BSONObject格式结果
	 */
	public BSONObject findBQL(String BQL, String value) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			BQL = urlEncoder(BQL) + "&values=[" + urlEncoder(value) + "]";
			String mURL = "https://api.bmob.cn/1/cloudQuery?bql=" + BQL;

			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
				conn.connect();
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(findBQL)"
						+ e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(findBQL)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return resultForBSONObject(result);
	}

	/**
	 * 获取服务器时间
	 * @return BSONObject格式
	 */
	public BSONObject getServerTime() {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/timestamp/";
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
				conn.connect();
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(getServerTime)"
						+ e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(getServerTime)"
						+ e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return resultForBSONObject(result);
	}

	/**
	 * 查询表记录数
	 * 
	 * @param tableName
	 *            表名
	 * @return 统计值
	 */
	public int count(String tableName) {
		return count(tableName, "{}");
	}

	/**
	 * 条件查询记录数
	 * 
	 * @param tableName
	 *            表名
	 * @param where
	 *            查询条件(JSON格式)
	 * @return 统计值
	 */
	public int count(String tableName, String where) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/classes/" + tableName
					+ "?where=" + urlEncoder(where) + "&count=1&limit=0";
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
				conn.connect();
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(count)" + e.getMessage();
				System.err.println("Warn: " + result);
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(count)" + e.getMessage();
				System.err.println("Warn: " + result);
			}
		} else {
			result = MSG_UNREGISTERED;
			System.err.println("Warn: " + result);
		}
		int count = -1;
		if (result.contains(MSG_NOT_FOUND) || result.contains(MSG_ERROR)
				|| result.equals(MSG_UNREGISTERED)) {
			return count;
		} else {
			if (result.contains("count")) {
				count = Integer.valueOf(result.replaceAll("[^0-9]",
						STRING_EMPTY));
			}
		}
		return count;
	}

	/**
	 * 修改记录
	 * 
	 * @param tableName
	 *            表名
	 * @param objectId
	 *            objectId
	 * @param paramContent
	 *            JSON格式参数
	 * @return BSONObject格式结果
	 */
	public BSONObject update(String tableName, String objectId, String paramContent) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/classes/" + tableName + "/"
					+ objectId;
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_PUT);
				conn.setDoOutput(true);
				conn.connect();
				printWriter(conn, paramContent);
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(update)"
						+ e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(update)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return resultForBSONObject(result);
	}

	/**
	 * 插入记录
	 * 
	 * @param tableName
	 *            表名
	 * @param paramContent
	 *            JSON格式参数
	 * @return BSONObject格式结果
	 */
	public BSONObject insert(String tableName, String paramContent) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/classes/" + tableName;
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_POST);
				conn.setDoOutput(true);
				conn.connect();
				printWriter(conn, paramContent);
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(insert)"
						+ e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(insert)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return resultForBSONObject(result);
	}

	/**
	 * 删除记录
	 * 
	 * @param tableName
	 *            表名
	 * @param objectId
	 *            objectId
	 * @return BSONObject格式结果
	 */
	public BSONObject delete(String tableName, String objectId) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/classes/" + tableName + "/"
					+ objectId;
			try {
				conn = connectionCommonSetting(conn, new URL(mURL),
						METHOD_DELETE);
				conn.connect();
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(delete)"
						+ e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(delete)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return resultForBSONObject(result);
	}

	/**
	 * 查询支付订单
	 * 
	 * @param payId
	 *            交易编号
	 * @return BSONObject格式结果
	 */
	public BSONObject findPayOrder(String payId) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/pay/" + payId;
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
				conn.connect();
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(findPayOrder)"
						+ e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(findPayOrder)"
						+ e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return resultForBSONObject(result);
	}

	/**
	 * 推送消息
	 * 
	 * @param JSON格式
	 *            data 详细使用方法参照
	 *            http://docs.bmob.cn/restful/developdoc/index.html
	 *            ?menukey=develop_doc&key=develop_restful#index_消息推送简介
	 * @return BSONObject格式结果
	 */
	public BSONObject pushMsg(String data) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/push";
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_POST);
				conn.setDoOutput(true);
				conn.connect();
				printWriter(conn, data);
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(pushMsg)"
						+ e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(pushMsg)" + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return resultForBSONObject(result);
	}

	/**
	 * 调用云端代码
	 * 
	 * @param funcName
	 *            云函数名
	 * @param paramContent
	 *            JSON格式参数
	 * @return BSONObject格式结果
	 */
	public BSONObject callFunction(String funcName, String paramContent) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/functions/" + funcName;
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_POST);
				conn.setDoOutput(true);
				conn.connect();
				printWriter(conn, paramContent);
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(callFunction)"
						+ e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(callFunction)"
						+ e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return resultForBSONObject(result);
	}

	/**
	 * 文件上传
	 * 
	 * @param paramContent
	 *            file 完整路径文件
	 * @return String
	 */
	public String uploadFile(String file) {
		String result = STRING_EMPTY;
		if (isInit()) {
			HttpURLConnection conn = null;
			// 获取文件名
			String fileName = file.trim();
			fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
			String mURL = "https://api.bmob.cn/1/files/" + fileName;
			try {
				FileInputStream fis = new FileInputStream(file);
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_POST);
				conn.setDoOutput(true);
				conn.connect();
				// 一次读多个字节
				byte[] tempbytes = new byte[1];
				OutputStream os = conn.getOutputStream();
				while ((fis.read(tempbytes)) != -1) {
					os.write(tempbytes);
				}
				os.flush();
				os.close();
				fis.close();
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_FILE_NOT_FOUND + CHAR_RISK + e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return result;
	}

	/**
	 * 登录账户获取所有APP信息
	 * 
	 * @param user
	 * @param passwd
	 * @return BSONObject格式结果
	 */
	public BSONObject login(String user, String passwd) {
		String result = STRING_EMPTY;
		HttpURLConnection conn = null;
		String mURL = "https://api.bmob.cn/1/apps";
		try {
			conn = connectionLoginSetting(conn, new URL(mURL), METHOD_GET,
					user, passwd);
			conn.connect();
			result = getResultFromConnection(conn);
			conn.disconnect();
		} catch (FileNotFoundException e) {
			result = MSG_NOT_FOUND + CHAR_RISK + "(Login)" + e.getMessage();
		} catch (Exception e) {
			result = MSG_ERROR + CHAR_RISK + "(Login)" + e.getMessage();
		}
		return resultForBSONObject(result);
	}

	/**
	 * 获取APP所有表信息 必需填写Master-Key
	 * 
	 * @return BSONObject格式结果
	 */
	public BSONObject findAllTables() {
		String result = STRING_EMPTY;
		if (isInit() && !MASTER_KEY.equals(STRING_EMPTY)) {
			HttpURLConnection conn = null;
			String mURL = "https://api.bmob.cn/1/schemas";
			try {
				conn = connectionCommonSetting(conn, new URL(mURL), METHOD_GET);
				conn.connect();
				result = getResultFromConnection(conn);
				conn.disconnect();
			} catch (FileNotFoundException e) {
				result = MSG_NOT_FOUND + CHAR_RISK + "(findAllTables)"
						+ e.getMessage();
			} catch (Exception e) {
				result = MSG_ERROR + CHAR_RISK + "(findAllTables)"
						+ e.getMessage();
			}
		} else {
			result = MSG_UNREGISTERED;
		}
		return resultForBSONObject(result);
	}

	public int getTimeout() {
		return TIME_OUT;
	}

	public void setTimeout(int timeout) {
		TIME_OUT = timeout;
	}

	private void printWriter(HttpURLConnection conn, String paramContent)
			throws UnsupportedEncodingException, IOException {
		PrintWriter out = new PrintWriter(new OutputStreamWriter(
				conn.getOutputStream(), UTF8));
		out.write(paramContent);
		out.flush();
		out.close();
	}

	private String getResultFromConnection(HttpURLConnection conn)
			throws UnsupportedEncodingException, IOException {
		StringBuffer result = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), UTF8));
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}
		reader.close();
		return result.toString();
	}

	private HttpURLConnection connectionLoginSetting(HttpURLConnection conn,
			URL url, String method, String user, String passwd)
			throws IOException {
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setDoInput(true);
		conn.setReadTimeout(TIME_OUT);

		conn.setUseCaches(false);
		conn.setInstanceFollowRedirects(true);

		conn.setRequestProperty(BMOB_EMAIL_TAG, user);
		conn.setRequestProperty(BMOB_PASSWORD_TAG, passwd);
		conn.setRequestProperty(CONTENT_TYPE_TAG, CONTENT_TYPE_JSON);
		return conn;
	}

	private HttpURLConnection connectionCommonSetting(HttpURLConnection conn,
			URL url, String method) throws IOException {
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(method);
		conn.setDoInput(true);
		conn.setReadTimeout(TIME_OUT);

		conn.setUseCaches(false);
		conn.setInstanceFollowRedirects(true);

		conn.setRequestProperty(BMOB_APP_ID_TAG, APP_ID);
		conn.setRequestProperty(BMOB_REST_KEY_TAG, REST_API_KEY);
		if (!MASTER_KEY.equals(STRING_EMPTY)) {
			conn.setRequestProperty(BMOB_MASTER_KEY_TAG, MASTER_KEY);
		}

		conn.setRequestProperty(CONTENT_TYPE_TAG, CONTENT_TYPE_JSON);
		return conn;
	}

	private String urlEncoder(String str) {
		try {
			return URLEncoder.encode(str, UTF8);
		} catch (UnsupportedEncodingException e1) {
			return str;
		}
	}

	// 查询结果转换成BSONObject
	// 结果可能返回null
	private BSONObject resultForBSONObject(String result) {
		BSONObject bson = null;
		if (result.equals(Bmob.MSG_UNREGISTERED)
				|| result.contains(Bmob.MSG_NOT_FOUND)
				|| result.contains(Bmob.MSG_ERROR)) {
			BSON.Warn(result);
		} else {
			bson = new BSONObject(result);
		}
		return bson;
	}

}
