package com.elemeHelper.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

public class HttpUtil {

	public static void main(String[] args) {
		postRequest("https://www.cnblogs.com/112313", null);
	}

	public static String getRequest(String url) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).build();
		get.setConfig(config);
		get.setHeader("Accept", "text/plain;charset=utf-8");
		get.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		get.setHeader("user-agent", "Mozilla/5.0 (Linux; Android 5.1; OPPO R9tm Build/LMY47I; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.49 Mobile MQQBrowser/6.2 TBS/043128 Safari/537.36 V1_AND_SQ_7.0.0_676_YYB_D PA QQ/7.0.0.3135 NetType/4G WebP/0.3.0 Pixel/1080");

		HttpResponse response = null;
		try {
			response = client.execute(get);
			Header[] headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				System.err.println(headers[i].getName() + "----" + headers[i].getValue());
			}
			HttpEntity entity = response.getEntity();
			String resp = EntityUtils.toString(entity,"gbk");
			System.out.println(resp);
			return resp;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String getRequest(String url,Map<String, String> header) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).build();
		get.setConfig(config);
		get.setHeader("Accept", "text/plain;charset=utf-8");
		get.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		get.setHeader("user-agent", "Mozilla/5.0 (Linux; Android 5.1; OPPO R9tm Build/LMY47I; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.49 Mobile MQQBrowser/6.2 TBS/043128 Safari/537.36 V1_AND_SQ_7.0.0_676_YYB_D PA QQ/7.0.0.3135 NetType/4G WebP/0.3.0 Pixel/1080");
		for (String key : header.keySet()) {
			get.setHeader(key, header.get(key));
		}
		HttpResponse response = null;
		try {
			response = client.execute(get);
			Header[] headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				System.err.println(headers[i].getName() + "----" + headers[i].getValue());
			}
			HttpEntity entity = response.getEntity();
			String resp = EntityUtils.toString(entity,"utf-8");
			System.out.println(resp);
			return resp;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	public static String postRequest(String url,Map<String, String> param) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).build();
		post.setConfig(config);
		post.setHeader("Accept", "text/plain;charset=utf-8");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		post.setHeader("user-agent", "Mozilla/5.0 (Linux; Android 5.1; OPPO R9tm Build/LMY47I; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.49 Mobile MQQBrowser/6.2 TBS/043128 Safari/537.36 V1_AND_SQ_7.0.0_676_YYB_D PA QQ/7.0.0.3135 NetType/4G WebP/0.3.0 Pixel/1080");
		if (param!=null) {
			UrlEncodedFormEntity paramEntity=null;
			List<NameValuePair> paramList=new ArrayList<>();
			BasicNameValuePair paramItem=null;
			for (String key : param.keySet()) {
				paramItem = new BasicNameValuePair(key, param.get(key));
				paramList.add(paramItem);
			}
			try {
				paramEntity=new UrlEncodedFormEntity(paramList);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			post.setEntity(paramEntity);
		}
		HttpResponse response = null;
		try {
			response = client.execute(post);
			Header[] headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				System.err.println(headers[i].getName() + "----" + headers[i].getValue());
			}
			HttpEntity entity = response.getEntity();
			String resp = EntityUtils.toString(entity,"utf-8");
			System.out.println(resp);
			return resp;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String postJsonRequest(String url,Map<String, String> param) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).build();
		post.setConfig(config);
		post.setHeader("Accept", "application/json;charset=utf-8");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		post.setHeader("user-agent", "Mozilla/5.0 (Linux; Android 5.1; OPPO R9tm Build/LMY47I; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.49 Mobile MQQBrowser/6.2 TBS/043128 Safari/537.36 V1_AND_SQ_7.0.0_676_YYB_D PA QQ/7.0.0.3135 NetType/4G WebP/0.3.0 Pixel/1080");
		if (param!=null) {
			String jsonParam = JSONObject.toJSONString(param);
			StringEntity paramEntity=new StringEntity(jsonParam.toString(),"utf-8");
			post.setEntity(paramEntity);
		}
		HttpResponse response = null;
		try {
			response = client.execute(post);
			Header[] headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				System.err.println(headers[i].getName() + "----" + headers[i].getValue());
			}
			HttpEntity entity = response.getEntity();
			String resp = EntityUtils.toString(entity,"utf-8");
			System.out.println(resp);
			return resp;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static String postRequest(String url,Map<String, String> header,Map<String, String> param) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).build();
		post.setConfig(config);
		post.setHeader("Accept", "text/plain;charset=utf-8");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		post.setHeader("user-agent", "Mozilla/5.0 (Linux; Android 5.1; OPPO R9tm Build/LMY47I; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.49 Mobile MQQBrowser/6.2 TBS/043128 Safari/537.36 V1_AND_SQ_7.0.0_676_YYB_D PA QQ/7.0.0.3135 NetType/4G WebP/0.3.0 Pixel/1080");
		for (String key : header.keySet()) {
			post.setHeader(key, header.get(key));
		}
		if (param!=null) {
			UrlEncodedFormEntity paramEntity=null;
			List<NameValuePair> paramList=new ArrayList<>();
			BasicNameValuePair paramItem=null;
			for (String key : param.keySet()) {
				paramItem = new BasicNameValuePair(key, param.get(key));
				paramList.add(paramItem);
			}
			try {
				paramEntity=new UrlEncodedFormEntity(paramList);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			post.setEntity(paramEntity);
		}
		HttpResponse response = null;
		try {
			response = client.execute(post);
			Header[] headers = response.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				System.err.println(headers[i].getName() + "----" + headers[i].getValue());
			}
			HttpEntity entity = response.getEntity();
			String resp = EntityUtils.toString(entity,"utf-8");
			System.out.println(resp);
			return resp;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
