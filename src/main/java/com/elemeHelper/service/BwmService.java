package com.elemeHelper.service;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.elemeHelper.dao.TokenDao;
import com.elemeHelper.dao.UserDao;
import com.elemeHelper.entity.Token;
import com.elemeHelper.entity.User;
import com.elemeHelper.http.HttpUtil;
import com.elemeHelper.result.Result;

@Service
public class BwmService {
	
	private static final String url_get_openid = "http://capi.yika66.com/Code.aspx?uName=USER";
	private static final String url_login = "http://kapi.yika66.com:20153/User/login?uName=USER&pWord=PASS&Developer=OPENID";
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
			String token = split[0];
			//....
			tokenDao.save(new Token(token,new Date(),isBind.getId(),0,1));
		}
		return new Result("登录成功，账户余额："+split[1]);
	}
	
	private String getOpenId(String userName) {
		url = url_get_openid.replace("USER", userName);
		String resp = HttpUtil.getRequest(url);
		if (resp==null) {
			System.err.println("bwm OpenId 获取失败");
		}
		return resp;
	}

	private String login(User user){
		String openId = getOpenId(user.getName());
		url = url_login.replace("USER", user.getName()).replace("PASS", user.getPass()).replace("OPENID", openId);
		String resp = HttpUtil.getRequest(url);
		if (resp==null) {
			System.err.println("bwm 登录失败");
		}
		return resp;
	}
	

}
