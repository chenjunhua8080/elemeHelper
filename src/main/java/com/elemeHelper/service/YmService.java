package com.elemeHelper.service;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.elemeHelper.dao.TokenDao;
import com.elemeHelper.dao.UserDao;
import com.elemeHelper.entity.Token;
import com.elemeHelper.entity.User;
import com.elemeHelper.http.HttpUtil2;
import com.elemeHelper.result.Result;

@Service
public class YmService {
	
//	private static final String url_ym_host = "http://www.mangopt.com:9000/doApi";
	private static final String url_login = "http://api.fxhyd.cn/UserInterface.aspx?action=login&username=NAME&password=PASS";
	private static final String url_get_info = "http://api.fxhyd.cn/UserInterface.aspx?action=getaccountinfo&token=TOKEN";
	private static final String url_get_list_phone = "http://api.fxhyd.cn/UserInterface.aspx?action=getmobile&token=TOKEN&itemid=ITEMID";//&excludeno=170.171
	private static final String url_get_that_phone = "http://api.fxhyd.cn/UserInterface.aspx?action=getmobile&token=TOKEN&itemid=ITEMID&mobile=PHONE";
	private static final String url_message = "http://api.fxhyd.cn/UserInterface.aspx?action=getsms&token=TOKEN&itemid=ITEMID&mobile=PHONE&release=1";
	private static final String url_release_phone = "http://api.fxhyd.cn/UserInterface.aspx?action=release&token=TOKEN&itemid=ITEMID&mobile=PHONE";
	private static final String url_black_phone ="http://api.fxhyd.cn/UserInterface.aspx?action=addignore&token=TOKEN&itemid=ITEMID&mobile=PHONE";
	
