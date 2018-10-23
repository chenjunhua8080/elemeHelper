package com.elemeHelper.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	public String login(User user,HttpServletRequest request,RedirectAttributes redirect) {
		PageResult result = userService.login(user);
		if (result.getPage().equals(PageUtil.index)) {
			redirect.addFlashAttribute("user", user);
//			request.getSession().setAttribute("user", user);
			return "redirect:/page/"+result.getPage();
		}else {
			request.setAttribute("error", result);
		}
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
