package life.majiang.community.controller;

import life.majiang.community.domain.User;
import life.majiang.community.dto.NotificationDTO;
import life.majiang.community.dto.PaginationDTO;
import life.majiang.community.dto.QuestionDTO;
import life.majiang.community.service.NotificationService;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author CZS
 * @create 2022-07-13 11:34
 * <p>
 * 个人中心页面controller
 **/

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{action}")//使用{参数名称}描述路径参数，下面使用@PathVariable接收路径参数
    public String profile(HttpServletRequest request, Model model,
                          @PathVariable(name = "action") String action,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "5") Integer size) {
        //所有页面持久化登录状态 交给 拦截器

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";//如果没有用户信息，则跳转到首页进行登录
        }

        if ("questions".equals(action)) {
            //我的问题 部分
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的问题");

            //获取当前用户当前页所有问题记录(包括头像url地址)
            PaginationDTO<QuestionDTO> myPaginationDTO = questionService.list(user.getAccountId(), page, size);
            model.addAttribute("pagination", myPaginationDTO);//发送 当前用户当前页 所有问题记录
        } else if ("replies".equals(action)) {
            //最新回复 部分
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "最新回复");

            //获取当前用户当前页所有通知记录
            PaginationDTO<NotificationDTO> myPaginationDTO = notificationService.list(user.getAccountId(), page, size);
            model.addAttribute("pagination", myPaginationDTO);//发送 当前用户当前页 所有通知记录
        }

        return "profile";
    }
}
