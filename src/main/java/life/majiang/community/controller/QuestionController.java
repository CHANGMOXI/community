package life.majiang.community.controller;

import life.majiang.community.domain.User;
import life.majiang.community.service.NotificationService;
import life.majiang.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author CZS
 * @create 2022-07-18 20:23
 *
 * 问题详情页面controller
 *
 **/

@Controller
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{id}")
    public String question(@PathVariable(name = "id") Integer id, Model model, HttpServletRequest request){
        //展示通知数
//        User user = (User) request.getSession().getAttribute("user");
//        if (user != null){
//            //user不为空，则已经登录，发送 未读通知数
//            model.addAttribute("unreadCount",notificationService.unreadCount(user.getAccountId()));
//        }

        return questionService.question(id,model);
    }
}
