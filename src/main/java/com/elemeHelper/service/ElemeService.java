package com.elemeHelper.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
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
import com.elemeHelper.dao.LogRedpacketDao;
import com.elemeHelper.entity.Cookie;
import com.elemeHelper.entity.User;
import com.elemeHelper.entity.logRedpacket;
import com.elemeHelper.http.HttpUtil;
import com.elemeHelper.result.PageResult;
import com.elemeHelper.result.Result;
import com.elemeHelper.util.PageUtil;

@Service
public class ElemeService {

	private static final String url_openRedpacket = "https://h5.ele.me/restapi/marketing/promotion/weixin/OPENID";
	private static final String url_getLuckyNumber = "https://h5.ele.me/restapi/marketing/themes/3289/group_sns/REDPACKETID";
	private static final String url_checkCookie = "https://h5.ele.me/restapi/traffic/redpacket/check";
	private static String url=null;
	
	@Autowired
	private CookieDao cookieDao;
	@Autowired
	private LogRedpacketDao logDao;

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
			cookieStr=cookie.getValue();
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
				if (promotion_records!=null) {
					openCount= promotion_records.size();
					List<logRedpacket> log = logDao.getListByRedpacketIdAndOpenId(redpacketId, cookie.getUserId());
					if (log==null||log.size()==0) {
						logDao.save(new logRedpacket(redpacketId,"",cookie.getUserId(),cookie.getPhone(),"",0,sessionUser.getId()));
						cookieDao.use(cookie.getId());
					}
				}
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
	
	public static String getRedpackId(String str){
		try {
			str = URLDecoder.decode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Pattern p = Pattern.compile("\"packet_id\":\"(.*?)\"");
		Matcher m = p.matcher(str);
		String result = null;
		if (m.find()) {
			result = m.group(1);
			System.out.println(result);
			return result;
		}
		return null;
	}
	
	public static String getUserId(String cookie){
		try {
			cookie = URLDecoder.decode(cookie, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Pattern p = Pattern.compile("USERID=(.*?);");
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


	public Result addCookie(Cookie cookie, HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser==null) {
			return new Result(-1,"登录失效，请重新登录");
		}
		String cookieStr = cookie.getValue();
		String userId = getUserId(cookieStr);
		if (userId==null) {
			return new Result(-1,"无效cookie");
		}
		List<Cookie> list = cookieDao.getByDatalevelNotAndUserId(-1, userId);
		if (list!=null&&list.size()>0) {
			return new Result(-1,"这个cookie已经上传过了,换一个吧");
		}
		Map<String, String> head=new HashMap<>();
		head.put("cookie", cookieStr);
		Map<String, String> param=new HashMap<>();
		param.put("user_id", userId);
		param.put("packet_id", "0");
		param.put("lat", "");
		param.put("lng", "");
		String resp = HttpUtil.postRequest(url_checkCookie, head, param);
		if (resp==null||getRedpackId(resp)==null) {
			return new Result(-1,"无效cookie");
		}
		cookie.setUserId(userId);
		cookie.setDatalevel(0);
		cookie.setCreateDate(new Date());
		cookie.setCount(0);
		cookie.setCreatorId(sessionUser.getId());
		cookie.setType(0);
		Cookie save = cookieDao.save(cookie);
		if (save==null) {
			return new Result(-1,"上传失败");
		}
		return new Result("上传成功");
	}


	public Result delCookie(Long cookieId, HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser==null) {
			return new Result(-1,"登录失效，请重新登录");
		}
		int i=cookieDao.del(cookieId);
		if (i==0) {
			return new Result(-1,"删除失败");
		}
		return new Result("删除成功!");
	}
	
	public PageResult list(HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser==null) {
			return new PageResult(PageUtil.redirect_login,"登录失效，请重新登录");
		}
		Long creatorId = sessionUser.getId();
		List<Cookie> cookies = cookieDao.getAllByDatalevelNotAndCreatorId(-1, creatorId);
		request.setAttribute("cookies", cookies);
		return new PageResult(PageUtil.eleme_cookie_list,null);
	}
}
