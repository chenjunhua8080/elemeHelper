package com.elemeHelper.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.transaction.annotation.Transactional;

import com.elemeHelper.dao.CookieDao;
import com.elemeHelper.dao.LogPromotionDao;
import com.elemeHelper.dao.LogRedpacketDao;
import com.elemeHelper.dao.SignInfoDao;
import com.elemeHelper.dao.TokenDao;
import com.elemeHelper.entity.Cookie;
import com.elemeHelper.entity.LogPromotion;
import com.elemeHelper.entity.SignInfo;
import com.elemeHelper.entity.Sign;
import com.elemeHelper.entity.Token;
import com.elemeHelper.entity.User;
import com.elemeHelper.entity.logRedpacket;
import com.elemeHelper.entity.Prize;
import com.elemeHelper.http.HttpUtil2;
import com.elemeHelper.result.PageResult;
import com.elemeHelper.result.Result;
import com.elemeHelper.util.PageUtil;

import sun.misc.BASE64Decoder;

@Transactional
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
	public static final String url_newuser_prizecode ="http://ele.hongbao.show/webService/UserVisitController?m=getPrizeCode";
	public static final String url_check_new_1104 ="http://stoneflying.cn/e/e.php?tel=PHONE";
	public static final String url_check_new_1119 ="https://h5.ele.me/restapi/marketing/hongbao/h5/grab";
	
	// 拆包
	public static final String url_get_redpacket = "https://h5.ele.me/restapi/traffic/redpacket/check";
	public static final String url_open_redpacket = "https://h5.ele.me/restapi/traffic/redpacket/open";
	
	// 新客
	public static final String url_new_platform = "https://h5.ele.me/restapi/marketing/promotion/refer/USERID";

	// 抽会员
	public static final String url_get_vip = "https://h5.ele.me/restapi/member/v1/users/USERID/supervip/growth/prize";
	public static final String url_is_vip = "https://h5.ele.me/restapi/member/v1/users/USERID/supervip?latitude=23.09339&longitude=113.315966&source=3";
	
	//查询红包
	public static final String url_get_share = "https://h5.ele.me/restapi/promotion/v3/users/USERID/hongbaos?offset=0&limit=20&cart_sub_channel=share";
	public static final String url_get_coupons = "https://h5.ele.me/restapi/promotion/v1/users/USERID/coupons?cart_sub_channel=share";
	
	//29-13
	public static final String url_check_29_13 = "https://newretail.ele.me/newretail/act/newguestwelfare?city_id=CITY&lat=LAT&lng=LNG&user_id=USERID";
	public static final String url_get_29_13="https://newretail.ele.me/newretail/act/takewelfare?city_id=CITY&device_id=DEVICE&lat=LAT&lng=LNG&redbag_location=INDEX&user_id=USER";
			
	//1111
	public static final String url_get_shoplist1="https://newretail.ele.me/newretail/main/shoplist?address=&cat_id=0&channel=fresh&device_id=DEVICE&fromalipay=0&pn=1&rn=20&rule_id=0&scene_id=0&scene_type=shop&type=1&user_type=newuser&lng=120.13185&lat=30.87806&city_id=CITY";
	public static final String url_get_shoplist="https://newretail-huodong.ele.me/newretail/shuangshiyi/venue?address=&city_id=4&device_id=DEVICE&lat=LAT&lng=LNG&sub_id=&with_tb=0";
	public static final String url_get_1111Au="https://newretail-huodong.ele.me/newretail/shuangshiyi/signgiftcash?city_id=CITY&lat=LAT&lng=LNG&shop_id=SHOPID";
	public static final String url_get_1111Au_sum="https://newretail-huodong.ele.me/newretail/shuangshiyi/giftcash?city_id=CITY&from=shop&lat=LAT&lng=LNG&shop_id=SHOPID";
	
	//收货地址
	public static final String url_get_address = "https://h5.ele.me/restapi/member/v1/users/USERID/addresses";
	
	//订单
	public static final String url_get_order = "https://h5.ele.me/restapi/bos/v2/users/USERID/orders?limit=20&offset=0";
	
	//统一图片资源
	public static final String url_img_host ="https://fuss10.elemecdn.com/";
	
	//福袋
	public static final String url_get_mission ="https://h5.ele.me/restapi/marketing/v1/users/USERID/new_user_mission/rewards?user_id=USERID&device_id=&latitude=LAT&longitude=LNG";
	public static final String url_get_sudhana ="https://h5.ele.me/restapi/traffic/sudhana/send";
	public static final String url_get_newusergift ="https:/h5.ele.me/marketing/v3/users/USERID/new_user_gifts?geohash=ws0e6ecy9uzx";
	
	//签到
	public static final String url_sign_info ="https://h5.ele.me/restapi/member/v1/users/USERID/sign_in/info";
	public static final String url_sign ="https://h5.ele.me/restapi/member/v1/users/USERID/sign_in";
	public static final String url_sign_captcha ="https://h5.ele.me/restapi/member/v1/users/USERID/sign_in/risk/captcha";
	public static final String url_sign_prize ="https://h5.ele.me/restapi/member/v1/users/USERID/sign_in/daily/prize";
	public static final String url_sign_prize_v2 ="https://h5.ele.me/restapi/member/v2/users/USERID/sign_in/daily/prize";
	public static final String url_sign_wechar ="https://h5.ele.me/restapi/member/v1/users/USERID/sign_in/wechat";
	public static final String url_sign_abort ="https://h5.ele.me/restapi/member/v1/users/USERID/sign_in/abort";
	
	//限量
	public static final String url_limit_check ="https://h5.ele.me/restapi/member/v1/sign_in/limit/hongbao/info?channel=app";
	public static final String url_limit_receive ="https://h5.ele.me/restapi/member/v1/users/USERID/sign_in/limit/hongbao";
	
	private static String url = null;

	@Autowired
	private CookieDao cookieDao;
	@Autowired
	private LogRedpacketDao logRedpacketDao;
	@Autowired
	private LogPromotionDao logPromotionDao;
	@Autowired
	private SignInfoDao signInfoDao;
	@Autowired
	private BwmService bwmService;
	@Autowired
	private MgyService mgyService;
	@Autowired
	private YmService ymService;
	@Autowired
	private LzService lzService;
	@Autowired
	private TokenDao tokenDao;
	
	private String lng="113.327099";//经度
	private String lat="23.102179";//纬度

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

		List<Cookie> cookies = cookieDao.getAllByDatalevelAndTypeAndCountLessThan(0,0,5);
		String openId = null;
		String sign = null;
		String trackId = null;
		String cookieStr = null;
		int openCount = 0;
		boolean success = false;
		for (Cookie cookie : cookies) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
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
					"https://fanyi.bdstatic.com/static/translation/img/header/logo_cbfea26.png");
			try {
				mapParam.put("weixin_username", URLEncoder.encode("百什么度", "utf-8"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			mapParam.put("method", "phone");
			mapParam.put("phone", cookie.getPhone());
			mapParam.put("group_sn", redpacketId);
			mapParam.put("sign", sign);
			mapParam.put("track_id", trackId);
			openId = getOpenId(cookieStr);
			url = url_open_luck_redpacket.replace("OPENID", openId);
			String body = HttpUtil2.postRequest(url, mapHeader, mapParam);
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
		String body = HttpUtil2.getRequest(url,"utf-8");
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
		List<Cookie> list = cookieDao.getByDatalevelAndUserId(0, userId);
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
		String resp = HttpUtil2.postRequest(url_check_cookie, head, param);
//		if (resp == null || getRedpackId(resp) == null) {
		if (resp == null || !resp.contains("\"code\":\"200\"")) {
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
	
	public Result addCookie(Map<String, String> cookies,String phone, HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		Cookie cookie=new Cookie();
		cookie.setUserId(cookies.get("USERID"));
		cookie.setDatalevel(0);
		cookie.setCreateDate(new Date());
		cookie.setCount(0);
		cookie.setCreatorId(sessionUser.getId());
		cookie.setType(1);
		cookie.setPhone(phone);
		cookie.setValue(cookies.get("cookie"));
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

	public PageResult detailListCookie(HttpServletRequest request,int type) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser == null) {
			return new PageResult(PageUtil.redirect_login2, "登录失效，请重新登录");
		}
		Long creatorId = sessionUser.getId();
		List<Cookie> cookies = cookieDao.getAllByCreatorIdAndType(creatorId, type);
		request.setAttribute("cookies", cookies);
		return new PageResult(PageUtil.eleme_detail, null);
	}
	
	public PageResult listCookie(HttpServletRequest request,int type) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser == null) {
			return new PageResult(PageUtil.redirect_login2, "登录失效，请重新登录");
		}
		Long creatorId = sessionUser.getId();
		List<Cookie> cookies = cookieDao.getAllByDatalevelAndCreatorIdAndType(0, creatorId,type);
		request.setAttribute("cookies", cookies);
		return new PageResult(PageUtil.eleme_cookie_list, null);
	}

	public PageResult listLuck(HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser == null) {
			return new PageResult(PageUtil.redirect_login2, "登录失效，请重新登录");
		}
		Long creatorId = sessionUser.getId();
		List<LogPromotion> list = logPromotionDao.getListByDatalevelAndCreatorIdOrderByIdDesc(0, creatorId);
		request.setAttribute("promotions", list);
		return new PageResult(PageUtil.eleme_luck, null);
	}

	public Result run3(HttpServletRequest request) {
		HttpSession session = request.getSession();
		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			return new Result(-1,"重新登录系统");
		}
		User ymUser = (User) session.getAttribute("ym");
		if (ymUser == null) {
			return new Result(-1,"未登录易码");
		}
		User lz = (User) session.getAttribute("lz");
		if (lz == null) {
			return new Result(-1,"未登录联众");
		}
		Token token = tokenDao.getLastToken(6,ymUser.getId());
		
		int count=0;
