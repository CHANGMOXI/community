package life.majiang.community.controller;

import life.majiang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author CZS
 * @create 2022-07-11 20:53
 *
 * GitHub登录功能的controller
 *
 **/

@Controller
public class AuthorizeController {

    @Autowired
    private UserService userService;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           HttpServletRequest request, HttpServletResponse response){

        //调用业务层方法：GitHub登录功能
        return userService.loginByGitHub(code, request, response);

    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        return userService.logout(request,response);
    }
}
