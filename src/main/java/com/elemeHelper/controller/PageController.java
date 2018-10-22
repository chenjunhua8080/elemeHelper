package com.elemeHelper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

	@GetMapping("/page/index")
	public String index() {
		return "index";
	}
	
	@GetMapping("/page/register")
	public String register() {
		return "register";
	}
	
	@GetMapping("/page/login")
	public String login() {
		return "login";
	}
	
}