//		String packetId="15432039863422899";
		String packetId=null;
		Map<String, String> result=new HashMap<>();
		while (count<3) {
			String phone ="";
			boolean isNew=false;
			while (true) {
				try {
					Thread.sleep(5000);
					phone=ymService.getPhone(token.getToken());
					if (phone==null) {
						return new Result(-1,"获取号码失败");
					}
					List<Cookie> exist = cookieDao.getAllByDatalevelAndPhone(0, phone);
					//数据库已有，但未拉黑
					if (exist!=null&&exist.size()>0) {
						continue;
					}
					isNew=checkNew1119(phone);
				} catch (Exception e) {
					e.printStackTrace();
				}
//				if (isNew||packetId!=null) {
				if (!isNew) {
					break;
				}else {
					System.out.println(phone+" : 已注册，拉黑号码");
					ymService.blackPhone(token.getToken(),phone);
				}
			}
			String validate_token = sendCode(phone);
			if (validate_token==null) {
				System.err.println("登录风险，需要识别验证码");
				Map<String, String> captchas=null;
				try {
					captchas = getCaptchas(phone, session.getServletContext().getRealPath(""));
				} catch (ParseException | IOException e) {
					e.printStackTrace();
				}
				String captcha_value = lzService.upload(captchas.get("captcha_base64"), request);
				validate_token = sendCode(phone, captchas.get("captcha_hash"), captcha_value);
				int a=0;
				while (validate_token==null) {
					try {
						Thread.sleep(5000);
						captchas=getCaptchas(phone, session.getServletContext().getRealPath(""));
						captcha_value = lzService.upload(captchas.get("captcha_base64"), request);
						validate_token=sendCode(phone, captchas.get("captcha_hash"), captcha_value);
						if (validate_token!=null&&validate_token.contains("滑动")) {
							continue;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					a++;
					if (a>2) {
						break;
					}
				}
				if (a>2) {
					continue;
				}
			}
			if (validate_token.contains("滑动")) {
				continue;
			}
			String validate_code = null;
			
			int b=0;
			while (validate_code==null||!validate_code.contains("验证码")) {
				try {
					Thread.sleep(5000);
					validate_code=ymService.getMessage(token.getToken(),phone);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				b++;
				if (b>15) {
					break;
				}
			}
			if (b>15) {
				continue;
			}
			validate_code = getValidata(validate_code);
			Map<String, String> cookies = login(phone, validate_code, validate_token);
			addCookie(cookies,phone,request);

			if (packetId==null) {
				packetId = getRedpacket(cookies);
			}
			
			String msg="";
			boolean isOpen = openRedpacket(cookies, packetId);
			if (isOpen) {
				count++;
			}
			msg+="帮拆:"+isOpen+";";
			boolean isNewPlatform = getNewPlatform(cookies, phone);
			msg+="新客:"+isNewPlatform+";";
			
			getMission(cookies);
			getSudhana(cookies);
			
			boolean getVip = getVip(cookies);
			msg+="抽会员:"+getVip+";";
			boolean isVip = isVip(cookies);
			msg+="是否会员:"+isVip+";";
			boolean get29_13 = get29_13(cookies, "1");
			msg+="29-13:"+get29_13+";";
			msg+="大额红包:";
			List<String> shares = getShare(cookies);
			for (int i = 0; i < shares.size(); i++) {
				msg+=shares.get(i);
			}
			List<String> coupons = getCoupons(cookies);
			for (int i = 0; i < coupons.size(); i++) {
				msg+=coupons.get(i);
			}
			int get1111Au = get1111Au2(cookies);
			msg+=" 双十一金："+get1111Au;
			result.put("phone", phone);
			result.put("msg", msg);
			return new Result(result);
		}
		return new Result(result);
	}
	
	public Result run2(HttpServletRequest request) {
		HttpSession session = request.getSession();
		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			return new Result(-1,"重新登录系统");
		}
		User mgyUser = (User) session.getAttribute("mgy");
		if (mgyUser == null) {
			return new Result(-1,"未登录芒果云");
		}
		User lz = (User) session.getAttribute("lz");
		if (lz == null) {
			return new Result(-1,"未登录联众");
		}
		Token token = tokenDao.getLastToken(5,mgyUser.getId());
		
		int count=0;
//		String packetId="15432039863422899";
		String packetId=null;
		Map<String, String> result=new HashMap<>();
		while (count<3) {
			String phone ="";
			boolean isNew=false;
			while (true) {
				try {
					Thread.sleep(5000);
					phone=mgyService.getPhone(token.getToken());
					if (phone==null) {
						return new Result(-1,"获取号码失败");
					}
					List<Cookie> exist = cookieDao.getAllByDatalevelAndPhone(0, phone);
					//数据库已有，但未拉黑
					if (exist!=null&&exist.size()>0) {
						continue;
					}
					isNew=checkNew1119(phone);
				} catch (Exception e) {
					e.printStackTrace();
				}
//				if (isNew||packetId!=null) {
				if (isNew) {
					break;
				}else {
					System.out.println(phone+" : 已注册，拉黑号码");
					mgyService.blackPhone(token.getToken(),phone);
				}
			}
			String validate_token = sendCode(phone);
			if (validate_token==null) {
				System.err.println("登录风险，需要识别验证码");
				Map<String, String> captchas=null;
				try {
					captchas = getCaptchas(phone, session.getServletContext().getRealPath(""));
				} catch (ParseException | IOException e) {
					e.printStackTrace();
				}
				String captcha_value = lzService.upload(captchas.get("captcha_base64"), request);
				validate_token = sendCode(phone, captchas.get("captcha_hash"), captcha_value);
				int a=0;
				while (validate_token==null) {
					try {
						Thread.sleep(5000);
						captchas=getCaptchas(phone, session.getServletContext().getRealPath(""));
						captcha_value = lzService.upload(captchas.get("captcha_base64"), request);
						validate_token=sendCode(phone, captchas.get("captcha_hash"), captcha_value);
						if (validate_token!=null&&validate_token.contains("滑动")) {
							continue;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					a++;
					if (a>2) {
						break;
					}
				}
				if (a>2) {
					continue;
				}
			}
			if (validate_token.contains("滑动")) {
				continue;
			}
			String validate_code = null;
			
			int b=0;
			while (validate_code==null||!validate_code.contains("验证码")) {
				try {
					Thread.sleep(5000);
					validate_code=mgyService.getMessage(token.getToken(),phone);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				b++;
				if (b>15) {
					break;
				}
			}
			if (b>15) {
				continue;
			}
			validate_code = getValidata(validate_code);
			Map<String, String> cookies = login(phone, validate_code, validate_token);
			addCookie(cookies,phone,request);

			if (packetId==null) {
				packetId = getRedpacket(cookies);
			}
			
			String msg="";
			boolean isOpen = openRedpacket(cookies, packetId);
			if (isOpen) {
				count++;
			}
			msg+="帮拆:"+isOpen+";";
			boolean isNewPlatform = getNewPlatform(cookies, phone);
			msg+="新客:"+isNewPlatform+";";
			
			getMission(cookies);
			getSudhana(cookies);
			
			boolean getVip = getVip(cookies);
			msg+="抽会员:"+getVip+";";
			boolean isVip = isVip(cookies);
			msg+="是否会员:"+isVip+";";
			boolean get29_13 = get29_13(cookies, "1");
			msg+="29-13:"+get29_13+";";
			msg+="大额红包:";
			List<String> shares = getShare(cookies);
			for (int i = 0; i < shares.size(); i++) {
				msg+=shares.get(i);
			}
			List<String> coupons = getCoupons(cookies);
			for (int i = 0; i < coupons.size(); i++) {
				msg+=coupons.get(i);
			}
			int get1111Au = get1111Au2(cookies);
			msg+=" 双十一金："+get1111Au;
			result.put("phone", phone);
			result.put("msg", msg);
			return new Result(result);
		}
		return new Result(result);
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
		
		int count=0;
		String packetId="15432828642028866";
		Map<String, String> result=new HashMap<>();
		while (count<3) {
			String phone ="";
			boolean isNew=false;
			while (true) {
				try {
					Thread.sleep(5000);
					phone=bwmService.getPhone(token.getToken(), "56206");
					if (phone.contains("过期")) {
						bwmService.getNewToken(request);
						token = tokenDao.getLastToken(1,bwmUser.getId());
						phone = bwmService.getPhone(token.getToken(), "56206");
					}
					isNew=checkNew1119(phone);
					List<Cookie> exist = cookieDao.getAllByDatalevelAndPhone(0, phone);
					//数据库已有，但未拉黑
					if (exist!=null&&exist.size()>0) {
						continue;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
//				if (isNew||packetId==null) {
				if (isNew) {
					break;
				}else {
					System.out.println(phone+" : 已注册，拉黑、自动释放释放号码");
					bwmService.blackPhone(phone, "56206", token.getToken());
				}
			}
			String validate_token = sendCode(phone);
			if (validate_token==null) {
//				if (validate_token==null) {
//					System.err.println("登录风险，需要识别验证码，跳过");
//					continue;
//				}
				System.err.println("登录风险，需要识别验证码");
				Map<String, String> captchas=null;
				try {
					captchas = getCaptchas(phone, session.getServletContext().getRealPath(""));
				} catch (ParseException | IOException e) {
					e.printStackTrace();
				}
				String captcha_value = lzService.upload(captchas.get("captcha_base64"), request);
				validate_token = sendCode(phone, captchas.get("captcha_hash"), captcha_value);
				int a=0;
				while (validate_token==null) {
					try {
						Thread.sleep(5000);
						captchas=getCaptchas(phone, session.getServletContext().getRealPath(""));
						captcha_value = lzService.upload(captchas.get("captcha_base64"), request);
						validate_token=sendCode(phone, captchas.get("captcha_hash"), captcha_value);
					} catch (Exception e) {
						e.printStackTrace();
					}
					a++;
					if (a>2) {
						break;
					}
				}
				if (a>2) {
					continue;
				}
			}
			String validate_code = bwmService.getMessage(phone, token.getToken());
			
			int b=0;
			while (validate_code==null||!validate_code.contains("验证码")) {
				try {
					Thread.sleep(5000);
					validate_code=bwmService.getMessage(phone, token.getToken());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				b++;
				if (b>15) {
					break;
				}
			}
			if (b>15) {
				continue;
			}
			validate_code = getValidata(validate_code);
			Map<String, String> cookies = login(phone, validate_code, validate_token);
			addCookie(cookies,phone,request);

			if (packetId==null) {
				packetId = getRedpacket(cookies);
			}
			
			String msg="";
			boolean isOpen = openRedpacket(cookies, packetId);
			if (isOpen) {
				count++;
			}
			msg+="帮拆:"+isOpen+";";
			boolean isNewPlatform = getNewPlatform(cookies, phone);
			msg+="新客:"+isNewPlatform+";";
			
			getMission(cookies);
			getSudhana(cookies);
			
			boolean getVip = getVip(cookies);
			msg+="抽会员:"+getVip+";";
			boolean isVip = isVip(cookies);
			msg+="是否会员:"+isVip+";";
			boolean get29_13 = get29_13(cookies, "1");
			msg+="29-13:"+get29_13+";";
			msg+="大额红包:";
			List<String> shares = getShare(cookies);
			for (int i = 0; i < shares.size(); i++) {
				msg+=shares.get(i);
			}
			List<String> coupons = getCoupons(cookies);
			for (int i = 0; i < coupons.size(); i++) {
				msg+=coupons.get(i);
			}
			int get1111Au = get1111Au2(cookies);
			msg+=" 双十一金："+get1111Au;
			result.put("phone", phone);
			result.put("msg", msg);
			return new Result(result);
		}
		return new Result(result);
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
		String body = HttpUtil2.postRequest(url, null, null);
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
		String prizeCoide= HttpUtil2.getRequest(url_newuser_prizecode,"utf-8");
		Map<String, String> param = new HashMap<>();
		param.put("mobile", phone);
		param.put("r_url", "");// http://api.hongbao.show/webService/
		param.put("prizeCode", prizeCoide);
		Map<String, String> header = new HashMap<String, String>();
		header.put("Accept", "application/json");
		header.put("Content-Type", "application/x-www-form-urlencoded");
		header.put("Referer", "http://ele.hongbao.show/webService/shopGather/elm-new/index.html?channelId=32010380");
		String body = HttpUtil2.postRequest(url_newuser, header, param);
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(body);
			Object resultcode = jsonObject.get("resultcode");
			if (resultcode.toString().equals("200")) {
				JSONObject result = (JSONObject) parser.parse(jsonObject.get("result").toString());
				if (result.get("error") == null) {
					JSONObject data = (JSONObject) parser.parse(result.get("data").toString());
					String amount =  data.get("amount").toString();
					if (amount.equals("15")) {
						return true;
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	public boolean checkNew1104(String phone) {
		String body = HttpUtil2.getRequest(url_check_new_1104.replace("PHONE", phone),"utf-8");
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(body);
			Object isnew = jsonObject.get("new");
			if (isnew.equals("new")) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	public boolean checkNew1119(String phone) {
		Map<String, String> param = new HashMap<>();
		param.put("phone", phone);
		param.put("group_sn", "8bc2d4df4dd1642f84ce80ae371af033");// http://api.hongbao.show/webService/
		param.put("logical_geohash", "ws0e6s45wmtj");
		param.put("weixin_uid", "");
		Map<String, String> header = new HashMap<String, String>();
//		header.put("Accept", "application/json");
//		header.put("Content-Type", "application/x-www-form-urlencoded");
//		header.put("Referer", "https://h5.ele.me/baida/?group_sn=8bc2d4df4dd1642f84ce80ae371af033");
		String body = HttpUtil2.postRequest(url_check_new_1119, header, param);
		if (body!=null && (body.contains("注册")||body.contains("新用户"))) {
			return true;
		}
		return false;
	}

	public Map<String, String> getCaptchas(String phone,String path) throws ParseException, IOException {
		Map<String, String> result = new HashMap<>();
		Map<String, String> param = new HashMap<>();
		param.put("captcha_str", phone);
		String resp = HttpUtil2.postRequest(url_get_img_captchas, param);
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
		String file =new Date().getTime() + ".jpg";
		System.out.println(file);
		FileOutputStream fileOutputStream = new FileOutputStream( path + file);
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
		String resp = HttpUtil2.postRequest(url_send_code, param);
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
	
	public String sendCode(String phone) {
		Map<String, String> param = new HashMap<>();
		param.put("mobile", phone);
		param.put("captcha_hash", "");
		param.put("captcha_value", "");
		String resp = HttpUtil2.postRequest(url_send_code, param);
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = null;
		String validate_token=null;
		try {
			if (resp.contains("validate_token")) {
				jsonObj = (JSONObject) parser.parse(resp);
				validate_token = (String) jsonObj.get("validate_token");
			}
			if (resp.contains("滑动")) {
				validate_token = "滑动";
			}
		} catch (ParseException e) {
			System.err.println(jsonObj);
			e.printStackTrace();
		}
		return validate_token;
	}
	
	public Map<String, String> login(String phone,String validate_code,String validate_token) {
		Map<String, String> cookies = new HashMap<>();
//		cookies.put("perf_ssid", "ydy7x8ftlgz9le0fk6ifasbxaeyv7j69_2018-09-25");
//		cookies.put("ubt_ssid", "jg77sedwsj91c3lqbc0rwiorw5yrbyg0_2018-09-25");
//		cookies.put("_utrace", "2dc40edd80a3c0c0a9a845025f75edb8_2018-09-25");
//		cookies.put("track_id",
//				"1537864108|773ee26f253b7c15a2d556b4115254391d8d04e0b9bccc8913|6cad10e81bb1d56cf7237c82ef5aec47");
		Map<String, String> param = new HashMap<>();
		param.put("mobile", phone);
		param.put("validate_code", validate_code);
		param.put("validate_token", validate_token);
		Map<String, String> resp = HttpUtil2.getCookieByPostRequest(url_login,cookies, param);
		return resp;
	}
	
	public String getRedpacket(Map<String, String> cookies) {
		String packetId=null;
		Map<String, String> param = new HashMap<>();
		param.put("user_id", cookies.get("USERID"));
		param.put("lat", lat);
		param.put("lng", lng);
		param.put("packet_id", "0");
		cookies.put("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134");
		String resp = HttpUtil2.postRequest(url_get_redpacket, cookies, param);
		String body = resp;
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
		param.put("user_id",cookies.get("USERID"));
		param.put("lat", lat);
		param.put("lng", lng);
		param.put("packet_id", packetId);
		param.put("nickname", "百什么度");
		param.put("avatar", "https://fanyi.bdstatic.com/static/translation/img/header/logo_cbfea26.png");
		cookies.put("Referer", "https://h5.ele.me/grouping/activity/?id="+packetId);
		cookies.put("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134");
		String resp = HttpUtil2.postRequest(url_open_redpacket, cookies, param);
		String body = resp;
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
	public boolean getNewPlatform(Map<String, String> cookies,String phone) {
		String userId=cookies.get("USERID");
		String url=url_new_platform.replace("USERID", userId);
		Map<String, String> data = new HashMap<>();
		data.put("refer_code", "bbc9baf3f6a3bf8e8697d6fdf58bcb59");
		data.put("refer_user_id", "145998491");
		data.put("phone", phone);
		data.put("latitude", lat);
		data.put("longitude", lng);
		data.put("platform", "3");
		data.put("refer_channel_code", "1");
		data.put("refer_channel_type", "2");
		cookies.put("Referer","https://h5.ele.me/fire/water/");
		cookies.put("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134");
		String resp = HttpUtil2.postRequest(url, cookies, data);
		String body = resp;
		if (body.contains("promotion_items")) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) parser.parse(body);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			JSONArray promotion_items = (JSONArray) jsonObject.get("promotion_items");
			JSONObject item=null;
			for (int i = 0; i < promotion_items.size(); i++) {
				item=(JSONObject) promotion_items.get(i);
				System.out.println(item.get("name")+"领取成功："+item.get("sum_condition")+"----"+item.get("amount"));
				if (item.get("amount").equals("15")) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean getVip(Map<String, String> cookies) {
		String userId=cookies.get("USERID");
		String url=url_get_vip.replace("USERID", userId);
		Map<String, String> param = new HashMap<>();
		param.put("channel", "wingpay_banner_1");
		param.put("latitude", lat);
		param.put("longitude", lng);
		String resp = HttpUtil2.postRequest(url, cookies, param);
		String body = resp;
		if (body.contains("header_text")) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) parser.parse(body);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String header_text = (String) jsonObject.get("header_text");
			System.out.println("抽会员："+header_text);
			if (header_text.contains("一个月")) {
				return true;
			}
		}
		return false;
	}
	
	public List<String> getShare(Map<String, String> cookies) {
		List<String> list=new ArrayList<>();
		String userId=cookies.get("USERID");
		String url=url_get_share.replace("USERID", userId);
		String resp = HttpUtil2.getRequest(url, cookies);
		String body=resp;
		if (body.contains("amount")) {
			JSONParser parser = new JSONParser();
			JSONArray jsonArray=null;
			JSONObject jsonObject = null;
			String name=null;
			String sum_condition=null;
			String amount=null;
			String item="";
			try {
				jsonArray = (JSONArray) parser.parse(body);
				for (int i = 0; i < jsonArray.size(); i++) {
					jsonObject=(JSONObject) jsonArray.get(i);
					name=jsonObject.get("name").toString();
					sum_condition=jsonObject.get("sum_condition").toString();
					amount=jsonObject.get("amount").toString();
					item=name+":"+sum_condition+"-"+amount+";";
					System.out.println(item);
					if (Double.valueOf(amount)>=5) {
						list.add(item);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	public JSONArray getAllShare(Map<String, String> cookies) {
		JSONArray jsonArray=new JSONArray();
		String userId=cookies.get("USERID");
		String url=url_get_share.replace("USERID", userId);
		String resp = HttpUtil2.getRequest(url, cookies);
		String body=resp;
		if (body.contains("amount")) {
			JSONParser parser = new JSONParser();
			try {
				jsonArray = (JSONArray) parser.parse(body);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return jsonArray;
	}
	public JSONArray getAllCoupons(Map<String, String> cookies) {
		JSONArray jsonArray=new JSONArray();
		String userId=cookies.get("USERID");
		String url=url_get_coupons.replace("USERID", userId);
		String resp = HttpUtil2.getRequest(url, cookies);
		String body=resp;
		if (body.contains("reduce_amount")) {
			JSONParser parser = new JSONParser();
			try {
				jsonArray = (JSONArray) parser.parse(body);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return jsonArray;
	}
	
	public List<String> getCoupons(Map<String, String> cookies) {
		List<String> list=new ArrayList<>();
		String userId=cookies.get("USERID");
		String url=url_get_coupons.replace("USERID", userId);
		String resp = HttpUtil2.getRequest(url, cookies);
		String body=resp;
		if (body.contains("reduce_amount")) {
			JSONParser parser = new JSONParser();
			JSONArray jsonArray=null;
			JSONObject jsonObject = null;
			String name=null;
			String sum_condition=null;
			String amount=null;
			String item="";
			try {
				jsonArray = (JSONArray) parser.parse(body);
				for (int i = 0; i < jsonArray.size(); i++) {
					jsonObject=(JSONObject) jsonArray.get(i);
					name=jsonObject.get("name").toString();
					sum_condition=jsonObject.get("sum_condition").toString();
					amount=jsonObject.get("reduce_amount").toString();
					item=name+":"+sum_condition+"-"+amount+";";
					System.out.println(item);
					if (Double.valueOf(amount)>=5) {
						list.add(item);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public boolean isVip(Map<String, String> cookies) {
		String userId=cookies.get("USERID");
		String url=url_is_vip.replace("USERID", userId);
		String resp = HttpUtil2.getRequest(url, cookies);
		String body=resp;
		if (body.contains("status")) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) parser.parse(body);
				Integer status = Integer.valueOf(jsonObject.get("status").toString());
				if (status>2) {
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean check29_13(Map<String, String> cookies) {
		String userId=cookies.get("USERID");
		String url=url_check_29_13
				.replace("CITY", "4")
				.replace("LAT", lat)
				.replace("LNG", lng)
				.replace("USERID", userId);
		String resp = HttpUtil2.getRequest(url, cookies);
		String body=resp;
		if (body.contains("red_package_location")) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = null;
			JSONArray jsonArray=null;
			Integer status=0;
			try {
				jsonObject = (JSONObject) parser.parse(body);
				jsonObject=(JSONObject) jsonObject.get("result");
				jsonObject=(JSONObject) jsonObject.get("welfare_redpacket");
				jsonArray= (JSONArray) parser.parse(jsonObject.get("new_guest_redpacket").toString());
				jsonObject = (JSONObject) jsonArray.get(0);
				status = Integer.valueOf(jsonObject.get("status").toString());
				if (status==0) {
					return true;
				}
//				for (int i = 0; i < jsonArray.size(); i++) {
//					jsonObject = (JSONObject) jsonArray.get(i);
//					status = Integer.valueOf(jsonObject.get("status").toString());
//					if (status==0) {
//						return i+1;
//					}
//				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public boolean get29_13(Map<String, String> cookies,String index) {
		boolean check29_13 = check29_13(cookies);
		if (!check29_13) {
			return false;
		}
		String userId=cookies.get("USERID");
		String url=url_get_29_13
				.replace("CITY", "4")
				.replace("LAT", lat)
				.replace("LNG", lng)
				.replace("DEVICE", "")
				.replace("INDEX", index)
				.replace("USERID", userId);
		String resp = HttpUtil2.getRequest(url, cookies);
		String body=resp;
		if (body.contains("money")) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = null;
			JSONArray jsonArray=null;
			try {
				jsonObject = (JSONObject) parser.parse(body);
				jsonObject=(JSONObject) jsonObject.get("result");
				jsonArray= (JSONArray) parser.parse(jsonObject.get("red_packet").toString());
				for (int i = 0; i < jsonArray.size(); i++) {
					jsonObject = (JSONObject) jsonArray.get(i);
					System.out.println("果蔬商超："+jsonObject.get("threshold")+"-"+jsonObject.get("money"));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	public List<String> getShoplist(Map<String, String> cookies) {
		List<String> list=new ArrayList<>();
		String url=url_get_shoplist
				.replace("LAT",lat)
				.replace("LNG", lng)
				.replace("DEVICE", "");
		String resp = HttpUtil2.getRequest(url, cookies);
		String body=resp;
		if (body.contains("is_eleven")) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = null;
			JSONArray jsonArray=null;
			String wid=null;
			try {
				jsonObject = (JSONObject) parser.parse(body);
				jsonObject=(JSONObject) jsonObject.get("result");
				jsonObject=(JSONObject) jsonObject.get("venueentries");
				jsonObject=(JSONObject) jsonObject.get("content");
				jsonArray= (JSONArray) parser.parse(jsonObject.get("shop_list").toString());
				for (int i = 0; i < jsonArray.size(); i++) {
					jsonObject=(JSONObject) jsonArray.get(i);
					jsonObject=(JSONObject) jsonObject.get("shop_info");
					Object is_eleven = jsonObject.get("is_eleven");
					if (is_eleven!=null) {
						wid = (String) jsonObject.get("wid");
						list.add(wid);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public boolean get1111AuCheck(Map<String, String> cookies) {
		String url=url_get_1111Au_sum
				.replace("CITY", "4")
				.replace("LAT", lat)
				.replace("LNG", lng)
				.replace("SHOPID", "2233255575");
		String resp = HttpUtil2.getRequest(url, cookies);
		String body=resp;
		if (body.contains("signed_num")) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) parser.parse(body);
				jsonObject=(JSONObject) jsonObject.get("result");
				jsonObject=(JSONObject) jsonObject.get("shop");
				Integer num = Integer.valueOf(jsonObject.get("signed_num").toString());
				Integer total = Integer.valueOf(jsonObject.get("total_num").toString());
				if (num<total) {
					return true;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public int get1111AuSum(Map<String, String> cookies) {
		int sum=0;
		String url=url_get_1111Au_sum
				.replace("CITY", "4")
				.replace("LAT", lat)
				.replace("LNG", lng)
				.replace("SHOPID", "2233255575");
		String resp = HttpUtil2.getRequest(url, cookies);
		String body=resp;
		if (body.contains("giftcash")) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) parser.parse(body);
				jsonObject=(JSONObject) jsonObject.get("result");
				Integer giftcash = Integer.valueOf(jsonObject.get("giftcash").toString());
				sum=giftcash/100;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return sum;
	}
	
	public int get1111Au(Map<String, String> cookies,List<String> shops) {
		int count = 0;
		String url=url_get_1111Au
				.replace("CITY", "4")
				.replace("LAT", lat)
				.replace("LNG", lng);
		for (int i = 0; i < shops.size(); i++) {
			String url2=url.replace("SHOPID", shops.get(i));
			String resp = HttpUtil2.getRequest(url2, cookies);
			String body=resp;
			if (body.contains("signed_num")) {
				JSONParser parser = new JSONParser();
				JSONObject jsonObject = null;
				try {
					jsonObject = (JSONObject) parser.parse(body);
					jsonObject=(JSONObject) jsonObject.get("result");
					jsonObject=(JSONObject) jsonObject.get("shop");
					System.err.println(jsonObject.get("toast"));
					Integer num = Integer.valueOf(jsonObject.get("signed_num").toString());
					Integer total = Integer.valueOf(jsonObject.get("total_num").toString());
					count++;
					if (num>=total) {
						return count;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return count;
	}
	
	public Result get1111Au(Map<String, String> cookies) {
		boolean get1111AuCheck = get1111AuCheck(cookies);
		if (!get1111AuCheck) {
			return new Result(-1," 每日最多领取5次");
		}
		List<String> shoplist = getShoplist(cookies);
		if (shoplist==null||shoplist.size()==0) {
			return new Result(-1," 暂无活动商家");
		}
		int init = get1111AuSum(cookies);
		int count = get1111Au(cookies, shoplist)+init;
		int sum=0;
		if (count>0) {
			sum= get1111AuSum(cookies);
			return new Result(" 双十一金领取成功："+init +" >>> "+sum);
		}
		return new Result(" 双十一金："+init +" 领取次数: "+count);
	}
	
	public int get1111Au2(Map<String, String> cookies) {
		List<String> shoplist = getShoplist(cookies);
		get1111Au(cookies, shoplist);
		return get1111AuSum(cookies);
	}
	
	public Result get1111Au2ForDB(HttpServletRequest request) {
		HttpSession session = request.getSession();
		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			return new Result(-1,"重新登录系统");
		}
		List<Cookie> list = cookieDao.getAllByDatalevelAndCreatorIdAndType(0, sessionUser.getId(), 1);
		String result="";
		for (int i = 0; i < list.size(); i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Cookie cookie = list.get(i);
			Map<String, String> cookies=cookieStrToCookieMap(cookie);
			List<String> shoplist = getShoplist(cookies);
			get1111Au(cookies, shoplist);
			int sum = get1111AuSum(cookies);
			String phone = cookie.getPhone();
			result+=phone+"---"+sum+";";
		}
		return new Result(result);
	}
	
	private Map<String, String> cookieStrToCookieMap(Cookie cookie) {
		String str=cookie.getValue();
		String[] split = str.split(";");
		Map<String, String> cookies=new HashMap<>();
		for (int j = 0; j < split.length; j++) {
			String[] split2 = split[j].split("=");
			cookies.put(split2[0], split2[1]);
		}
		return cookies;
	}
	
	public Result getAddress(HttpServletRequest request,Long cookieId) {
		HttpSession session = request.getSession();
		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			return new Result(-1,"重新登录系统");
		}
		Cookie cookie= cookieDao.findOne(cookieId);
		if (cookie==null) {
			new Result(-1,"未找到对应的cookie");
		}
		String url=url_get_address.replace("USERID", cookie.getUserId());
		Map<String, String> headerMap=new HashMap<>();
		headerMap.put("cookie", cookie.getValue());
		String resp = HttpUtil2.getRequest(url, headerMap);
		if (resp!=null &&resp.contains("id")) {
			return new Result(resp);
		}
		return new Result(-1,resp);
	}
	public PageResult getAddressToPage(HttpServletRequest request,Long cookieId) {
		HttpSession session = request.getSession();
		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			request.setAttribute("error", "请重新登录系统");
			return new PageResult(PageUtil.redirect_login2,"请重新登录系统");
		}
		Cookie cookie= cookieDao.findOne(cookieId);
		if (cookie==null) {
			request.setAttribute("error", "未找到对应的Cookie");
			return new PageResult(PageUtil.eleme_address,"未找到对应的Cookie");
		}
		String url=url_get_address.replace("USERID", cookie.getUserId());
		Map<String, String> headerMap=new HashMap<>();
		headerMap.put("cookie", cookie.getValue());
		String resp = HttpUtil2.getRequest(url, headerMap);
		if (resp!=null &&resp.contains("id")) {
			JSONParser jsonParser=new JSONParser();
			JSONArray jsonArray=null;
			try {
				jsonArray = (JSONArray) jsonParser.parse(resp);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			request.setAttribute("addresses", jsonArray);
			return new PageResult(PageUtil.eleme_address,resp);
		}else if (resp!=null&&resp.contains("未登录")) {
			request.setAttribute("error", "Cookie已失效");
		}
		return new PageResult(PageUtil.eleme_address,null);
	}
	
	@SuppressWarnings("unchecked")
	public PageResult getOrderToPage(HttpServletRequest request,Long cookieId) {
		HttpSession session = request.getSession();
		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			request.setAttribute("error", "请重新登录系统");
			return new PageResult(PageUtil.redirect_login2,"请重新登录系统");
		}
		Cookie cookie= cookieDao.findOne(cookieId);
		if (cookie==null) {
			request.setAttribute("error", "未找到对应的Cookie");
			return new PageResult(PageUtil.eleme_order,"未找到对应的Cookie");
		}
		String url=url_get_order.replace("USERID", cookie.getUserId());
		Map<String, String> headerMap=new HashMap<>();
		headerMap.put("cookie", cookie.getValue());
		String resp = HttpUtil2.getRequest(url, headerMap);
		if (resp!=null &&resp.contains("id")) {
			JSONParser jsonParser=new JSONParser();
			JSONArray jsonArray=null;
			JSONObject jsonObject=null;
			String imgUrl =null;
			Object imgHash=null;
			try {
				jsonArray = (JSONArray) jsonParser.parse(resp);
				for (int i = 0; i < jsonArray.size(); i++) {
					jsonObject=(JSONObject) jsonArray.get(i);
					imgHash = jsonObject.get("restaurant_image_hash");
					if (imgHash !=null) {
						imgUrl = getImgUrl(imgHash.toString());
						jsonObject.put("imgUrl", imgUrl);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			request.setAttribute("orders", jsonArray);
			return new PageResult(PageUtil.eleme_order,resp);
		}else if (resp!=null&&resp.contains("未登录")) {
			request.setAttribute("error", "Cookie已失效");
		}
		return new PageResult(PageUtil.eleme_order,null);
	}
	
	public PageResult getRedpackToPage(HttpServletRequest request,Long cookieId) {
		HttpSession session = request.getSession();
		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser == null) {
			request.setAttribute("error", "请重新登录系统");
			return new PageResult(PageUtil.redirect_login2,"请重新登录系统");
		}
		Cookie cookie= cookieDao.findOne(cookieId);
		if (cookie==null) {
			request.setAttribute("error", "未找到对应的Cookie");
			return new PageResult(PageUtil.eleme_redpack,"未找到对应的Cookie");
		}
		Map<String, String> cookies=new HashMap<>();
		cookies.put("cookie", cookie.getValue());
		cookies.put("USERID", cookie.getUserId());
		try {
			JSONArray shares = getAllShare(cookies);
			JSONArray coupons = getAllCoupons(cookies);
			request.setAttribute("shares", shares);
			request.setAttribute("coupons", coupons);
		} catch (Exception e) {
			request.setAttribute("error", "Cookie已失效");
			e.printStackTrace();
		}
		return new PageResult(PageUtil.eleme_redpack,null);
	}
	
	private String getImgUrl(String imgHash) {
		String result="";
		String a=imgHash.substring(0, 1);
		String b=imgHash.substring(1, 3);
		String c=imgHash.substring(3, imgHash.length());
		String d=null;
		if (imgHash.contains("jpg")) {
			d="jpg";
		}else if (imgHash.contains("png")) {
			d="png";
		}else if (imgHash.contains("gif")) {
			d="gif";
		}else {
			d="jpeg";
		}
		result=url_img_host+a+"/"+b+"/"+c+"."+d;
		return result;
	}
	
	public List<String> getMission(Map<String, String> headerMap) {
		List<String> list=new ArrayList<>();
		String userId=headerMap.get("USERID");
		String url=url_get_mission
				.replace("USERID", userId)
				.replace("LNG", lng)
				.replace("LAT", lat);
		headerMap.put("Referer","https://h5.ele.me/newuser/newmission/");
		headerMap.put("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134");
		String resp = HttpUtil2.getRequest(url, headerMap);
		String body=resp;
		if (body.contains("amount")) {
			if (body.contains("amount")) {
				return null;
			}
			JSONParser parser = new JSONParser();
			JSONArray jsonArray=null;
			JSONObject jsonObject = null;
			String name=null;
			String sum_condition=null;
			String amount=null;
			String item="";
			try {
				jsonArray = (JSONArray) parser.parse(body);
				for (int i = 0; i < jsonArray.size(); i++) {
					jsonObject=(JSONObject) jsonArray.get(i);
					name=jsonObject.get("name").toString();
					sum_condition=jsonObject.get("sum_condition").toString();
					amount=jsonObject.get("amount").toString();
					item=name+":"+sum_condition+"-"+amount+";";
					System.out.println(item);
					if (Double.valueOf(amount)>=5) {
						list.add(item);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	public boolean getSudhana(Map<String, String> headerMap) {
		String userId=headerMap.get("USERID");
		Map<String, String> data = new HashMap<>();
		data.put("lng", lng);
		data.put("lat", lat);
		data.put("channel", "alipay_eleshh");
		data.put("userId", userId);
		data.put("refer_channel_type", "2");
		headerMap.put("Referer","https://h5.ele.me/utopia/?channel=alipay_eleshh");
		headerMap.put("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134");
		String resp = HttpUtil2.postRequest(url_get_sudhana, headerMap, data);
		String body = resp;
		if (body.contains("amount")) {
			if (body.contains("amount")) {
				return true;
			}
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) parser.parse(body);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			JSONArray promotion_items = (JSONArray) jsonObject.get("data");
			JSONObject item=null;
			for (int i = 0; i < promotion_items.size(); i++) {
				item=(JSONObject) promotion_items.get(i);
				System.out.println(item.get("title")+"领取成功："+item.get("threshold")+"----"+item.get("amount"));
				if (Double.valueOf(item.get("amount").toString())>=8.0) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int getSignInfo(Map<String, String> headerMap) {
		String userId=headerMap.get("USERID");
//		headerMap.put("Referer","https://h5.ele.me/utopia/?channel=alipay_eleshh");
//		headerMap.put("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134");
		String url=url_sign_info.replace("USERID", userId);
		String resp = HttpUtil2.getRequest(url, headerMap);
		String body = resp;
		if (body.contains("statuses")) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) parser.parse(body);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String statuses = jsonObject.get("statuses").toString();
			int count = 0;
		    Pattern p = Pattern.compile("1");
		    Matcher m = p.matcher(statuses);
		    while (m.find()) {
		        count++;
		    }
			return count;
		}
		return 0;
	}
	
	public PageResult toSignPage(HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser == null) {
			return new PageResult(PageUtil.redirect_login2, "登录失效，请重新登录");
		}
		getSignAll(request);
		return new PageResult(PageUtil.eleme_sign, null);
	}
	/*
		签到流程
		获取签到信息
		GET   /restapi/member/v1/users/3419912794/sign_in/info
		三选一页面
		GET /restapi/member/v1/users/3419912794/sign_in/risk/captcha
		签到
		POST /restapi/member/v1/users/3419912794/sign_in
			{"user_id":3419912794,"latitude":"0.0","longitude":"0.0"}
			{"user_id":1926141657,"latitude":"0.0","longitude":"0.0","source":"main"}
		获取签到信息
		GET   /restapi/member/v1/users/3419912794/sign_in/info
		翻牌 "type": 1 会员抵用 "type": 2 红包	"type": 5 品质 "type": 4 金币 "type": 3 加倍
		POST /restapi/member/v1/users/3419912794/sign_in/daily/prize
			{"user_id":3419912794,"latitude":"0.0","longitude":"0.0"}
			v2:{"user_id":3419912794,"change":"app","index":1}
			cookie:
			ubt_ssid=xg4ebfi69vhr2r8bsqckrtabsfpjtnqt_2018-02-18
			perf_ssid=5ttrycwuegxas84lizo9lc3s2buwxn79_2018-02-18
			_utrace=a61fab1cc835cd714a82054c55157c29_2018-01-10
			cna=kUlAFAwys10CAXWIKVBKZyj1
			isg=BJ2dupYWmsbLY36plIY4UDGop3uXutEMOJDLrV9i0fQjFr9IJwr93WjvRQwQ5-nE
			track_id=1538874282|d770116a9220cba2069439542f7ce585be3d8a441d079bb36b|ef407a06ebcc38981d2a47aedac39128
			SID=w4SoHh5KH4Ao8FlpWxpyOcoGJ54Fqip7lhcw
			USERID=3419912794;
		分享
		POST /restapi/member/v1/users/3419912794/sign_in/wechat
		{"user_id":3419912794,"latitude":"0.0","longitude":"0.0"}
		获取签到信息
		GET   /restapi/member/v1/users/3419912794/sign_in/info
		翻牌
		POST /restapi/member/v1/users/3419912794/sign_in/daily/prize
		获取签到信息
		GET   /restapi/member/v1/users/3419912794/sign_in/info
	 */
	/**
	 * 签到
	 * @param headerMap
	 * @return
	 */
	public int sign(Map<String, String> headerMap) {
		String userId=headerMap.get("USERID");
		Map<String, String> data = new HashMap<>();
		data.put("lng", lng);
		data.put("lat", lat);
		data.put("userId", userId);
		data.put("source", "main");
//		headerMap.put("Referer","https://h5.ele.me/utopia/?channel=alipay_eleshh");
//		headerMap.put("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134");
		String url=url_sign.replace("USERID", userId);
		String resp = HttpUtil2.postRequest(url, headerMap,data);
		String body = resp;
		if (body.contains("message")) {
			//包含但不限于重签、漏签
			if (body.contains("未登录")) {
				return -1;
			}else if (body.contains("重复签到")) {
				return -2;
			}else if (body.contains("没有签到")) {
				return -3;
			}
		}
		return 0;
	}
	public boolean signAbort(Map<String, String> headerMap) {
		String userId=headerMap.get("USERID");
		Map<String, String> data = new HashMap<>();
		data.put("lng", lng);
		data.put("lat", lat);
		data.put("userId", userId);
//		headerMap.put("Referer","https://h5.ele.me/utopia/?channel=alipay_eleshh");
//		headerMap.put("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134");
		String url=url_sign_abort.replace("USERID", userId);
		String resp = HttpUtil2.postRequest(url, headerMap,data);
		String body = resp;
		if (body.contains("message")) {
			return false;
		}
		return true;
	}
	
	public String getSignCaptcha(Map<String, String> headerMap) {
		String userId=headerMap.get("USERID");
		String url=url_sign_captcha.replace("USERID", userId);
		String resp = HttpUtil2.getRequest(url, headerMap);
		String body = resp;
		if (body.contains("statuses")) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = null;
			try {
				jsonObject = (JSONObject) parser.parse(body);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return jsonObject.get("statuses").toString();
		}
		return null;
	}
	
	public List<Prize> getSignPrize(Map<String, String> headerMap) {
		List<Prize> prizes=new ArrayList<>();
		String userId=headerMap.get("USERID");
		Map<String, String> data = new HashMap<>();
		data.put("channel", "app");
		data.put("index", "1");
//		data.put("userId", userId);
//		headerMap.put("Referer","https://h5.ele.me/utopia/?channel=alipay_eleshh");
//		headerMap.put("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134");
		String url=url_sign_prize_v2.replace("USERID", userId);
		String resp = HttpUtil2.postRequest(url, headerMap,data);
		String body = resp;
		if (body.contains("status")) {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = null;
			JSONArray jsonArray=null;
			try {
				jsonArray = (JSONArray) parser.parse(body);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < jsonArray.size(); i++) {
				jsonObject=(JSONObject) jsonArray.get(i);
				if (jsonObject.get("status").toString().equals("1")) {
					JSONArray jsonPrizes = (JSONArray) jsonObject.get("prizes");
					for (int j = 0; j < jsonPrizes.size(); j++) {
						JSONObject jsonPrize = (JSONObject) jsonPrizes.get(j);
						Prize prize = new Prize();
						Object sum_condition = jsonPrize.get("sum_condition");
						if (sum_condition==null) {
							sum_condition="0";
						}
						prize.setSum_condition(Integer.valueOf(sum_condition.toString()));
						prize.setAmount(Integer.valueOf(jsonPrize.get("amount").toString()));
						prize.setName(jsonPrize.get("name")==null?"金币":jsonPrize.get("name").toString());
						prize.setType(Integer.valueOf(jsonObject.get("type").toString()));
						prizes.add(prize);
					}
					break;
				}
			}
		}
		return prizes;
	}
	
	public boolean signShareWechat(Map<String, String> headerMap) {
		String userId=headerMap.get("USERID");
		Map<String, String> data = new HashMap<>();
		data.put("lng", lng);
		data.put("lat", lat);
		data.put("userId", userId);
//		headerMap.put("Referer","https://h5.ele.me/utopia/?channel=alipay_eleshh");
//		headerMap.put("User-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/17.17134");
		String url=url_sign_wechar.replace("USERID", userId);
		String resp = HttpUtil2.postRequest(url, headerMap,data);
		String body = resp;
		if (body.contains("message")) {
			return false;
		}
		return true;
	}
	
	public Result signAll(HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser == null) {
			return new Result(-1,"登录失效，请重新登录");
		}
		Long creatorId = sessionUser.getId();
		List<Cookie> cookies = cookieDao.getAllByCreatorIdAndType(creatorId, 1);
		Cookie cookie=null;
		Map<String, String> headerMap=new HashMap<>();
		for (int i = 0; i < cookies.size(); i++) {
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			cookie= cookies.get(i);
			headerMap.put("USERID", cookie.getUserId());
			headerMap.put("cookie", cookie.getValue());
		
			SignInfo signInfo =null;
			Sign sign =null;
			List<Sign> signs=new ArrayList<>();
			List<Prize> prizes=new ArrayList<>();
			
			getSignCaptcha(headerMap);
			int signStatus = sign(headerMap);
			if (signStatus==0) {
				//签到成功，天数+1；等于7天签到完成
				//查询是否有签到记录
				List<SignInfo> signInfoList = signInfoDao.getAllSigning(creatorId,cookie.getId());
				int size=signInfoList.size();
				if (signInfoList!=null && size>0) {
					boolean needNewSign=false;
					//处理超过7天签到同一轮次的
					for (int j = 0; j < signInfoList.size(); j++) {
						SignInfo abort = signInfoList.get(j);
						Calendar cal1 = Calendar.getInstance();
						cal1.setTime(signInfoList.get(j).getEndDate());
						Calendar cal2 = Calendar.getInstance();
						int day1= cal1.get(Calendar.DAY_OF_YEAR);
						int day2 = cal2.get(Calendar.DAY_OF_YEAR);
						if (day2>day1) {
							needNewSign=true;
							abort.setAbort(true);
							signInfoDao.save(abort);
						}
					}
					if (needNewSign) {
						//开始一轮新的签到
						Calendar calendar = Calendar.getInstance();
						calendar.add(Calendar.DAY_OF_YEAR, +6);
						signInfo=new SignInfo(cookie.getId(), creatorId, new Date(), calendar.getTime());
					}else {
						//已有的签到记录，更新count
						signInfo = signInfoList.get(size-1);
						int count = signInfo.getCount();
						signInfo.setCount(++count);
						if (count==7) {
							signInfo.setFinish(true);
						}
					}
				}else {
					//查询服务的已有的签到记录，更新count,补流水
					int signCount = getSignInfo(headerMap);
					Calendar calendar1 = Calendar.getInstance();
					calendar1.add(Calendar.DAY_OF_YEAR, -signCount);
					Calendar calendar2 = Calendar.getInstance();
					calendar2.add(Calendar.DAY_OF_YEAR, +6-signCount);
					signInfo=new SignInfo(cookie.getId(),creatorId,calendar1.getTime(),calendar2.getTime());
					if (signCount!=0) {
						//加1？是包括今天？
						signInfo.setCount(signCount+1);
						Calendar calendar = Calendar.getInstance();
						for (int j = 0; j < signCount; j++) {
							calendar.setTime(new Date());
							calendar.add(Calendar.DAY_OF_YEAR, j+1);
							SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
							String signDate = df.format(calendar.getTime());
							signs.add(new Sign(creatorId,signInfo,"已签到：不在本系统签到",signDate));
						}
					}
				}
				//签到流水
				sign=new Sign(creatorId, signInfo);
				//翻牌
				List<Prize> prize1 = getSignPrize(headerMap);
				prizes.addAll(prize1);
				//分享
				boolean isShare = signShareWechat(headerMap);
				if (isShare) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//再翻牌
					List<Prize> prize2 = getSignPrize(headerMap);
					prizes.addAll(prize2);
				}
			}else if (signStatus==-1) {
				//未登录 cookie 无效
				signInfo=new SignInfo(cookie.getId(),creatorId);
				signInfo.setAbort(true);
				signInfo.setCount(0);
				//设置签到流水
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String signDate = df.format(new Date());
				sign=new Sign(creatorId, signInfo,"签到失败，cookie无效：未登录",signDate);
			}else if (signStatus==-2) {
				//重复签到			
			}else if (signStatus==-3) {
				//没有连续签到
				//查询签到间隔一天以上，终止签到
				List<SignInfo> signList = signInfoDao.getAllSigning(creatorId, cookie.getId());
				if (signList.size()>0) {
					for (int j = 0; j < signList.size(); j++) {
						SignInfo abort= signList.get(j);
						Calendar cal1 = Calendar.getInstance();
						cal1.setTime(abort.getCreateDate());
						Calendar cal2 = Calendar.getInstance();
						int day1= cal1.get(Calendar.DAY_OF_YEAR);
						int day2 = cal2.get(Calendar.DAY_OF_YEAR);
						if (day2-day1>1) {
							signAbort(headerMap);		
							abort.setAbort(true);
							signInfoDao.save(abort);
						}
					}
				}else {
					signAbort(headerMap);
				}
				//重新签到
				sign(headerMap);
				
				//记录一轮新的签到
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_YEAR, +6);
				signInfo=new SignInfo(cookie.getId(), creatorId, new Date(), calendar.getTime());
				//签到流水
				sign=new Sign(creatorId, signInfo);
				//翻牌
				List<Prize> prize1 = getSignPrize(headerMap);
				prizes.addAll(prize1);
				//分享
				boolean isShare = signShareWechat(headerMap);
				if (isShare) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//再翻牌
					List<Prize> prize2 = getSignPrize(headerMap);
					prizes.addAll(prize2);
				}
			}
//			getSignInfo(headerMap);
			
			//保存数据
			if (signInfo!=null) {
				for (int j = 0; j < prizes.size(); j++) {
					prizes.get(j).setCreatorId(creatorId);
					prizes.get(j).setSign(sign);
				}
				sign.setPrizes(prizes);
				signs.add(sign);
				signInfo.setSigns(signs);
				signInfoDao.save(signInfo);
			}
			
			//测试一个，跳出循环
			//break;
			if (i>7) {
				break;	
			}
		}
		Result signs = getSignAll(request);
		return signs;
	}
	
	public Result getSignAll(HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser == null) {
			return new Result(-1,"登录失效，请重新登录");
		}
		Long creatorId = sessionUser.getId();
		List<SignInfo> signInfos = signInfoDao.getAllByCreatorIdOrderByCookieIdDesc(creatorId);
		request.setAttribute("signInfos", signInfos);
		return new Result(signInfos);
	}
	
	public Result getSignAdd(HttpServletRequest request) {
		SignInfo signInfo=new SignInfo();
		List<Sign> signs=new ArrayList<>();
		List<Prize> prizes1=new ArrayList<>();
		List<Prize> prizes2=new ArrayList<>();
		
		Sign sign1=new Sign();
		sign1.setSignInfo(signInfo);
		sign1.setText("p1");
		Sign sign2=new Sign();
		sign2.setSignInfo(signInfo);
		sign2.setText("p2");
		signs.add(sign1);
		signs.add(sign2);
		
		Prize prize1=new Prize();
		prize1.setName("pz1");
		prize1.setSign(sign1);
		Prize prize11=new Prize();
		prize11.setName("pz11");
		prize11.setSign(sign1);
		prizes1.add(prize1);
		prizes1.add(prize11);
		
		Prize prize2=new Prize();
		prize2.setName("pz2");
		prize2.setSign(sign2);
		Prize prize22=new Prize();
		prize22.setName("pz22");
		prize22.setSign(sign2);
		prizes2.add(prize2);
		prizes2.add(prize22);
		
		sign1.setPrizes(prizes1);
		sign2.setPrizes(prizes2);
		
		signInfo.setSigns(signs);
		SignInfo save = signInfoDao.save(signInfo);
		
		return new Result(save);
	}

	public Long checkLimitHongbao() {
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
	
	public Result getLimitHongbao(HttpServletRequest request,String ck) {
//		HttpSession session = request.getSession();
//		User sessionUser = (User) session.getAttribute("user");
//		if (sessionUser == null) {
//			return new Result(-1,"重新登录系统");
//		}
		Map<String, String> headerMap =new HashMap<>();
		headerMap.put("cookie", ck);
		Map<String, String> paramMap =new HashMap<>();
		paramMap.put("channel", "app");
		paramMap.put("userId", "145998491");
		String resp = HttpUtil2.postRequest(url_limit_receive.replace("USERID", "145998491"), headerMap, paramMap);
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
	/**
	 * 本地整点时间比对他服务器的时间，在10s内，返回true
	 * @param serverTime
	 * @return
	 */
	public boolean checkTime(Long serverTime) {
		int[] times= {10,14,17,20};
		Calendar cal=null;
		Long receiveTime=null;
		for (int i = 0; i < times.length; i++) {
			cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY,times[i]);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.MILLISECOND, 0);
			receiveTime=cal.getTimeInMillis()/1000;
			if (receiveTime-serverTime<=1 && receiveTime-serverTime>=0) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isCheck=true;
	public static boolean isBegin=false;
	public static int sleep=1000;
	public static void main(String[] args) throws InterruptedException {
		
		ElemeService elemeService = new ElemeService();
	    // 继承Thread类实现多线程
	    new Thread() {
	        public void run() {
	            while (true) {
	            	this.setName("111----");
	            	try {
						Thread.sleep(sleep);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	            	if (isCheck) {
	            		isBegin = elemeService.checkTime(System.currentTimeMillis()/1000);
		            	System.out.println(this.getName()+isBegin+":"+System.currentTimeMillis()/1000);
					}
	            	if (isBegin) {
	            		isCheck=false;
	            		sleep=300;
	            		Result limitHongbao = elemeService.getLimitHongbao(null, "ubt_ssid=pqb61wu4u50njfzpdv7fcxv1m6pdvsfl_2018-12-10; perf_ssid=m4ippwe9yrch9qsqiv4mqlw1vr2iecoi_2018-12-10; cna=L8SLFMkxiVcCAXeDdOFZnUUh; _utrace=269784fac6bc0dc37083fd372b5202de_2018-12-10; track_id=1544420161|d4f6e86787eef20bb869770c1341c0ba9bfaf39ca966c23232|b88f5be905463fb2da9a52dd255b4c62; USERID=145998491; SID=IaAZNx5Juy0bVFhvuW8XQPkaSrzBZ61l9zZg; isg=BPPzpAqrq5fPs2fDbynh77hcgvfdKIcA_D3Sm6WQFJJJpBJGLfkVOSs1WtRKBN_i");
						if (limitHongbao.getCode()!=-1) {
							this.interrupt();
							break;
						}
					}
	            }
	        }
	    }.start();
	}

}
