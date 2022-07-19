package life.majiang.community.controller;

import life.majiang.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author CZS
 * @create 2022-07-13 11:34
 *
 * 个人中心页面controller
 *
 **/

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private UserService userService;

    @GetMapping("/{action}")//使用{参数名称}描述路径参数，下面使用@PathVariable接收路径参数
    public String profile(HttpServletRequest request, Model model,
                          @PathVariable(name = "action") String action,
                          @RequestParam(name = "page",defaultValue = "1") Integer page,
                          @RequestParam(name = "size",defaultValue = "5") Integer size){
        //所有页面持久化登录状态 交给 拦截器

        return userService.profile(request,model,action,page,size);
    }
}
