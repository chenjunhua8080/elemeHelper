package com.elemeHelper.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.elemeHelper.entity.User;
import com.elemeHelper.result.Result;
import com.elemeHelper.service.YmService;

@RestController
public class YmController {

	@Autowired
	private YmService ymService;
	
	@PostMapping("/ymLogin")
	public Result login(User user,HttpServletRequest request,RedirectAttributes redirect) {
		Result result = ymService.login(user,request);
		return result;
	}
	
}
