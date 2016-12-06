package com.cjt.trade.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author caojiantao
 * http操作工具类
 */
public class HttpUtil {

	/**
	 * 发起get请求，获取返回文本信息
	 */
	public static String doGet(String url){
		HttpURLConnection connection = null;
		// 使用StringBuilder避免在while循环体中多次创建String对象而浪费资源
		StringBuilder builder = new StringBuilder();
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				reader.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (connection != null) {
				connection.disconnect();
			}
		}
		return builder.toString();
	}
	
	/**
	 * 发起post请求，获取返回文本信息
	 */
	public static String doPost(String url, Map<String, String> map){
		HttpURLConnection connection = null;
		StringBuilder builder = new StringBuilder();
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			// post请求必须设置此项
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
			writer.write(getParamter(map));
			writer.flush();
			writer.close();
			System.out.println(connection.getResponseCode());
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// 获取返回数据
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				reader.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if (connection != null) {
				connection.disconnect();
			}
		}
		return builder.toString();
	}
	
	/**
	 * 将封装的map参数转换为String字符串，以便写入输出流中
	 */
	public static String getParamter(Map<String, String> map){
		StringBuilder builder = new StringBuilder();
		for (Entry<String, String> entry : map.entrySet()) {
			builder.append(entry.getKey());
			builder.append("=");
			builder.append(entry.getValue());
			builder.append("&");
		}
		// 去掉最后一个&
		return builder.substring(0, builder.length() -  1);
	}
	
	/**
	 * 当做java application测试
	 */
	public static void main(String[] args) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("account", "1");
		map.put("pwd", "1");
		System.out.print(doPost("http://localhost/backend/loginIn.action", map));
	}
}
