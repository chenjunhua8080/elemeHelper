package com.elemeHelper.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elemeHelper.dao.CookieDao;
import com.elemeHelper.dao.LogPromotionDao;
import com.elemeHelper.dao.LogRedpacketDao;
import com.elemeHelper.dao.TokenDao;
import com.elemeHelper.entity.Cookie;
import com.elemeHelper.entity.LogPromotion;
import com.elemeHelper.entity.Token;
import com.elemeHelper.entity.User;
import com.elemeHelper.entity.logRedpacket;
import com.elemeHelper.http.HttpUtil;
import com.elemeHelper.result.PageResult;
import com.elemeHelper.result.Result;
import com.elemeHelper.util.PageUtil;

import sun.misc.BASE64Decoder;

@Service
public class ElemeService {

	// 拼手气
	private static final String url_open_luck_redpacket = "https://h5.ele.me/restapi/marketing/promotion/weixin/OPENID";
	private static final String url_get_lucky_number = "https://h5.ele.me/restapi/marketing/themes/3289/group_sns/REDPACKETID";
	private static final String url_check_cookie = "https://h5.ele.me/restapi/traffic/redpacket/check";
	// 登录
	public static final String url_get_img_captchas = "https://h5.ele.me/restapi/eus/v3/captchas";
	public static final String url_send_code = "https://h5.ele.me/restapi/eus/login/mobile_send_code";
	public static final String url_login = "https://h5.ele.me/restapi/eus/login/login_by_mobile";
	
	// 检测
	public static final String url_newuser = "http://ele.hongbao.show/webService/shopGatherController?m=elmNew";
	
	// 拆包
	public static final String url_get_redpacket = "https://h5.ele.me/restapi/traffic/redpacket/check";
	public static final String url_open_redpacket = "https://h5.ele.me/restapi/traffic/redpacket/open";
	
	// 新客
	public static final String url_new_platform = "https://h5.ele.me/restapi/marketing/promotion/refer/USERID";

	private static String url = null;

	@Autowired
	private CookieDao cookieDao;
	@Autowired
	private LogRedpacketDao logRedpacketDao;
	@Autowired
	private LogPromotionDao logPromotionDao;
	@Autowired
	private BwmService bwmService;
	@Autowired
	private LzService lzService;
	@Autowired
	private TokenDao tokenDao;

	public Result openRedpacket(String redpacketLink, HttpServletRequest request) {
		if (redpacketLink == null || !Pattern.matches("^https?://.*?&sn=.*?$", redpacketLink)) {
			return new Result(-1, "请输入正确的链接");
		}
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser == null) {
			return new Result(-1, "请重新登录系统");
		}
		String redpacketId = getRedPacketId(redpacketLink);
		if (redpacketId == null) {
			return new Result(-1, "领取失败:红包id获取失败");
		}
		int luckyNumber = getLuckyNumber(redpacketId);
		if (luckyNumber == 0) {
			return new Result(-1, "领取失败:最大包位置获取失败");
		}
		Map<String, String> mapHeader = new HashMap<>();
		Map<String, String> mapParam = new HashMap<>();

