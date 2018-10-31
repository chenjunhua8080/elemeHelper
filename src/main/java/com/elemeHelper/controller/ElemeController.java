package com.elemeHelper.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.elemeHelper.entity.Cookie;
import com.elemeHelper.result.Result;
import com.elemeHelper.service.ElemeService;

@RestController
public class ElemeController {

	@Autowired
	private ElemeService elemeService;

	@PostMapping("/openRedpacket")
	public Result login(String link, HttpServletRequest request) {
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
	
	@ResponseBody
	@GetMapping("/run")
	public Result run(HttpServletRequest request) {
		Result result = elemeService.run(request);
		return result;
	}
	

}
