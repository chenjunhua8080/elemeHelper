package com.elemeHelper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.elemeHelper.entity.User;
import com.elemeHelper.result.PageResult;
import com.elemeHelper.service.UserService;

@Controller
public class UserController {

	@Autowired
	private UserService userService;
	
	@PostMapping("/register")
	public String register(User user,String email,PageResult result) {
		result = userService.register(user, email);
		return result.getPage();
	}
	
	@PostMapping("/login")
	public String login(User user,PageResult result) {
		result=userService.login(user);
		System.out.println(result.getData());
		return result.getPage();
	}
	
}