	private static final String itemId="352";
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private TokenDao tokenDao;
	
	
	public Result login(User user, HttpServletRequest request) {
		String name = user.getName();
		String pass = user.getPass();
		if (user==null||name.equals("")||pass.equals("")) {
			return new Result(-1, "请填写完整信息");
		}
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser==null) {
			return new Result(-1, "请重新登录系统");
		}
		String token = login(user);
		if (token==null) {
			return new Result(-1,"易码登录失败");
		}else {
			User isBind = userDao.getByNameAndPassAndTypeAndCreatorId(name, pass, 6, sessionUser.getId());
			if (isBind==null) {
				user.setType(6);
				user.setCreatorId(sessionUser.getId());
				user.setCreateDate(new Date());
				user.setDatalevel(0);
				isBind=userDao.save(user);
			}
			request.getSession().setAttribute("ym", isBind);
			tokenDao.save(new Token(token,new Date(),isBind.getId(),0,6));
			String info = getInfo(token);
			return new Result("登录成功，账户属性："+info);
		}
	}
	
	public void autoLogin(HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser==null) {
			return;
		}
		if (request.getSession().getAttribute("ym")==null) {
			List<User> users = userDao.getByCreatorIdAndTypeOrderByIdDesc(sessionUser.getId(), 6);
			if (users!=null&&users.size()>0) {
				User user = users.get(0);
				Result result = login(user, request);
				if (result.getCode()==0) {
					request.getSession().setAttribute("ymMsg", result.getData());
				}
			}
		}
	}
	
	/**
	 * getSummary
	 * 提交参数：token=登录时返回的令牌
	 * 调用实例：http://www.mangopt.com:9000/doApi/getSummary?token=登录时返回的令牌
	 * 返回值：1|等级|余额|批量取号数
	 * 备注：建议取用户信息的间隔至少在1分钟以上。
	 * @param user
	 * @return
	 */
	private String getInfo(String token){
		String url = url_get_info.replace("TOKEN", token);
		String resp = HttpUtil2.getRequest(url,"utf-8");
		if (resp!=null && resp.contains("success|")) {
			return resp.substring(8, resp.length());
		}
		System.err.println("ym 登录失败:"+resp);
		return null;
	}
	
	/**
	 * loginIn
	 * 提交参数：name=API账号&password=密码
	 * 调用实例：http://www.mangopt.com:9000/doApi/loginIn?name=API账号&password=密码
	 * 返回值：1|token(token是重要的返回参数，后面所有的请求都要传这个参数值)
	 * @param user
	 * @return
	 */
	private String login(User user){
		String url = url_login.replace("NAME", user.getName()).replace("PASS", user.getPass());
		String resp = HttpUtil2.getRequest(url,"utf-8");
		if (resp!=null && resp.contains("success|")) {
			return resp.substring(8, resp.length());
		}
		System.err.println("ym 登录失败:"+resp);
		return null;
	}
	
	/**
	 * getPhone
	 * 提交参数：
	 * 一般调用参数：sid=项目id&token=登录时返回的令牌
	 * 同时取两个以上的码，项目id之间用逗号(,)隔开，如sid=1000,1001。如果要获取指定号码，再在后面加一个phone=要指定获取的号码 
	 * 返回值：1|手机号     
	 * 当返回0|系统暂时没有可用号码，请过3秒再重新取号。 
	 * 当返回0|余额不足，当前余额为0.00元。 存在余额不足的字眼，请停止软件运行。 
	 * 当返回0|超出频率，请延时3秒再请求。 
	 * 当返回0|请软件主动延时3秒再请求，对于没加任何延时的，平台监控到并发高的会封号处理。
	 * @param token
	 * @return
	 */
	public String getPhone(String token){
		String url = url_get_list_phone.replace("ITEMID", itemId).replace("TOKEN", token);
		String resp = HttpUtil2.getRequest(url,"utf-8");
		if (resp!=null && resp.contains("success|")) {
			return resp.substring(8, resp.length());
		}
		System.err.println("ym 获取号码失败:"+resp);
		return null;
	}
	/**
	 * getPhone
	 * 提交参数：
	 * 一般调用参数：sid=项目id&token=登录时返回的令牌
	 * 同时取两个以上的码，项目id之间用逗号(,)隔开，如sid=1000,1001。如果要获取指定号码，再在后面加一个phone=要指定获取的号码 
	 * 返回值：1|手机号     
	 * 当返回0|系统暂时没有可用号码，请过3秒再重新取号。 
	 * 当返回0|余额不足，当前余额为0.00元。 存在余额不足的字眼，请停止软件运行。 
	 * 当返回0|超出频率，请延时3秒再请求。 
	 * 当返回0|请软件主动延时3秒再请求，对于没加任何延时的，平台监控到并发高的会封号处理。
	 * @param token
	 * @return
	 */
	public String getPhone(String token,String phone){
		String url = url_get_that_phone.replace("ITEMID", itemId).replace("TOKEN", token).replace("PHONE", phone);
		String resp = HttpUtil2.getRequest(url,"utf-8");
		if (resp!=null && resp.contains("success|")) {
			return resp.substring(8, resp.length());
		}
		System.err.println("ym 获取号码失败:"+resp);
		return null;
	}
	/**
	 * cancelRecv
	 * 提交参数：sid=项目id&phone=要释放的手机号&token=登录时返回的令牌
	 * 调用实例：http://www.mangopt.com:9000/doApi/cancelRecv?sid=项目id&phone=要释放的手机号&token=登录时返回的令牌
	 * 返回值：1|操作成功
	 * 备注：如果是正常取到了短信，是不用操作加入黑名单和释放手机号的
	 * @param token
	 * @param phone
	 * @return
	 */
	public String releasePhone(String token,String phone){
		String url = url_release_phone.replace("ITEMID", itemId).replace("TOKEN", token).replace("PHONE", phone);
		String resp = HttpUtil2.getRequest(url,"utf-8");
		if (resp!=null && resp.contains("success")) {
			return resp;
		}
		System.err.println("ym 释放号码失败:"+resp);
		return null;
	}
	/**
	 * addBlacklist
	 * 提交参数：sid=项目id&phone=要加入黑名单的手机号&token=登录时返回的令牌
	 * 调用实例：http://www.mangopt.com:9000/doApi/addBlacklist?sid=项目id&phone=要加入黑名单的手机号&token=登录时返回的令牌
	 * 返回值：1|操作成功
	 * 备注：如果是正常取到了短信，是不用操作加入黑名单和释放手机号的
	 * @param token
	 * @param phone
	 * @return
	 */
	public String blackPhone(String token,String phone){
		String url = url_black_phone.replace("ITEMID", itemId).replace("TOKEN", token).replace("PHONE", phone);
		String resp = HttpUtil2.getRequest(url,"utf-8");
		if (resp!=null && resp.contains("success")) {
			return resp;
		}
		System.err.println("ym 拉黑号码失败:"+resp);
		return null;
	}
	/**
	 * getMessage
	 * 提交参数：sid=项目id&phone=取出来的手机号&token=登录时返回的令牌&author=软件作者 用户名(这里是传作者注册时的用户名)。
	 * 调用实例：http://www.mangopt.com:9000/doApi/getMessage?sid=项目id&phone=取出来的手机号&token=登录时返回的令牌
	 * 返回值：1|短信内容
	 * 备注：当返回0|还没有接收到短信，请过3秒再试，请软件主动3秒再重新取短信内容。一般项目的短信在1分钟左右能取到，个别比较慢的也应该在3分钟左右能取到。所以重试间隔3秒的情况下一般循环获取20~60次之间即可。如果一超过60次取不到短信，可以加黑该手机号。
	 * 返回 0| 请软件主动延时3秒再请求，对于没加任何延时的，平台监控到并发高的会封号处理。
	 * @param token
	 * @param phone
	 * @return
	 */
	public String getMessage(String token,String phone){
		String url = url_message.replace("ITEMID", itemId).replace("TOKEN", token).replace("PHONE", phone);
		String resp = HttpUtil2.getRequest(url,"utf-8");
		if (resp!=null && resp.contains("success|")) {
			return resp.substring(8, resp.length());
		}
		System.err.println("ym 获取短信失败:"+resp);
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



}
