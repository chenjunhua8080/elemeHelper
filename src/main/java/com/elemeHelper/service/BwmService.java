package com.elemeHelper.service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.elemeHelper.dao.TokenDao;
import com.elemeHelper.dao.UserDao;
import com.elemeHelper.entity.Token;
import com.elemeHelper.entity.User;
import com.elemeHelper.http.HttpUtil2;
import com.elemeHelper.result.PageResult;
import com.elemeHelper.result.Result;
import com.elemeHelper.util.PageUtil;

@Service
public class BwmService {
	
	private static final String url_get_openid = "http://capi.yika66.com/Code.aspx?uName=USER";
	private static final String url_login = "http://kapi.yika66.com:20153/User/login?uName=USER&pWord=PASS&Developer=OPENID";
	private static final String url_get_items = "http://kapi.yika66.com:20153/User/getItems?token=TOKEN&tp=ut";
	private static final String url_get_list_phone = "http://kapi.yika66.com:20153/User/getPhone?PhoneType=157&Count=COUNT&ItemId=ITEMID&token=TOKEN";
	private static final String url_get_that_phone = "http://kapi.yika66.com:20153/User/getPhone?&Phone=PHONE";
	private static final String url_release_phone = "http://kapi.yika66.com:20153/User/releasePhone?phoneList=LIST&token=TOKEN";
	private static final String url_message = "http://kapi.yika66.com:20153/User/getMessage?code=CODE&token=TOKEN";	
	private static final String url_logout = "http://kapi.yika66.com:20153/User/exit?uName=USER&token=TOKEN";
	private static final String url_black_phone ="http://kapi.yika66.com:20153/User/addBlack?token=TOKEN&phoneList=LIST";
	
