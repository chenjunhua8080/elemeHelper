package com.elemeHelper.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.elemeHelper.entity.User;
import com.elemeHelper.result.PageResult;
import com.elemeHelper.service.UserService;
import com.elemeHelper.util.PageUtil;

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
	public String login(User user,HttpServletRequest request) {
		PageResult result = userService.login(user,request);
		return result.getPage();
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user!=null) {
			session.removeAttribute("user");
		}
		return PageUtil.user_login;
	}
	
}
