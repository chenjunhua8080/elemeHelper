package com.elemeHelper.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.elemeHelper.result.PageResult;
import com.elemeHelper.service.BwmService;
import com.elemeHelper.service.ElemeService;
import com.elemeHelper.service.LzService;
import com.elemeHelper.service.MgyService;
import com.elemeHelper.util.PageUtil;

@Controller
public class PageController {
	
	@Autowired
	private ElemeService elemeService;
	@Autowired
	private BwmService bwmService;
	@Autowired
	private MgyService mgyService;
	@Autowired
	private LzService lzService;

	@GetMapping("/index")
	public String index2() {
		return PageUtil.index;
	}
	
	@GetMapping("/page/index")
	public String index() {
		return PageUtil.index;
	}
	
	@GetMapping("/page/register")
	public String register() {
		return PageUtil.user_register;
	}
	
	@GetMapping("/page/login")
	public String login() {
		return PageUtil.user_login;
	}
	
	@GetMapping("/page/activity")
	public String activity(HttpServletRequest request) {
		mgyService.autoLogin(request);
		lzService.autoLogin(request);
		return PageUtil.eleme_activity;
	}
	
	@GetMapping("/page/items")
	public String getItems(HttpServletRequest request) {
		PageResult pageResult = bwmService.getItems(request);
		return pageResult.getPage();
	}
	
	@GetMapping("/page/luck")
	public String luck(HttpServletRequest request) {
		PageResult list = elemeService.listLuck(request);
		return list.getPage();
	}
	
	@GetMapping("/page/cookie")
	public String cookie(HttpServletRequest request) {
		PageResult list = elemeService.listCookie(request,0);
		return list.getPage();
	}
	
	@GetMapping("/page/cookie/add")
	public String addCookie(HttpServletRequest request) {
		return PageUtil.eleme_cookie_add;
	}
	
	@GetMapping("/page/1111")
	public String page1111(HttpServletRequest request) {
		return PageUtil.eleme_1111;
	}
	
	@GetMapping("/page/detail")
	public String detail(HttpServletRequest request) {
		PageResult list=elemeService.detailListCookie(request, 1);
		return list.getPage();
	}
	
	@GetMapping("/page/address")
	public String getAddress(HttpServletRequest request,Long cookieId) {
		PageResult result = elemeService.getAddressToPage(request,cookieId);
		return result.getPage();
	}
	
	@GetMapping("/page/order")
	public String getOrder(HttpServletRequest request,Long cookieId) {
		PageResult result = elemeService.getOrderToPage(request,cookieId);
		return result.getPage();
	}
	
	@GetMapping("/page/redpack")
	public String getRedpack(HttpServletRequest request,Long cookieId) {
		PageResult result = elemeService.getRedpackToPage(request,cookieId);
		return result.getPage();
	}
}
