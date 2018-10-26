package com.elemeHelper.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elemeHelper.dao.CookieDao;
import com.elemeHelper.entity.Cookie;
import com.elemeHelper.entity.User;
import com.elemeHelper.http.HttpUtil;
import com.elemeHelper.result.PageResult;
import com.elemeHelper.result.Result;
import com.elemeHelper.util.PageUtil;

@Service
public class ElemeService {

	private static final String url_openRedpacket = "https://h5.ele.me/restapi/marketing/promotion/weixin/OPENID";
	private static final String url_getLuckyNumber = "https://h5.ele.me/restapi/marketing/themes/3289/group_sns/REDPACKETID";
	private static String url=null;
	
	@Autowired
	private CookieDao cookieDao;

	public Result openRedpacket(String redpacketLink,HttpServletRequest request){
		if (redpacketLink==null||!Pattern.matches("^https?://.*?&sn=.*?$", redpacketLink)) {
			return new Result(-1, "请输入正确的链接");
		}
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser==null) {
			return new Result(-1, "请重新登录系统");
		}
		String redpacketId=getRedPacketId(redpacketLink);
		if (redpacketId == null) {
			return new Result(-1,"领取失败:红包id获取失败");
		}
		int luckyNumber = getLuckyNumber(redpacketId);
		if (luckyNumber==0) {
			return new Result(-1,"领取失败:最大包位置获取失败");
		}
		Map<String, String> mapHeader=new HashMap<>();
		Map<String, String> mapParam=new HashMap<>();
		
		List<Cookie> cookies = cookieDao.getAllByDatalevelNotAndCountLessThan(-1, 5);
		String openId = null;
		String sign = null;
		String trackId = null;
		String cookieStr=null;
		int openCount=0;
		boolean success=false;
		for (Cookie cookie : cookies) {
			cookieStr=cookie.getCookie();
			sign = getSign(cookieStr);
			trackId = getTrackId(cookieStr);
			mapHeader.put("cookie", cookieStr);
			mapParam.put("device_id", "");
			mapParam.put("hardware_id", "");
			mapParam.put("latitude", "");
			mapParam.put("longitude", "");
			mapParam.put("platform", 0+"");
			mapParam.put("unionid", "fuck");
			mapParam.put("weixin_avatar", "http://thirdqq.qlogo.cn/qqapp/101204453/FC8486BF4A505A66DA4A2A7DA27A91EF/40");
			mapParam.put("weixin_username", "admin");
			mapParam.put("method", "phone");
			mapParam.put("phone", cookie.getPhone());
			mapParam.put("group_sn", redpacketId);
			mapParam.put("sign", sign);
			mapParam.put("track_id", trackId);
			openId = getOpenId(cookieStr);
			url = url_openRedpacket.replace("OPENID", openId);
			String body = HttpUtil.postRequest(url, mapHeader,mapParam);
			if (body != null) {
				JSONParser parser = new JSONParser();
				JSONObject jsonObject = null;
				try {
					jsonObject = (JSONObject) parser.parse(body);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				JSONArray promotion_records = (JSONArray) jsonObject.get("promotion_records");
				openCount= promotion_records.size();
			}
			if (luckyNumber-1==openCount) {
				success=true;
				break;
			}
		}
		if (success) {
			return new Result("下一个即是大红包，速速领取哦");
		}else {
			return new Result(-1,"机器人使用过渡了，明天再来吧");
		}
	}
	
	
	public static String getRedPacketId(String url) {
		Pattern p = Pattern.compile("&sn=(.*?)&");
		Matcher m = p.matcher(url);
		String result = null;
		if (m.find()) {
			result = m.group(1);
			System.out.println(result);
			return result;
		}
		return null;
	}

	public static int getLuckyNumber(String redPacketId) {
		url = url_getLuckyNumber.replace("REDPACKETID", redPacketId);
		String body = HttpUtil.getRequest(url);
		if (body.contains("lucky_number")) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) parser.parse(body);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Object lucky_number = jsonObject.get("lucky_number");
			return Integer.valueOf(lucky_number.toString());
		}
		return 0;
	}

	public static String getSign(String cookie){
		try {
			cookie = URLDecoder.decode(cookie, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Pattern p = Pattern.compile("\"eleme_key\":\"(.*?)\"");
		Matcher m = p.matcher(cookie);
		String result = null;
		if (m.find()) {
			result = m.group(1);
			System.out.println(result);
			return result;
		}
		return null;
	}
	public static String getTrackId(String cookie){
		try {
			cookie = URLDecoder.decode(cookie, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Pattern p = Pattern.compile("track_id=(.*?);");
		Matcher m = p.matcher(cookie);
		String result = null;
		if (m.find()) {
			result = m.group(1);
			System.out.println(result);
			return result;
		}
		return null;
	}
	public static String getOpenId(String cookie){
		try {
			cookie = URLDecoder.decode(cookie, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Pattern p = Pattern.compile("\"openid\":\"(.*?)\"");
		Matcher m = p.matcher(cookie);
		String result = null;
		if (m.find()) {
			result = m.group(1);
			System.out.println(result);
			return result;
		}
		return null;
	}


	public static boolean isNewUser(String param, String cookie) throws Exception {
		String openId = getOpenId(cookie);
		url = url_openRedpacket.replace("OPENID", openId);
		String body = HttpUtil.postRequest(url, null, null);
		if (body != null) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(body);
			JSONArray promotion_items = (JSONArray) jsonObject.get("promotion_items");
			JSONObject redpacketItem=null;
			boolean isnewuser=false;
			for (int i = 0; i < promotion_items.size(); i++) {
				redpacketItem=(JSONObject) promotion_items.get(i);
				isnewuser=(boolean) redpacketItem.get("is_new_user");
				if (isnewuser) {
					return true;
				}
			}
		}
		return false;
	}


	public Result addCookie(String cookie, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}


	public Result delCookie(Long cookie, HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser==null) {
			return new Result(-1,"登录失效，请重新登录");
		}
		return null;
	}
	
	public PageResult list(HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser==null) {
			return new PageResult(PageUtil.redirect_login,"登录失效，请重新登录");
		}
		Long creatorId = null;
		List<Cookie> cookies = cookieDao.getAllByDatalevelNotAndCreatorId(0, creatorId);
		request.setAttribute("cookies", cookies);
		return new PageResult(PageUtil.eleme_cookie_list,null);
	}
}
