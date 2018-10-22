package com.elemeHelper.service;

import java.util.Date;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.elemeHelper.dao.BindDao;
import com.elemeHelper.dao.UserDao;
import com.elemeHelper.entity.Bind;
import com.elemeHelper.entity.User;
import com.elemeHelper.result.PageResult;

@Transactional
@Service
public class UserService {

	@Autowired
	private UserDao userDao;
	@Autowired
	private BindDao bindDao;
	
	public PageResult register(User user,String email) {
		String name = user.getName();
		String pass = user.getPass();
		if (!Pattern.matches("^[A-z,0-9]{6,11}$", name)) {
			return new PageResult("register", "用户名格式不正确");
		}
		if (!Pattern.matches("^\\d{6}$", pass)) {
			return new PageResult("register", "密码不正确");
		}
		if (!Pattern.matches("^\\d{5,10}@qq.com$", email)) {
			return new PageResult("register", "邮箱格式不正确");
		}
		User isRegister = userDao.getByName(name);
		if (isRegister != null) {
			// 已注册
			return new PageResult("register", "用户名已注册");
		}
		user.setCreateDate(new Date());
		user.setCreatorId(0);
		user.setDatalevel(0);
		user.setType(0);
		user = userDao.save(user);
		if (user==null) {
			return new PageResult("register", "注册失败");
		}
		Bind bind = new Bind();
		bind.setAccount(email);
		bind.setCreatorId(user.getId());;
		//邮箱
		bind.setType(0);
		bind.setCreateDate(new Date());;
		bind.setDatalevel(0);
		bind=bindDao.save(bind);
		if (bind==null) {
			return new PageResult("register", "邮箱绑定失败");
		}
	    return new PageResult("login",user);
	}
	
	public PageResult login(User user) {
		String name = user.getName();
		String pass = user.getPass();
		if (!Pattern.matches("^[A-z,0-9]{6,11}$", name)) {
			return new PageResult("login", "用户名格式不正确");
		}
		if (!Pattern.matches("^\\d{6}$", pass)) {
			return new PageResult("login","密码不正确");
		}
		User login = userDao.getByNameAndPassAndType(name, pass,0);
		if (login == null) {
			// 登录失败
			return new PageResult("login", "登录失败，用户名或密码错误");
		}
		return new PageResult("login",user);
	}

}
