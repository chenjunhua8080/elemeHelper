package com.elemeHelper.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elemeHelper.dao.UserDao;
import com.elemeHelper.entity.User;
import com.elemeHelper.http.HttpUtil;
import com.elemeHelper.result.Result;

@Service
public class LzService {

	private static final String url_upload = "https://v2-api.jsdama.com/upload";
	private static final String url_report_error = "https://v2-api.jsdama.com/report-error";
	private static final String url_check_points = "https://v2-api.jsdama.com/check-points";
	private static final String softwareId = "11520";
	private static final String softwareSecret = "OQCFvZ7hI3IQQW0vL3upiZ1c1ia9sUGgzSVk51Ed";
	private static final String captchaType = "1001";

	@Autowired
	private UserDao userDao;

	public Result login(User user, HttpServletRequest request) {
		String name = user.getName();
		String pass = user.getPass();
		if (user == null || name.equals("") || pass.equals("")) {
			return new Result(-1, "请填写完整信息");
		}
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser == null) {
			return new Result(-1, "请重新登录系统");
		}
		Result checkPoints = checkPoints(user);
		if (checkPoints.getCode()==0) {
			Long userId = sessionUser.getId();
			User lzUser = userDao.getByNameAndPassAndTypeAndCreatorId(name, pass, 2, userId);
			if (lzUser == null) {
				user.setType(2);
				user.setCreatorId(userId);
				user.setCreateDate(new Date());
				user.setDatalevel(0);
				userDao.save(user);
			}
		}
		return checkPoints;
	}

	public Result upload(String base64, HttpServletRequest request) throws Exception {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser == null) {
			return new Result(-1, "请重新登录系统");
		}
		Long userId = sessionUser.getId();
		User lzUser = userDao.getByCreatorIdAndType(userId, 2);
		Map<String, String> param = new HashMap<>();
		param.put("softwareId", softwareId);
		param.put("softwareSecret", softwareSecret);
		param.put("username", lzUser.getName());
		param.put("password", lzUser.getPass());
		param.put("captchaData", base64);
		param.put("captchaType", captchaType);
		String resp = HttpUtil.postJsonRequest(url_upload, param);
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) parser.parse(resp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String msg = jsonObject.get("message").toString();
		String code = jsonObject.get("code").toString();
		if (!code.equals("0")) {
			new Result(-1, msg);
		}
		return new Result(msg);
	}

	public Result reportError(String captchaId, HttpServletRequest request) throws Exception {
		User sessionUser = (User) request.getSession().getAttribute("user");
		if (sessionUser == null) {
			return new Result(-1, "请重新登录系统");
		}
		Long userId = sessionUser.getId();
		User lzUser = userDao.getByCreatorIdAndType(userId, 2);
		Map<String, String> param = new HashMap<>();
		param.put("softwareId", softwareId);
		param.put("softwareSecret", softwareSecret);
		param.put("username", lzUser.getName());
		param.put("password", lzUser.getPass());
		param.put("captchaId", captchaId);
		param.put("captchaType", captchaType);
		String resp = HttpUtil.postJsonRequest(url_report_error, param);
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) parser.parse(resp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String msg = jsonObject.get("message").toString();
		String code = jsonObject.get("code").toString();
		if (!code.equals("0")) {
			new Result(-1, msg);
		}
		return new Result(msg);
	}

	public Result checkPoints(User user) {
		Map<String, String> map = new HashMap<>();
		map.put("softwareId", softwareId);
		map.put("softwareSecret", softwareSecret);
		map.put("username", user.getName());
		map.put("password", user.getPass());
		String resp = HttpUtil.postJsonRequest(url_check_points, map);
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		try {
			jsonObject = (JSONObject) parser.parse(resp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String msg = jsonObject.get("message").toString();
		String code = jsonObject.get("code").toString();
		Object data = jsonObject.get("data");
		if (!code.equals("0")&&data==null) {
			return new Result(-1, msg);
		}
		try {
			jsonObject = (JSONObject) parser.parse(data.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String availablePoints = jsonObject.get("availablePoints").toString();
		return new Result("登录成功，可用点数："+availablePoints);
	}

	public static String unicode2String(String unicode) {
		StringBuffer string = new StringBuffer();
		String[] hex = unicode.split("\\\\u");
		for (int i = 1; i < hex.length; i++) {
			int data = Integer.parseInt(hex[i], 16);
			string.append((char) data);
		}
		return string.toString();
	}

}
