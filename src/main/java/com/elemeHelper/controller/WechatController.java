package com.elemeHelper.controller;

import com.elemeHelper.dao.TokenDao;
import com.elemeHelper.entity.Token;
import com.elemeHelper.entity.User;
import com.elemeHelper.result.Result;
import com.elemeHelper.service.ElemeService;
import com.elemeHelper.service.MgyService;
import com.elemeHelper.service.UserService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wx")
public class WechatController {

    @Autowired
    private ElemeService elemeService;
    @Autowired
    private MgyService mgyService;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenDao tokenDao;

    @GetMapping("/getCode")
    public String getCode(HttpServletRequest request, String phone) {
        HttpSession session = request.getSession();
        User user = new User("123456aa", "123456");
        userService.login(user, request);
        mgyService.autoLogin(request);
        User mgyUser = (User) session.getAttribute("mgy");
        if (mgyUser == null) {
            return "获取失败";
        }
        Token token = tokenDao.getLastToken(5, mgyUser.getId());
        mgyService.getPhone(token.getToken(), phone);
        String code = null;
        int b = 0;
        while (code == null) {
            try {
                Thread.sleep(5000);
                System.out.println("第"+(b+1)+"次获取验证码");
                code = mgyService.getMessage(token.getToken(), phone);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            b++;
            if (b > 15) {
                break;
            }
        }
        if (code==null){
            return "获取验证码失败";
        }
        return code;
    }

    @GetMapping("/getVip")
    public Result getVip(HttpServletRequest request) {
        return elemeService.runWX(request);
    }

}