		List<Cookie> cookies = cookieDao.getAllByDatalevelNotAndCountLessThan(-1, 5);
		String openId = null;
		String sign = null;
		String trackId = null;
		String cookieStr = null;
		int openCount = 0;
		boolean success = false;
		for (Cookie cookie : cookies) {
			cookieStr = cookie.getValue();
			sign = getSign(cookieStr);
			trackId = getTrackId(cookieStr);
			mapHeader.put("cookie", cookieStr);
			mapParam.put("device_id", "");
			mapParam.put("hardware_id", "");
			mapParam.put("latitude", "");
			mapParam.put("longitude", "");
			mapParam.put("platform", 0 + "");
			mapParam.put("unionid", "fuck");
			mapParam.put("weixin_avatar",
					"http://thirdqq.qlogo.cn/qqapp/101204453/FC8486BF4A505A66DA4A2A7DA27A91EF/40");
			mapParam.put("weixin_username", "admin");
			mapParam.put("method", "phone");
			mapParam.put("phone", cookie.getPhone());
			mapParam.put("group_sn", redpacketId);
			mapParam.put("sign", sign);
			mapParam.put("track_id", trackId);
			openId = getOpenId(cookieStr);
			url = url_open_luck_redpacket.replace("OPENID", openId);
			String body = HttpUtil.postRequest(url, mapHeader, mapParam);
			if (body != null) {
				JSONParser parser = new JSONParser();
				JSONObject jsonObject = null;
				try {
					jsonObject = (JSONObject) parser.parse(body);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				JSONArray promotion_records = (JSONArray) jsonObject.get("promotion_records");
				if (promotion_records != null) {
					openCount = promotion_records.size();
					List<logRedpacket> log = logRedpacketDao.getListByRedpacketIdAndOpenId(redpacketId,
							cookie.getUserId());
					if (log == null || log.size() == 0) {
						logRedpacket save = logRedpacketDao.save(new logRedpacket(redpacketId, null, cookie.getUserId(),
								cookie.getPhone(), 0, sessionUser.getId()));
						cookieDao.use(cookie.getId());
						JSONArray promotion_item = (JSONArray) jsonObject.get("promotion_items");
						if (promotion_item != null) {
							JSONObject jsonObj = null;
							LogPromotion promotion = null;
							for (int i = 0; i < promotion_item.size(); i++) {
								jsonObj = (JSONObject) promotion_item.get(i);
								promotion = new LogPromotion();
								promotion.setCreatorId(sessionUser.getId());
								promotion.setLogRedpacketId(save.getId());
								promotion.setPhone((String) jsonObj.get("phone"));
								promotion.setName((String) jsonObj.get("name"));
								promotion.setAmount(String.valueOf(jsonObj.get("amount")));
								promotion.setSum_condition(String.valueOf(jsonObj.get("sum_condition")));
								promotion.setValidity_periods((String) jsonObj.get("validity_periods"));
								promotion.setType(0);
								logPromotionDao.save(promotion);
							}
						}
					}
				}
			}
			if (luckyNumber - 1 == openCount) {
				success = true;
				break;
			} else if (openCount >= luckyNumber) {
				return new Result(-1, "最大包已被领取了");
			}
		}
		if (success) {
			return new Result("下一个即是大红包，速速领取哦");
		} else {
			return new Result(-1, "机器人使用过渡了，明天再来吧");
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
		url = url_get_lucky_number.replace("REDPACKETID", redPacketId);
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

	public static String getSign(String cookie) {
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

	public static String getTrackId(String cookie) {
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

	public static String getOpenId(String cookie) {
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

	public static String getRedpackId(String str) {
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

	public static String getUserId(String cookie) {
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
	
	public static String getValidata(String str) {
		Pattern p = Pattern.compile("验证码是(\\d{6})");
		Matcher m = p.matcher(str);
		String result = null;
		if (m.find()) {
			result = m.group(1);
			System.out.println(result);
			return result;
		}
		return null;
	}

	public Result addCookie(Cookie cookie, HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser == null) {
			return new Result(-1, "登录失效，请重新登录");
		}
		String cookieStr = cookie.getValue();
		String userId = getUserId(cookieStr);
		if (userId == null) {
			return new Result(-1, "无效cookie");
		}
		List<Cookie> list = cookieDao.getByDatalevelNotAndUserId(-1, userId);
		if (list != null && list.size() > 0) {
			return new Result(-1, "这个cookie已经上传过了,换一个吧");
		}
		Map<String, String> head = new HashMap<>();
		head.put("cookie", cookieStr);
		Map<String, String> param = new HashMap<>();
		param.put("user_id", userId);
		param.put("packet_id", "0");
		param.put("lat", "");
		param.put("lng", "");
		String resp = HttpUtil.postRequest(url_check_cookie, head, param);
		if (resp == null || getRedpackId(resp) == null) {
			return new Result(-1, "无效cookie");
		}
		cookie.setUserId(userId);
		cookie.setDatalevel(0);
		cookie.setCreateDate(new Date());
		cookie.setCount(0);
		cookie.setCreatorId(sessionUser.getId());
		cookie.setType(0);
		Cookie save = cookieDao.save(cookie);
		if (save == null) {
			return new Result(-1, "上传失败");
		}
		return new Result("上传成功");
	}

	public Result delCookie(Long cookieId, HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser == null) {
			return new Result(-1, "登录失效，请重新登录");
		}
		int i = cookieDao.del(cookieId);
		if (i == 0) {
			return new Result(-1, "删除失败");
		}
		return new Result("删除成功!");
	}

	public PageResult listCookie(HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser == null) {
			return new PageResult(PageUtil.redirect_login2, "登录失效，请重新登录");
		}
		Long creatorId = sessionUser.getId();
		List<Cookie> cookies = cookieDao.getAllByDatalevelNotAndCreatorId(-1, creatorId);
		request.setAttribute("cookies", cookies);
		return new PageResult(PageUtil.eleme_cookie_list, null);
	}

	public PageResult listLuck(HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser == null) {
			return new PageResult(PageUtil.redirect_login2, "登录失效，请重新登录");
		}
		Long creatorId = sessionUser.getId();
		List<LogPromotion> list = logPromotionDao.getListByDatalevelAndCreatorId(0, creatorId);
		request.setAttribute("promotions", list);
		return new PageResult(PageUtil.eleme_luck, null);
	}

	public Result run(HttpServletRequest request) {
		HttpSession session = request.getSession();
		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			return new Result(-1,"重新登录系统");
		}
		User bwmUser = (User) session.getAttribute("bwm");
		if (bwmUser == null) {
			return new Result(-1,"未登录百万码");
		}
		User lz = (User) session.getAttribute("lz");
		if (lz == null) {
			return new Result(-1,"未登录联众");
		}
		Token token = tokenDao.getLastToken(1,bwmUser.getId());
		String phone = bwmService.getPhone(token.getToken(), "56206");
		if (phone.contains("过期")) {
			bwmService.getNewToken(request);
			token = tokenDao.getLastToken(1,bwmUser.getId());
			phone = bwmService.getPhone(token.getToken(), "56206");
		}
		Map<String, String> captchas=null;
		try {
			captchas = getCaptchas(phone, session.getServletContext().getRealPath(""));
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		String captcha_value = lzService.upload(captchas.get("captcha_base64"), request);
		String validate_token = sendCode(phone, captchas.get("captcha_hash"), captcha_value);
		while (validate_token==null) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			captcha_value = lzService.upload(captchas.get("captcha_base64"), request);
			validate_token=sendCode(phone, captchas.get("captcha_hash"), captcha_value);
		}
		String validate_code = bwmService.getMessage(phone, token.getToken());
		while (!validate_code.contains("验证码")) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			validate_code=bwmService.getMessage(phone, token.getToken());
		}
		validate_code = getValidata(validate_code);
		Map<String, String> cookies = login(phone, validate_code, validate_token);
		String packetId = getRedpacket(cookies);
		boolean isOpen = openRedpacket(cookies, packetId);
		return new Result(isOpen);
	}

	/**
	 * 通过领取红包返回，是否新用户
	 * 
	 * @param param
	 * @param cookie
	 * @return
	 * @throws Exception
	 */
	public boolean isNewUser(String param, String cookie) throws Exception {
		String openId = getOpenId(cookie);
		url = url_open_luck_redpacket.replace("OPENID", openId);
		String body = HttpUtil.postRequest(url, null, null);
		if (body != null) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(body);
			JSONArray promotion_items = (JSONArray) jsonObject.get("promotion_items");
			JSONObject redpacketItem = null;
			boolean isnewuser = false;
			for (int i = 0; i < promotion_items.size(); i++) {
				redpacketItem = (JSONObject) promotion_items.get(i);
				isnewuser = (boolean) redpacketItem.get("is_new_user");
				if (isnewuser) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 通过特殊接口，判断是否新用户
	 * 
	 * @param phone
	 * @return
	 */
	public boolean isNewUser(String phone) {
		Map<String, String> param = new HashMap<>();
		param.put("mobile", phone);
		param.put("r_url", "");// http://api.hongbao.show/webService/
		Map<String, String> header = new HashMap<String, String>();
		header.put("Accept", "application/json");
		header.put("Content-Type", "application/x-www-form-urlencoded");
		header.put("Referer", "http://ele.hongbao.show/webService/shopGather/elm-new/index.html?channelId=32010380");
		String resp = HttpUtil.postRequest(url_newuser, header, param);
		System.out.println(resp);
		return false;
	}

	public Map<String, String> getCaptchas(String phone,String path) throws ParseException, IOException {
		Map<String, String> result = new HashMap<>();
		Map<String, String> param = new HashMap<>();
		param.put("captcha_str", phone);
		String resp = HttpUtil.postRequest(url_get_img_captchas, param);
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = null;
		jsonObj = (JSONObject) parser.parse(resp);
		String captcha_hash = (String) jsonObj.get("captcha_hash");
		String captcha_image = (String) jsonObj.get("captcha_image");
		String captcha_base64 = captcha_image.substring("data:image/jpeg;base64,".length(), captcha_image.length());
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] b;
		b = decoder.decodeBuffer(captcha_base64);
		for (int i = 0; i < b.length; ++i) {
			if (b[i] < 0) {// 调整异常数据
				b[i] += 256;
			}
		}
		String file = path + new Date().getTime() + ".jpg";
		System.out.println(file);
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileOutputStream.write(b);
		fileOutputStream.flush();
		fileOutputStream.close();

		result.put("captcha_hash", captcha_hash);
		result.put("captcha_path", file);
		result.put("captcha_base64", captcha_base64);
		return result;
	}

	public String sendCode(String phone,String captcha_hash,String captcha_value) {
		Map<String, String> param = new HashMap<>();
		param.put("mobile", phone);
		param.put("captcha_hash", captcha_hash);
		param.put("captcha_value", captcha_value);
		String resp = HttpUtil.postRequest(url_send_code, param);
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = null;
		Object validate_token=null;
		try {
			jsonObj = (JSONObject) parser.parse(resp);
			validate_token = jsonObj.get("validate_token");
		} catch (ParseException e) {
			System.err.println(jsonObj);
			e.printStackTrace();
		}
		if (validate_token==null) {
			return null;
		}
		return validate_token.toString();
	}
	
	public Map<String, String> login(String phone,String validate_code,String validate_token) {
		Map<String, String> header = new HashMap<>();
		header.put("cookie", "perf_ssid=ys0xa75xhmg3sgcuj4ts2rxe994ia42e_2018-10-14; ubt_ssid=ajxucwkxi2omx2o3e6efttwd4c2cytjr_2018-10-14; _utrace=fc5f68e484068e39207537d353b2883a_2018-10-14; snsInfo[101204453]=%7B%22city%22%3A%22%22%2C%22constellation%22%3A%22%22%2C%22eleme_key%22%3A%22b3225995880342b7a5b568ffcf970510%22%2C%22figureurl%22%3A%22http%3A%2F%2Fqzapp.qlogo.cn%2Fqzapp%2F101204453%2F13702FA623DF912DD822E9FF212F2484%2F30%22%2C%22figureurl_1%22%3A%22http%3A%2F%2Fqzapp.qlogo.cn%2Fqzapp%2F101204453%2F13702FA623DF912DD822E9FF212F2484%2F50%22%2C%22figureurl_2%22%3A%22http%3A%2F%2Fqzapp.qlogo.cn%2Fqzapp%2F101204453%2F13702FA623DF912DD822E9FF212F2484%2F100%22%2C%22figureurl_qq_1%22%3A%22http%3A%2F%2Fthirdqq.qlogo.cn%2Fqqapp%2F101204453%2F13702FA623DF912DD822E9FF212F2484%2F40%22%2C%22figureurl_qq_2%22%3A%22http%3A%2F%2Fthirdqq.qlogo.cn%2Fqqapp%2F101204453%2F13702FA623DF912DD822E9FF212F2484%2F100%22%2C%22gender%22%3A%22%E7%94%B7%22%2C%22is_lost%22%3A0%2C%22is_yellow_vip%22%3A%220%22%2C%22is_yellow_year_vip%22%3A%220%22%2C%22level%22%3A%220%22%2C%22msg%22%3A%22%22%2C%22nickname%22%3A%22%E8%BD%BB%E9%A2%A6%22%2C%22openid%22%3A%2213702FA623DF912DD822E9FF212F2484%22%2C%22province%22%3A%22%22%2C%22ret%22%3A0%2C%22vip%22%3A%220%22%2C%22year%22%3A%220%22%2C%22yellow_vip_level%22%3A%220%22%2C%22name%22%3A%22%E8%BD%BB%E9%A2%A6%22%2C%22avatar%22%3A%22http%3A%2F%2Fthirdqq.qlogo.cn%2Fqqapp%2F101204453%2F13702FA623DF912DD822E9FF212F2484%2F40%22%7D; track_id=1539527993|b5ffd878c7ed0a9494cb3d7df26015e21614b51f4ec898609a|92a23ab44531c0e81ce8061a0600b539; USERID=2250533306; SID=gVk6Nj9eJaA80qlcqds7ZIPDGuULzuNZIrWw");
		Map<String, String> param = new HashMap<>();
		param.put("mobile", phone);
		param.put("validate_code", validate_code);
		param.put("validate_token", validate_token);
		Map<String, String> resp = HttpUtil.getCookieByPostRequest(url_login,header, param);
		return resp;
	}
	
	public String getRedpacket(Map<String, String> cookies) {
		String packetId=null;
		Map<String, String> param = new HashMap<>();
		param.put("user_id", "userid");
		param.put("lat", "23.09339");
		param.put("lng", "113.315966");
		param.put("packet_id", "0");
		Map<String, String> resp = HttpUtil.setCookieByPostRequest(url_get_redpacket, cookies, param);
		String body = resp.get("body");
		if (body.contains("成功")) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj = null;
			JSONObject jsonData = null;
			try {
				jsonObj = (JSONObject) parser.parse(body);
				jsonData = (JSONObject) jsonObj.get("data");
				packetId = (String) jsonData.get("packet_id");
				System.out.println("取包成功："+packetId);
				System.out.println("红包金额："+jsonData.get("total_amount"));
				System.out.println("拆开金额："+jsonData.get("opened_amount"));
			} catch (ParseException e) {
				System.err.println(jsonObj);
				e.printStackTrace();
			}
		}
		return packetId;
	}
	
	public boolean openRedpacket(Map<String, String> cookies,String packetId) {
		Map<String, String> param = new HashMap<>();
		param.put("user_id","userId");
		param.put("lat", "23.09339");
		param.put("lng", "113.315966");
		param.put("packet_id", packetId);
		param.put("nickname", "拆房大队");
		param.put("avatar", "https://thirdqq.qlogo.cn/g?b=sdk&k=9AFfpMJtickCFia2LIjpYKPQ&s=100&t=1535538633");
		Map<String, String> resp = HttpUtil.setCookieByPostRequest(url_open_redpacket, cookies, param);
		String body = resp.get("body");
		if (body.contains("成功")) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj = null;
			JSONObject jsonData = null;
			try {
				jsonObj = (JSONObject) parser.parse(body);
				jsonData = (JSONObject) jsonObj.get("data");
				Double openAmount  = Double.valueOf(jsonData.get("opening_amount").toString());
				if (openAmount!=null && openAmount>0) {
					packetId = (String) jsonData.get("packet_id");
					System.out.println("红包ID："+packetId);
					System.out.println("红包金额："+jsonData.get("total_amount"));
					System.out.println("拆开金额："+jsonData.get("opening_amount"));
					System.out.println("未拆金额："+jsonData.get("remaining_amount"));
					return true;
				}
			} catch (ParseException e) {
				System.err.println(jsonObj);
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		ElemeService service = new ElemeService();
		service.isNewUser("13413527258");
	}
}
