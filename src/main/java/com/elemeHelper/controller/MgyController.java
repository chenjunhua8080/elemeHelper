package com.elemeHelper.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.elemeHelper.entity.User;
import com.elemeHelper.result.Result;
import com.elemeHelper.service.MgyService;

@RestController
public class MgyController {

	@Autowired
	private MgyService mgyService;
	
	@PostMapping("/mgyLogin")
	public Result login(User user,HttpServletRequest request,RedirectAttributes redirect) {
		Result result = mgyService.login(user,request);
		return result;
	}
	
}