	private String url=null;
	
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
		String info = login(user);
		String[] split = info.split("&");
		if (split.length==1) {
			return new Result(-1,"登录失败："+info);
		}else {
			User isBind = userDao.getByNameAndPassAndTypeAndCreatorId(name, pass, 1, sessionUser.getId());
			if (isBind==null) {
				user.setType(1);
				user.setCreatorId(sessionUser.getId());
				user.setCreateDate(new Date());
				user.setDatalevel(0);
				isBind=userDao.save(user);
			}
			request.getSession().setAttribute("bwm", isBind);
			String token = split[0];
			//....
			tokenDao.save(new Token(token,new Date(),isBind.getId(),0,1));
		}
		return new Result("登录成功，账户余额："+split[1]);
	}
	
	public void autoLogin(HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser==null) {
			return;
		}
		if (request.getSession().getAttribute("bwm")==null) {
			List<User> users = userDao.getByCreatorIdAndTypeOrderByIdDesc(sessionUser.getId(), 1);
			if (users!=null&&users.size()>0) {
				User user = users.get(0);
				Result result = login(user, request);
				if (result.getCode()==0) {
					request.getSession().setAttribute("bwmMsg", result.getData());
				}
			}
		}
	}
	
	public boolean getNewToken(HttpServletRequest request) {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser==null) {
			return false;
		}
		List<User> users = userDao.getByCreatorIdAndTypeOrderByIdDesc(sessionUser.getId(), 1);
		if (users!=null&&users.size()>0) {
			User user = users.get(0);
			Result result = login(user, request);
			if (result.getCode()==0) {
				request.getSession().setAttribute("bwmMsg", result.getData());
			}
		}
		return false;
	}
	
	private String getOpenId(String userName) {
		url = url_get_openid.replace("USER", userName);
		String resp = HttpUtil2.getRequest(url,"gbk");
		if (resp==null||resp.length()>200) {
			System.err.println("bwm OpenId 获取失败");
			return "null";
		}
		return resp;
	}

	private String login(User user){
		String openId = getOpenId(user.getName());
		url = url_login.replace("USER", user.getName()).replace("PASS", user.getPass()).replace("OPENID", "");
		String resp = HttpUtil2.getRequest(url,"gbk");
		if (resp==null) {
			System.err.println("bwm 登录失败");
		}
		return resp;
	}
	
	public Result logout(HttpSession session){
		User bwmUser = (User)session.getAttribute("bwm");
		String user="";
		if (bwmUser!=null) {
			user=bwmUser.getName();
		}
		User sessionUser = (User) session.getAttribute("user");
		if (sessionUser!=null) {
			Token token = tokenDao.getLastToken(1, bwmUser.getId());
			url = url_logout.replace("USER", user).replace("TOKEN", token.getToken());
			String resp = HttpUtil2.getRequest(url,"gbk");
			session.removeAttribute("bwm");
			return new Result(resp);
		}
		return null;
	}
	
	public PageResult getItems(HttpServletRequest request) {
		PageResult pageResult=null;
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser==null) {
			pageResult = new PageResult(PageUtil.redirect_login2,"请重新登录系统");
			request.setAttribute("error", pageResult);
			return pageResult;
		}
		User bwmUser = (User) request.getSession().getAttribute("bwm");
		if (bwmUser==null) {
			pageResult = new PageResult(PageUtil.eleme_activity,"请先登录百万码");
			request.setAttribute("error", pageResult);
			return pageResult;
		}
		Token token = tokenDao.getLastToken(1, bwmUser.getId());
		if (token==null) {
			pageResult = new PageResult(PageUtil.eleme_activity,"登录超时,请重新登录百万码");
			request.setAttribute("error", pageResult);
			return pageResult;
		}
		Map<String, String> items=null;
		try {
			items = getItems(token.getToken());
			//logout(request, token.getToken());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (items==null) {
			pageResult=new PageResult(PageUtil.eleme_bwmitems,"项目获取失败");
			request.setAttribute("error", pageResult);
			return pageResult;
		}
		pageResult=new PageResult(PageUtil.eleme_bwmitems,items);
		request.setAttribute("items", pageResult);
		return pageResult;
	}
	
	private Map<String, String> getItems(String token) throws Exception {
		Map<String, String> map = new HashMap<>();
		url = url_get_items.replace("TOKEN", token);
		String respBody = HttpUtil2.getRequest(url,"gbk");
		String[] items = respBody.split("&");
		for (int i = 0; i < items.length-1; i++) {
			if (i==0 || i%3==0) {
				map.put(items[i], "项目ID："+items[i] +"----项目描述："+items[i+1] +"----单价："+items[i+2]);
			}
		}
		return map;
	}
	
	public String getPhone(String token,String itemId){
		url = url_get_list_phone.replace("COUNT", "1").replace("ITEMID", itemId).replace("TOKEN", token);
		String respBody = HttpUtil2.getRequest(url,"gbk");
		respBody=respBody.replace(";", "");
		return respBody;
	}
	
	public List<String> getPhone(String token, String itemId,String count){
		url = url_get_list_phone.replace("COUNT", count).replace("ITEMID", itemId).replace("TOKEN", token);
		String respBody = HttpUtil2.getRequest(url,"gbk");
		String[] phoneList = respBody.split(";");
		List<String> list = Arrays.asList(phoneList);
		return list;
	}

	public String getPhone(String phone){
		url = url_get_that_phone.replace("PHONE", phone);
		String respBody = HttpUtil2.getRequest(url,"gbk");
		return respBody;
	}
	
	public String releasePhone(List<String> list,String itemId,String token){
		String phoneStr="";
		for (String item : list) {
			phoneStr+=item+"-"+itemId+";";
		}
		url = url_release_phone.replace("LIST", phoneStr).replace("TOKEN", token);
		String respBody = HttpUtil2.getRequest(url,"gbk");
		return respBody;
	}
	
	public String releasePhone(String phone,String itemId,String token){
		String phoneStr="";
		phoneStr+=phone+"-"+56206+";";
		url = url_release_phone.replace("LIST", phoneStr).replace("TOKEN", token);
		String respBody = HttpUtil2.getRequest(url,"gbk");
		return respBody;
	}

	public String blackPhone(String phone,String itemId,String token){
		String phoneStr="";
		phoneStr+=56206+"-"+phone+";";
		url = url_black_phone.replace("LIST", phoneStr).replace("TOKEN", token);
		String respBody = HttpUtil2.getRequest(url,"gbk");
		return respBody;
	}
	
	public String getMessage(String phone,String token){
		url = url_message.replace("CODE", "utf8").replace("TOKEN", token);
		if (phone!=null) {
			url = url+"&Phone="+phone;
		}
		String respBody = null;
		try {
			respBody = HttpUtil2.getRequest(url,"gbk");
			if (respBody.contains("验证码")) {
				releasePhone(phone, "56206", token);
//				releasePhone(phone, "56206", token);
			}
		} catch (Exception e) {
			System.err.println("获取短信失败："+e.getMessage());
		}
		return respBody;
	}


}
