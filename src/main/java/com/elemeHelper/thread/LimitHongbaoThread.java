package com.elemeHelper.thread;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.elemeHelper.http.HttpUtil2;
import com.elemeHelper.result.Result;

public class LimitHongbaoThread extends Thread{
	
	public LimitHongbaoThread() {
		// TODO Auto-generated constructor stub
	}
	public LimitHongbaoThread(String name) {
		this.setName(name);
	}
	
	//限量
	public static final String url_limit_check ="https://h5.ele.me/restapi/member/v1/sign_in/limit/hongbao/info?channel=app";
	public static final String url_limit_receive ="https://h5.ele.me/restapi/member/v1/users/USERID/sign_in/limit/hongbao";
	
	public static Long checkLimitHongbao() {
		String resp = HttpUtil2.getRequest(url_limit_check,"utf-8");
		String body = resp;
		if (body.contains("current_at")) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) parser.parse(body);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return Long.valueOf(jsonObject.get("current_at").toString());
		}
		return (long) 0;
	}
	
	/**
	 * 本地整点时间比对他服务器的时间，在10s内，返回true
	 * @param serverTime
	 * @return
	 */
	public static boolean checkTime(Long serverTime) {
		int[] times= {10,14,17,20};
		Calendar cal=null;
		Long receiveTime=null;
		for (int i = 0; i < times.length; i++) {
			cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY,times[i]);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
			receiveTime=cal.getTimeInMillis();
			if (receiveTime-serverTime<=3000 && receiveTime-serverTime>=0) {
				return true;
			}
		}
		return false;
	}
	
	public static Result getLimitHongbao(String ck) {
		Map<String, String> headerMap =new HashMap<>();
		headerMap.put("cookie", ck);
		headerMap.put("User-agent", "Mozilla/5.0 (Linux; Android 5.0; SM-N9100 Build/LRX21V) > AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 > Chrome/37.0.0.0 Mobile Safari/537.36 V1_AND_SQ_5.3.1_196_YYB_D > QQ/5.3.1.2335 NetType/WIFI");
		Map<String, String> paramMap =new HashMap<>();
		paramMap.put("channel", "app");
		paramMap.put("userId", "145998491");
		String resp = HttpUtil2.postRequest(url_limit_receive.replace("USERID", "4765838938"), headerMap, paramMap);
		String body = resp;
		if (body.contains("message")) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) parser.parse(body);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return new Result(-1,resp);
		}
		System.out.println("ok");
		return new Result(0,resp);
	}
	
	public static boolean isCheck=true;
	public static boolean isBegin=false;
	public static int sleep=1000;
	
	@Override
	public void run() {
		 while (true) {
         	try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
         	if (isCheck) {
         		isBegin = checkTime(System.currentTimeMillis());
	            	System.out.println(this.getName()+isBegin+":"+System.currentTimeMillis());
				}
         	if (isBegin) {
         		isCheck=false;
         		sleep=600;
         		Result limitHongbao = getLimitHongbao("SID=iIhNhN0A3Rh1qbtT6jHXtHCF7B2IQBIbH7Hw;USERID=4765838938;track_id=1543295210|e8a5f3b5ca53306fad9740177b3d99497afdc1ee3b746159c4|72f72fe14b683a44e3ef4d81776240b0;");
					if (limitHongbao.getCode()!=-1) {
						this.interrupt();
						break;
					}
				}
         }
	}
	
	public static void main(String[] args) {
		System.err.println(System.currentTimeMillis());
		System.err.println(checkLimitHongbao());
		System.err.println(System.currentTimeMillis());
		for (int i = 0; i < 100; i++) {
			new LimitHongbaoThread("T"+i+" : ").start();
		}
	}

}
