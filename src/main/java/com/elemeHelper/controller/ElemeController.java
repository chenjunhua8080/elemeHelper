package com.elemeHelper.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elemeHelper.entity.Cookie;
import com.elemeHelper.result.Result;
import com.elemeHelper.service.ElemeService;

@RestController
public class ElemeController {

	@Autowired
	private ElemeService elemeService;

	@PostMapping("/openRedpacket")
	public Result openRedpacket(String link, HttpServletRequest request) {
		Result result = elemeService.openRedpacket(link,request);
		return result;
	}
	@PostMapping("/cookie/add")
	public Result addCookie(Cookie cookie, HttpServletRequest request) {
		Result result = elemeService.addCookie(cookie,request);
		return result;
	}
	@PostMapping("/cookie/del")
	public Result delCookie(Long cookieId, HttpServletRequest request) {
		Result result = elemeService.delCookie(cookieId,request);
		return result;
	}
	
	@GetMapping("/run")
	public Result run(HttpServletRequest request) {
		Result result = elemeService.run3(request);
		return result;
	}
	
	@PostMapping("/e/mobile_send_code")
	public Result mobile_send_code(String phone,String captcha_hash, String captcha_value,HttpServletRequest request) throws ParseException, IOException {
		Result result=null;
		if (captcha_hash==null||captcha_value==null||captcha_hash.equals("")||captcha_value.equals("")) {
			String validate_token = elemeService.sendCode(phone);
			if (validate_token==null) {
				String realPath = request.getSession().getServletContext().getRealPath("");
				Map<String, String> captcha = elemeService.getCaptchas(phone, realPath);
				result=new Result(-1,"请输入验证码");
				result.setData(captcha);
			}else {
				result=new Result(validate_token);
			}
		}else {
			String validate_token = elemeService.sendCode(phone, captcha_hash, captcha_value);
			if (validate_token==null) {
				result=new Result(-1,"验证码不正确");
			}else {
				result = new Result(validate_token);
			}
		}
		return result;
	}
	@PostMapping("/e/login")
	public Result login(String phone,String validate_code,String validate_token, HttpServletRequest request) {
		Result result =null;
		Map<String, String> map = elemeService.login(phone, validate_code, validate_token);
		request.getSession().setAttribute("loginInfo", map);
		String userid = map.get("USERID");
		if (userid!=null) {
			result=new Result("登录成功");
		}else {
			result=new Result(-1,"登录失败");
		}
		return result;
	}
	@GetMapping("/e/1111Au/cookie")
	public Result get1111AuByCookie(HttpServletRequest request) {
		Result result = elemeService.get1111Au2ForDB(request);
		return result;
	}
	
	@PostMapping("/e/1111Au")
	public Result get1111Au(HttpServletRequest request) {
		Object loginInfo = request.getSession().getAttribute("loginInfo");
		if (loginInfo==null) {
			return new Result(-1,"未登录要领取双十一金的饿了么账号");
		}
		@SuppressWarnings("unchecked")
		Map<String, String> cookies=(Map<String, String>) loginInfo;
		boolean get1111AuCheck = elemeService.get1111AuCheck(cookies);
		if (!get1111AuCheck) {
			return new Result(-1,"每日最多领取5次");
		}
		List<String> shoplist = elemeService.getShoplist(cookies);
		if (shoplist==null||shoplist.size()==0) {
			return new Result(-1,"暂无活动商家");
		}
		int init = elemeService.get1111AuSum(cookies);
		int count = elemeService.get1111Au(cookies, shoplist)+init;
		int sum=0;
		if (count>0) {
			sum= elemeService.get1111AuSum(cookies);
			return new Result("领取成功："+init +" >>> "+sum);
		}
		return new Result("领取成功："+init +" 领取次数: "+count);
	}

}
