package com.elemeHelper.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Random;
import java.util.Set;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

public class HttpUtil {

	public static void main(String[] args) throws Exception {
		String[] names={"赵","钱","孙","李","周","吴","郑","王","冯","陈","褚","卫","蒋","沈","韩","杨","朱","秦","尤","许","何","吕","施","张","孔","曹","严","华","金","魏","陶","姜","戚"};
		String name = names[(int) (Math.random() * names.length)]+"先生";
		String phone ="1651000"+ (int) (Math.random() * 9999);
		String text="越做越大!";
		Map<String,String> map=new HashMap<>();
		map.put("submitdata","1$"+name+"}2$2}3$1}4$"+phone+"}5$1}6$}7$3}8$}9$2}10$}11$3}12$}13$1}14$}15$2}16$}17$4}18$}19$3}20$}21$4}22$}23$2}24$}25$1|11|14|17}26$}27$2|4|5|9}28$}29$1}30$}31$3}32$暂无}33$}34$"+text);
		String url="https://www.wjx.cn/joinnew/processjq.ashx?curid=42677459&starttime=2019%2F7%2F17%2015%3A47%3A14&source=directphone&submittype=1&ktimes=340&hlv=1&rn=3073433234.66648860&rname=%E5%B0%8F%E5%B9%B3%E5%A7%90%E5%A7%90&t=1563263429499&jqnonce=e657ee15-83d6-4e90-8ed2-1b520148e354&jqsign=d746dd04%2C92e7%2C5d81%2C9de3%2C0c431059d245";
		String resp = postRequest(url, map);
		System.err.println("*************"+resp);
		assert resp != null;
		if (resp.contains("JoinID")){
			String id=resp.substring(resp.indexOf("&JoinID=")+"&JoinID=".length(),resp.indexOf("&jidx="));
			url="https://www.wjx.cn/joinnew/userawardactivity.ashx?starttime=2019%2F7%2F16%2014%3A00%3A32&t=1563257013593&activity=42677459&joinactivity="+id+"&mob=1";
			resp = postRequest(url, null);
			System.err.println("*************"+resp);
			if (resp.contains("_")){
				Map<String,String> gift=new HashMap<>();
				gift.put("184507","LED灯");
				gift.put("184508","乐扣礼盒套装");
				gift.put("184509","1元微信红包");
				gift.put("184510","2元微信红包");
				gift.put("184511","10元微信红包");
				gift.put("184512","5元微信红包");
				String giftId = resp.split("_")[0];
				System.out.println("*************中奖啦--"+gift.get(giftId));
				System.out.println("https://www.wjx.cn/mobile/getred.aspx?activity=42677459&joinactivity="+id+"&awardId="+giftId);
			}
		}

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
		HttpHost proxy = new HttpHost("47.107.94.125",22);
		RequestConfig config = RequestConfig.custom()
//			.setProxy(proxy)
			.setConnectTimeout(10000)
			.setSocketTimeout(10000)
			.setConnectionRequestTimeout(3000)
			.build();
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
				paramEntity=new UrlEncodedFormEntity(paramList,"utf-8");
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
	public static Map<String, String> setCookieByGetRequest(String url,Map<String, String> cookies) {
		cookies.remove("body");
		Map<String, String> result=new HashMap<>();
		CookieStore cookieStore=new BasicCookieStore();
		BasicClientCookie reqCookie=null;
		for (String key : cookies.keySet()) {
			reqCookie=new BasicClientCookie(key, cookies.get(key));
			cookieStore.addCookie(reqCookie);
		}
		CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		HttpGet get = new HttpGet(url);
		RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).build();
		get.setConfig(config);
		get.setHeader("Accept", "text/plain;charset=utf-8");
		get.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		get.setHeader("user-agent", "Mozilla/5.0 (Linux; Android 5.1; OPPO R9tm Build/LMY47I; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.49 Mobile MQQBrowser/6.2 TBS/043128 Safari/537.36 V1_AND_SQ_7.0.0_676_YYB_D PA QQ/7.0.0.3135 NetType/4G WebP/0.3.0 Pixel/1080");
		HttpResponse response = null;
		try {
			response = client.execute(get);

			List<Cookie> respCookies = cookieStore.getCookies();
			Cookie respCookie=null;
			for (int i = 0; i < respCookies.size(); i++) {
				respCookie = respCookies.get(i);
				result.put(respCookie.getName(), respCookie.getValue());
				System.out.println("cookie_"+respCookie.getName()+" : "+respCookie.getValue());
			}
			HttpEntity entity = response.getEntity();
			String resp = EntityUtils.toString(entity,"utf-8");
			result.put("body", resp);
			System.err.println(resp);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			/*try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		}
		return result;
	}
	public static Map<String, String> setCookieByPostRequest(String url,Map<String, String> cookies,Map<String, String> param) {
		cookies.remove("body");
		Map<String, String> result=new HashMap<>();
		/*CookieStore cookieStore=new BasicCookieStore();
		BasicClientCookie reqCookie=null;
		for (String key : cookies.keySet()) {
			reqCookie=new BasicClientCookie(key, cookies.get(key));
			cookieStore.addCookie(reqCookie);
		}
		CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();*/
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

			List<Cookie> respCookies = cookieStore.getCookies();
			Cookie respCookie=null;
			for (int i = 0; i < respCookies.size(); i++) {
				respCookie = respCookies.get(i);
				result.put(respCookie.getName(), respCookie.getValue());
				System.out.println("cookie_"+respCookie.getName()+" : "+respCookie.getValue());
			}
			HttpEntity entity = response.getEntity();
			String resp = EntityUtils.toString(entity,"utf-8");
			result.put("body", resp);
			System.err.println(resp);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			/*try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		}
		return result;
	}
	private static CloseableHttpClient client;
	private static CookieStore cookieStore;
	public static Map<String, String> getCookieByPostRequest(String url,Map<String, String> cookies,Map<String, String> param) {
		Map<String, String> result=new HashMap<>();
			cookieStore=new BasicCookieStore();
		BasicClientCookie reqCookie=null;
		for (String key : cookies.keySet()) {
			reqCookie=new BasicClientCookie(key, cookies.get(key));
			cookieStore.addCookie(reqCookie);
		}
		client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
		HttpPost post = new HttpPost(url);
		RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).build();
		post.setConfig(config);
		post.setHeader("Accept", "text/plain;charset=utf-8");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		post.setHeader("user-agent", "Mozilla/5.0 (Linux; Android 5.1; OPPO R9tm Build/LMY47I; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/53.0.2785.49 Mobile MQQBrowser/6.2 TBS/043128 Safari/537.36 V1_AND_SQ_7.0.0_676_YYB_D PA QQ/7.0.0.3135 NetType/4G WebP/0.3.0 Pixel/1080");
//		for (String key : header.keySet()) {
//			post.setHeader(key, header.get(key));
//		}
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

			List<Cookie> respCookies = cookieStore.getCookies();
			Cookie respCookie=null;
			for (int i = 0; i < respCookies.size(); i++) {
				respCookie = respCookies.get(i);
				result.put(respCookie.getName(), respCookie.getValue());
				System.out.println("cookie_"+respCookie.getName()+" : "+respCookie.getValue());
			}
			HttpEntity entity = response.getEntity();
			String resp = EntityUtils.toString(entity,"utf-8");
			result.put("body", resp);
			System.err.println(resp);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			/*try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		}
		return result;
	}
	
	
	public static String postJsonRequest(String url,Map<String, String> param) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		RequestConfig config = RequestConfig.custom().setConnectTimeout(10000).build();
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
			try {
				response = client.execute(post);
			} catch (Exception e) {
				try {
					response = client.execute(post);
				} catch (Exception e2) {
					e2.printStackTrace();
					System.err.println("识别验证码失败：上传图片异常:"+e2.getMessage());
				}
			}
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
